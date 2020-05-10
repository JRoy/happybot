package io.github.jroy.happybot.sql.og;

import io.github.jroy.happybot.Main;
import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.sql.SQLManager;
import io.github.jroy.happybot.util.C;
import io.github.jroy.happybot.util.Channels;
import io.github.jroy.happybot.util.Roles;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("FieldCanBeLocal")
public class OGCommandManager extends ListenerAdapter {

  private final Connection connection;
  private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `cmds` ( `id` INT(10) NOT NULL AUTO_INCREMENT , `name` VARCHAR(255) NOT NULL , `content` BINARY(255) NOT NULL , `userid` VARCHAR(255) NOT NULL , UNIQUE (`id`)) ENGINE = InnoDB;";
  private final String SELECT_BY_USER = "SELECT * FROM `cmds` WHERE `userid` = ?;";
  private final String SELECT_BY_NAME = "SELECT * FROM `cmds` WHERE `name` = ?;";
  private final String SELECT_BY_ID = "SELECT * FROM `cmds` WHERE `id` = ?;";
  private final String INSERT_COMMAND = "INSERT INTO `cmds` (`name`, `content`, `userid`) VALUES (?, ?, ?)";
  private final String UPDATE_COMMAND_NAME = "UPDATE `cmds` SET name = ? WHERE id = ?;";
  private final String UPDATE_COMMAND_CONTENT = "UPDATE `cmds` SET content = ? WHERE id = ?;";
  private final Map<String, OGAction> ogActionMap = new HashMap<>();
  private final Map<String, String> ogActionMapByUser = new HashMap<>();
  private boolean init = false;

  public OGCommandManager(SQLManager sqlManager) {
    this.connection = sqlManager.getConnection();
    try {
      connection.createStatement().executeUpdate(CREATE_TABLE);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onStatusChange(StatusChangeEvent event) {
    if (event.getNewStatus() == JDA.Status.CONNECTED && !init) {
      init = true;
      for (Member curMember : C.getGuild().getMembers()) {
        if (C.hasRole(curMember, Roles.OG) && hasCommand(curMember.getUser().getId())) {
          String commandName = getCommandFromId(getCommandId(curMember.getUser().getId()));
          registerCommand(commandName);
        }
      }
    }
  }

  @Override
  public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
    if (ogActionMap.containsKey(e.getMessageId()) && C.hasRole(e.getMember(), Roles.SUPER_ADMIN)) {
      String type = e.getReactionEmote().getName();
      if (type.equalsIgnoreCase("✅")) {
        OGAction action = ogActionMap.get(e.getMessageId());

        String cmdname = approveCommand(action);

        Channels.STAFF_QUEUE.getChannel().retrieveMessageById(ogActionMapByUser.get(action.getUserId())).complete().editMessage("✅ Command ^" + cmdname + " has been approved by " + e.getMember().getAsMention()).queue();
        C.privChannel(Objects.requireNonNull(e.getGuild().getMemberById(action.getUserId())), "Your custom command has been approved!");

        ogActionMap.remove(e.getMessageId());
        ogActionMapByUser.remove(action.getUserId());
      } else if (type.equalsIgnoreCase("❌")) {
        OGAction action = ogActionMap.get(e.getMessageId());
        String cmdname = action.getPendingName();
        if (action.getActionType().equals(OGActionType.NAME)) {
          cmdname = cmdname.split("[|]")[1];
        }
        C.privChannel(Objects.requireNonNull(e.getGuild().getMemberById(action.getUserId())), "Your custom command has been denied!");
        Channels.STAFF_QUEUE.getChannel().retrieveMessageById(ogActionMapByUser.get(action.getUserId())).complete().editMessage("❌ Command ^" + cmdname + " has been denied by " + e.getMember().getAsMention()).queue();
        ogActionMapByUser.remove(action.getUserId());
        ogActionMap.remove(e.getMessageId());
      }
    }
  }

  private String approveCommand(OGAction action) {
    switch (action.getActionType()) {
      case COMMAND: {
        createCommand(action.getUserId(), action.getPendingName(), action.getPendingContent());
        break;
      }
      case NAME: {
        setCommandName(action.getPendingId(), action.getPendingName());
        break;
      }
      case CONTENT: {
        setCommandContent(action.getPendingId(), action.getPendingContent());
        break;
      }
    }
    String cmdname = action.getPendingName();
    if (action.getActionType().equals(OGActionType.NAME)) {
      cmdname = cmdname.split("[|]")[1];
    }

    return cmdname;
  }

  public boolean requestCommand(OGAction action) {
    Member member = C.getGuild().getMemberById(action.getUserId());
    if (C.hasRole(member, Roles.MODERATOR)) {
      approveCommand(action);
      return true;
    } else {
      doRequestCommand(action);
      return false;
    }
  }

  private void doRequestCommand(OGAction action) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.setTitle("New OG Command Pending Approval!");
    builder.setDescription("Please react with a check to approve this command or a cross-out to deny it!");
    builder.addField("Action", action.getActionType().getTranslation(), false);
    if (action.getActionType().equals(OGActionType.NAME)) {
      builder.addField("Command Name", action.getPendingName().split("[|]")[1], false);
    } else {
      builder.addField("Command Name", action.getPendingName(), false);
    }
    builder.addField("Command Content", action.getPendingContent(), false);
    User user = C.getGuild().getMemberById(action.getUserId()).getUser();
    builder.setThumbnail(user.getAvatarUrl());
    builder.setFooter("ID: " + user.getId(), null).setTimestamp(OffsetDateTime.now());

    Message message = Channels.STAFF_QUEUE.getChannel().sendMessage(builder.build()).complete();
    message.addReaction("✅").queue();
    message.addReaction("❌").queue();
    ogActionMap.put(message.getId(), action);
    ogActionMapByUser.put(action.getUserId(), message.getId());
  }

  public boolean isPendingAction(String userId) {
    return ogActionMapByUser.containsKey(userId);
  }

  public boolean hasCommand(String userId) {
    try {
      PreparedStatement statement = connection.prepareStatement(SELECT_BY_USER);
      statement.setString(1, userId);
      return statement.executeQuery().next();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean isCommand(String commandName) {
    try {
      PreparedStatement statement = connection.prepareStatement(SELECT_BY_NAME);
      statement.setString(1, commandName);
      return statement.executeQuery().next();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public int getCommandId(String userId) {
    try {
      PreparedStatement statement = connection.prepareStatement(SELECT_BY_USER);
      statement.setString(1, userId);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        return rs.getInt("id");
      } else {
        return -1;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }

  public void createCommand(String userId, String commandName, String commandContent) {
    try {
      PreparedStatement statement = connection.prepareStatement(INSERT_COMMAND);
      statement.setString(1, commandName);
      statement.setBytes(2, commandContent.getBytes(StandardCharsets.UTF_8));
      statement.setString(3, userId);
      statement.execute();
      registerCommand(commandName);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void registerCommand(String commandName) {
    Main.getCommandClient().addCommand(new CommandBase(commandName, null, "Custom Command for OG+", CommandCategory.OG) {
      @Override
      protected void executeCommand(CommandEvent e) {
        e.reply(getCommandContent(commandName));
      }
    });
  }

  public String getCommandFromId(int id) {
    try {
      PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID);
      statement.setInt(1, id);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        return rs.getString("name");
      } else {
        return "";
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
  }

  public String getCommandContent(String commandName) {
    try {
      PreparedStatement statement = connection.prepareStatement(SELECT_BY_NAME);
      statement.setString(1, commandName);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        return new String(rs.getBytes("content"), StandardCharsets.UTF_8).replace("\0", "");
      } else {
        return "";
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
  }

  public String getCommandContentFromId(int commandId) {
    try {
      PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID);
      statement.setInt(1, commandId);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        return new String(rs.getBytes("content"), StandardCharsets.UTF_8).replace("\0", "");
      } else {
        return "";
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return "";
    }
  }

  public void setCommandName(int commandId, String commandName) {
    try {
      String[] split = commandName.split("[|]");
      PreparedStatement statement = connection.prepareStatement(UPDATE_COMMAND_NAME);
      statement.setString(1, split[1]);
      statement.setInt(2, commandId);
      statement.execute();
      Main.getCommandClient().removeCommand(split[0]);
      registerCommand(split[1]);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void setCommandContent(int commandId, String commandContent) {
    try {
      PreparedStatement statement = connection.prepareStatement(UPDATE_COMMAND_CONTENT);
      statement.setBytes(1, commandContent.getBytes(StandardCharsets.UTF_8));
      statement.setInt(2, commandId);
      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}

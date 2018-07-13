package io.github.jroy.happybot.commands;

import io.github.jroy.happybot.commands.base.CommandBase;
import io.github.jroy.happybot.commands.base.CommandCategory;
import io.github.jroy.happybot.commands.base.CommandEvent;
import io.github.jroy.happybot.util.C;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

public class AvatarCommand extends CommandBase {

    public AvatarCommand() {
        super("avatar", "[<user>]", "Gives you the avatar of a user.", CommandCategory.FUN);
    }

    @Override
    protected void executeCommand(CommandEvent e) {
        User target = e.getMember().getUser();

        if (!e.getArgs().isEmpty()) {
            Member fromName = C.getMemberFromName(e.getArgs());
            if (e.containsMention())
                target = e.getMentionedMember().getUser();
            else if (fromName != null)
                target = fromName.getUser();
        }

        e.reply(new EmbedBuilder().setAuthor(C.getFullName(target), target.getAvatarUrl(), target.getEffectiveAvatarUrl())
        .setDescription(C.bold("Avatar"))
        .setImage(target.getAvatarUrl()).build());
    }
}

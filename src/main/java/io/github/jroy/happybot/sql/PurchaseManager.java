package io.github.jroy.happybot.sql;

import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PurchaseManager {

  private final SQLManager sqlManager;

  private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `purchases` ( `id` INT(50) NOT NULL AUTO_INCREMENT , `userId` VARCHAR(255) NOT NULL , `itemId` INT(255) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
  private static final String SELECT_ALL_REWARDS_FROM_USER = "SELECT * FROM `purchases` WHERE userId = ?;";
  private static final String SELECT_REWARD_FROM_USER = "SELECT * FROM `purchases` WHERE userId = ? AND itemId = ?;";
  private static final String ADD_REWARD = "INSERT INTO `purchases` (userId, itemId) VALUES (?, ?);";
  private static final String DELETE_REWARD = "DELETE FROM `purchases` WHERE userId = ? and itemId = ? LIMIT 1;";

  public PurchaseManager(SQLManager sqlManager) {
    log.info("Loading Purchase Manger...");
    this.sqlManager = sqlManager;
    try {
      sqlManager.getConnection().createStatement().execute(CREATE_TABLE);
    } catch (SQLException e) {
      e.printStackTrace();
      log.error("Error while creating table: " + e.getMessage());
    }
    log.info("Loaded Purchase Manager!");
  }

  public boolean hasReward(String userId, Reward reward) {
    try {
      PreparedStatement statement = sqlManager.getConnection().prepareStatement(SELECT_REWARD_FROM_USER);
      statement.setString(1, userId);
      statement.setInt(2, reward.getId());
      return statement.executeQuery().next();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public ResultSet getAllRewards(String userId) throws SQLException {
    PreparedStatement statement = sqlManager.getConnection().prepareStatement(SELECT_ALL_REWARDS_FROM_USER);
    statement.setString(1, userId);
    return statement.executeQuery();
  }

  public List<Reward> getAllRewardsList(String userId) throws SQLException {
    PreparedStatement statement = sqlManager.getConnection().prepareStatement(SELECT_ALL_REWARDS_FROM_USER);
    statement.setString(1, userId);
    ResultSet set = statement.executeQuery();
    List<Reward> rewards = new ArrayList<>();
    while (set.next()) {
      rewards.add(Reward.getFromId(set.getInt("itemId")));
    }
    return rewards;
  }

  public void addReward(String userId, Reward reward) {
    try {
      PreparedStatement statement = sqlManager.getConnection().prepareStatement(ADD_REWARD);
      statement.setString(1, userId);
      statement.setInt(2, reward.getId());
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void deleteSingleReward(String userId, Reward reward) {
    try {
      PreparedStatement statement = sqlManager.getConnection().prepareStatement(DELETE_REWARD);
      statement.setString(1, userId);
      statement.setInt(2, reward.getId());
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public SQLManager getSqlManager() {
    return sqlManager;
  }
}

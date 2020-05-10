package io.github.jroy.happybot.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserToken {

  private final int id;
  private final String userId;
  private int coins;
  private long epoch;
  private final SQLManager sqlManager;

  UserToken(SQLManager sqlManager, String userID) throws SQLException {
    ResultSet resultSet = sqlManager.getConnection().createStatement().executeQuery("SELECT * FROM user WHERE userid = " + userID + ";");
    resultSet.next();

    this.id = resultSet.getInt("id");
    this.userId = resultSet.getString("userid");
    this.coins = resultSet.getInt("coins");
    this.epoch = resultSet.getLong("epoch");

    this.sqlManager = sqlManager;
  }

  public int getId() {
    return id;
  }

  public String getUserId() {
    return userId;
  }

  public int getCoins() {
    return coins;
  }

  public void setCoins(int coins) throws SQLException {
    PreparedStatement statement = sqlManager.getConnection().prepareStatement("UPDATE user SET coins = ? WHERE id = " + id + ";");
    statement.setInt(1, coins);
    statement.execute();
    this.coins = coins;
  }

  public void addCoins(int increment) throws SQLException {
    setCoins(Math.addExact(coins, increment));
  }

  public void takeCoins(int subtraction) throws SQLException {
    setCoins(Math.subtractExact(coins, subtraction));
  }

  public long getEpoch() {
    return epoch;
  }

  public void setEpoch(long epoch) throws SQLException {
    PreparedStatement statement = sqlManager.getConnection().prepareStatement("UPDATE user SET epoch = ? WHERE id = " + id + ";");
    statement.setLong(1, epoch);
    statement.execute();
    this.epoch = epoch;
  }

}

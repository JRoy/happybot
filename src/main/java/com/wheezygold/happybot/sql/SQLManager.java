package com.wheezygold.happybot.sql;

import com.wheezygold.happybot.util.C;
import com.wheezygold.happybot.util.Constants;
import com.wheezygold.happybot.util.Logger;
import net.dv8tion.jda.core.entities.Member;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SQLManager {

    private Connection connection;
    private String password;
    private SQLManager instance;

    public SQLManager(String sqlPassword) {
        password = sqlPassword;
        Logger.info("Loading SQLManager...");
        try {
            connect();
            Logger.info("Connected to SQL!");
        } catch (SQLException | ClassNotFoundException e) {
            Logger.error("Error connecting to the SQL Database: " + e.getMessage());
        }
        instance = this;
    }

    public UserToken getUser(String userId) throws SQLException {
        if (isActiveUser(userId))
            return new UserToken(instance, userId);
        return null;
    }

    public void newUser(String userId) throws SQLException {
        if (!isActiveUser(userId)) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO user (userid, coins, epoch) VALUES (?, ?, ?);");
            statement.setString(1, userId);
            statement.setInt(2, 0);
            statement.setLong(3, 0);
            statement.execute();
        }
    }

    public boolean isActiveUser(String userId) throws SQLException {
        ResultSet result = connection.createStatement().executeQuery("SELECT * FROM user WHERE userid = '" + userId + "';");
        return result.next();
    }

    public boolean isActiveUserH(String userId) {
        try {
            ResultSet result = connection.createStatement().executeQuery("SELECT * FROM user WHERE userid = '" + userId + "';");
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<Integer, Map<Member, Integer>> getTop(int amount) throws SQLException {
        Map<Integer, Map<Member, Integer>> topBal = new HashMap<>();
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM user ORDER BY coins DESC;");
        for (int i = 0; i < amount; i++) {
            resultSet.next();
            if (C.getGuild().getMemberById(resultSet.getString("userid")) == null) {
                resultSet.next();
            }
            topBal.put(i + 1, new HashMap<>());
            topBal.get(i + 1).put(C.getGuild().getMemberById(resultSet.getString("userid")), resultSet.getInt("coins"));
        }
        return topBal;
    }

    private void connect() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/" + Constants.SQL_DATABASE_NAME.get(), Constants.SQL_USERNAME.get(), password);
        }

    }

    public Connection getConnection() {
        return connection;
    }
}

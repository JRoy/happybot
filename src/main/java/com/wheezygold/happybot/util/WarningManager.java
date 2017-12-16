package com.wheezygold.happybot.util;

import com.wheezygold.happybot.sql.SQLManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("FieldCanBeLocal")
public class WarningManager {

    private Connection connection;

    private String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `warnings` ( `id` INT(50) NOT NULL AUTO_INCREMENT , `targetid` VARCHAR(50) NOT NULL , `staffid` VARCHAR(50) NOT NULL , `reason` VARCHAR(255) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
    private String SELECT_USER = "SELECT * FROM warnings WHERE targetid = ?;";
    private String CREATE_WARNING = "INSERT INTO warnings (targetid, staffid, reason) VALUES (?, ?, ?);";
    private String DELETE_WARNING = "DELETE FROM `warnings` WHERE id = ?;";


    public WarningManager(SQLManager sqlManager) {
        this.connection = sqlManager.getConnection();
        try {
            connection.createStatement().executeUpdate(CREATE_TABLE);
        } catch (SQLException e) {
            Logger.error("Error while creating the table: " + e.getMessage());
        }
    }

    public void spawnWarning(String targetID, String staffID, String reason) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(CREATE_WARNING);
        statement.setString(1, targetID);
        statement.setString(2, staffID);
        statement.setString(3, reason);
        statement.execute();
    }

    public ResultSet fetchWarnings(String targetID) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SELECT_USER);
        statement.setString(1, targetID);
        return statement.executeQuery();
    }

    public boolean deleteWarning(int warnId) {
        try {
            PreparedStatement statement = connection.prepareStatement(DELETE_WARNING);
            statement.setInt(1, warnId);
            statement.execute();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

}

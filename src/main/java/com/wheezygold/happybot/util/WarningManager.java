package com.wheezygold.happybot.util;

import com.wheezygold.happybot.sql.SQLManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("FieldCanBeLocal")
public class WarningManager {

    private Connection connection;

    private String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `warnings` ( `id` INT(50) NOT NULL AUTO_INCREMENT , `targetid` VARCHAR(50) NOT NULL , `staffid` VARCHAR(50) NOT NULL , `reason` VARCHAR(100) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
    private String SELECT_USSER = "SELECT * FROM warnings WHERE targetid = ?;";


    public WarningManager(SQLManager sqlManager) {
        this.connection = sqlManager.getConnection();
        try {
            connection.createStatement().executeUpdate(CREATE_TABLE);
        } catch (SQLException e) {
            Logger.error("Error while creating the table: " + e.getMessage());
        }
    }

    public void spawnWarning(String targetID, String staffID, String reason) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO warnings (targetid, staffid, reason) VALUES (?, ?, ?);");
        statement.setString(1, targetID);
        statement.setString(2, staffID);
        statement.setString(3, reason);
        statement.execute();
    }

    public ResultSet fetchWarnings(String targetID) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SELECT_USSER);
        statement.setString(1, targetID);
        return statement.executeQuery();
    }

}

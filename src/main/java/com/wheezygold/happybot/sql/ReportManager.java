package com.wheezygold.happybot.sql;

import com.wheezygold.happybot.util.Logger;

import javax.annotation.CheckForNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("FieldCanBeLocal")
public class ReportManager {

    private final Connection connection;

    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `reports` ( `id` INT(50) NOT NULL AUTO_INCREMENT , `reporterid` VARCHAR(50) NOT NULL , `reportedid` VARCHAR(50) NOT NULL , `channelid` VARCHAR(255) NOT NULL , `reason` VARCHAR(255) NOT NULL , `handleid` VARCHAR(50) NOT NULL DEFAULT '0' , `handlereason` VARCHAR(255) NOT NULL DEFAULT 'NONE' , `status` INT(50) NOT NULL DEFAULT '0' , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
    private final String CREATE_REPORT = "INSERT INTO `reports` (reportedid, reporterid, channelid, reason) VALUES (?, ?, ?, ?);";
    private final String SELECT_REPORT = "SELECT * FROM `reports` WHERE id = ?;";
    private final String UPDATE_STATUS = "UPDATE `reports` SET handleid = ?, handlereason = ?, status = ? WHERE id = ?";

    public ReportManager(SQLManager sqlManager) {
        connection = sqlManager.getConnection();
        try {
            connection.createStatement().executeUpdate(CREATE_TABLE);
        } catch (SQLException e) {
            Logger.error("Error while creating the table: " + e.getMessage());
        }
    }

    public int spawnReport(String targetId, String fromId, String channelId, String reason) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(CREATE_REPORT);
        statement.setString(1, targetId);
        statement.setString(2, fromId);
        statement.setString(3, channelId);
        statement.setString(4, reason);
        statement.execute();
        ResultSet rs = statement.getGeneratedKeys();
        int key = -1;
        if (rs.next()) {
            key = rs.getInt(1);
        }
        return key;
    }

    @CheckForNull
    public ReportToken getReportAsToken(int reportId) {
        try {
            PreparedStatement statement = connection.prepareStatement(SELECT_REPORT);
            statement.setInt(1, reportId);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                return new ReportToken(reportId, set.getString("reporterid"), set.getString("reportedid"), set.getString("channelid"), set.getString("reason"), set.getString("handleid"), set.getString("handlereason"), set.getInt("status"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isValidReport(int reportId) {
        try {
            PreparedStatement statement = connection.prepareStatement(SELECT_REPORT);
            statement.setInt(1, reportId);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public String getReportAuthor(int reportId) {
        try {
            PreparedStatement statement = connection.prepareStatement(SELECT_REPORT);
            statement.setInt(1, reportId);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                return set.getString("reporterid");
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public String getReportTarget(int reportId) {
        try {
            PreparedStatement statement = connection.prepareStatement(SELECT_REPORT);
            statement.setInt(1, reportId);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                return set.getString("reportedid");
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public int getReportStatus(int reportId) {
        try {
            PreparedStatement statement = connection.prepareStatement(SELECT_REPORT);
            statement.setInt(1, reportId);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                return set.getInt("status");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            return -1;
        }
    }

    public void acceptReport(int reportId, String staffId, String reason) {
        try {
            PreparedStatement statement = connection.prepareStatement(UPDATE_STATUS);
            statement.setString(1, staffId);
            statement.setString(2, reason);
            statement.setInt(3, 1);
            statement.setInt(4, reportId);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void denyReport(int reportId, String staffId, String reason) {
        try {
            PreparedStatement statement = connection.prepareStatement(UPDATE_STATUS);
            statement.setString(1, staffId);
            statement.setString(2, reason);
            statement.setInt(3, 2);
            statement.setInt(4, reportId);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

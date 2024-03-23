package org.se13.sqlite.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;


public class ConfigRepositoryImpl implements ConfigRepository {
    // DB connection
    private Connection connect() {
        String url = "jdbc:sqlite:./tetris.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // 초기 테이블 생성
    public void createNewTableConfig() {
        String sql = "CREATE TABLE IF NOT EXISTS config ("
                + "	id integer PRIMARY KEY,"
                + "	settings text NOT NULL" // JSON 형식의 설정 값
                + ");";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // 초기 설정 값 삽입
    @Override
    public void insertDefaultConfig(int id) {
        JSONObject json = new JSONObject();
        json.put("mode", "default");
        json.put("gridWidth", 10);
        json.put("gridHeight", 22);
        json.put("keyLeft", 75);
        json.put("keyRight", 77);
        json.put("keyDown", 80);
        json.put("keyRotateLeft", 120);
        json.put("keyRotateRight", 121);
        json.put("keyPause", 32);

        String sql = "INSERT INTO config (id, settings) VALUES(?,?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, json.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateConfig(int id, String mode, int gridWidth, int gridHeight, int keyLeft, int keyRight, int keyDown, int keyRotateLeft, int keyRotateRight, int keyPause) {
        JSONObject json = new JSONObject();
        json.put("mode", mode);
        json.put("gridWidth", gridWidth);
        json.put("gridHeight", gridHeight);
        json.put("keyLeft", keyLeft);
        json.put("keyRight", keyRight);
        json.put("keyDown", keyDown);
        json.put("keyRotateLeft", keyRotateLeft);
        json.put("keyRotateRight", keyRotateRight);
        json.put("keyPause", keyPause);

        String sql = "UPDATE config SET settings = ? WHERE id = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, json.toString());
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getConfig(int id) {
        String sql = "SELECT settings FROM config WHERE id = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String settingsJson = rs.getString("settings");
                JSONObject json = new JSONObject(settingsJson);

                Map<String, Object> result = new HashMap<>();
                result.put("mode", json.getString("mode"));
                result.put("gridWidth", json.getInt("gridWidth"));
                result.put("gridHeight", json.getInt("gridHeight"));
                result.put("keyLeft", json.getInt("keyLeft"));
                result.put("keyRight", json.getInt("keyRight"));
                result.put("keyDown", json.getInt("keyDown"));
                result.put("keyRotateLeft", json.getInt("keyRotateLeft"));
                result.put("keyRotateRight", json.getInt("keyRotateRight"));
                result.put("keyPause", json.getInt("keyPause"));

                return result;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public void clearConfig(int id) {
        String sql = "DELETE FROM config WHERE id = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

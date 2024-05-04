package org.se13.sqlite.config;

import org.json.JSONObject;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;


public class ConfigRepositoryImpl implements ConfigRepository {
    public ConfigRepositoryImpl() {
        super();

        this.createNewTableConfig();
        this.insertDefaultConfig();
    }

    public static ConfigRepositoryImpl getInstance() {
        if (configRepositoryImpl == null) {
            configRepositoryImpl = new ConfigRepositoryImpl();
        }

        return configRepositoryImpl;
    }

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
        //String dropSql = "DROP TABLE IF EXISTS config;";

        String sql = "CREATE TABLE IF NOT EXISTS config ("
                + "	id integer PRIMARY KEY CHECK (id=0),"
                + "	settings text NOT NULL" // JSON 형식의 설정 값
                + ");";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            //stmt.execute(dropSql);
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // 초기 설정 값 삽입
    @Override
    public void insertDefaultConfig() {
        JSONObject json = new JSONObject();
        json.put("mode", "default");
        json.put("screenWidth", 300);
        json.put("screenHeight", 400);
        json.put("keyLeft", "a");
        json.put("keyRight", "d");
        json.put("keyDown", "s");
        json.put("keyRotate", "e");
        json.put("keyPause", "p");
        json.put("keyDrop", "w");
        json.put("keyExit", "q");

        String sql = "INSERT INTO config (id, settings) VALUES(0,?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, json.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error inserting default config: "+ e.getMessage());
        }
    }

    @Override
    public void updateConfig(String mode, int gridWidth, int gridHeight, String keyLeft, String keyRight, String keyDown, String keyRotate, String keyPause, String keyDrop, String keyExit) {
        JSONObject json = new JSONObject();
        json.put("mode", mode);
        json.put("screenWidth", gridWidth);
        json.put("screenHeight", gridHeight);
        json.put("keyLeft", keyLeft);
        json.put("keyRight", keyRight);
        json.put("keyDown", keyDown);
        json.put("keyRotate", keyRotate);
        json.put("keyPause", keyPause);
        json.put("keyDrop", keyDrop);
        json.put("keyExit", keyExit);

        String sql = "UPDATE config SET settings = ? WHERE id = 0";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, json.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating config: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getConfig() {
        String sql = "SELECT settings FROM config WHERE id = 0";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String settingsJson = rs.getString("settings");
                JSONObject json = new JSONObject(settingsJson);

                Map<String, Object> result = new HashMap<>();
                result.put("mode", json.getString("mode"));
                result.put("screenWidth", json.getInt("screenWidth"));
                result.put("screenHeight", json.getInt("screenHeight"));
                result.put("keyLeft", json.get("keyLeft"));
                result.put("keyRight", json.get("keyRight"));
                result.put("keyDown", json.get("keyDown"));
                result.put("keyRotate", json.get("keyRotate"));
                result.put("keyPause", json.get("keyPause"));
                result.put("keyDrop", json.get("keyDrop"));
                result.put("keyExit", json.get("keyExit"));

                return result;
            }
        } catch (SQLException e) {
            System.out.println("Error getting config: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void clearConfig() {
        String sql = "DELETE FROM config WHERE id = 0";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public int[] getScreenSize() {
        Map<String, Object> config = getConfig();

        if (config == null){
            return new int[]{300, 400};
        }

        int screenWidth = (Integer) config.get("screenWidth");
        int screenHeight = (Integer) config.get("screenHeight");

        return new int[]{screenWidth, screenHeight};
    }

    @Override
    public String getBlockColorMode() {
        Map<String, Object> config = getConfig();

        if (config == null){
            return "default";
        }

        return (String) config.get("mode");
    }

    private static ConfigRepositoryImpl configRepositoryImpl;
}

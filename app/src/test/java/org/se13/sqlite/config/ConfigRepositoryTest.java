package org.se13.sqlite.config;

import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class ConfigRepositoryTest {

    ConfigRepositoryImpl configRepository = new ConfigRepositoryImpl();

    @AfterEach
    void deleteTestDB() {
        Path dbPath = Paths.get("./tetris.db");
        try {
            Files.deleteIfExists(dbPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Config 테이블 생성 테스트")
    @Order(1)// 테이블 생성 테스트만 우선적으로 실행
    void createNewTableConfigTest() throws SQLException {
        configRepository.createNewTableConfig();

        String url = "jdbc:sqlite:./tetris.db";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement("SELECT name FROM sqlite_master WHERE type='table' AND name='config'")) {
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next());
                assertEquals("config", rs.getString("name"));
                assertFalse(rs.next());
            }
        }
    }

    @Test
    @DisplayName("설정 초기 설정값 테스트")
    @Order(2)
    void insertConfigTest() throws SQLException {
        // 객체 생성자에 insertConfig(0)이 포함되어 있다
        configRepository.createNewTableConfig();

        JSONObject json = new JSONObject();
        json.put("mode", "default");
        json.put("screenWidth", 300);
        json.put("screenHeight", 400);
        json.put("keyLeft", 'a');
        json.put("keyRight", 'd');
        json.put("keyDown", 's');
        json.put("keyRotateLeft", 120);
        json.put("keyRotateRight", 'e');
        json.put("keyPause", 'p');
        json.put("keyDrop", 'w');
        json.put("keyExit", 'q');

        String url = "jdbc:sqlite:./tetris.db";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM config WHERE id = 0")) {
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(json.toString(), rs.getString("settings"));
                assertFalse(rs.next());
            }
        }
    }

    @Test
    @DisplayName("설정 값 getter 테스트")
    @Order(3)
    void getConfigTest() {
        configRepository.createNewTableConfig();

        Map<String, Object> config = configRepository.getConfig(0);
        assertNotNull(config);
        assertEquals(11, config.size());
        assertEquals("default", config.get("mode"));
        assertEquals(300, config.get("screenWidth"));
        assertEquals(400, config.get("screenHeight"));
        assertEquals(97, config.get("keyLeft"));
        assertEquals(100, config.get("keyRight"));
        assertEquals(115, config.get("keyDown"));
        assertEquals(120, config.get("keyRotateLeft"));
        assertEquals(101, config.get("keyRotateRight"));
        assertEquals(112, config.get("keyPause"));
        assertEquals(119, config.get("keyDrop"));
        assertEquals(113, config.get("keyExit"));
    }

    @Test
    @DisplayName("설정 update 테스트")
    @Order(4)
    void updateConfigTest() {
        configRepository.createNewTableConfig();

        configRepository.updateConfig(
                0,
                "test",
                100,
                200,
                'q',
                'w',
                'e',
                'r',
                't',
                'y',
                'u',
                'i');
        Map<String, Object> config = configRepository.getConfig(0);
        assertNotNull(config);
        assertEquals("test", config.get("mode"));
        assertEquals(100, config.get("screenWidth"));
        assertEquals(200, config.get("screenHeight"));
        assertEquals(113, config.get("keyLeft"));
        assertEquals(119, config.get("keyRight"));
        assertEquals(101, config.get("keyDown"));
        assertEquals(114, config.get("keyRotateLeft"));
        assertEquals(116, config.get("keyRotateRight"));
        assertEquals(121, config.get("keyPause"));
        assertEquals(117, config.get("keyDrop"));
        assertEquals(105, config.get("keyExit"));
    }

    @Test
    @DisplayName("설정 값 삭제 테스트")
    @Order(5)
    void clearConfigTest() throws SQLException {
        configRepository.createNewTableConfig();
        configRepository.clearConfig(0);
        String url = "jdbc:sqlite:./tetris.db";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM config WHERE id = 0")) {
            try (ResultSet rs = pstmt.executeQuery()) {
                assertFalse(rs.next());
            }
        }
    }
}

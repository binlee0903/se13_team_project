package org.se13.sqlite.ranking;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RankingRepositoryImplTest {

    RankingRepositoryImpl rankingRepository = new RankingRepositoryImpl();

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
    @DisplayName("Ranking 테이블 생성 테스트")
    @Order(1)// 테이블 생성 테스트만 우선적으로 실행
    void createNewTableRankingTest() throws SQLException {
        rankingRepository.createNewTableRanking();

        String url = "jdbc:sqlite:./tetris.db";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement("SELECT name FROM sqlite_master WHERE type='table' AND name='ranking'")) {
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next());
                assertEquals("ranking", rs.getString("name"));
                assertFalse(rs.next());
            }
        }
    }

    @Test
    @DisplayName("점수 insert 테스트")
    void insertRankingTest() throws SQLException {
        rankingRepository.createNewTableRanking();
        rankingRepository.insertRanking("insertRanking1", 1, false, "easy");
        rankingRepository.insertRanking("insertRanking2", 2, true, "hard");

        String url = "jdbc:sqlite:./tetris.db";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM ranking ORDER BY score")) {
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next());
                assertEquals("insertRanking1", rs.getString("name"));
                assertEquals(1, rs.getInt("score"));
                assertFalse(rs.getBoolean("isItem"));
                assertEquals("easy", rs.getString("diff"));
                assertTrue(rs.next());
                assertEquals("insertRanking2", rs.getString("name"));
                assertEquals(2, rs.getInt("score"));
                assertTrue(rs.getBoolean("isItem"));
                assertEquals("hard", rs.getString("diff"));
                assertFalse(rs.next());
            }
        }
    }

    @Test
    @DisplayName("점수 getter 테스트")
    void getRankingTest() throws SQLException {
        rankingRepository.createNewTableRanking();
        String url = "jdbc:sqlite:./tetris.db";
        try (Connection conn = DriverManager.getConnection(url)) {
            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ranking (name, score, isItem, diff) VALUES(?,?,?,?)")) {
                pstmt.setString(1, "getRanking1");
                pstmt.setInt(2, 10);
                pstmt.setBoolean(3, false);
                pstmt.setString(4, "easy");
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ranking (name, score, isItem, diff) VALUES(?,?,?,?)")) {
                pstmt.setString(1, "getRanking2");
                pstmt.setInt(2, 20);
                pstmt.setBoolean(3, true);
                pstmt.setString(4, "hard");
                pstmt.executeUpdate();
            }
        }
        List<Map<String, Object>> ranking = rankingRepository.getRankingList();
        assertNotNull(ranking);
        assertEquals(2, ranking.size());
        assertEquals("getRanking1", ranking.get(1).get("name"));
        assertEquals(10, ranking.get(1).get("score"));
        assertFalse((Boolean) ranking.get(1).get("isItem"));
        assertEquals("easy", ranking.get(1).get("diff"));
        assertEquals("getRanking2", ranking.getFirst().get("name"));
        assertEquals(20, ranking.getFirst().get("score"));
        assertTrue((Boolean) ranking.getFirst().get("isItem"));
        assertEquals("hard", ranking.getFirst().get("diff"));
        assertTrue((int) ranking.getFirst().get("score") > (int) ranking.get(1).get("score"));
    }

    @Test
    @DisplayName("랭킹 테이블 초기화 테스트")
    void clearRankingTest() throws SQLException {
        rankingRepository.createNewTableRanking();
        String url = "jdbc:sqlite:./tetris.db";
        try (Connection conn = DriverManager.getConnection(url)) {
            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ranking (name, score, isItem, diff) VALUES(?,?,?,?), (?,?,?,?), (?,?,?,?)")) {
                pstmt.setString(1, "clearRanking1");
                pstmt.setInt(2, 400);
                pstmt.setBoolean(3, true);
                pstmt.setString(4, "clearRanking1");
                pstmt.setString(5, "clearRanking2");
                pstmt.setInt(6, 500);
                pstmt.setBoolean(7, false);
                pstmt.setString(8, "clearRanking2");
                pstmt.setString(9, "clearRanking3");
                pstmt.setInt(10, 600);
                pstmt.setBoolean(11, true);
                pstmt.setString(12, "clearRanking3");
                pstmt.executeUpdate();
            }
        }
        rankingRepository.clearRanking();
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM ranking")) {
            try (ResultSet rs = pstmt.executeQuery()) {
                assertFalse(rs.next()); // 다음 행이 존재하면 테스트 실패
            }
        }
    }
}

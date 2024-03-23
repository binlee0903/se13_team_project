package org.se13.sqlite.ranking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankingRepositoryImpl implements RankingRepository {
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

    @Override
    public void createNewTableRanking() {
        String sql = "CREATE TABLE IF NOT EXISTS ranking ("
                + "	id integer PRIMARY KEY AUTOINCREMENT,"
                + "	name text NOT NULL,"
                + " score INTEGER NOT NULL"
                + ");";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void insertRanking(String name, int score) {
        String sql = "INSERT INTO ranking (name, score) VALUES(?,?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getRanking() {
        String sql = "SELECT * FROM ranking ORDER BY score DESC LIMIT 10";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            List<Map<String, Object>> results = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> result = new HashMap<>();
                result.put("name", rs.getString("name"));
                result.put("score", rs.getInt("score"));
                results.add(result);
            }
            return results;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public void clearRanking() {
        String sql = "DELETE FROM ranking";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // 테스트 코드
    public static void main(String[] args) {
        RankingRepository rankingRepository = new RankingRepositoryImpl();
        rankingRepository.createNewTableRanking();
        rankingRepository.insertRanking("test4", 400);
        rankingRepository.insertRanking("test5", 500);
        rankingRepository.insertRanking("test6", 600);
        rankingRepository.insertRanking("test7", 700);
        rankingRepository.insertRanking("test8", 800);
        rankingRepository.insertRanking("test9", 900);
        rankingRepository.insertRanking("test10", 1000);
    }
}

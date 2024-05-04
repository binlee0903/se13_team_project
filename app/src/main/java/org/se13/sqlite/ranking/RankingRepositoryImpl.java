package org.se13.sqlite.ranking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
                + " score INTEGER NOT NULL,"
                + " isItem BOOLEAN NOT NULL,"
                + " diff TEXT NOT NULL"
                + ");";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void insertRanking(String name, int score, boolean isItem, String diff) {
        String sql = "INSERT INTO ranking (name, score, isItem, diff) VALUES(?,?,?,?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, score);
            pstmt.setBoolean(3, isItem);
            pstmt.setString(4, diff);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Ranking> getRankingList() {
        String sql = "SELECT * FROM ranking ORDER BY score DESC LIMIT 10";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            List<Ranking> results = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int score = rs.getInt("score");
                boolean isItem = rs.getBoolean("isItem");
                String diff = rs.getString("diff");
                results.add(new Ranking(id, name, score, isItem, diff));
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
}

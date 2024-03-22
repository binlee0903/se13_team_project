package org.se13.sqlite.ranking;

import java.util.List;
import java.util.Map;

public interface RankingRepository {
    void createNewTableRanking();
    void insertRanking(String name, int score);
    List<Map<String, Object>> getRanking();
    void clearRanking();
}

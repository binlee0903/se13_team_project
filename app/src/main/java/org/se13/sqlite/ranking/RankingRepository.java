package org.se13.sqlite.ranking;

import java.util.List;
import java.util.Map;

public interface RankingRepository {
    void insertRanking(String name, int points);
    List<Map<String, Object>> getRanking();
    void clearRanking();
}

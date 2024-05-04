package org.se13.sqlite.ranking;

import java.util.List;

public interface RankingRepository {
    void createNewTableRanking();
    void insertRanking(String name, int score, boolean isItem, String diff);
    List<Ranking> getRankingList();
    void clearRanking();
}

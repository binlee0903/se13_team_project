package org.se13.sqlite.ranking;

public class Ranking {
    public Ranking(int id, String name, int score, boolean isItem, String diff) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.isItem = isItem;
        this.diff = diff;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getScore() {
        return score;
    }
    public boolean isItem() {
        return isItem;
    }
    public String getDiff() {
        return diff;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public void setItem(boolean item) {
        isItem = item;
    }
    public void setDiff(String diff) {
        this.diff = diff;
    }

    private int id;
    private String name;
    private int score;
    private boolean isItem;
    private String diff;
}
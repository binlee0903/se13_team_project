package org.se13.game.rule;

public record BlockWeight(int easy, int normal, int hard) {

    public static int Easy = 12;
    public static int Normal = 10;
    public static int Hard = 8;
    public static BlockWeight DefaultWeight = new BlockWeight(Easy, Normal, Hard);

    public int of(GameLevel level) {
        return switch (level) {
            case EASY -> easy;
            case NORMAL -> normal;
            case HARD -> hard;
        };
    }
}

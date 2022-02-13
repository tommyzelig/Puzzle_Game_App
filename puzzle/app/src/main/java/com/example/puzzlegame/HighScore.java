package com.example.puzzlegame;

public class HighScore {
    private int points;
    private int level;
    private long time;

    public HighScore(String line) {
        String[] parts = line.split("\\|");
        this.points = Integer.parseInt(parts[0]);
        this.level = Integer.parseInt(parts[1]);
        this.time = Long.parseLong(parts[2]);
    }

    public HighScore(int points, int level, long time) {
        this.points = points;
        this.level = level;
        this.time = time;
    }

    @Override
    public String toString() {
        return points + "|" + level + "|" + time;
    }

    public int getPoints() {
        return points;
    }

    public int getLevel() {
        return level;
    }

    public long getTime() {
        return time;
    }
}

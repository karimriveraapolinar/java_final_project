package game.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks player progress and statistics.
 * Level, times, records, leaderboard place.
 */
public class PlayerStats {
    private int currentLevel;
    private List<Long> bestTimes;
    private int leaderboardRank;

    public PlayerStats() {
        this.currentLevel = 1;
        this.bestTimes = new ArrayList<>();
        // Initialize with some default values or empty
        for (int i = 0; i < 5; i++) {
            bestTimes.add(Long.MAX_VALUE);
        }
        this.leaderboardRank = 0;
    }

    /**
     * @return the current level number.
     */
    public int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * @param currentLevel the level number to set.
     */
    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    /**
     * Updates the best time for a level if the new time is faster.
     * @param level the level number.
     * @param time the completion time in milliseconds.
     */
    public void updateBestTime(int level, long time) {
        if (level >= 1 && level <= bestTimes.size()) {
            if (time < bestTimes.get(level - 1)) {
                bestTimes.set(level - 1, time);
            }
        }
    }

    /**
     * @param level the level number.
     * @return the best time for that level.
     */
    public long getBestTime(int level) {
        if (level >= 1 && level <= bestTimes.size()) {
            return bestTimes.get(level - 1);
        }
        return 0;
    }

    /**
     * @return the current rank on the leaderboard.
     */
    public int getLeaderboardRank() {
        return leaderboardRank;
    }

    /**
     * @param leaderboardRank the rank to set.
     */
    public void setLeaderboardRank(int leaderboardRank) {
        this.leaderboardRank = leaderboardRank;
    }
}

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

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public void updateBestTime(int level, long time) {
        if (level >= 1 && level <= bestTimes.size()) {
            if (time < bestTimes.get(level - 1)) {
                bestTimes.set(level - 1, time);
            }
        }
    }

    public long getBestTime(int level) {
        if (level >= 1 && level <= bestTimes.size()) {
            return bestTimes.get(level - 1);
        }
        return 0;
    }

    public int getLeaderboardRank() {
        return leaderboardRank;
    }

    public void setLeaderboardRank(int leaderboardRank) {
        this.leaderboardRank = leaderboardRank;
    }
}

package net.teatwig.mineswraft;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;
import java.util.stream.IntStream;

/**
 * Created by timo on 24.03.2016.
 */
class Statistics {

    private static int[] playedGames = new int[3];
    private static int[] wonGames = new int[3];
    private static Duration[] bestTime = new Duration[3];
    private static int[] currentStreak = new int[3];
    private static int[] longestWinningStreak = new int[3];
    private static int[] longestLosingStreak = new int[3];
    private static Duration[] averageTime = new Duration[3];
    // TODO best times or maybe name entry?

    static void addGameResult(boolean won, Difficulty difficulty, Duration newTime) {
        if (Controller.isExpNonogramModeEnabled()) {
            System.out.println("Stats for Nonogram-Mode won't be saved with the normal ones, you cheater!");
            return;
        }

        int difType = difficulty.getType();
        if (difType != Difficulty.CUSTOM) {
            playedGames[difType] += 1;
            if (won) {
                wonGames[difType] += 1;
                currentStreak[difType] = currentStreak[difType] < 0 ? 1 : currentStreak[difType] + 1;
                if (bestTime[difType] == null || (newTime != Duration.ZERO && newTime.compareTo(bestTime[difType]) < 0)) {
                    bestTime[difType] = newTime;
                }
            } else {
                currentStreak[difType] = currentStreak[difType] > 0 ? -1 : currentStreak[difType] - 1;
            }

            int streak = currentStreak[difType];
            if (streak < longestLosingStreak[difType]) {
                longestLosingStreak[difType] = streak;
            } else if (streak > longestWinningStreak[difType]) {
                longestWinningStreak[difType] = streak;
            }
        }
    }

    static void showStatisticsDialog() {
        Dialog<?> statisticsDialog = new Dialog<>();
        Controller.setDialogIcon(statisticsDialog);
        statisticsDialog.setTitle("Statistics");
        HBox content = new HBox(
                new Label(statisticsStrFor(Difficulty.EASY)),
                new Label(statisticsStrFor(Difficulty.MEDIUM)),
                new Label(statisticsStrFor(Difficulty.HARD))
        );
        content.setSpacing(20);
        content.setAlignment(Pos.TOP_LEFT); // TODO doesn't work??

        statisticsDialog.getDialogPane().setContent(content);
        statisticsDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        statisticsDialog.show();
    }

    private static String statisticsStrFor(int difType) {
        String difficultyName;
        switch (difType) {
            case Difficulty.EASY:
                difficultyName = "Easy";
                break;
            case Difficulty.MEDIUM:
                difficultyName = "Medium";
                break;
            case Difficulty.HARD:
                difficultyName = "Hard";
                break;
            default:
                return "ERROR";
        }

        return "- " + difficultyName + "-\n" +
                "Games played: " + playedGames[difType] + "\n" +
                "Games won: " + wonGames[difType] + "\n" +
                "Win percentage: " + (int) ((wonGames[difType] * 100f) / playedGames[difType]) + "%\n" +
                (bestTime[difType] == null ? "" : "Best time: " + bestTime[difType].getSeconds() + "." + String.valueOf(bestTime[difType].getNano()).replaceFirst("(0+)$", "") + " s\n") +
                "Longest winning streak: " + longestWinningStreak[difType] + "\n" +
                "Longest losing streak: " + -longestLosingStreak[difType] + "\n" +
                "Current streak: " + currentStreak[difType];
    }

    private static final double CURRENT_STATS_VERSION = 1.2;

    /**
     * Stats version / data
     * 1.0: B64(version:double,playedGames:int[],wonGames:int[],bestTime:Duration[])
     * 1.1: version:double,SB64(playedGames:int[],wonGames:int[],bestTime:Duration[]) // Scrambled(byte[])=byte[pos]^pos%256
     * 1.2: version:double,SB64(playedGames:int[],wonGames:int[],bestTime:Duration[],currentStreak:int[],longestWinningStreak:int[],longestLosingStreak:int[])
     */
    static void load() {
        try {
            byte[] inBytes = Files.readAllBytes(getStatsFilePath());

            double statsFileVersion = Double.parseDouble(new String(Arrays.copyOfRange(inBytes, 1, 4)));
            if (statsFileVersion != CURRENT_STATS_VERSION) {
                Files.copy(getStatsFilePath(), getStatsFilePath(statsFileVersion), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Created backup of Version " + statsFileVersion + " stats.");
            }

            byte[] statBytes = Arrays.copyOfRange(inBytes, 5, inBytes.length);
            IntStream.range(0, statBytes.length).forEach(pos -> statBytes[pos] = (byte) (statBytes[pos] ^ pos % 256));
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(statBytes));
            ObjectInputStream in = new ObjectInputStream(bais);

            // all Versions so far
            playedGames = (int[]) in.readObject();
            wonGames = (int[]) in.readObject();
            bestTime = (Duration[]) in.readObject();
            if (statsFileVersion == 1.2) {
                currentStreak = (int[]) in.readObject();
                longestWinningStreak = (int[]) in.readObject();
                longestLosingStreak = (int[]) in.readObject();
            }

            in.close();
            bais.close();
        } catch (NumberFormatException | NoSuchFileException ex) {
            System.out.println("No stats loaded. File " + getStatsFilePath() + " will be created on quit.");
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    static void save() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(playedGames);
            out.writeObject(wonGames);
            out.writeObject(bestTime);
            out.writeObject(currentStreak);
            out.writeObject(longestWinningStreak);
            out.writeObject(longestLosingStreak);
            out.close();
            byte[] bytes = Base64.getEncoder().encode(baos.toByteArray());
            baos.close();
            IntStream.range(0, bytes.length).forEach(pos -> bytes[pos] = (byte) (bytes[pos] ^ pos % 256));
            Files.write(getStatsFilePath(), String.format(Locale.ENGLISH, "[%.1f]", CURRENT_STATS_VERSION).getBytes());
            Files.write(getStatsFilePath(), bytes, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static Path getStatsFilePath() {
        return getStatsFilePath(-1);
    }

    private static Path getStatsFilePath(double append) {
        String statsFileName = "stats" + (append != -1 ? append : "") + ".dat";
        String statsPath = System.getenv("APPDATA"); // not full path for checking
        char sep = File.separatorChar;
        if (statsPath != null) { // windows?
            statsPath += sep + "Mineswraft";
        } else {
            statsPath = System.getProperty("user.home") + sep + ".config/Mineswraft";
        }
        if (Files.notExists(Paths.get(statsPath))) {
            new File(statsPath).mkdir();
        }
        return new File(statsPath, statsFileName).toPath();
    }

}

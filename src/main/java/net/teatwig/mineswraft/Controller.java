package net.teatwig.mineswraft;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import static javafx.scene.input.MouseButton.*;
import javafx.scene.input.MouseEvent;
import static javafx.scene.input.MouseEvent.*;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.Duration;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by timo on 03.03.2016.
 */
public class Controller {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private VBox structureVBox;
    @FXML
    private GridPane gridPane;
    @FXML
    private Label infoLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label remainingLabel;
    @FXML
    private ToggleGroup difficultyToggles;
    @FXML
    private RadioMenuItem customToggle;
    @FXML
    private CheckMenuItem colorToggle;
    @FXML
    private RadioMenuItem expAchieveToggle;

    private Board board;
    private Toggle currentDifficultyToggle;
    private boolean colorThemeActive = false;
    private Difficulty currentDifficulty, lastDifficulty; // TODO remove lastDifficulty because it's not needed any longer
    private IntegerProperty gridWith = new SimpleIntegerProperty();

    private static final int BUTTON_SIZE = 25;
    private static final String STYLE_CLOSED = "-fx-background-radius: 0";
    private static final String STYLE_OPEN = "-fx-border-color: #DDD; -fx-border-size: 1px; -fx-background-radius: 0; -fx-background-color: #EFEFEF";
    private static final String STYLE_MINE = "-fx-border-color: #DDD; -fx-border-size: 1px; -fx-background-radius: 0; -fx-background-color: #E1E1E1";

    private void startTimerUpdateThread() {
        Task timerUpdate = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (board.isGameInProgress() || !board.isFirstMoveDone()) {
                    Platform.runLater(() -> {
                        long timer = board.getTimePassed().getSeconds();
                        timeLabel.setText(String.valueOf(timer));
                    });
                    Thread.sleep(1000);
                }
                return null;
            }
        };
        Thread th = new Thread(timerUpdate);
        th.setDaemon(true);
        th.start();
    }

    /**
     * Loads Statistic from file, sets Difficulty and starts a new game.
     */
    public void initialize() {
        Statistics.load();

        currentDifficultyToggle = difficultyToggles.getSelectedToggle();
        updateDifficulty();

        newGame();
    }

    private void addAchievement(Achievement achievement) {
        String name = achievement.toString();

        HBox achievementHBox = new HBox();
        achievementHBox.setStyle("-fx-background-color: #39f");
        achievementHBox.setPadding(new Insets(10));
        achievementHBox.setSpacing(10);
        achievementHBox.setAlignment(Pos.CENTER);
        achievementHBox.setEffect(new DropShadow(5, Color.valueOf("black")));

        Text achievementGotText = new Text("You've got the "+name+" achievement!");
        Text clickToCloseText = new Text("X");
        clickToCloseText.setFont(Font.font(null, FontWeight.BOLD, 20));
        clickToCloseText.setFill(Paint.valueOf("#CCC"));

        achievementGotText.wrappingWidthProperty().bind(gridWith.subtract(40)); // padding*2 + spacing + 10orsomething?

        achievementHBox.getChildren().addAll(achievementGotText, clickToCloseText);
        achievementHBox.setOnMouseClicked(e -> {
            structureVBox.getChildren().remove(achievementHBox);
            autoResizeStage();
        });

        structureVBox.getChildren().add(achievementHBox);
        autoResizeStage();
    }

    private void newGame() {
        int width  = currentDifficulty.getWidth();
        gridWith.set(width * BUTTON_SIZE);
        int height = currentDifficulty.getHeight();
        int mines  = currentDifficulty.getMines();
        remainingLabel.setText(String.valueOf(mines));
        infoLabel.setText("");

        gridPane.getChildren().clear();
        IntStream.range(0, height).forEach(y -> IntStream.range(0, width).forEach(x -> {
            Button btn = new Button();
            btn.setPrefSize(BUTTON_SIZE, BUTTON_SIZE);
            btn.setMinSize(BUTTON_SIZE, BUTTON_SIZE);
            btn.setMaxSize(BUTTON_SIZE, BUTTON_SIZE);
            btn.setFocusTraversable(false);
            btn.setStyle(STYLE_CLOSED);
            btn.setPadding(Insets.EMPTY);
            btn.setFont(Font.font(null, FontWeight.BOLD, 16));
            btn.setOnMouseReleased(this::mouseHandler);
            btn.setOnMousePressed(this::mouseHandler);
            btn.setOnMouseExited(e -> pressSource = null);
            btn.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                primPressed =  e.isPrimaryButtonDown();
                secPressed =  e.isSecondaryButtonDown();
            });
            gridPane.add(btn, x, y);
            gridPane.setAlignment(Pos.CENTER);
        }));

        board = new Board(width, height, mines, currentDifficulty.getType());
        startTimerUpdateThread();

        autoResizeStage();
    }

    public void newGameAlert() {
        if(!board.isGameInProgress() || confirmDialogOK(NEW_GAME)) {
            // using short-circuit evaluation for success!
            newGame();
        }
    }

    public void quit(Event event) {
        if(event != null) {
            event.consume();
        }
        if(!board.isGameInProgress() || confirmDialogOK(QUIT)) {
            Statistics.save();
            Platform.exit();
        }
    }

    public void difficultyChangeHandler() {
        Toggle newToggle = difficultyToggles.getSelectedToggle();
        // happens when Accelerator for currentDifficultyToggle is pressed
        if(newToggle == null) {
            currentDifficultyToggle.setSelected(true);
        } else if(newToggle != currentDifficultyToggle || currentDifficultyToggle == customToggle) {
            if(!board.isGameInProgress() || confirmDialogOK(CHANGE_DIFFICULTY)) {
                currentDifficultyToggle = newToggle;
                updateDifficulty();
                newGame();
            } else {
                difficultyToggles.selectToggle(currentDifficultyToggle);
                if(currentDifficultyToggle != customToggle) {
                    customToggle.setText("Custom");
                }
            }
        }
    }

    private static final int NEW_GAME = 0, QUIT = 1, CHANGE_DIFFICULTY = 2;

    /**
     * Displays an Alert for the specified origin code
     * @param origin type of alert to display
     * @return true if OK pressed on alert, otherwise false
     */
    private boolean confirmDialogOK(int origin) {
        Alert confirm;
        switch(origin) {
            case NEW_GAME: confirm = confirmAlert("New Game", "Do you really want to start a new game?"); break;
            case QUIT: confirm = confirmAlert("Quit", "Do you really want to start a new game?"); break;
            case CHANGE_DIFFICULTY: confirm = confirmAlert("Difficulty Change", "Do you really want to change your difficulty?"); break;
            default: confirm = confirmAlert("Error", "Unspecified Confirm Dialog Origin-Code.");
        }

        boolean result = confirm.showAndWait().get() == ButtonType.OK;
        if(result == true) {
            switch (origin) {
                case NEW_GAME:
                case QUIT:
                case CHANGE_DIFFICULTY:
                    writeLosingStatistic();
            }
        }

        return result;
    }

    private boolean primPressed = false, secPressed = false;
    private Object pressSource = null; // null if mouse exited field

    private void mouseHandler(MouseEvent e) {
        EventType event = e.getEventType();
        MouseButton button = e.getButton();
        if(event == MOUSE_PRESSED) {
            pressSource = e.getSource();
        }
        Coordinate coordinate = Coordinate.of(
            GridPane.getColumnIndex((Node) e.getSource()),
            GridPane.getRowIndex((Node) e.getSource())
        );

        if (e.isSecondaryButtonDown() && board.isFirstMoveDone() && !board.getField(coordinate).isOpen()) {
            board.toggleMarking(coordinate);
            syncGridAndBoard();
        } else if(pressSource != null) {
            // openField && (doubleClick || release && (middle || both))
            if (board.isFirstMoveDone() && board.getField(coordinate).isOpen()
                    && ((e.getClickCount() == 2 && button == PRIMARY)
                    || event == MOUSE_RELEASED && (button == MIDDLE || (primPressed && secPressed)))) {
                board.chord(coordinate);
                syncGridAndBoard();
            } else if (event == MOUSE_RELEASED && button == PRIMARY && secPressed == false) {
                board.click(coordinate);
                syncGridAndBoard();
            }
        }
    }

    private void removeMouseHandlersFromGridPane() {
        gridPane.getChildren().stream().forEach(child -> {
            child.setOnMousePressed(null);
            child.setOnMouseReleased(null);
        });
    }

    private void syncGridAndBoard() {
        if(board.isGameOver()) {
            writeLosingStatistic();
            removeMouseHandlersFromGridPane();
            infoLabel.setText("GAME OVER");
        } else if(board.isGameWon()) {
            writeWinningStatistic();
            removeMouseHandlersFromGridPane();
            infoLabel.setText("You're a winner!");
        }
        ObservableList<Node> gridPaneChildren = gridPane.getChildren();
        IntStream.range(0, gridPaneChildren.size()).forEach(i ->
            syncFieldAndChild(board.getField(i), (Button) gridPaneChildren.get(i))
        );

        remainingLabel.setText(String.valueOf(board.getRemainingMines()));

        if(expAchievementsEnabled() && !board.getNewAchievements().isEmpty()) {
            Iterator<Achievement> newAchieveIter = board.getNewAchievements().iterator();
            while(newAchieveIter.hasNext()) {
                Achievement curr = newAchieveIter.next();
                addAchievement(curr);
                newAchieveIter.remove();
            }
        }
    }

    private void syncFieldAndChild(Field f, Button c) {
        if(f.isMarked()) {
            c.setTextFill(Color.valueOf("#CC0"));
            c.setText("\u2691"); // flag
        } else if(f.isOpen()) {
            if(f.getSurroundingMines() > 0) {
                c.setText(String.valueOf(f.getSurroundingMines()));
                setColors(c);
            } // else should never happen because you don't have to unmark free spaces => get empty text
            c.setStyle(STYLE_OPEN);
        } else {
            c.setTextFill(Color.valueOf("black"));
            c.setText("");
        }

        if(board.isGameOver() || board.isGameWon()) {
            if(f.isMine()) {
                if(f.isMarked() || board.isGameWon()) { // marked and unmarked when won
                    c.setTextFill(Color.valueOf("#AAA"));
                }
                c.setText("\uD83D\uDCA3"); // bomb
                c.setFont(Font.font(null, FontWeight.NORMAL, 12));
                c.setStyle(STYLE_MINE);
            } else if(f.isMarked()) {
                c.setTextFill(Color.valueOf("#C00")); // wrongly marked
            }
        }
    }

    public void openStatisticsDialog() {
        Statistics.showStatisticsDialog();
    }

    private void writeLosingStatistic() {
        Statistics.addGameResult(false, currentDifficulty, Duration.ZERO);
    }

    private void writeWinningStatistic() {
        Statistics.addGameResult(true, currentDifficulty, board.getTimePassed());
    }

    public void customDifficultyHandler() {
        Optional<int[]> customData = customDialog().showAndWait();
        if(customData.isPresent()) {
            int[] customArr = customData.get();
            int width  = customArr[0];
            int height = customArr[1];
            int mines  = customArr[2];
            width = width < 9 ? 9 : (width > 30 ? 30 : width);
            height = height < 9 ? 9 : (height > 24 ? 24 : height);
            mines = mines < 1 ? 1 : (mines > (width - 1) * (height - 1) ? (width - 1) * (height - 1) : mines);
            customToggle.setText(String.format("Custom (%d x %d, %d mine%s)", width, height, mines, mines>1?"s":""));
            difficultyChangeHandler();
        }
    }

    private void updateDifficulty() {
        lastDifficulty = currentDifficulty;

        RadioMenuItem r = (RadioMenuItem) currentDifficultyToggle;
        String name = r.getText().split(" ")[0];
        String value = r.getText().replaceFirst(".*\\((.*)\\).*", "$1").replaceAll("(\\d+)\\D+(\\d+)\\D+(\\d+)\\D+", "$1 $2 $3");
        int[] difficultyArr = Arrays.stream(value.split(" ")).mapToInt(Integer::parseInt).toArray();
        currentDifficulty = new Difficulty(name, difficultyArr[0], difficultyArr[1], difficultyArr[2]);
    }

    private Alert confirmAlert(String title, String header) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        setDialogIcon(alert);
        alert.setHeaderText(header);
        alert.setContentText("The current game will be discarded and count as a lost game in the statistics.");
        alert.getButtonTypes().add(ButtonType.CANCEL);
        return alert;
    }

    private Dialog<int[]> customDialog() {
        Dialog<int[]> customInput = new Dialog<>();
        customInput.setTitle("Custom");
        setDialogIcon(customInput);
        customInput.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);

        TextField width = new TextField();
        TextField height = new TextField();
        TextField mines = new TextField();
        grid.add(new Label("Width (9-30):"), 0, 0);
        grid.add(width, 1, 0);
        grid.add(new Label("Height (9-24):"), 0, 1);
        grid.add(height, 1, 1);
        grid.add(new Label("Mines (up to 64-667):"), 0, 2);
        grid.add(mines, 1, 2);

        customInput.getDialogPane().setContent(grid);

        customInput.setResultConverter(button -> {
            if(button == ButtonType.OK) {
                return new int[] {Integer.valueOf(width.getText()), Integer.valueOf(height.getText()), Integer.valueOf(mines.getText())};
            }
            return null;
        });

        return customInput;
    }

    public void colorToggleHandler() {
        colorThemeActive = colorToggle.isSelected();
        if(board.isFirstMoveDone())
            syncGridAndBoard();
    }

    private static final double OPACITY = 0.5;

    private void setColors(Button b) {
        if(colorThemeActive) {
            switch (b.getText()) {
                case "8":
                    b.setTextFill(Color.rgb(0x7B, 0x7B, 0x7B, OPACITY));
                    break;
                case "7":
                    b.setTextFill(Color.rgb(0, 0, 0, OPACITY));
                    break;
                case "6":
                    b.setTextFill(Color.rgb(0, 0x7B, 0x7B, OPACITY));
                    break;
                case "5":
                    b.setTextFill(Color.rgb(0x7B, 0, 0, OPACITY));
                    break;
                case "4":
                    b.setTextFill(Color.rgb(0, 0, 0x7B, OPACITY));
                    break;
                case "3":
                    b.setTextFill(Color.rgb(255, 0, 0, OPACITY));
                    break;
                case "2":
                    b.setTextFill(Color.rgb(0, 0x7B, 0, OPACITY));
                    break;
                case "1":
                    b.setTextFill(Color.rgb(0, 0, 255, OPACITY));
                    break;
            }
        } else {
            b.setTextFill(Color.rgb(0, 0, 0, OPACITY));
        }
    }

    static void setDialogIcon(Dialog dialog) {
        getStage(dialog.getDialogPane()).getIcons().addAll(gameIcon());
    }

    private static Stage getStage(Pane pane) {
        return (Stage) pane.getScene().getWindow();
    }

    private void autoResizeStage() {
        try {
            getStage(rootPane).sizeToScene();
        } catch(NullPointerException ex) {

        }
    }

    private static boolean expAchievementsEnabled = false;
    public void updateExpAchieveToggle() {
        expAchievementsEnabled = expAchieveToggle.isSelected();
        if(expAchievementsEnabled() && Achievement.EXP_ACHIEVE.isObtained() == false) {
            addAchievement(Achievement.EXP_ACHIEVE.setObtainedAndGet());
        }
    }

    static boolean expAchievementsEnabled() {
        return expAchievementsEnabled;
    }

    public static Image[] gameIcon() {
        Class c = Controller.class;
        return new Image[] {
            new Image(c.getResourceAsStream("images/icon16.png")),
            new Image(c.getResourceAsStream("images/icon32.png")),
            new Image(c.getResourceAsStream("images/icon64.png"))
        };
    }
}

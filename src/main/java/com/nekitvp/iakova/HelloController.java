package com.nekitvp.iakova;

import java.net.URL;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class HelloController {

    // Верхнее табло
    @FXML
    private AnchorPane scoreboardPane;
    @FXML
    private Label teamScore1, teamScore2, teamScore3, teamScore4, teamScore5, teamScore6, teamScore7, teamScore8;
    @FXML
    private Label teamName1, teamName2, teamName3, teamName4, teamName5, teamName6, teamName7, teamName8;
    @FXML
    private ImageView teamCoin1, teamCoin2, teamCoin3, teamCoin4, teamCoin5, teamCoin6, teamCoin7, teamCoin8;

    // Нижняя часть – экраны
    @FXML
    private StackPane rootPane;
    @FXML
    private AnchorPane screen1, screen2, screen3, screen4;
    // FXML-узлы для экрана 1 и 2
    @FXML
    private ImageView afishaImage;         // для экрана 1
    @FXML
    private Label teamResponseLabel;

    @FXML
    private ImageView blackSquareImage;      // для экрана 2

    // FXML-узлы для экран 4 (таймеры)
    @FXML
    private Label timerBlue, timerRed, timerGreen, timerYellow, timerOrange, timerPurple, timerGray, timerPink;

    private Pane[] screens;
    private int currentScreenIndex = 0;

    private Label[] teamScoreLabels;
    private ImageView[] teamCoinImages;
    private int[] teamScores = new int[8];
    private int selectedTeam = 0;

    // Логика экранов
    private ScreenLogic[] screenLogics;
    private ScreenLogic currentScreenLogic;

    @FXML
    private void initialize() {

        afishaImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/nekitvp/iakova/afisha.jpg")).toExternalForm()));


        blackSquareImage.setImage(new Image(
                Objects.requireNonNull(getClass().getResource("/com/nekitvp/iakova/squeue.png")).toExternalForm()));

        screens = new Pane[] {screen1, screen2, screen3, screen4};
        showScreen(0);

        teamScoreLabels = new Label[] {teamScore1, teamScore2, teamScore3, teamScore4, teamScore5, teamScore6,
                teamScore7, teamScore8
        };
        teamCoinImages = new ImageView[] {teamCoin1, teamCoin2, teamCoin3, teamCoin4, teamCoin5, teamCoin6, teamCoin7,
                teamCoin8
        };

        for (int i = 0; i < teamScores.length; i++) {
            teamScores[i] = 0;
            updateScore(i);
        }

        screenLogics = new ScreenLogic[] {
                new Screen1Logic(),
                new Screen2Logic(screen2, blackSquareImage, teamResponseLabel),
                new Screen3Logic(screen3),
                new Screen4Logic(timerBlue, timerRed, timerGreen, timerYellow, timerOrange, timerPurple, timerGray,
                        timerPink)
        };
        currentScreenLogic = screenLogics[0];

        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::handleKeyPress);
                newScene.getRoot().requestFocus();
            }
        });
        rootPane.heightProperty()
                .addListener((obs, oldVal, newVal) -> resizeElements(rootPane.getWidth(), newVal.doubleValue()));
    }

    private void resizeElements(double width, double height) {

        afishaImage.setFitHeight(height);
        afishaImage.setFitWidth(width);
        blackSquareImage.setFitWidth(width / 2);
        blackSquareImage.setFitHeight(height / 2);

    }


    private void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        // Обработка командных клавиш для экранов (B, R, G, Y, O, P, M, F, а также 0)
        switch (code) {
            case B, R, G, Y, O, P, M, F, DIGIT0, C -> currentScreenLogic.handleTeamKeyPress(event);
            case S -> {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setFullScreen(true);
            }
            case DIGIT1 -> selectedTeam = 0;
            case DIGIT2 -> selectedTeam = 1;
            case DIGIT3 -> selectedTeam = 2;
            case DIGIT4 -> selectedTeam = 3;
            case DIGIT5 -> selectedTeam = 4;
            case DIGIT6 -> selectedTeam = 5;
            case DIGIT7 -> selectedTeam = 6;
            case DIGIT8 -> selectedTeam = 7;
            case UP -> animateCoinDrop(selectedTeam, 5);
            case DOWN -> decreaseScore(selectedTeam);
            case F1 -> animateCoinDrop(selectedTeam, 10);
            case F2 -> animateCoinDrop(selectedTeam, 20);
            case F3 -> animateCoinDrop(selectedTeam, 30);
            case F4 -> animateCoinDrop(selectedTeam, 40);
            case F5 -> animateCoinDrop(selectedTeam, 50);
            case F6 -> animateCoinDrop(selectedTeam, 60);
            case F7 -> animateCoinDrop(selectedTeam, 70);
            case F8 -> animateCoinDrop(selectedTeam, 80);
            case LEFT -> {
                showScreen((currentScreenIndex - 1 + screens.length) % screens.length);
                currentScreenLogic = screenLogics[currentScreenIndex];
                currentScreenLogic.onScreenShow();
            }
            case RIGHT -> {
                showScreen((currentScreenIndex + 1) % screens.length);
                currentScreenLogic = screenLogics[currentScreenIndex];
                currentScreenLogic.onScreenShow();
            }
            default -> {
            }
        }
    }

    private void updateScore(int team) {
        teamScoreLabels[team].setText(String.valueOf(teamScores[team]));
        String imagePath = getCoinImagePath(teamScores[team]);
        Image stackImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        teamCoinImages[team].setImage(stackImage);
    }

    private String getCoinImagePath(int score) {
        if (score <= 40) {
            return "/coin/1.png";
        }
        if (score <= 80) {
            return "/coin/2.png";
        }
        if (score <= 120) {
            return "/coin/3.png";
        }
        if (score <= 160) {
            return "/coin/4.png";
        }
        if (score <= 200) {
            return "/coin/5.png";
        }
        if (score <= 240) {
            return "/coin/6.png";
        }
        if (score <= 280) {
            return "/coin/7.png";
        }
        if (score <= 320) {
            return "/coin/8.png";
        }
        if (score <= 360) {
            return "/coin/10.png";
        }
        if (score <= 400) {
            return "/coin/11.png";
        }
        if (score <= 450) {
            return "/coin/12.png";
        }
        return "/coin/13.png";
    }

    private void animateCoinDrop(int team, int coinValue) {
        String coinImageResource;
        String coinSoundResource;
        if (coinValue == 5) {
            coinImageResource = "/coin.png";
            coinSoundResource = "/coin_sound.mp3";
        } else {
            switch (coinValue) {
                case 10 -> coinImageResource = "/coin/1.png";
                case 20 -> coinImageResource = "/coin/2.png";
                case 30 -> coinImageResource = "/coin/3.png";
                case 40 -> coinImageResource = "/coin/4.png";
                case 50 -> coinImageResource = "/coin/5.png";
                case 60 -> coinImageResource = "/coin/6.png";
                case 70 -> coinImageResource = "/coin/7.png";
                case 80 -> coinImageResource = "/coin/8.png";
                default -> coinImageResource = "/coin_special.png";
            }
            coinSoundResource = "/coin_sound.mp3";
        }

        Image coinImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(coinImageResource)));
        ImageView fallingCoin = new ImageView(coinImage);
        fallingCoin.setFitWidth(30);
        fallingCoin.setFitHeight(30);

        Point2D targetScene = teamCoinImages[team].localToScene(0, 0);
        Point2D target = scoreboardPane.sceneToLocal(targetScene);
        double teamCoinWidth = teamCoinImages[team].getBoundsInLocal().getWidth();
        double fallingCoinWidth = fallingCoin.getFitWidth();
        double targetX = target.getX() + (teamCoinWidth - fallingCoinWidth) / 2;
        double targetY = target.getY();

        double startX = targetX;
        double startY = -fallingCoin.getFitHeight();
        fallingCoin.setLayoutX(startX);
        fallingCoin.setLayoutY(startY);
        scoreboardPane.getChildren().add(fallingCoin);

        double distance = targetY - startY;
        TranslateTransition transition = new TranslateTransition(Duration.millis(500), fallingCoin);
        transition.setFromY(0);
        transition.setToY(distance);
        transition.setInterpolator(Interpolator.EASE_IN);
        transition.setOnFinished(event -> {
            scoreboardPane.getChildren().remove(fallingCoin);
            teamScores[team] += coinValue;
            updateScore(team);
            AudioClip sound = new AudioClip(
                    Objects.requireNonNull(getClass().getResource(coinSoundResource)).toExternalForm());
            sound.play();
        });
        transition.play();
    }

    private void decreaseScore(int team) {
        teamScores[team] = Math.max(0, teamScores[team] - 5);
        updateScore(team);
    }

    private void showScreen(int index) {
        currentScreenIndex = index;
        screens[index].toFront();
    }
}

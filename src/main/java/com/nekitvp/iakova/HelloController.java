package com.nekitvp.iakova;

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
import javafx.util.Duration;

import java.util.Objects;

public class HelloController {

    @FXML
    private StackPane rootPane;

    @FXML
    private Pane screen1, screen2, screen3, screen4;
    private Pane[] screens;
    private int currentScreenIndex = 0;

    @FXML
    private AnchorPane scoreboardPane;

    @FXML
    private Label teamScore1, teamScore2, teamScore3, teamScore4, teamScore5, teamScore6, teamScore7, teamScore8;

    // Надписи с названиями команд (заданы в FXML)
    @FXML
    private Label teamName1, teamName2, teamName3, teamName4, teamName5, teamName6, teamName7, teamName8;

    @FXML
    private ImageView teamCoin1, teamCoin2, teamCoin3, teamCoin4, teamCoin5, teamCoin6, teamCoin7, teamCoin8;

    private Label[] teamScoreLabels;
    private ImageView[] teamCoinImages;
    private int[] teamScores = new int[8];
    private int selectedTeam = 0;

    @FXML
    private void initialize() {
        // Инициализация экранов нижней части
        screens = new Pane[]{screen1, screen2, screen3, screen4};
        showScreen(0);

        teamScoreLabels = new Label[]{teamScore1, teamScore2, teamScore3, teamScore4, teamScore5, teamScore6, teamScore7, teamScore8};
        teamCoinImages = new ImageView[]{teamCoin1, teamCoin2, teamCoin3, teamCoin4, teamCoin5, teamCoin6, teamCoin7, teamCoin8};

        // Инициализация счётов для каждой команды
        for (int i = 0; i < teamScores.length; i++) {
            teamScores[i] = 0;
            updateScore(i);
        }

        // Устанавливаем обработчик клавиш после появления сцены
        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::handleKeyPress);
                newScene.getRoot().requestFocus();
            }
        });
    }

    private void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        switch (code) {
            case DIGIT1 -> selectedTeam = 0;
            case DIGIT2 -> selectedTeam = 1;
            case DIGIT3 -> selectedTeam = 2;
            case DIGIT4 -> selectedTeam = 3;
            case DIGIT5 -> selectedTeam = 4;
            case DIGIT6 -> selectedTeam = 5;
            case DIGIT7 -> selectedTeam = 6;
            case DIGIT8 -> selectedTeam = 7;
            case UP -> animateCoinDrop(selectedTeam);
            case DOWN -> decreaseScore(selectedTeam);  // Добавленный обработчик клавиши вниз
            case LEFT -> showScreen((currentScreenIndex - 1 + screens.length) % screens.length);
            case RIGHT -> showScreen((currentScreenIndex + 1) % screens.length);
            default -> {}
        }
    }

    private void updateScore(int team) {
        teamScoreLabels[team].setText(String.valueOf(teamScores[team]));
        String imagePath = getCoinImagePath(teamScores[team]);
        Image stackImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        teamCoinImages[team].setImage(stackImage);
    }

    private String getCoinImagePath(int score) {
        if (score <= 40) return "/coin/1.png";
        if (score <= 80) return "/coin/2.png";
        if (score <= 120) return "/coin/3.png";
        if (score <= 160) return "/coin/4.png";
        if (score <= 200) return "/coin/5.png";
        if (score <= 240) return "/coin/6.png";
        if (score <= 280) return "/coin/7.png";
        if (score <= 320) return "/coin/8.png";
        if (score <= 360) return "/coin/10.png";
        if (score <= 400) return "/coin/11.png";
        if (score <= 450) return "/coin/12.png";
        return "/coin/13.png";
    }

    private void animateCoinDrop(int team) {
        // Загружаем изображение монетки
        Image coinImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/coin.png")));
        ImageView fallingCoin = new ImageView(coinImage);
        fallingCoin.setFitWidth(30);
        fallingCoin.setFitHeight(30);

        // Вычисляем целевую позицию с учетом преобразования координат
        Point2D targetScene = teamCoinImages[team].localToScene(0, 0);
        Point2D target = scoreboardPane.sceneToLocal(targetScene);
        double targetX = target.getX();
        double targetY = target.getY();

        // Стартовая позиция – сверху (над scoreboardPane)
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
            teamScores[team] += 5;
            updateScore(team);
            // Воспроизводим звук при попадании монетки
            AudioClip sound = new AudioClip(Objects.requireNonNull(getClass().getResource("/coin_sound.mp3")).toExternalForm());
            sound.play();
        });
        transition.play();
    }

    // Новый метод для уменьшения счета выбранной команды
    private void decreaseScore(int team) {
        teamScores[team] = Math.max(0, teamScores[team] - 5);
        updateScore(team);
    }

    private void showScreen(int index) {
        currentScreenIndex = index;
        screens[index].toFront();
    }
}

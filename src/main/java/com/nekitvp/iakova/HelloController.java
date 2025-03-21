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
    private Label teamScoreBrain1, teamScoreBrain2, teamScoreBrain3, teamScoreBrain4, teamScoreBrain5, teamScoreBrain6, teamScoreBrain7, teamScoreBrain8, doublePointsLabel;
    @FXML
    private Label teamName1, teamName2, teamName3, teamName4, teamName5, teamName6, teamName7, teamName8;
    @FXML
    private ImageView teamCoin1, teamCoin2, teamCoin3, teamCoin4, teamCoin5, teamCoin6, teamCoin7, teamCoin8;

    // Нижняя часть – экраны
    @FXML
    private StackPane rootPane;
    @FXML
    private AnchorPane screen1, screen2, screen3, screen4, screen5;
    // FXML-узлы для экрана 1 и 2
    @FXML
    private ImageView afishaImage;         // для экрана 1
    @FXML
    private ImageView logoImage;         // для экрана 1
    @FXML
    private Label teamResponseLabel;

    @FXML
    private Label timerScreen5Label;

    @FXML
    private ImageView blackSquareImage;      // для экрана 2

    // Элементы нового экрана (screen4)
    @FXML
    private Label timerLabelBrain;         // Большой таймер
    @FXML
    private Label currentTeamLabel;   // "Отвечает: ..."
    @FXML
    private Label bonusLabel;         // Появляется при остановке таймера в пределах первой секунды


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

        logoImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/logo.png")).toExternalForm()));

        blackSquareImage.setImage(new Image(
                Objects.requireNonNull(getClass().getResource("/com/nekitvp/iakova/squeue.png")).toExternalForm()));

        screens = new Pane[] {screen1, screen2, screen3, screen4, screen5};
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

        String imageUrl = Objects.requireNonNull(getClass().getResource("/com/nekitvp/iakova/up.jpg")).toExternalForm();
        scoreboardPane.setStyle("-fx-background-image: url('" + imageUrl + "'); -fx-background-size: cover;");

        screenLogics = new ScreenLogic[] {
                new Screen1Logic(screen1),
                new Screen2Logic(screen2, blackSquareImage, teamResponseLabel),
                new Screen3Logic(screen3),
                new Screen4Logic(screen4, timerLabelBrain, currentTeamLabel, bonusLabel,
                        teamScoreBrain1, teamScoreBrain2, teamScoreBrain3, teamScoreBrain4,
                        teamScoreBrain5, teamScoreBrain6, teamScoreBrain7, teamScoreBrain8, doublePointsLabel),
                new Screen5Logic(screen5, timerScreen5Label)
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

        blackSquareImage.setFitWidth(width / 2);
        blackSquareImage.setFitHeight(height / 2);

    }


    private void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        // Обработка командных клавиш для экранов (B, R, G, Y, O, P, M, F, а также 0)
        switch (code) {
            case B, R, G, Y, O, P, M, F, DIGIT0, C, N, A, Z-> currentScreenLogic.handleTeamKeyPress(event);
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
            case UP -> animateSingleFastCoinDrop(selectedTeam);
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
        // Определяем количество монет в зависимости от нажатой клавиши
        int numberOfCoins = coinValue / 5; // Каждая монета дает 5 очков

        // Загружаем звук для анимации
        String coinSoundResource = "/sound/coin_drop.wav";
        AudioClip sound = new AudioClip(Objects.requireNonNull(getClass().getResource(coinSoundResource)).toExternalForm());

        // Позиции монет
        Point2D targetScene = teamCoinImages[team].localToScene(0, 0);
        Point2D target = scoreboardPane.sceneToLocal(targetScene);
        double teamCoinWidth = teamCoinImages[team].getBoundsInLocal().getWidth();
        double targetX = target.getX() + (teamCoinWidth - 30) / 2;  // Для монеты ширина 30 пикселей
        double targetY = target.getY();

        // Делаем падение монет в течение 4 секунд
        double totalTime = 4000; // 4 секунды
        double interval = totalTime / numberOfCoins; // Время между монетами
        int rareCoins = (int) (numberOfCoins * 0.3);  // 30% монет будут редкими
        int frequentCoins = numberOfCoins - rareCoins; // Оставшиеся монеты будут частыми

        // Падающие монеты
        for (int i = 0; i < rareCoins; i++) {
            double delay = interval * i;
            createFallingCoin(team, targetX, targetY, delay, 1, coinValue); // Передаем coinValue
        }

        // Падающие монеты (частые)
        for (int i = 0; i < frequentCoins; i++) {
            double delay = interval * (rareCoins + i);
            createFallingCoin(team, targetX, targetY, delay, 0.5, coinValue); // Передаем coinValue
        }

        // Добавляем звуковой эффект при падении монет
        sound.play();
    }

    private void createFallingCoin(int team, double targetX, double targetY, double delay, double speedMultiplier, int coinValue) {
        String coinImageResource = "/coin.png";  // Ресурс для монеты
        Image coinImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(coinImageResource)));
        ImageView fallingCoin = new ImageView(coinImage);
        fallingCoin.setFitWidth(30);
        fallingCoin.setFitHeight(30);

        // Начальная позиция монеты
        double startX = targetX;
        double startY = -fallingCoin.getFitHeight();
        fallingCoin.setLayoutX(startX);
        fallingCoin.setLayoutY(startY);
        scoreboardPane.getChildren().add(fallingCoin);

        // Анимация
        double distance = targetY - startY;
        TranslateTransition transition = new TranslateTransition(Duration.millis(1000 * speedMultiplier), fallingCoin);
        transition.setFromY(0);
        transition.setToY(distance);
        transition.setInterpolator(Interpolator.EASE_IN);
        transition.setDelay(Duration.millis(delay));  // Задержка между монетами
        transition.setOnFinished(event -> {
            scoreboardPane.getChildren().remove(fallingCoin);  // Убираем монету после падения
            teamScores[team] += 5;  // Добавляем 5 очков за монету
            updateScore(team);  // Обновляем счёт
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

    // Метод для анимации одного быстрого падения монеты
    private void animateSingleFastCoinDrop(int team) {
        // Загружаем короткий звук для быстрой анимации монеты
        String coinFastSoundResource = "/sound/coin_sound.mp3";
        AudioClip sound = new AudioClip(Objects.requireNonNull(getClass().getResource(coinFastSoundResource)).toExternalForm());

        // Определяем позицию для падения монеты (на основе положения монеты команды)
        Point2D targetScene = teamCoinImages[team].localToScene(0, 0);
        Point2D target = scoreboardPane.sceneToLocal(targetScene);
        double teamCoinWidth = teamCoinImages[team].getBoundsInLocal().getWidth();
        double targetX = target.getX() + (teamCoinWidth - 30) / 2;
        double targetY = target.getY();

        // Создаем падение одной монеты
        createSingleFallingCoin(team, targetX, targetY, 0);
        sound.play();
    }

    // Метод для создания анимации одной монеты
    private void createSingleFallingCoin(int team, double targetX, double targetY, double delay) {
        String coinImageResource = "/coin.png";  // Ресурс изображения монеты
        Image coinImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(coinImageResource)));
        ImageView fallingCoin = new ImageView(coinImage);
        fallingCoin.setFitWidth(30);
        fallingCoin.setFitHeight(30);

        // Начальная позиция монеты — чуть выше экрана
        double startX = targetX;
        double startY = -fallingCoin.getFitHeight();
        fallingCoin.setLayoutX(startX);
        fallingCoin.setLayoutY(startY);
        scoreboardPane.getChildren().add(fallingCoin);

        double distance = targetY - startY;
        // Устанавливаем короткую длительность анимации (200 мс)
        TranslateTransition transition = new TranslateTransition(Duration.millis(400), fallingCoin);
        transition.setFromY(0);
        transition.setToY(distance);
        transition.setInterpolator(Interpolator.EASE_IN);
        transition.setDelay(Duration.millis(delay));
        transition.setOnFinished(event -> {
            scoreboardPane.getChildren().remove(fallingCoin);  // Удаляем монету после анимации
            teamScores[team] += 5;  // Добавляем 5 очков за монету
            updateScore(team);    // Обновляем отображение счета
        });
        transition.play();
    }
}

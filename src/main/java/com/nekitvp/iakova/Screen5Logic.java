package com.nekitvp.iakova;

import java.util.Objects;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Screen5Logic implements ScreenLogic {

    private AnchorPane screenPane;
    private Label timerScreen5Label;

    // Исходное и оставшееся время в миллисекундах
    private long initialTimeMillis;
    private long remainingTimeMillis;

    private Timeline timeline;
    private boolean running = false;

    // MediaPlayer'ы для звуков
    private MediaPlayer timerStartPlayer;
    private MediaPlayer tickingPlayer;   // Звук тикания часов
    private MediaPlayer endPlayer;

    // Конструктор получает ссылку на корневой элемент экрана и Label с таймером
    public Screen5Logic(AnchorPane screenPane, Label timerScreen5Label) {
        this.screenPane = screenPane;
        this.timerScreen5Label = timerScreen5Label;

        String imageUrl = Objects.requireNonNull(getClass().getResource("/com/nekitvp/iakova/screen_background.jpg")).toExternalForm();
        // Устанавливаем фон по умолчанию при создании логики экрана
        screenPane.setStyle("-fx-background-image: url('" + imageUrl + "'); -fx-background-size: cover;");


        // Инициализируем MediaPlayer для звука запуска таймера
        Media timerMedia = new Media(
                Objects.requireNonNull(getClass().getResource("/sound/timer_start.wav")).toExternalForm());
        timerStartPlayer = new MediaPlayer(timerMedia);

        // Инициализируем MediaPlayer для звука нажатия команды
        Media endMedia = new Media(
                Objects.requireNonNull(getClass().getResource("/sound/endTime.wav")).toExternalForm());
        endPlayer = new MediaPlayer(endMedia);

        // Инициализируем MediaPlayer для звука тикания часов
        Media tickingMedia = new Media(
                Objects.requireNonNull(getClass().getResource("/sound/clock.wav")).toExternalForm());
        tickingPlayer = new MediaPlayer(tickingMedia);
        tickingPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        // Задаём первоначальное значение таймера (например, 20 минут)
        this.initialTimeMillis = 20 * 60 * 1000;
        resetTimer();
    }

    // Метод запуска таймера
    public void startTimer() {
        if (running) {
            return;
        }
        timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            remainingTimeMillis -= 100;
            updateTimerLabel();
            if (remainingTimeMillis <= 0) {
                timeline.stop();
                play(endPlayer);
                stop(tickingPlayer);
                running = false;
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        running = true;
        play(timerStartPlayer);
        play(tickingPlayer);
    }

    // Сброс таймера до исходного значения и остановка анимации
    public void resetTimer() {
        if (timeline != null) {
            timeline.stop();
        }
        remainingTimeMillis = initialTimeMillis;
        updateTimerLabel();
        running = false;
        stop(tickingPlayer);
    }

    // Увеличение первоначального значения на 5 минут
    public void increaseInitialTime() {
        initialTimeMillis += 5 * 60 * 1000;
        if (!running) {
            resetTimer();
        }
    }

    // Уменьшение первоначального значения на 5 минут (не менее 0)
    public void decreaseInitialTime() {
        if (initialTimeMillis >= 5 * 60 * 1000) {
            initialTimeMillis -= 5 * 60 * 1000;
        }
        if (!running) {
            resetTimer();
        }
    }

    // Обновление текста таймера в формате MM:SS.t
    private void updateTimerLabel() {
        long totalSeconds = remainingTimeMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        long tenthOfSecond = (remainingTimeMillis % 1000) / 100;
        timerScreen5Label.setText(String.format("%02d:%02d.%d", minutes, seconds, tenthOfSecond));
    }

    // Обработка нажатий клавиш
    @Override
    public void handleTeamKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        switch (code) {
            case C -> startTimer();         // Клавиша C запускает таймер
            case N -> resetTimer();         // Клавиша N сбрасывает таймер
            case A -> increaseInitialTime(); // Клавиша A увеличивает начальное время на 5 минут
            case Z -> decreaseInitialTime(); // Клавиша Z уменьшает начальное время на 5 минут
            default -> { }
        }
    }

    // Метод вызывается при переключении на экран – можно сбросить таймер
    @Override
    public void onScreenShow() {
        resetTimer();
    }


    // Запускает воспроизведение MediaPlayer: останавливает, сбрасывает позицию и запускает
    private void play(MediaPlayer player) {
        player.stop();
        player.seek(Duration.ZERO);
        player.play();
    }

    // Останавливает MediaPlayer
    private void stop(MediaPlayer player) {
        player.stop();
    }
}

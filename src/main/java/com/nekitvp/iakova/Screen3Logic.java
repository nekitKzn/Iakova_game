package com.nekitvp.iakova;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;

public class Screen3Logic implements ScreenLogic {
    private final AnchorPane screenPane;
    // Узлы, вынесенные в FXML:
    private final Label timerLabel;
    private final VBox sequenceVBox;

    // Набор для фиксации уникальных нажатых команд
    private final Set<Team> pressedTeams;

    // Таймер
    private AnimationTimer animationTimer;
    private long timerStartTime; // в наносекундах
    private boolean timerRunning = false;

    // Общее количество команд, которые ожидаются (например, из enum Team)
    private final int totalTeams = Team.values().length;

    public Screen3Logic(AnchorPane screenPane) {
        this.screenPane = screenPane;
        this.pressedTeams = new LinkedHashSet<>();

        // Получаем ссылки на узлы, определённые в FXML
        this.timerLabel = (Label) screenPane.lookup("#timerLabel");
        this.sequenceVBox = (VBox) screenPane.lookup("#sequenceVBox");

        String imageUrl = Objects.requireNonNull(getClass().getResource("/com/nekitvp/iakova/screen_background.jpg")).toExternalForm();
        // Устанавливаем фон по умолчанию при создании логики экрана
        screenPane.setStyle("-fx-background-image: url('" + imageUrl + "'); -fx-background-size: cover;");

        // Инициализация таймера (не запущен)
        initTimer();
    }

    private void initTimer() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsedNanos = now - timerStartTime;
                double elapsedSeconds = elapsedNanos / 1_000_000_000.0;
                timerLabel.setText(formatTime(elapsedSeconds));
            }
        };
    }

    // Форматирование времени в виде mm:ss.SSS
    private String formatTime(double elapsedSeconds) {
        int minutes = (int) (elapsedSeconds / 60);
        int seconds = (int) (elapsedSeconds % 60);
        int millis = (int) ((elapsedSeconds - (int) elapsedSeconds) * 1000);
        return String.format("%02d:%02d.%03d", minutes, seconds, millis);
    }

    @Override
    public void handleTeamKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();

        // Сброс экрана по нажатию N
        if (code == KeyCode.N) {
            resetScreen();
            return;
        }

        // Запуск таймера по нажатию C (если он ещё не запущен)
        if (code == KeyCode.C) {
            if (!timerRunning) {
                startTimer();
                playStartSound();
            }
            return;
        }

        // Если таймер не запущен, нажатия команд не обрабатываем
        if (!timerRunning) {
            return;
        }

        // Обработка нажатия клавиш для команд (из enum Team)
        for (Team team : Team.values()) {
            if (code == team.getKey()) {
                // Если команда уже была зафиксирована – игнорируем
                if (pressedTeams.contains(team)) {
                    return;
                }
                pressedTeams.add(team);
                double elapsedSeconds = (System.nanoTime() - timerStartTime) / 1_000_000_000.0;
                addTeamRectangle(team, pressedTeams.size(), formatTime(elapsedSeconds));
                playTeamPressSound();

                // Если все команды нажали кнопки – останавливаем таймер
                if (pressedTeams.size() >= totalTeams) {
                    stopTimer();
                    playEndSound();
                }
                break;
            }
        }
    }

    // Создание прямоугольника с информацией о команде и времени
    private void addTeamRectangle(Team team, int order, String time) {
        // Создаем StackPane как "прямоугольник" для команды
        StackPane rectPane = new StackPane();
        rectPane.setPrefHeight(80);
        rectPane.setMaxWidth(800);
        // Используем цвет из enum Team
        rectPane.setStyle("-fx-background-color: " + team.getColor() +
                "; -fx-border-color: black; -fx-border-width: 2;");

        // Текст: "order - TEAM_NAME - TIME"
        Label label = new Label(order + " - " + team.getText() + " - " + time);
        label.setStyle("-fx-font-size: 30px; -fx-text-fill: black; -fx-font-weight: bold;");
        label.setEffect(new DropShadow(5, 3, 3, Color.GRAY));

        rectPane.getChildren().add(label);
        // Добавляем прямоугольник в VBox (правую часть)
        sequenceVBox.getChildren().add(rectPane);
    }

    // Проигрываем звук при нажатии команды
    private void playTeamPressSound() {
        AudioClip clip = new AudioClip(
                Objects.requireNonNull(getClass().getResource("/sound/team_press.wav")).toExternalForm());
        clip.play();
    }

    private void playEndSound() {
        AudioClip clip = new AudioClip(
                Objects.requireNonNull(getClass().getResource("/sound/end.wav")).toExternalForm());
        clip.play();
    }
    private void playStartSound() {
        AudioClip clip = new AudioClip(
                Objects.requireNonNull(getClass().getResource("/sound/timer_start.wav")).toExternalForm());
        clip.play();
    }

    // Сброс экрана: очищаем последовательность и сбрасываем таймер
    private void resetScreen() {
        pressedTeams.clear();
        // Оставляем только заголовок (предполагается, что он первый элемент VBox)
        if (sequenceVBox.getChildren().size() > 1) {
            sequenceVBox.getChildren().remove(1, sequenceVBox.getChildren().size());
        }
        stopTimer();
        timerLabel.setText("00:00.000");
    }

    // Запуск таймера
    private void startTimer() {
        timerStartTime = System.nanoTime();
        timerRunning = true;
        animationTimer.start();
    }

    // Остановка таймера
    private void stopTimer() {
        timerRunning = false;
        animationTimer.stop();
    }

    @Override
    public void onScreenShow() {
        // При показе экрана сбрасываем последовательность и таймер
        resetScreen();
    }
}

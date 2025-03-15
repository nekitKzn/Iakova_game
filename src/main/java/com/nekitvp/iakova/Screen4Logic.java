package com.nekitvp.iakova;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

import java.util.Objects;

public class Screen4Logic implements ScreenLogic {
    // Массив меток таймеров в порядке, соответствующем enum Team:
    // (Team.values() предполагается в порядке: BLUE, RED, GREEN, YELLOW, ORANGE, PURPLE, GRAY, PINK)
    private final Label[] timerLabels;
    private final Timeline[] timelines;
    // Накопленное время для каждого таймера (в миллисекундах)
    private final long[] elapsedTimes;
    // Звуки старта и остановки
    private final AudioClip startSound;
    private final AudioClip stopSound;

    // Новые константы для увеличенных размеров таймеров
    private static final String NORMAL_STYLE_TEMPLATE = "-fx-font-size: %dpx; -fx-font-family: 'Monospaced'; -fx-text-fill: black; -fx-background-color: %s; -fx-font-weight: bold; -fx-alignment: center;";
    private static final int NORMAL_FONT_SIZE = 36;     // увеличенный размер шрифта
    private static final int STOPPED_FONT_SIZE = 44;      // увеличенный размер при остановке
    private static final double PREF_WIDTH = 220;         // увеличенная ширина таймера
    private static final double PREF_HEIGHT = 80;         // увеличенная высота таймера

    public Screen4Logic(Label timerBlue, Label timerRed, Label timerGreen, Label timerYellow,
                        Label timerOrange, Label timerPurple, Label timerGray, Label timerPink) {
        // Инициализация массива меток в том же порядке, что и Team.values()
        this.timerLabels = new Label[]{ timerBlue, timerRed, timerGreen, timerYellow, timerOrange, timerPurple, timerGray, timerPink };
        this.timelines = new Timeline[timerLabels.length];
        this.elapsedTimes = new long[timerLabels.length];

        Team[] teams = Team.values();
        for (int i = 0; i < timerLabels.length; i++) {
            // Устанавливаем начальное значение таймера в формате MM:SS:ms
            timerLabels[i].setText("00:00:000");
            elapsedTimes[i] = 0;
            // Устанавливаем фиксированные размеры, чтобы текст не смещался
            timerLabels[i].setPrefWidth(PREF_WIDTH);
            timerLabels[i].setPrefHeight(PREF_HEIGHT);
            // Устанавливаем стиль с использованием цвета команды из enum Team
            timerLabels[i].setStyle(String.format(NORMAL_STYLE_TEMPLATE, NORMAL_FONT_SIZE, teams[i].getColor()));
            final int idx = i;
            timelines[i] = new Timeline(new KeyFrame(Duration.millis(50), event -> {
                elapsedTimes[idx] += 50;
                long total = elapsedTimes[idx];
                int minutes = (int) (total / 60000);
                int seconds = (int) ((total % 60000) / 1000);
                int millis = (int) (total % 1000);
                timerLabels[idx].setText(String.format("%02d:%02d:%03d", minutes, seconds, millis));
            }));
            timelines[i].setCycleCount(Timeline.INDEFINITE);
        }

        // Загружаем звуковые эффекты (убедитесь, что файлы доступны по указанным путям)
        startSound = new AudioClip(Objects.requireNonNull(getClass().getResource("/sound/timer_start.wav")).toExternalForm());
        stopSound  = new AudioClip(Objects.requireNonNull(getClass().getResource("/sound/timer_stop.wav")).toExternalForm());
    }

    @Override
    public void handleTeamKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        // Если нажата клавиша 0 – сброс всех таймеров
        if (code == KeyCode.DIGIT0) {
            resetTimers();
            return;
        }
        // Если нажата клавиша C – запуск всех таймеров
        if (code == KeyCode.C) {
            startAllTimers();
            return;
        }
        // Останавливаем таймер для команды (порядок меток соответствует порядку в enum Team)
        Team[] teams = Team.values();
        for (int i = 0; i < timerLabels.length; i++) {
            if (code == teams[i].getKey()) {
                timelines[i].stop();
                playStopSound();
                break;
            }
        }
    }

    private void startAllTimers() {
        for (Timeline timeline : timelines) {
            timeline.play();
        }
        // При старте возвращаем нормальный стиль для всех таймеров
        Team[] teams = Team.values();
        for (int i = 0; i < timerLabels.length; i++) {
            timerLabels[i].setStyle(String.format(NORMAL_STYLE_TEMPLATE, NORMAL_FONT_SIZE, teams[i].getColor()));
        }
        playStartSound();
    }

    private void resetTimers() {
        for (int i = 0; i < timelines.length; i++) {
            timelines[i].stop();
            elapsedTimes[i] = 0;
            timerLabels[i].setText("00:00:000");
            timerLabels[i].setStyle(String.format(NORMAL_STYLE_TEMPLATE, NORMAL_FONT_SIZE, Team.values()[i].getColor()));
        }
    }

    private void playStartSound() {
        startSound.play();
    }

    private void playStopSound() {
        stopSound.play();
    }

    @Override
    public void onScreenShow() {
        resetTimers();
    }
}

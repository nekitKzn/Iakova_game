package com.nekitvp.iakova;

import java.util.HashMap;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.Objects;

public class Screen4Logic implements ScreenLogic {

    private AnchorPane screenPane;       // Корневой узел экрана для изменения фона
    private Label timerLabelBrain;       // Большой таймер (формат "SS:CS")
    private Label currentTeamLabel;      // "Отвечает <команда> команда"
    private Label bonusLabel;            // Показывается, если таймер остановлен в пределах первой секунды
    private Label doublePointsLabel;
    private Timeline timeline;
    private long remainingTime = 20000;  // 20 секунд в миллисекундах
    private boolean running = false;

    private Team selectedTeam;
    private Map<Team, Label> teamScoreLabels;

    // MediaPlayer'ы для звуков
    private MediaPlayer timerStartPlayer;
    private MediaPlayer answerPlayer;
    private MediaPlayer tickingPlayer;   // Звук тикания часов
    private MediaPlayer endPlayer;

    public Screen4Logic(AnchorPane screenPane, Label timerLabelBrain, Label currentTeamLabel, Label bonusLabel,
                        Label teamScoreBrain1, Label teamScoreBrain2, Label teamScoreBrain3, Label teamScoreBrain4,
                        Label teamScoreBrain5, Label teamScoreBrain6, Label teamScoreBrain7, Label teamScoreBrain8,
                        Label doublePointsLabel) {
        this.screenPane = screenPane;
        this.timerLabelBrain = timerLabelBrain;
        this.currentTeamLabel = currentTeamLabel;
        this.bonusLabel = bonusLabel;
        this.doublePointsLabel  = doublePointsLabel;
        updateTimerLabel();

        // Инициализируем MediaPlayer для звука запуска таймера
        Media timerMedia = new Media(
                Objects.requireNonNull(getClass().getResource("/sound/timer_start.wav")).toExternalForm());
        timerStartPlayer = new MediaPlayer(timerMedia);

        // Инициализируем MediaPlayer для звука нажатия команды
        Media answerMedia = new Media(
                Objects.requireNonNull(getClass().getResource("/sound/answer.wav")).toExternalForm());
        answerPlayer = new MediaPlayer(answerMedia);

        // Инициализируем MediaPlayer для звука нажатия команды
        Media endMedia = new Media(
                Objects.requireNonNull(getClass().getResource("/sound/endTime.wav")).toExternalForm());
        endPlayer = new MediaPlayer(endMedia);

        // Инициализируем MediaPlayer для звука тикания часов
        Media tickingMedia = new Media(
                Objects.requireNonNull(getClass().getResource("/sound/clock.wav")).toExternalForm());
        tickingPlayer = new MediaPlayer(tickingMedia);
        tickingPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        teamScoreLabels = new HashMap<>();
        teamScoreLabels.put(Team.BLUE, teamScoreBrain1);
        teamScoreLabels.put(Team.RED, teamScoreBrain2);
        teamScoreLabels.put(Team.GREEN, teamScoreBrain3);
        teamScoreLabels.put(Team.GRAY, teamScoreBrain4);
        teamScoreLabels.put(Team.YELLOW, teamScoreBrain5);
        teamScoreLabels.put(Team.ORANGE, teamScoreBrain6);
        teamScoreLabels.put(Team.PURPLE, teamScoreBrain7);
        teamScoreLabels.put(Team.PINK, teamScoreBrain8);
    }

    private void updateScore(int delta) {
        var label = teamScoreLabels.get(selectedTeam);
        int score = Integer.valueOf(label.getText());
        int result = Math.max(0, score + delta);
        label.setText(String.valueOf(result));
    }
    @Override
    public void handleTeamKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();

        // Сброс таймера на 20 секунд по нажатию клавиши N
        if (code == KeyCode.N) {
            bonusLabel.setText("");
            currentTeamLabel.setText("");
            resetTimer();
            return;
        }

        if (code == KeyCode.A) {
            updateScore(1);
        }

        if (code == KeyCode.Z) {
            updateScore(-1);
        }

        if (code == KeyCode.DIGIT0) {
            doublePointsLabel.setVisible(!doublePointsLabel.isVisible());
        }

        // Если нажата клавиша C и таймер не запущен,
        // то возобновляем, если уже начинался (remainingTime < 20000), иначе запускаем новый
        if (code == KeyCode.C && !running) {
            if (remainingTime < 20000) {
                resumeTimer();
            } else {
                startTimer();
            }
            screenPane.setStyle("-fx-background-color: black;");
            currentTeamLabel.setText("");
            bonusLabel.setText("");
            return;
        }

        if (isTeamKey(code)) {
            Team team = getTeamByKey(code);
            selectedTeam = team;
        }

        // Если таймер запущен и нажата клавиша команды, приостанавливаем таймер
        if (running && isTeamKey(code)) {
            pauseTimer();
            Team team = getTeamByKey(code);
            if (team != null) {
                currentTeamLabel.setText("Отвечает " + team.getText() + " команда");
                // Перекрашиваем экран в цвет команды (цвет берется из enum Team)
                screenPane.setStyle("-fx-background-color: " + team.getColor() + ";");
                // Воспроизводим звук нажатия команды через MediaPlayer
                play(answerPlayer);
            }
            // Если оставшееся время меньше или равно 1 секунде, показываем bonusLabel
            if (remainingTime <= 20000 && remainingTime >= 19000) {
                bonusLabel.setVisible(true);
                bonusLabel.setText("Ответ на ПЕРВОЙ секунде!");
            } else {
                bonusLabel.setText("");
            }
            return;
        }
    }

    private void startTimer() {
        running = true;
        remainingTime = 20000; // Начинаем с 20 секунд
        currentTeamLabel.setText("");

        // Воспроизводим звук запуска таймера
        play(timerStartPlayer);
        // Запускаем звук тикания часов
        play(tickingPlayer);

        timeline = new Timeline(new KeyFrame(Duration.millis(10), e -> {
            remainingTime -= 10;
            updateTimerLabel();
            if (remainingTime <= 0) {
                stopTimer();
                stop(tickingPlayer);
                currentTeamLabel.setText("Время вышло!");
                screenPane.setStyle("-fx-background-color: #797408;");
                play(endPlayer);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Останавливает таймер полностью
    private void stopTimer() {
        running = false;
        if (timeline != null) {
            timeline.stop();
        }
        stop(tickingPlayer);
    }

    // Приостанавливает таймер (например, при нажатии команды)
    private void pauseTimer() {
        if (timeline != null) {
            timeline.pause();
        }
        stop(tickingPlayer);
        running = false;
    }

    // Возобновляет таймер с того же места (только по нажатию C)
    private void resumeTimer() {
        if (timeline != null) {
            timeline.play();
        }
        running = true;
        play(tickingPlayer);
    }

    private void resetTimer() {
        stopTimer();
        remainingTime = 20000;
        currentTeamLabel.setText("");
        updateTimerLabel();
        // Сбрасываем фон экрана на исходный (например, чёрный)
        screenPane.setStyle("-fx-background-color: black;");
    }

    private void updateTimerLabel() {
        int seconds = (int) (remainingTime / 1000);
        int centiseconds = (int) ((remainingTime % 1000) / 10);
        timerLabelBrain.setText(String.format("%02d:%02d", seconds, centiseconds));
    }

    private boolean isTeamKey(KeyCode code) {
        for (Team team : Team.values()) {
            if (team.getKey() == code) {
                return true;
            }
        }
        return false;
    }

    private Team getTeamByKey(KeyCode code) {
        for (Team team : Team.values()) {
            if (team.getKey() == code) {
                return team;
            }
        }
        return null;
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

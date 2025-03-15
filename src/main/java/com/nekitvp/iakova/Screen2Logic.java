package com.nekitvp.iakova;

import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;

import java.util.Objects;

public class Screen2Logic implements ScreenLogic {
    private final AnchorPane screenPane;
    private final ImageView blackSquareImage;
    private final Label teamResultLabel;

    public Screen2Logic(AnchorPane screenPane, ImageView blackSquareImage, Label teamResultLabel) {
        this.screenPane = screenPane;
        this.blackSquareImage = blackSquareImage;
        this.teamResultLabel = teamResultLabel;
    }

    @Override
    public void handleTeamKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        // Если нажата клавиша 0 – сброс фона и текста
        if (code == KeyCode.DIGIT0) {
            screenPane.setStyle("-fx-background-color: black;");
            teamResultLabel.setText("");
            return;
        }
        // Проверяем, соответствует ли нажатая клавиша одной из команд из enum Team
        for (Team team : Team.values()) {
            if (code == team.getKey()) {
                // Устанавливаем фон согласно цвету из enum
                screenPane.setStyle("-fx-background-color: " + team.getColor() + ";");
                // Обновляем метку с текстом
                teamResultLabel.setText("Отвечает " + team.getText() + " команда");
                playSelectionSound();
                break;
            }
        }
    }

    private void playSelectionSound() {
        AudioClip sound = new AudioClip(Objects.requireNonNull(
                getClass().getResource("/sound/answer.wav")).toExternalForm());
        sound.play();
    }

    @Override
    public void onScreenShow() {
        // При показе экрана сбрасываем фон и текст метки
        screenPane.setStyle("-fx-background-color: black;");
        teamResultLabel.setText("");
    }
}

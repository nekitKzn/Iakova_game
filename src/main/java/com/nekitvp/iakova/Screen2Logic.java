package com.nekitvp.iakova;

import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;

import java.util.Objects;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Screen2Logic implements ScreenLogic {
    private final AnchorPane screenPane;
    private final ImageView blackSquareImage;
    private final Label teamResultLabel;
    private boolean answered = false;
    private boolean activated = false;
    private final String defaultStyle;

    public Screen2Logic(AnchorPane screenPane, ImageView blackSquareImage, Label teamResultLabel) {
        this.screenPane = screenPane;
        this.blackSquareImage = blackSquareImage;
        this.teamResultLabel = teamResultLabel;
        // Используем изображение для фонового оформления вместо стандартного чёрного цвета.
        // Убедитесь, что файл "screen_background.jpg" находится в папке ресурсов по указанному пути.
        String imageUrl = Objects.requireNonNull(getClass().getResource("/com/nekitvp/iakova/screen_background.jpg")).toExternalForm();
        this.defaultStyle = "-fx-background-image: url('" + imageUrl + "'); -fx-background-size: cover;";
        // Устанавливаем фон по умолчанию при создании логики экрана
        screenPane.setStyle(defaultStyle);

        // Задаем стиль для teamResultLabel: крупный жирный шрифт с золотым цветом, хорошо контрастирующим с сине-фиолетовым фоном.
        teamResultLabel.setStyle("-fx-font-size: 50px; -fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-text-fill: #2c2828;");
    }

    @Override
    public void handleTeamKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();

        // Сброс экрана по клавише N: фон, метка и флаги возвращаются в исходное состояние.
        if (code == KeyCode.N) {
            screenPane.setStyle(defaultStyle);
            teamResultLabel.setText("Черный квадрат");
            answered = false;
            activated = false;
            return;
        }

        // Если экран ещё не активирован (т.е. не нажата клавиша C), обрабатываем только её.
        if (!activated) {
            if (code == KeyCode.C) {
                teamResultLabel.setText("Нажимайте, если знаете ответ");
                activated = true;
                playActivation();
            }
            return;
        }

        // Если уже получен ответ, дальнейшие нажатия игнорируются.
        if (answered) {
            return;
        }

        // Обрабатываем нажатия клавиш для команд.
        for (Team team : Team.values()) {
            if (code == team.getKey()) {
                // При выборе команды фон меняется на однотонный цвет команды.
                screenPane.setStyle("-fx-background-color: " + team.getColor() + ";");
                teamResultLabel.setText("Отвечает " + team.getText() + " команда");
                playSelectionSound();
                answered = true;
                break;
            }
        }
    }

    private void playSelectionSound() {
        AudioClip sound = new AudioClip(Objects.requireNonNull(
                getClass().getResource("/sound/answer.wav")).toExternalForm());
        sound.play();
    }

    private void playActivation() {
        AudioClip sound = new AudioClip(Objects.requireNonNull(
                getClass().getResource("/sound/activation.wav")).toExternalForm());
        sound.play();
    }

    @Override
    public void onScreenShow() {
        // При показе экрана возвращаем фон, устанавливаем надпись "Черный квадрат" и сбрасываем флаги.
        screenPane.setStyle(defaultStyle);
        teamResultLabel.setText("Черный квадрат");
        answered = false;
        activated = false;
    }
}

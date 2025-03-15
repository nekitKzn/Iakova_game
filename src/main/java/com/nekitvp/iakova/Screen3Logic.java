package com.nekitvp.iakova;

import java.util.Objects;
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

import java.util.LinkedHashSet;
import java.util.Set;

public class Screen3Logic implements ScreenLogic {
    private final AnchorPane screenPane;
    private final VBox vbox;
    // Используем LinkedHashSet для сохранения порядка и исключения повторов
    private final Set<Team> pressedTeams;

    public Screen3Logic(AnchorPane screenPane) {
        this.screenPane = screenPane;
        this.pressedTeams = new LinkedHashSet<>();

        // Создаем VBox, который будет располагать элементы по вертикали и центрироваться
        vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(20));

        // Заголовок
        Label header = new Label("Последовательность команд");
        header.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-weight: bold;");
        vbox.getChildren().add(header);

        // Добавляем VBox в screenPane, заполняющий весь контейнер
        screenPane.getChildren().clear();
        AnchorPane.setTopAnchor(vbox, 0.0);
        AnchorPane.setBottomAnchor(vbox, 0.0);
        AnchorPane.setLeftAnchor(vbox, 0.0);
        AnchorPane.setRightAnchor(vbox, 0.0);
        screenPane.getChildren().add(vbox);
    }

    @Override
    public void handleTeamKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        // Если нажата клавиша 0 — сброс последовательности
        if (code == KeyCode.DIGIT0) {
            clearSequence();
            return;
        }
        // Проходим по всем командам из enum Team
        for (Team team : Team.values()) {
            if (code == team.getKey()) {
                // Если команда уже зафиксирована, игнорируем повторное нажатие
                if (pressedTeams.contains(team)) {
                    return;
                }
                // Добавляем команду в последовательность и отображаем её
                pressedTeams.add(team);
                addTeamRectangle(team, pressedTeams.size());
                playTeamPressSound();
                break;
            }
        }
    }

    private void addTeamRectangle(Team team, int order) {
        // Создаем StackPane как "прямоугольник" для команды
        StackPane rectPane = new StackPane();
        // Задаем размеры в 2 раза больше, чем ранее (50 -> 100, 400 -> 800)
        rectPane.setPrefHeight(100);
        rectPane.setMaxWidth(800);
        // Фон задаем согласно цвету команды из enum
        rectPane.setStyle("-fx-background-color: " + team.getColor() +
                "; -fx-border-color: black; -fx-border-width: 2;");

        // Создаем метку с текстом "order - TEAM_TEXT" (например, "1 - КРАСНАЯ")
        Label label = new Label(order + " - " + team.getText());
        label.setStyle("-fx-font-size: 40px; -fx-text-fill: black; -fx-font-weight: bold;");
        // При желании можно добавить DropShadow для эффектности:
        label.setEffect(new DropShadow(5, 3, 3, javafx.scene.paint.Color.GRAY));

        rectPane.getChildren().add(label);
        // Добавляем прямоугольник в VBox
        vbox.getChildren().add(rectPane);
    }

    private void playTeamPressSound() {
        // Предполагается, что звук расположен по пути /sound/team_press.wav в ресурсах
        AudioClip clip = new AudioClip(
                Objects.requireNonNull(getClass().getResource("/sound/team_press.wav")).toExternalForm());
        clip.play();
    }

    private void clearSequence() {
        pressedTeams.clear();
        // Оставляем только заголовок (на позиции 0)
        if (vbox.getChildren().size() > 1) {
            vbox.getChildren().remove(1, vbox.getChildren().size());
        }
    }

    @Override
    public void onScreenShow() {
        // При показе экрана сбрасываем последовательность команд
        clearSequence();
    }
}

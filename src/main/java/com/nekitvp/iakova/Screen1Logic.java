package com.nekitvp.iakova;

import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.util.Objects;

public class Screen1Logic implements ScreenLogic {

    @Override
    public void handleTeamKeyPress(KeyEvent event) {
        // На этом экране нажатия кнопок не приводят к действиям
    }

    @Override
    public void onScreenShow() {
        // Можно добавить вывод в консоль или другую логику при переключении на экран 1
        System.out.println("Экран 1 активен – показ афиши");
    }

    public Screen1Logic(AnchorPane screenPane) {
        String imageUrl = Objects.requireNonNull(getClass().getResource("/com/nekitvp/iakova/afisha.jpg")).toExternalForm();
        screenPane.setStyle("-fx-background-image: url('"+imageUrl+"'); -fx-background-size: cover; -fx-background-repeat: no-repeat;-fx-background-position: center center;");
    }
}

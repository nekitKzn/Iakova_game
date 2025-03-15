package com.nekitvp.iakova;

import javafx.scene.input.KeyEvent;

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
}

package com.nekitvp.iakova;

import javafx.scene.input.KeyEvent;

public interface ScreenLogic {
    // Обработка нажатий на кнопки команд (например, b, r, g, y, o, p, m, f)
    void handleTeamKeyPress(KeyEvent event);

    // Метод, вызываемый при активации экрана (опционально)
    default void onScreenShow() {}
}

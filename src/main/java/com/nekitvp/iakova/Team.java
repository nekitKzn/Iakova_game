package com.nekitvp.iakova;

import javafx.scene.input.KeyCode;

public enum Team {

    BLUE("Синяя команда", "#2f3386", "СИНЯЯ", KeyCode.B),
    RED("Красная команда", "#a41515", "КРАСНАЯ", KeyCode.R),
    GREEN("Зеленая команда", "#15a02f", "ЗЕЛЕНАЯ", KeyCode.G),
    YELLOW("Желтая команда", "#e9f163", "ЖЕЛТАЯ", KeyCode.Y),
    ORANGE("Оранжевая команда", "#e86734", "ОРАНЖЕВАЯ", KeyCode.O),
    PURPLE("Фиолетовая команда", "#8A2BE2", "ФИОЛЕТОВАЯ", KeyCode.P),
    GRAY("Серая команда", "#a5a5a9", "СЕРАЯ", KeyCode.M),
    PINK("Розовая команда", "#e829b4", "РОЗОВАЯ", KeyCode.F);

    private final String name;
    private final String color;
    private final String text;
    private final KeyCode key;

    Team(String name, String color, String text, KeyCode key) {
        this.name = name;
        this.color = color;
        this.text = text;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getText() {
        return text;
    }

    public KeyCode getKey() {
        return key;
    }
}

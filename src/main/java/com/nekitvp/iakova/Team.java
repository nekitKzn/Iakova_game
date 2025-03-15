package com.nekitvp.iakova;

import javafx.scene.input.KeyCode;

public enum Team {

    BLUE("Синяя команда", "#1E90FF", "СИНЯЯ", KeyCode.B),
    RED("Красная команда", "#ad3333", "КРАСНАЯ", KeyCode.R),
    GREEN("Зеленая команда", "#4f6b34", "ЗЕЛЕНАЯ", KeyCode.G),
    YELLOW("Желтая команда", "#FFD700", "ЖЕЛТАЯ", KeyCode.Y),
    ORANGE("Оранжевая команда", "#FF8C00", "ОРАНЖЕВАЯ", KeyCode.O),
    PURPLE("Фиолетовая команда", "#8A2BE2", "ФИОЛЕТОВАЯ", KeyCode.P),
    GRAY("Серая команда", "#808080", "СЕРАЯ", KeyCode.M),
    PINK("Розовая команда", "#FF1493", "РОЗОВАЯ", KeyCode.F);

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

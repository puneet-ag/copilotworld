package com.dpworld.copilotworld.panel;

import javax.swing.*;

public class AvatarModel {
    private final String name;
    private final String code;

    private final Icon icon;

    public AvatarModel(String name, String code, Icon icon) {
        this.name = name;
        this.code = code;
        this.icon = icon;

    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public Icon getIcon() { return icon;}
}

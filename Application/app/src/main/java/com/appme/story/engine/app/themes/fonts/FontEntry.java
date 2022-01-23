package com.appme.story.engine.app.themes.fonts;

public class FontEntry {
    public boolean fromStorage;
    public String name;

    public FontEntry(boolean fromStorage, String name) {
        this.fromStorage = fromStorage;
        this.name = name;
    }


    @Override
    public String toString() {
        return "FontEntry{" +
                "fromStorage=" + fromStorage +
                ", name='" + name + '\'' +
                '}';
    }
}

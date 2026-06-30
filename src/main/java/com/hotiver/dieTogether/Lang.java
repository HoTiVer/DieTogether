package com.hotiver.dieTogether;

public enum Lang {
    EN("en"),
    RU("ru"),
    UK("uk");

    private final String languageKey;

    private Lang(String languageKey) {
        this.languageKey = languageKey;
    }
    public String getLanguageKey() {
        return languageKey;
    }
}

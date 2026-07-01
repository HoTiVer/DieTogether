package com.hotiver.dieTogether.common;

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

    public static Lang fromString(String code) {
        if (code == null || code.isBlank()) {
            return EN;
        }

        String cleanedCode = code.trim().toUpperCase();
        try {
            return Lang.valueOf(cleanedCode);
        } catch (IllegalArgumentException e) {
            return EN;
        }
    }
}

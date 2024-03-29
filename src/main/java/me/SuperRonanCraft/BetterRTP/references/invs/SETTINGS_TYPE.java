package me.SuperRonanCraft.BetterRTP.references.invs;

public enum SETTINGS_TYPE {
    BOOLEAN(Boolean.class), STRING(String.class), INTEGER(Integer.class);

    private java.io.Serializable type;

    SETTINGS_TYPE(java.io.Serializable type) {
        this.type = type;
    }

    java.io.Serializable getType() {
        return type;
    }
}
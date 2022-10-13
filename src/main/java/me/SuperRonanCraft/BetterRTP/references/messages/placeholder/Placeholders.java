package me.SuperRonanCraft.BetterRTP.references.messages.placeholder;

public enum Placeholders {

    COMMAND("command"),
    PLAYER_NAME("player"),
    COOLDOWN("cooldown"),
    //Location
    LOCATION_X("x"),
    LOCATION_Y("y"),
    LOCATION_Z("z"),
    //World
    WORLD("world"),
    //ints
    ATTEMPTS("attempts"),
    PRICE("price"),
    DELAY("delay"),
    //Other
    BIOME("biome")
    ;

    public final String name;

    Placeholders(String name) {
        this.name = "%" + name + "%";
    }

}

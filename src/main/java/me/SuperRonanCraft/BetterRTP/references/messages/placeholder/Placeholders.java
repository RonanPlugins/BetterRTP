package me.SuperRonanCraft.BetterRTP.references.messages.placeholder;

public enum Placeholders {

    COMMAND("command"),
    PLAYER_NAME("player"),
    //Location
    LOCATION_X("x"),
    LOCATION_Y("y"),
    LOCATION_Z("z"),
    //World
    WORLD("world"),
    //ints
    ATTEMPTS("attempts"),
    PRICE("price"),
    //Other
    BIOME("biome")
    ;

    public final String name;

    Placeholders(String name) {
        this.name = "%" + name + "%";
    }

}

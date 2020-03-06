package me.SuperRonanCraft.BetterRTP.references.invs.enums;

public enum RTP_INV_ITEMS {
    NORMAL("paper", 1),
    BACK("book", 1, "Back", 0);

    public String item, name;
    public int amt, slot = -1;

    RTP_INV_ITEMS(String item, int amt) {
        this.item = item;
        this.amt = amt;
    }

    RTP_INV_ITEMS(String item, int amt, String name, int slot) {
        this.item = item;
        this.amt = amt;
        this.name = name;
        this.slot = slot;
    }
}

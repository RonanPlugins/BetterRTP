package me.SuperRonanCraft.BetterRTP.references.file;

public class Files {
    private final LangFile langFile = new LangFile();
    private final FileBasics basics = new FileBasics();

    LangFile getLang() {
        return langFile;
    }

    public FileBasics.FILETYPE getType(FileBasics.FILETYPE type) {
        return basics.types.get(basics.types.indexOf(type));
    }

    public void loadAll() {
        basics.load();
        langFile.load();
    }
}


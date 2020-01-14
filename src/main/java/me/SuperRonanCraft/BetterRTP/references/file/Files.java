package me.SuperRonanCraft.BetterRTP.references.file;

public class Files {
    private LangFile langFile = new LangFile();
    private FileBasics basics = new FileBasics();

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


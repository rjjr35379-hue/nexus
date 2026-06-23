package rj.nexus.systems.dialogue;

public enum DialogCategory {
    GREETING("greeting"), PASSIVE("passive"), NEGATIVE("negative"),
    OFFERING("offering"), CONTEXTUAL("contextual"), LEAVING("leaving"),
    SPECIAL("special"), PLAYER_GREETING("player_greeting");
    private final String folder;

    DialogCategory(String f) {
        folder = f;
    }

    public static DialogCategory fromString(String s) {
        for (var c : values()) if (c.folder.equalsIgnoreCase(s)) return c;
        return PASSIVE;
    }

    public String folderName() {
        return folder;
    }
}

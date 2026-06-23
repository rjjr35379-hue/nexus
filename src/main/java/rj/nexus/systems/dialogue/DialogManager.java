package rj.nexus.systems.dialogue;

import net.minecraft.resources.Identifier;

public class DialogManager {
    public static final int PLAYER_COLOR = 0x6AAFE6;
    private static DialogState npc, player;

    public static void show(String text, String cat, Identifier sound, float lifetime) {
        if (text == null || text.isEmpty()) return;
        npc = new DialogState(text, cat, sound, lifetime);
    }

    public static void show(String text) {
        show(text, "neutral", null, 80f);
    }

    public static void show(String text, float lifetime) {
        show(text, "neutral", null, lifetime);
    }

    public static void showPlayer(String text, float lifetime) {
        if (text == null || text.isEmpty()) return;
        DialogState s = new DialogState(text, "player", null, lifetime);
        s.overrideColor = PLAYER_COLOR;
        player = s;
    }

    public static void showPlayer(String text) {
        showPlayer(text, 60f);
    }

    public static void dismiss() {
        if (npc != null) npc.dismiss();
        if (player != null) player.dismiss();
    }

    public static void clearNpc() {
        npc = null;
    }

    public static void clearPlayer() {
        player = null;
    }

    public static DialogState getCurrent() {
        return npc;
    }

    public static DialogState getPlayerState() {
        return player;
    }
}

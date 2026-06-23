package rj.nexus.systems.dialogue;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Set;

public class DialogState {
    public static final float SCROLL_SPEED = 0.75f;
    public static final int FADE_IN_TICKS = 8;
    public static final int FADE_OUT_TICKS = 20;
    private static final Set<Character> MUTE = Set.of(' ', ',', '.');

    public final DialogContainer container;
    public final String category;
    public final Identifier soundEvent;
    public float cursor = 0f, age = 0f, lifetime, lastBeepAge = -999f;
    public boolean finished = false, hasBeepedOnce = false;
    public Integer overrideColor = null;
    public String lastChar = null;
    private boolean frozen = false, silent = false;

    public DialogState(String text, String category, Identifier soundEvent, float lifetime) {
        this.container = new DialogContainer(text);
        this.category = category;
        this.soundEvent = soundEvent;
        this.lifetime = lifetime;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void freeze() {
        frozen = true;
    }

    public boolean isSilent() {
        return silent;
    }

    public void silence() {
        silent = true;
    }

    public void dismiss() {
        if (lifetime > 0) lifetime = FADE_OUT_TICKS;
        freeze();
    }

    public float fadeAlpha() {
        float fi = Math.min(age / FADE_IN_TICKS, 1f), fo = Math.min((FADE_OUT_TICKS + lifetime) / FADE_OUT_TICKS, 1f);
        return Math.min(fi, fo);
    }

    public boolean shouldRemove() {
        return age >= FADE_IN_TICKS && fadeAlpha() <= 0.1f;
    }

    public boolean canBeep() {
        return lastChar == null || !MUTE.contains(lastChar.charAt(0));
    }

    public List<String> getVisibleLines(float delta) {
        if (!Minecraft.getInstance().isPaused()) {
            if (!frozen) cursor += delta * SCROLL_SPEED;
            age += delta;
            if (container.flagDone) lifetime -= delta;
        }
        List<String> lines = container.constructText((int) cursor);
        finished = container.flagDone;
        if (!lines.isEmpty()) {
            String last = lines.get(lines.size() - 1);
            if (!last.isEmpty()) lastChar = String.valueOf(last.charAt(last.length() - 1));
        }
        return lines;
    }
}

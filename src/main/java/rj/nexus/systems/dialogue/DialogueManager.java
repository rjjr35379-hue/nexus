package rj.nexus.systems.dialogue;

import java.util.ArrayDeque;
import java.util.Deque;

public class DialogueManager {
    public static final DialogueManager INSTANCE = new DialogueManager();
    private static final float FADE = 10f;
    private final Deque<Dialogue> queue = new ArrayDeque<>();
    private Dialogue current;
    private float timer, fade;

    private DialogueManager() {
    }

    public void show(Dialogue d) {
        queue.addLast(d);
        if (current == null) advance();
    }

    public void show(String text) {
        show(Dialogue.of(text));
    }

    public void show(String text, String speaker, float lifetime) {
        show(Dialogue.of(text, speaker, lifetime));
    }

    public void tick() {
        if (current == null) return;
        timer--;
        if (timer <= 0) {
            if (!queue.isEmpty()) advance();
            else {
                fade--;
                if (fade <= 0) current = null;
            }
        }
    }

    public void clear() {
        queue.clear();
        current = null;
        timer = 0;
        fade = 0;
    }

    private void advance() {
        current = queue.isEmpty() ? null : queue.pollFirst();
        if (current != null) {
            timer = current.lifetime();
            fade = FADE;
        }
    }

    public boolean isActive() {
        return current != null;
    }

    public Dialogue getCurrent() {
        return current;
    }

    public float getAlpha() {
        if (current == null) return 0f;
        float in = Math.min(timer / FADE, 1f);
        float out = timer > 0 ? 1f : Math.max(fade / FADE, 0f);
        return Math.min(in, out);
    }
}

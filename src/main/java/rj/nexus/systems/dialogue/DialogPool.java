package rj.nexus.systems.dialogue;

import net.minecraft.client.player.LocalPlayer;
import rj.nexus.systems.dialogue.condition.DialogCondition;

public class DialogPool {
    private final DialogCondition condition;
    private final int weight;
    private final String text;

    public DialogPool(DialogCondition c, int w, String t) {
        condition = c;
        weight = w;
        text = t;
    }

    public boolean test(LocalPlayer p) {
        return condition == null || condition.test(p);
    }

    public int getWeight() {
        return weight;
    }

    public String getText() {
        return text;
    }
}

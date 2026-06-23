package rj.nexus.systems.dialogue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DialogPoolManager {
    private static final RandomSource RNG = RandomSource.create();

    public static Optional<DialogPool> pick(DialogCategory cat) {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p == null) return Optional.empty();
        List<DialogPool> m = new ArrayList<>();
        for (var pool : DialogPoolLoader.INSTANCE.getPools(cat)) if (pool.test(p)) m.add(pool);
        if (m.isEmpty()) return Optional.empty();
        int total = m.stream().mapToInt(DialogPool::getWeight).sum(), roll = RNG.nextInt(Math.max(1, total)), acc = 0;
        for (var pool : m) {
            acc += pool.getWeight();
            if (roll < acc) return Optional.of(pool);
        }
        return Optional.of(m.get(m.size() - 1));
    }

    public static void trigger(DialogCategory cat) {
        trigger(cat, null, 80f);
    }

    public static void trigger(DialogCategory cat, Identifier sound, float lt) {
        pick(cat).ifPresent(pool -> {
            String c = switch (cat) {
                case NEGATIVE -> "negative";
                case GREETING -> "positive";
                default -> "neutral";
            };
            DialogManager.show(pool.getText(), c, sound, lt);
        });
    }

    public static void triggerPlayer(DialogCategory cat, float lt) {
        pick(cat).ifPresent(pool -> DialogManager.showPlayer(pool.getText(), lt));
    }

    public static void triggerPlayer(DialogCategory cat) {
        triggerPlayer(cat, 60f);
    }
}

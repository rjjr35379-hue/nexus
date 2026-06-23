package rj.nexus.systems.screenshake;


import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ShakeCenter {
    private static final List<Entry> ACTIVE = new ArrayList<>();

    private ShakeCenter() {}

    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(mc -> {
            if (!mc.isPaused()) {
                Iterator<Entry> it = ACTIVE.iterator();
                while (it.hasNext()) {
                    Entry e = it.next();
                    if (e.shake.expired(e.elapsed)) { it.remove(); continue; }
                    e.elapsed++;
                }
            }
        });
    }

    public static void addScreenShake(ShakeParams params) {
        ACTIVE.add(new Entry(new TremorShake(params)));
    }

    public static void addBlastShake(ShakeParams params, Vec3 origin, double maxDist) {
        ACTIVE.add(new Entry(new BlastShake(params, origin, maxDist)));
    }

    public static void apply(PoseStack stack, float partial) {
        for (Entry e : ACTIVE) {
            e.shake.apply(stack, e.elapsed, partial);
        }
    }

    private static final class Entry {
        final ShakeInstance shake;
        int elapsed = 0;
        Entry(ShakeInstance shake) { this.shake = shake; }
    }
}
package rj.nexus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import rj.nexus.systems.bedrock.particle.NexusParticles;
import rj.nexus.systems.init.NexusPackets;
import rj.nexus.systems.bedrock.resource.NexusResourceManager;
import rj.nexus.systems.bedrock.sodium.NexusSodiumCompat;
import rj.nexus.systems.cutscene.NexusCutsceneHandler;
import rj.nexus.systems.dialogue.DialogManager;
import rj.nexus.systems.dialogue.DialogState;
import rj.nexus.systems.screenshake.ShakeCenter;

@Environment(EnvType.CLIENT)
public class NexusClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        NexusPackets.registerClient();
        NexusCutsceneHandler.register();
        NexusSodiumCompat.init();
        NexusResourceManager.register();
        ShakeCenter.register();
        NexusParticles.init();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level == null) return;

            DialogState npc = DialogManager.getCurrent();
            DialogState plr = DialogManager.getPlayerState();
            if (npc != null) { npc.getVisibleLines(1f); if (npc.shouldRemove()) DialogManager.clearNpc(); }
            if (plr != null) { plr.getVisibleLines(1f); if (plr.shouldRemove()) DialogManager.clearPlayer(); }
        });

    }
}
package rj.nexus;
import com.mojang.blaze3d.platform.Lighting;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.renderer.Lightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rj.nexus.systems.init.NexusEntities;
import rj.nexus.systems.init.NexusPackets;

public class Nexus implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "nexus";
    @Override public void onInitialize() {
        NexusEntities.register();
        NexusPackets.registerCommon();
    }
}

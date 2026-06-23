package rj.nexus.systems.bedrock.particle.io;

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.Identifier;
import rj.nexus.systems.bedrock.particle.component.ParticleComponentMap;
import rj.nexus.systems.bedrock.particle.component.misc.EventSubpart;
import rj.nexus.systems.bedrock.particle.curve.Curve;

import java.util.Map;

public class ParticleEffect {
    public Description description;
    public ParticleComponentMap components;
    public Map<String, EventSubpart> events = ImmutableMap.of();
    public Map<String, Curve> curves        = ImmutableMap.of();

    public static class Description {
        public Identifier identifier;

        @SerializedName("basic_render_parameters")
        private Map<String, String> renderParameters;
        public String material;
        public String texturePath;

        public void postProcess() {
            if (renderParameters == null) return;
            material    = renderParameters.get("material");
            texturePath = renderParameters.get("texture");
        }
    }
}
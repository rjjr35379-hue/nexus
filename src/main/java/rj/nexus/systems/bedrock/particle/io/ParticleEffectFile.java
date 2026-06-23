package rj.nexus.systems.bedrock.particle.io;

import com.google.gson.annotations.SerializedName;

public class ParticleEffectFile {
    @SerializedName("format_version") public String formatVersion;
    @SerializedName("particle_effect") public ParticleEffect effect;
}

package rj.nexus.systems.bedrock.particle.component.particle;

import com.google.gson.annotations.SerializedName;
import rj.nexus.systems.bedrock.particle.component.ParticleComponent;

public class ParticleKillPlane implements ParticleComponent<ParticleKillPlane> {
    @SerializedName("plane")
    public float[] plane = new float[4];
}

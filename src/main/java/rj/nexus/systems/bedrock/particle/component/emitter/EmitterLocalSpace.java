package rj.nexus.systems.bedrock.particle.component.emitter;

import com.google.gson.annotations.SerializedName;
import rj.nexus.systems.bedrock.particle.component.ParticleComponent;

public class EmitterLocalSpace implements ParticleComponent<EmitterLocalSpace> {
    @SerializedName("position")
    public boolean position = false; // default: false

    @SerializedName("rotation")
    public boolean rotation = false; // default: false

    @SerializedName("velocity")
    public boolean velocity = false; // default: false
}

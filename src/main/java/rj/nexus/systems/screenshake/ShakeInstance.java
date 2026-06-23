package rj.nexus.systems.screenshake;


import com.mojang.blaze3d.vertex.PoseStack;

public interface ShakeInstance {
    void apply(PoseStack stack, int elapsed, float partial);
    boolean expired(int elapsed);
}
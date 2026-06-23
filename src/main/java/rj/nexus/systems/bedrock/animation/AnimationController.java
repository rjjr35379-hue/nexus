package rj.nexus.systems.bedrock.animation;


import org.joml.Quaternionf;
import rj.nexus.systems.bedrock.model.BedrockBone;
import rj.nexus.systems.bedrock.model.BedrockModel;

import java.util.ArrayList;
import java.util.List;

public class AnimationController {
    private final BedrockModel model;
    private final List<BoneSnapshot> bindPose = new ArrayList<>();
    private BedrockAnimation current;
    private float time = 0f;
    private float prevTime = 0f;
    private float speed = 1f;
    private long lastNanos = -1;
    private State state = State.STOPPED;

    private double worldX, worldY, worldZ;

    public AnimationController(BedrockModel model) {
        this.model = model;
        for (BedrockBone b : model.getBones()) bindPose.add(new BoneSnapshot(b));
    }

    public void play(BedrockAnimation anim) {
        current = anim;
        time = 0f;
        prevTime = 0f;
        state = State.PLAYING;
        lastNanos = -1;
    }

    public void stop() {
        state = State.STOPPED;
        time = 0f;
        prevTime = 0f;
        current = null;
        restore();
    }

    public void pause()  { state = State.PAUSED; }
    public void resume() { if (state == State.PAUSED) state = State.PLAYING; }
    public void setSpeed(float s) { speed = s; }
    public boolean isPlaying() { return state == State.PLAYING; }
    public BedrockAnimation getCurrent() { return current; }

    public void setWorldPosition(double x, double y, double z) {
        worldX = x; worldY = y; worldZ = z;
    }

    public void tick() {
        if (state != State.PLAYING || current == null) return;
        current.getSounds().forEach((t, data) -> {
        });
    }

    public void update() {
        long now = System.nanoTime();
        if (state != State.PLAYING || current == null) { lastNanos = -1; return; }
        if (lastNanos < 0) { lastNanos = now; return; }

        float dt = Math.min((now - lastNanos) / 1_000_000_000f, 0.1f) * speed;
        lastNanos = now;

        prevTime = time;
        time += dt;

        if (current.length > 0 && time >= current.length) {
            if (current.loop) {
                current.fireKeyframeEvents(model, prevTime, current.length, worldX, worldY, worldZ);
                time %= current.length;
                prevTime = 0f;
            } else {
                time = current.length;
                state = State.STOPPED;
            }
        }

        if (state == State.PLAYING) {
            current.fireKeyframeEvents(model, prevTime, time, worldX, worldY, worldZ);
        }
    }

    public void applyToModel() {
        restore();
        if (current != null && state != State.STOPPED) current.apply(model, time);
    }

    public void restore() {
        List<BedrockBone> bs = model.getBones();
        for (int i = 0; i < bs.size(); i++) bindPose.get(i).restore(bs.get(i));
    }

    public enum State { STOPPED, PLAYING, PAUSED }

    private static class BoneSnapshot {
        final float x, y, z, xs, ys, zs;
        final Quaternionf rot;

        BoneSnapshot(BedrockBone b) {
            x = b.x; y = b.y; z = b.z;
            xs = b.xScale; ys = b.yScale; zs = b.zScale;
            rot = new Quaternionf(b.rotation);
        }

        void restore(BedrockBone b) {
            b.x = x; b.y = y; b.z = z;
            b.xScale = xs; b.yScale = ys; b.zScale = zs;
            b.rotation.set(rot);
        }
    }
}
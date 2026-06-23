package rj.nexus.systems.cutscene;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import rj.nexus.systems.init.NexusEntities;

public final class NexusCutsceneHandler {

    private static @Nullable NexusClientCameraEntity cameraEntity = null;
    private static @Nullable NexusCutsceneExecutor executor = null;

    private NexusCutsceneHandler() {
    }

    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(mc -> {
            if (mc.isPaused() || mc.player == null) {
                if (mc.player == null) stopCutscene();
                return;
            }
            if (!isActive()) return;
            if (mc.options.getCameraType() != CameraType.FIRST_PERSON) {
                mc.options.setCameraType(CameraType.FIRST_PERSON);
            }
            ensureCameraSet(mc);
            final boolean ended = executor.tick(cameraEntity);
            if (ended) {
                final NexusCutsceneData next = executor.getData().getNext();
                if (next != null) {
                    startCutscene(next);
                    return;
                }
                if (executor.getData().getStopMode() == NexusCutsceneData.StopMode.AUTOMATIC) {
                    stopCutscene();
                }
            }
        });
    }

    public static void startCutscene(final NexusCutsceneData data) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        if (cameraEntity == null || !(mc.getCameraEntity() instanceof NexusClientCameraEntity)) {
            cameraEntity = new NexusClientCameraEntity(NexusEntities.CLIENT_CAMERA, mc.level);
        }
        if (!data.getPositions().isEmpty()) {
            final NexusCameraPos first = data.getPositions().getFirst();
            final Vec3 p = first.pos();
            cameraEntity.setPos(p);
            cameraEntity.xo = p.x;
            cameraEntity.yo = p.y;
            cameraEntity.zo = p.z;
            cameraEntity.setYRot(first.yaw());
            cameraEntity.yRotO = first.yaw();
            cameraEntity.setXRot(first.pitch());
            cameraEntity.xRotO = first.pitch();
        }
        mc.setCameraEntity(cameraEntity);
        executor = new NexusCutsceneExecutor(data);
    }

    public static void stopCutscene() {
        if (!isActive()) return;
        cameraEntity = null;
        executor = null;
        final Minecraft mc = Minecraft.getInstance();
        mc.setCameraEntity(mc.player);
    }

    public static void moveCamera(final NexusCutsceneData newData) {
        if (!isActive()) return;
        final float[] rot = executor.getCameraRot(executor.getTick(), 0f);
        final Vec3 pos = executor.getCameraPos(executor.getTick(), 0f);
        final NexusCutsceneData stitched = NexusCutsceneData.create()
                .add(new NexusCameraPos(pos, rot[0], rot[1], executor.getCameraRoll(executor.getTick(), 0f)))
                .duration(newData.getDuration())
                .curve(newData.getCurveType())
                .timeEasing(newData.getTimeEasing())
                .lookEasing(newData.getLookEasing())
                .stopMode(newData.getStopMode());
        newData.getPositions().forEach(stitched::add);
        executor = new NexusCutsceneExecutor(stitched);
    }

    public static boolean isActive() {
        return cameraEntity != null && executor != null;
    }

    public static @Nullable NexusCutsceneExecutor getExecutor() {
        return executor;
    }

    private static void ensureCameraSet(final Minecraft mc) {
        if (!(mc.getCameraEntity() instanceof NexusClientCameraEntity) && cameraEntity != null) {
            mc.setCameraEntity(cameraEntity);
        }
    }
}

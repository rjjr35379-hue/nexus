package rj.nexus.systems.bedrock.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import rj.nexus.systems.bedrock.model.BedrockBone;
import rj.nexus.systems.bedrock.model.BedrockModel;
import rj.nexus.systems.bedrock.model.render.NexusRenderPassInfo;

public abstract class NexusArmorRenderer<T extends Item, R extends HumanoidRenderState> {

    protected final BedrockModel model;

    protected NexusArmorRenderer(BedrockModel model) {
        this.model = model;
    }

    public abstract Identifier getTextureLocation(R renderState, EquipmentSlot slot);

    public RenderType getRenderType(R renderState, Identifier texture) {
        return RenderTypes.armorCutoutNoCull(texture);
    }


    public String getBoneNameForSlot(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD  -> "armorHead";
            case CHEST -> "armorBody";
            case LEGS  -> "armorLeftLeg";
            case FEET  -> "armorLeftBoot";
            default    -> "";
        };
    }

    public <A extends HumanoidModel<R>> void render(
            R renderState, A baseModel,
            PoseStack poseStack, SubmitNodeCollector renderTasks,
            EquipmentSlot slot, int packedLight) {

        Identifier texture = getTextureLocation(renderState, slot);
        RenderType renderType = getRenderType(renderState, texture);
        if (renderType == null) return;

        baseModel.setupAnim(renderState);
        syncBonesFromBaseModel(baseModel, renderState, slot);

        renderTasks.submitCustomGeometry(poseStack, renderType, (pose, vc) -> {
            PoseStack ps = new PoseStack();
            ps.pushPose();
            ps.last().set(pose);

            ps.translate(0, 24f / 16f, 0);
            ps.scale(-1, -1, 1);

            NexusRenderPassInfo passInfo = new NexusRenderPassInfo(
                    model, ps, renderState.x, renderState.y, renderState.z);

            for (String boneName : getBoneNamesForSlot(slot)) {
                BedrockBone bone = model.getBone(boneName);
                if (bone != null) bone.render(ps, vc, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1, passInfo);
            }

            ps.popPose();
        });
    }

    @SuppressWarnings("rawtypes")
    protected <A extends HumanoidModel> void syncBonesFromBaseModel(A baseModel, R renderState, EquipmentSlot slot) {
        for (String boneName : getBoneNamesForSlot(slot)) {
            BedrockBone bone = model.getBone(boneName);
            if (bone == null) continue;
            var part = getModelPartForBone(baseModel, boneName);
            if (part == null) continue;
            bone.rotation.identity().rotateZYX(part.zRot, -part.yRot, -part.xRot);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private  net.minecraft.client.model.geom.ModelPart getModelPartForBone(HumanoidModel model, String boneName) {
        return switch (boneName) {
            case "armorHead"      -> model.head;
            case "armorBody"      -> model.body;
            case "armorLeftArm"   -> model.leftArm;
            case "armorRightArm"  -> model.rightArm;
            case "armorLeftLeg", "armorLeftBoot"   -> model.leftLeg;
            case "armorRightLeg", "armorRightBoot" -> model.rightLeg;
            default -> null;
        };
    }

    protected String[] getBoneNamesForSlot(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD  -> new String[]{"armorHead"};
            case CHEST -> new String[]{"armorBody", "armorLeftArm", "armorRightArm"};
            case LEGS  -> new String[]{"armorLeftLeg", "armorRightLeg"};
            case FEET  -> new String[]{"armorLeftBoot", "armorRightBoot"};
            default    -> new String[0];
        };
    }
}
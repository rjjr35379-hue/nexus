package rj.nexus.systems.bedrock.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jspecify.annotations.Nullable;
import rj.nexus.systems.bedrock.model.pojo.*;
import rj.nexus.systems.bedrock.model.render.NexusRenderPassInfo;

import java.util.*;

@Environment(EnvType.CLIENT)
public class BedrockModel {
    protected final Map<String, BedrockBone>   byName   = new LinkedHashMap<>();
    protected final Map<String, GeoLocator>    byLocator = new LinkedHashMap<>();
    protected final List<BedrockBone>          bones    = new ArrayList<>();
    protected final BedrockBone                root     = new BedrockBone();

    public BedrockModel(BedrockModelFile file) {
        BedrockModelFile.GeometryNew geo = file.getGeometry();
        if (geo == null) return;
        DescriptionItem desc = geo.getDescription();
        int tw = 64, th = 64;
        if (desc != null) {
            tw = desc.getTextureWidth();
            th = desc.getTextureHeight();
        }
        if (geo.getBones() != null) buildBones(geo.getBones(), tw, th);
    }

    private void buildBones(BoneItem[] items, int tw, int th) {
        for (BoneItem item : items) {
            BedrockBone bone = new BedrockBone();
            float[] piv = item.getPivot();
            if (piv != null) {
                bone.x = -piv[0];
                bone.y =  piv[1];
                bone.z =  piv[2];
            }
            float[] rot = item.getRotation();
            if (rot != null) bone.rotation.rotateZYX(
                    (float)  Math.toRadians(rot[2]),
                    (float) -Math.toRadians(rot[1]),
                    (float) -Math.toRadians(rot[0]));
            bone.mirror = item.isMirror();
            bone.index  = bones.size();
            bones.add(bone);
            byName.put(item.getName(), bone);
        }
        for (int i = 0; i < items.length; i++) {
            BoneItem item = items[i];
            BedrockBone bone = bones.get(i);
            String par = item.getParent();
            if (par != null && byName.containsKey(par)) {
                bone.parent = byName.get(par);
                bone.parent.addChild(bone);
            } else {
                bone.parent = root;
                root.addChild(bone);
            }
            if (item.getCubes() != null) for (CubeItem ci : item.getCubes()) addCube(bone, ci, tw, th);

            if (item.getLocators() != null) {
                item.getLocators().forEach((locName, loc) -> {
                    GeoLocator geo = loc.bake(locName, bone);
                    bone.addLocator(geo);
                    byLocator.put(locName, geo);
                });
            }
        }
        convertPivots(root);
    }

    private void addCube(BedrockBone bone, CubeItem ci, int tw, int th) {
        float[] org  = Arrays.copyOf(ci.getOrigin(), 3);
        float[] sz   = ci.getSize();
        float infl   = ci.getInflate();
        org[0] = -(org[0] + sz[0]);

        float[] cRot = ci.getRotation() != null ? Arrays.copyOf(ci.getRotation(), 3) : null;
        float[] cPiv = ci.getPivot()    != null ? Arrays.copyOf(ci.getPivot(), 3)    : null;
        if (cRot != null) {
            cRot[0] = (float) -Math.toRadians(cRot[0]);
            cRot[1] = (float) -Math.toRadians(cRot[1]);
            cRot[2] = (float)  Math.toRadians(cRot[2]);
        }
        if (cPiv != null) cPiv[0] = -cPiv[0];

        float ox = cPiv == null ? org[0] - bone.x : org[0] - cPiv[0];
        float oy = cPiv == null ? org[1] - bone.y : org[1] - cPiv[1];
        float oz = cPiv == null ? org[2] - bone.z : org[2] - cPiv[2];

        BedrockCube cube;
        if (ci.getFaceUv() != null) {
            cube = new BedrockCubePerFace(ox, oy, oz, sz[0], sz[1], sz[2], infl, tw, th, ci.getFaceUv());
        } else {
            float[] uv = ci.getUv() != null ? ci.getUv() : new float[]{0, 0};
            cube = new BedrockCubeBox(uv[0], uv[1], ox, oy, oz, sz[0], sz[1], sz[2], infl, ci.isMirror(), tw, th);
        }

        if (cRot == null || cPiv == null) {
            bone.cubes.add(cube);
        } else {
            BedrockBone w = new BedrockBone();
            w.x = cPiv[0]; w.y = cPiv[1]; w.z = cPiv[2];
            w.rotation.rotateZYX(cRot[2], cRot[1], cRot[0]);
            w.cubes.add(cube);
            w.parent = bone;
            bone.addChild(w);
        }
    }

    private void convertPivots(BedrockBone node) {
        for (BedrockBone c : node.getChildren()) convertPivots(c);
        if (node.parent != null && node.parent != root) {
            node.x -= node.parent.x;
            node.y -= node.parent.y;
            node.z -= node.parent.z;
        }
    }


    public void renderToBuffer(PoseStack ps, VertexConsumer vc, int lm, int ov) {
        root.render(ps, vc, lm, ov);
    }

    public void renderToBuffer(PoseStack ps, VertexConsumer vc, int lm, int ov, float r, float g, float b, float a) {
        root.render(ps, vc, lm, ov, r, g, b, a);
    }

    public void renderToBuffer(PoseStack ps, VertexConsumer vc, int lm, int ov, float r, float g, float b, float a,
                               @Nullable NexusRenderPassInfo renderPassInfo) {
        root.render(ps, vc, lm, ov, r, g, b, a, renderPassInfo);
    }

    public @Nullable BedrockBone getBone(String name)      { return byName.get(name); }
    public @Nullable GeoLocator getLocator(String name)   { return byLocator.get(name); }
    public List<BedrockBone>     getBones()                 { return bones; }
}
package rj.nexus.systems.bedrock.particle;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import rj.nexus.systems.bedrock.particle.component.ParticleComponents;
import rj.nexus.systems.bedrock.particle.component.particle.ParticleAppearanceBillboard;
import rj.nexus.systems.bedrock.particle.io.ParticleEffectFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class NexusParticleTextures {
    private static final Map<ParticleEffectFile, Map<String, Identifier>> CACHE = new HashMap<>();
    private static final Map<ParticleEffectFile, BufferedImage> IMAGES = new HashMap<>();

    public static void register(ParticleEffectFile file, InputStream imageStream) {
        try {
            BufferedImage image = ImageIO.read(imageStream);
            if (image == null) return;
            IMAGES.put(file, image);
            CACHE.put(file, new HashMap<>());

            var billboard = file.effect.components.get(ParticleComponents.PARTICLE_APPEARANCE_BILLBOARD);
            if (billboard != null && billboard.uv != null && billboard.uv.flipbook != null) {
                prebakeFlipbook(file, image, billboard);
            }
        } catch (Exception ignored) {}
    }

    private static void prebakeFlipbook(ParticleEffectFile file, BufferedImage image,
                                        ParticleAppearanceBillboard billboard) {
        var flip = billboard.uv.flipbook;
        int maxFrame;
        try { maxFrame = Math.max(1, (int) flip.max_frame.getConstant()); }
        catch (Exception e) { maxFrame = 16; }

        float xs = (float) image.getWidth()  / billboard.uv.textureWidth;
        float ys = (float) image.getHeight() / billboard.uv.textureHeight;

        for (int rndI = 0; rndI < 10; rndI++) {
            float baseU, baseV;
            try {
                float fakeRnd = rndI * 0.1f;
                var rt = gg.moonflower.molangcompiler.api.MolangRuntime.runtime()
                        .setVariable("particle_random_1", fakeRnd)
                        .setVariable("particle_random_2", fakeRnd)
                        .setVariable("particle_random_3", fakeRnd)
                        .setVariable("particle_random_4", fakeRnd)
                        .create();
                baseU = rt.resolve(flip.base_UV[0]);
                baseV = rt.resolve(flip.base_UV[1]);
            } catch (Exception e) { baseU = 0; baseV = 0; }

            for (int f = 0; f < maxFrame; f++) {
                float su = (baseU + f * flip.step_UV[0]) * xs;
                float sv = (baseV + f * flip.step_UV[1]) * ys;
                int sw = Math.max(1, (int)(flip.size_UV[0] * xs));
                int sh = Math.max(1, (int)(flip.size_UV[1] * ys));
                int ix = Math.max(0, Math.min((int) su, image.getWidth()  - sw));
                int iy = Math.max(0, Math.min((int) sv, image.getHeight() - sh));
                String key = rndI + "_" + f;
                uploadIfAbsent(file, key, image, ix, iy, sw, sh,
                        "particles/" + file.effect.description.identifier.getPath() + "_flip_" + rndI + "_" + f);
            }
        }
    }

    public static Identifier getOrCreateUVSlice(ParticleEffectFile file,
                                                float u, float v, float w, float h) {
        BufferedImage image = IMAGES.get(file);
        if (image == null) return fallback();
        String key = ((int) u) + "_" + ((int) v) + "_" + ((int) w) + "_" + ((int) h);
        Map<String, Identifier> map = CACHE.computeIfAbsent(file, k -> new HashMap<>());
        if (map.containsKey(key)) return map.get(key);
        int ix = Math.max(0, Math.min((int) u, image.getWidth()  - 1));
        int iy = Math.max(0, Math.min((int) v, image.getHeight() - 1));
        int iw = Math.max(1, Math.min((int) w, image.getWidth()  - ix));
        int ih = Math.max(1, Math.min((int) h, image.getHeight() - iy));
        String path = "particles/" + file.effect.description.identifier.getPath() + "_uv_" + key;
        return uploadIfAbsent(file, key, image, ix, iy, iw, ih, path);
    }

    public static Identifier getTexture(ParticleEffectFile file, int rndIndex, int frameIndex) {
        Map<String, Identifier> map = CACHE.get(file);
        if (map == null) return fallback();
        String key = rndIndex + "_" + frameIndex;
        if (map.containsKey(key)) return map.get(key);
        key = rndIndex + "_0";
        if (map.containsKey(key)) return map.get(key);
        return map.values().stream().findFirst().orElse(fallback());
    }

    public static Identifier getTexture(ParticleEffectFile file, int rndSeed) {
        return getTexture(file, Math.abs(rndSeed) % 10, 0);
    }

    private static Identifier uploadIfAbsent(ParticleEffectFile file, String key,
                                             BufferedImage src, int x, int y, int w, int h,
                                             String registryPath) {
        Map<String, Identifier> map = CACHE.computeIfAbsent(file, k -> new HashMap<>());
        if (map.containsKey(key)) return map.get(key);
        NativeImage ni = new NativeImage(w, h, false);
        for (int py = 0; py < h; py++) {
            for (int px = 0; px < w; px++) {
                int argb = src.getRGB(x + px, y + py);
                int a2 = (argb >> 24) & 0xFF;
                int r  = (argb >> 16) & 0xFF;
                int g  = (argb >> 8)  & 0xFF;
                int b2 =  argb        & 0xFF;
                ni.setPixel(px, py, (a2 << 24) | (b2 << 16) | (g << 8) | r);
            }
        }
        Identifier id = Identifier.fromNamespaceAndPath(NexusParticles.MOD_ID, registryPath);
        Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(() -> id.toString(), ni));
        map.put(key, id);
        return id;
    }

    private static Identifier fallback() {
        return Identifier.withDefaultNamespace("missingno");
    }
}
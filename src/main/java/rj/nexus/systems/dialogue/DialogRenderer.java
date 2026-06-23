package rj.nexus.systems.dialogue;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class DialogRenderer {
    public static final int LINE_H = 9;
    public static final int BEEP_INT = 2;

    private static List<String> lastRaw = List.of(), cachedWrap = List.of();

    public static void render(GuiGraphicsExtractor gfx) {
        DialogState npc = DialogManager.getCurrent();
        DialogState plr = DialogManager.getPlayerState();
        Font font = Minecraft.getInstance().font;
        float W = gfx.guiWidth(), H = gfx.guiHeight();

        if (plr != null) {
            plr.getVisibleLines(0.05f);
            if (plr.shouldRemove()) {
                DialogManager.clearPlayer();
                plr = null;
            }
        }
        if (npc != null) {
            npc.getVisibleLines(0.05f);
            if (npc.shouldRemove()) {
                DialogManager.clearNpc();
                npc = null;
            }
        }
        if (npc == null && plr == null) return;

        DialogState active = plr != null ? plr : npc;
        float alpha = active.fadeAlpha();
        int baseY = (int) (H - 60);

        int gradA = Math.min(255, (int) (alpha * 200));
        gfx.fillGradient(0, baseY - LINE_H * 3, (int) W, (int) H, argb(0, 0, 0, 0), argb(gradA, 0, 0, 0));

        if (alpha > 0) drawBlock(gfx, font, wrapped(active, font, W), W, baseY, alpha, active);
    }

    private static List<String> wrapped(DialogState s, Font f, float W) {
        var raw = s.getVisibleLines(0f);
        if (raw != lastRaw) {
            cachedWrap = wrap(raw, f, W * .9f);
            lastRaw = raw;
        }
        return cachedWrap;
    }

    private static void drawBlock(GuiGraphicsExtractor gfx, Font font, List<String> lines,
                                  float W, int baseY, float alpha, DialogState state) {
        int a = Math.min(255, (int) (alpha * 255));
        if (a <= 0 || lines.isEmpty()) return;
        int defCol = "player".equals(state.category) ? 0x6AAFE6 : 0xFFFFFF;
        int textCol = (a << 24) | ((state.overrideColor != null ? state.overrideColor : defCol) & 0xFFFFFF);

        int currentY = baseY;
        int start = Math.max(0, lines.size() - 3);
        for (int i = lines.size() - 1; i >= start; i--) {
            String line = lines.get(i);
            int lx = (int) (W / 2f - font.width(line) / 2f);
            double bgOp = Minecraft.getInstance().options.textBackgroundOpacity().get();
            if (bgOp > 0)
                gfx.fill(lx - 2, currentY - 2, lx + font.width(line) + 2, currentY + LINE_H + 2, argb((int) (alpha * bgOp * 255), 0, 0, 0));
            gfx.text(font, line, lx, currentY, textCol, false);
            currentY -= (LINE_H + 4);
        }
    }

    private static List<String> wrap(List<String> lines, Font font, float max) {
        List<String> r = new ArrayList<>();
        for (String line : lines) {
            if (line.isEmpty()) {
                r.add("");
                continue;
            }
            StringBuilder cur = new StringBuilder();
            for (String word : line.split(" ", -1)) {
                if (cur.isEmpty()) cur.append(word);
                else {
                    String c = cur + " " + word;
                    if (font.width(c) > max) {
                        r.add(cur.toString());
                        cur = new StringBuilder(word);
                    } else cur.append(" ").append(word);
                }
            }
            if (!cur.isEmpty()) r.add(cur.toString());
        }
        return r;
    }

    private static int argb(int a, int r, int g, int b) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
}

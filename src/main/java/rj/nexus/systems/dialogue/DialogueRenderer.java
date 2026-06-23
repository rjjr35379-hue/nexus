package rj.nexus.systems.dialogue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class DialogueRenderer {
    private static final int BOX_COLOR = 0x88000000;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int SPEAKER_COLOR = 0xFFFFDD44;
    private static final int PADDING = 6;
    private static final int MAX_WIDTH = 240;

    public static void render(GuiGraphicsExtractor gfx) {
        if (!DialogueManager.INSTANCE.isActive()) return;
        Dialogue d = DialogueManager.INSTANCE.getCurrent();
        float alpha = DialogueManager.INSTANCE.getAlpha();
        if (alpha <= 0f) return;
        Font font = Minecraft.getInstance().font;
        int sw = gfx.guiWidth(), sh = gfx.guiHeight(), a = (int) (alpha * 255);
        var lines = font.split(Component.literal(d.text()), MAX_WIDTH);
        boolean hasSpeaker = !d.speaker().isEmpty();
        int textH = lines.size() * (font.lineHeight + 1);
        int speakerH = hasSpeaker ? font.lineHeight + 3 : 0;
        int boxH = PADDING + speakerH + textH + PADDING, boxW = MAX_WIDTH + PADDING * 2;
        int bx = (sw - boxW) / 2, by = sh - 80 - boxH;
        gfx.fill(bx, by, bx + boxW, by + boxH, (a << 24) | (BOX_COLOR & 0x00FFFFFF));
        gfx.outline(bx, by, boxW, boxH, (a << 24) | 0x00FFFFFF);
        int ty = by + PADDING;
        if (hasSpeaker) {
            gfx.text(font, d.speaker(), bx + PADDING, ty, (a << 24) | (SPEAKER_COLOR & 0x00FFFFFF), true);
            ty += font.lineHeight + 3;
        }
        for (var line : lines) {
            gfx.text(font, line, bx + PADDING, ty, (a << 24) | (TEXT_COLOR & 0x00FFFFFF), false);
            ty += font.lineHeight + 1;
        }
    }
}

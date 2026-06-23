package rj.nexus.systems.dialogue;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DialogContainer {
    private static final int PAUSE_MULT = 4;
    private final List<String> segments;
    private final int totalLength;
    public boolean flagDone = false;

    public DialogContainer(String raw) {
        segments = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        int i = 0;
        while (i < raw.length()) {
            char c = raw.charAt(i);
            if (c == '/') {
                if (cur.length() > 0) {
                    segments.add(cur.toString());
                    cur.setLength(0);
                }
                int n = 0;
                while (i < raw.length() && raw.charAt(i) == '/') {
                    n++;
                    i++;
                }
                segments.add("/".repeat(n * PAUSE_MULT));
            } else if (raw.startsWith("&player", i)) {
                if (cur.length() > 0) {
                    segments.add(cur.toString());
                    cur.setLength(0);
                }
                segments.add("&player");
                i += 7;
            } else {
                cur.append(c);
                i++;
            }
        }
        if (cur.length() > 0) segments.add(cur.toString());
        int len = 0;
        for (String s : segments) len += segLen(s);
        this.totalLength = len;
    }

    private int segLen(String s) {
        if ("&player".equals(s)) return Minecraft.getInstance().getUser().getName().length();
        return s.length();
    }

    public int getTotalLength() {
        return totalLength;
    }

    public boolean isInPause(int progress) {
        int pos = 0;
        for (String s : segments) {
            int len = segLen(s);
            if (progress >= pos && progress < pos + len) return s.contains("/");
            pos += len;
        }
        return false;
    }

    public List<String> constructText(int progress) {
        String name = Minecraft.getInstance().getUser().getName();
        StringBuilder out = new StringBuilder();
        int pos = 0;
        for (String s : segments) {
            if (pos >= progress) break;
            String eff = "&player".equals(s) ? name : s;
            int len = eff.length();
            if (!eff.contains("/")) out.append(eff, 0, Math.min(progress - pos, len));
            pos += len;
        }
        if (progress >= totalLength) flagDone = true;
        List<String> lines = new ArrayList<>();
        Collections.addAll(lines, out.toString().split("\n", -1));
        return lines;
    }
}

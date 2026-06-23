package rj.nexus.systems.dialogue;

public record Dialogue(String text, String speaker, float lifetime) {
    public static Dialogue of(String text) {
        return new Dialogue(text, "", 80f);
    }

    public static Dialogue of(String text, String speaker, float lifetime) {
        return new Dialogue(text, speaker, lifetime);
    }
}

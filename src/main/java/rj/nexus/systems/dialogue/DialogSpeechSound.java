package rj.nexus.systems.dialogue;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class DialogSpeechSound extends AbstractTickableSoundInstance {
    public DialogSpeechSound(SoundEvent event) {
        super(event, SoundSource.VOICE, RandomSource.create());
        looping = true;
        delay = 0;
        volume = 0.4f;
        pitch = 1f;
        relative = true;
        attenuation = Attenuation.NONE;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        DialogState s = DialogManager.getCurrent();
        if (s == null || s.finished || s.isFrozen()) stop();
    }
}

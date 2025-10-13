package net.sphen.magicmodbuns.spells.actions;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public class PlaySoundAction {

    /**
     * @param level     The level to play sound in.
     * @param pos       The position to play sound at.
     * @param sound     The sound to play.
     * @param source    The source of sound.
     * @param volume    The volume modifier.
     * @param pitch     The pitch modifier.
     */
    public void execute(Level level, BlockPos pos, SoundEvent sound, SoundSource source, float volume, float pitch) {
        level.playSound(null, pos, sound, source, volume, pitch);
    }
}

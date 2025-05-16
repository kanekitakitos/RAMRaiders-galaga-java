package core.objectsInterface;

import javax.sound.sampled.Clip;

/**
 * Interface for managing sound effects in the game.
 * Provides methods to play, loop, stop, and manage audio clips.
 *
 *
 * @author Brandon Mejia
 *
 * @version 2025-05-14
 */
public interface ISoundEffects {
    /**
     * Plays a loaded sound once.
     *
     * @param soundName The logical name of the sound to be played.
     */
    void playSound(String soundName);

    /**
     * Adds a sound clip to the sound effects manager.
     *
     * @param soundName The logical name of the sound.
     * @param clip      The audio clip to be associated with the sound name.
     */
    void addSound(String soundName, Clip clip);

    /**
     * Plays a loaded sound in a continuous loop.
     *
     * @param soundName The logical name of the sound to be looped.
     */
    void loopSound(String soundName);

    /**
     * Stops the playback of a specific sound.
     *
     * @param soundName The logical name of the sound to be stopped.
     */
    void stopSound(String soundName);

    /**
     * Stops the playback of all currently playing sounds.
     */
    void stopAllSounds();

    /**
     * Checks if a specific sound is currently playing.
     *
     * @param soundName The logical name of the sound.
     * @return true if the sound is playing, false otherwise.
     */
    boolean isPlaying(String soundName);

    /**
     * Sets the volume for a specific sound.
     *
     * @param soundName The logical name of the sound.
     * @param volume    The volume level from 0.0 (mute) to 1.0 (maximum).
     */
    void setVolume(String soundName, float volume);

    /**
     * Sets the global volume for all sounds.
     *
     * @param volume The volume level from 0.0 (mute) to 1.0 (maximum).
     */
    void setGlobalVolume(float volume);

    /**
     * Releases the resources used by the audio clips.
     * Should be called when the sound system is no longer needed.
     */
    void dispose();
}
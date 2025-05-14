package core;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import core.objectsInterface.ISoundEffects;
import java.util.HashMap;
import java.util.Map;

/**
 * The SoundEffects class manages sound effects for a game, allowing for playback, looping,
 * volume control, and resource cleanup. It provides functionality to handle individual
 * sound clips and global volume settings.
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>
 * SoundEffects soundEffects = new SoundEffects();
 * soundEffects.addSound("explosion", explosionClip);
 * soundEffects.playSound("explosion");
 * soundEffects.setGlobalVolume(0.5f);
 * </pre>
 *
 * @preConditions:
 *                 - Sound clips must be valid and not null when added.
 *                 - Volume levels must be within the range [0.0, 1.0].
 *
 * @postConditions:
 *                  - Sound clips are stored and can be played, looped, or stopped.
 *                  - Global volume affects all sound clips.
 *                  - Resources are released when dispose() is called.
 *
 * @see <a href="https://docs.oracle.com/javase/7/docs/api/javax/sound/sampled/Clip.html">Clip API</a>
 * @see <a href="https://docs.oracle.com/javase/7/docs/api/javax/sound/sampled/FloatControl.html">FloatControl API</a>
 *
 * @author
 * @version 2025-03-25
 */
public class SoundEffects implements ISoundEffects
{
    private Map<String, Clip> soundClips = new HashMap<>();
    private float globalVolume = 1.0f; // Default global volume (0.0 to 1.0)

    /**
     * Default constructor for SoundEffects.
     */
    public SoundEffects() {}

    /**
     * Constructs a SoundEffects object with a predefined map of sound clips.
     *
     * @param soundClips A map of sound names to their corresponding Clip objects.
     */
    public SoundEffects(Map<String, Clip> soundClips)
    {
        this.soundClips = soundClips;
    }

    /**
     * Adds a sound clip to the sound effects manager.
     *
     * @param soundName The name of the sound.
     * @param clip      The Clip object representing the sound.
     */
    @Override
    public void addSound(String soundName, Clip clip)
    {
        if (soundName == null || soundName.trim().isEmpty())
        {
            System.err.println("SoundEffects: Invalid sound name provided.");
            return;
        }
        if (clip == null)
        {
            System.err.println("SoundEffects: Null clip provided for '" + soundName + "'.");
            return;
        }

        if (this.soundClips.containsKey(soundName))
        {
            System.out.println("SoundEffects: Sound '" + soundName + "' already exists. Replacing with new clip.");
            Clip oldClip = this.soundClips.get(soundName);
            if (oldClip.isOpen())
            {
                oldClip.close();
            }
        }

        soundClips.put(soundName, clip);
        setClipVolume(clip, this.globalVolume); // Apply global volume to the newly added sound
    }

    /**
     * Plays a sound clip by its name.
     *
     * @param soundName The name of the sound to play.
     */
    @Override
    public void playSound(String soundName)
    {
        Clip clip = soundClips.get(soundName);
        if (clip != null)
        {
            if (clip.isRunning())
            {
                clip.stop(); // Stop the sound if already playing to restart from the beginning
            }
            clip.setFramePosition(0); // Rewind to the beginning
            clip.start();
        }
        else
        {
            System.err.println("SoundEffects: Sound '" + soundName + "' not found.");
        }
    }

    /**
     * Loops a sound clip continuously by its name.
     *
     * @param soundName The name of the sound to loop.
     */
    @Override
    public void loopSound(String soundName)
    {
        Clip clip = soundClips.get(soundName);
        if (clip != null)
        {
            if (!clip.isRunning())
            {
                clip.setFramePosition(0);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
        else
        {
            System.err.println("SoundEffects: Sound '" + soundName + "' not found for looping.");
        }
    }

    /**
     * Stops a sound clip by its name.
     *
     * @param soundName The name of the sound to stop.
     */
    @Override
    public void stopSound(String soundName)
    {
        Clip clip = soundClips.get(soundName);
        if (clip != null && clip.isRunning())
        {
            clip.stop();
        }
        else if (clip == null)
        {
            System.err.println("SoundEffects: Sound '" + soundName + "' not found to stop.");
        }
    }

    /**
     * Stops all currently playing sound clips.
     */
    @Override
    public void stopAllSounds()
    {
        for (Clip clip : soundClips.values())
        {
            if (clip.isRunning())
            {
                clip.stop();
            }
        }
    }

    /**
     * Checks if a specific sound is currently playing.
     *
     * @param soundName The name of the sound to check.
     * @return True if the sound is playing, false otherwise.
     */
    @Override
    public boolean isPlaying(String soundName)
    {
        Clip clip = soundClips.get(soundName);
        if (clip != null)
        {
            return clip.isRunning();
        }
        System.err.println("SoundEffects: Sound '" + soundName + "' not found to check playing status.");
        return false;
    }

    /**
     * Sets the volume for a specific sound clip.
     *
     * @param soundName The name of the sound.
     * @param volume    The volume level (0.0 to 1.0).
     */
    @Override
    public void setVolume(String soundName, float volume)
    {
        Clip clip = soundClips.get(soundName);
        if (clip != null)
        {
            setClipVolume(clip, volume * this.globalVolume);
        }
        else
        {
            System.err.println("SoundEffects: Sound '" + soundName + "' not found to set volume.");
        }
    }

    /**
     * Sets the global volume for all sound clips.
     *
     * @param volume The global volume level (0.0 to 1.0).
     */
    @Override
    public void setGlobalVolume(float volume)
    {
        this.globalVolume = Math.max(0.0f, Math.min(1.0f, volume));
        for (String soundName : soundClips.keySet())
        {
            Clip clip = soundClips.get(soundName);
            if (clip != null)
            {
                setClipVolume(clip, this.globalVolume);
            }
        }
    }

    /**
     * Releases all resources associated with the sound effects manager.
     */
    @Override
    public void dispose()
    {
        stopAllSounds();
        for (Clip clip : soundClips.values())
        {
            if (clip.isOpen())
            {
                clip.close();
            }
        }
        soundClips.clear();
        System.out.println("SoundEffects: Resources released.");
    }

    /**
     * Sets the volume for a specific Clip object.
     *
     * @param clip        The Clip object.
     * @param volumeLevel The volume level (0.0 to 1.0).
     */
    private void setClipVolume(Clip clip, float volumeLevel)
    {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN))
        {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeLevel = Math.max(0.0f, Math.min(1.0f, volumeLevel));

            float dB;
            if (volumeLevel == 0.0f)
            {
                dB = gainControl.getMinimum();
            }
            else
            {
                dB = (float) (Math.log10(volumeLevel) * 20.0);
            }

            dB = Math.max(gainControl.getMinimum(), Math.min(dB, gainControl.getMaximum()));
            gainControl.setValue(dB);
        }
    }
}
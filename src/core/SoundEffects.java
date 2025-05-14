package core;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import core.objectsInterface.ISoundEffects;

// import assets.AudioLoader; // Remover esta importação

import java.util.HashMap;
import java.util.Map;

public class SoundEffects implements ISoundEffects
{

    private Map<String, Clip> soundClips =new HashMap<>();
    private float globalVolume = 1.0f; // Volume global padrão (0.0 a 1.0)


    public SoundEffects()
    {
    }

    public SoundEffects(Map<String, Clip> soundClips)
    {
        this.soundClips = soundClips;
    }

    @Override
    public void addSound(String soundName, Clip clip)
    {
        if (soundName == null || soundName.trim().isEmpty())
        {
            System.err.println("SoundEffects: Nome do som inválido para adicionar.");
            return;
        }
        if (clip == null) {
            System.err.println("SoundEffects: Clip de som nulo fornecido para '" + soundName + "'.");
            return;
        }
    
        if (this.soundClips.containsKey(soundName))
        {
            System.out.println("SoundEffects: Som '" + soundName + "' já existe. Substituindo pelo novo clip.");
            Clip oldClip = this.soundClips.get(soundName);
            if (oldClip.isOpen()) {
                oldClip.close();
            }
        }

        soundClips.put(soundName, clip);
        setClipVolume(clip, this.globalVolume); // Aplica o volume global ao som recém-adicionado
    }

    @Override
    public void playSound(String soundName)
    {
        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            if (clip.isRunning())
            {
                clip.stop(); // Para o som se já estiver tocando para reiniciar do início
            }
            clip.setFramePosition(0); // Rebobina para o início
            clip.start();
        } else {
            System.err.println("SoundEffects: Som '" + soundName + "' não encontrado para tocar.");
        }
    }

    @Override
    public void loopSound(String soundName)
    {
        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            if (!clip.isRunning()) { // Só inicia o loop se não estiver tocando
                clip.setFramePosition(0);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } else {
            System.err.println("SoundEffects: Som '" + soundName + "' não encontrado para loop.");
        }
    }

    @Override
    public void stopSound(String soundName) {
        Clip clip = soundClips.get(soundName);
        if (clip != null && clip.isRunning())
        {
            clip.stop();
        } else if (clip == null) {
            System.err.println("SoundEffects: Som '" + soundName + "' não encontrado para parar.");
        }
        // Se o clipe não for nulo mas não estiver tocando, não faz nada.
    }

    @Override
    public void stopAllSounds()
    {
        for (Clip clip : soundClips.values())
        {
            if (clip.isRunning()) {
                clip.stop();
            }
        }
    }

    @Override
    public boolean isPlaying(String soundName)
    {
        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            return clip.isRunning();
        }
        System.err.println("SoundEffects: Som '" + soundName + "' não encontrado para verificar se está tocando.");
        return false;
    }

    private void setClipVolume(Clip clip, float volumeLevel)
    {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN))
        {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeLevel = Math.max(0.0f, Math.min(1.0f, volumeLevel)); // Garante que volumeLevel está entre 0.0 e 1.0

            float dB;
            if (volumeLevel == 0.0f) {
                dB = gainControl.getMinimum(); // Mudo
            } else {
                // Converte volume linear (0-1) para dB.
                // Math.log10(0) é indefinido, então tratamos volumeLevel == 0.0f separadamente.
                dB = (float) (Math.log10(volumeLevel) * 20.0);
            }
            
            // Garante que o valor de dB está dentro do intervalo permitido pelo controle
            dB = Math.max(gainControl.getMinimum(), Math.min(dB, gainControl.getMaximum()));
            gainControl.setValue(dB);
        }
    }

    @Override
    public void setVolume(String soundName, float volume) {
        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            // O volume individual é modulado pelo volume global.
            // Se você quiser que o volume individual substitua o global, apenas use 'volume'.
            setClipVolume(clip, volume * this.globalVolume); 
        } else {
            System.err.println("SoundEffects: Som '" + soundName + "' não encontrado para definir volume.");
        }
    }

    @Override
    public void setGlobalVolume(float volume) {
        this.globalVolume = Math.max(0.0f, Math.min(1.0f, volume)); // Garante que o volume global está entre 0.0 e 1.0
        // Atualiza o volume para todos os clipes carregados
        for (String soundName : soundClips.keySet()) {
            // Para manter volumes individuais, você precisaria armazená-los separadamente
            // e recalculá-los aqui. Para simplificar, estamos reaplicando o volume global
            // (ou uma combinação, se setVolume individual foi chamado antes).
            // Se setVolume individual foi chamado, o clipe já tem um volume.
            // Reaplicar o global pode ser feito de forma mais inteligente.
            // Por ora, vamos assumir que setGlobalVolume ajusta todos os sons para este nível global.
            Clip clip = soundClips.get(soundName);
            if(clip != null) {
                 // Se você quiser que o volume individual seja preservado e apenas modulado pelo global:
                 // float individualVolume = getIndividualVolumeFor(soundName); // precisaria de um mapa para isso
                 // setClipVolume(clip, individualVolume * this.globalVolume);
                 // Por simplicidade, vamos apenas aplicar o novo global diretamente:
                 setClipVolume(clip, this.globalVolume);
            }
        }
    }
    
    @Override
    public void dispose() {
        stopAllSounds();
        for (Clip clip : soundClips.values()) {
            if (clip.isOpen()) {
                clip.close();
            }
        }
        soundClips.clear();
        System.out.println("SoundEffects: Recursos liberados.");
    }
}

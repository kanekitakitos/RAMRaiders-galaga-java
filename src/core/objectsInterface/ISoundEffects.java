package core.objectsInterface;
import javax.sound.sampled.Clip;

public interface ISoundEffects {
    /**
     * Toca um som carregado uma vez.
     * @param soundName O nome lógico do som a ser tocado.
     */
    void playSound(String soundName);

    void addSound(String soundName, Clip clip);
    /**
     * Toca um som carregado em loop contínuo.
     * @param soundName O nome lógico do som a ser tocado em loop.
     */
    void loopSound(String soundName);

    /**
     * Para a reprodução de um som específico.
     * @param soundName O nome lógico do som a ser parado.
     */
    void stopSound(String soundName);

    /**
     * Para a reprodução de todos os sons que estão tocando.
     */
    void stopAllSounds();

    /**
     * Verifica se um som específico está tocando atualmente.
     * @param soundName O nome lógico do som.
     * @return true se o som estiver tocando, false caso contrário.
     */
    boolean isPlaying(String soundName);

    /**
     * Define o volume para um som específico.
     * @param soundName O nome lógico do som.
     * @param volume Nível de volume de 0.0 (mudo) a 1.0 (máximo).
     */
    void setVolume(String soundName, float volume);

    /**
     * Define o volume global para todos os sons.
     * @param volume Nível de volume de 0.0 (mudo) a 1.0 (máximo).
     */
    void setGlobalVolume(float volume);

    /**
     * Libera os recursos utilizados pelos clipes de áudio.
     * Deve ser chamado quando o sistema de som não for mais necessário.
     */
    void dispose();
}
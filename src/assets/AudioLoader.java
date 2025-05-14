package assets;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Classe utilitária para carregar recursos de áudio do jogo.
 * Fornece métodos estáticos para carregar arquivos de áudio utilizando
 * os formatos suportados nativamente pela API Java Sound.
 *
 * <p>Exemplo de uso:</p>
 * <pre>
 *     Clip somExplosao = AudioLoader.loadAudio("explosao.wav");
 *     if (somExplosao != null) {
 *         somExplosao.start();
 *     }
 *     Clip musicaFundo = AudioLoader.loadAudio("musica_tema.wav");
 *     if (musicaFundo != null) {
 *         musicaFundo.loop(Clip.LOOP_CONTINUOUSLY);
 *     }
 * </pre>
 *
 * @preConditions:
 * - Os arquivos de áudio devem estar em formatos nativamente suportados pelo Java Sound API
 *   (ex: .wav, .au, .aiff com codificação PCM).
 * - Os arquivos de áudio devem existir no diretório `/assets/audio/`.
 * - Os nomes dos arquivos devem ser válidos e incluir as extensões de arquivo (por exemplo, .wav).
 *
 * @postConditions:
 * - Arquivos de áudio carregados com sucesso serão retornados como objetos Clip.
 * - Falhas no carregamento (ex: arquivo não encontrado, formato não suportado)
 *   retornarão null.
 * - Todos os recursos serão devidamente tratados.
 *
 * @author Brandon Mejia
 * @version 2024-05-23
 */
public class AudioLoader {

    /**
     * Carrega um único arquivo de áudio do diretório de áudio dos assets.
     * Suporta formatos nativos da API Java Sound como .wav, .au, e .aiff
     * com codificações PCM.
     *
     * @param fileName O nome do arquivo de áudio a ser carregado (ex: "som.wav").
     * @return O áudio carregado como um objeto Clip, ou null se o carregamento falhar.
     */
    public static Clip loadAudio(String fileName)
    {
        try {
            // É importante usar um BufferedInputStream para que o AudioSystem possa marcar/resetar o stream
            InputStream audioSrc = AudioLoader.class.getResourceAsStream("/assets/audio/" + fileName);
            if (audioSrc == null) {
                System.err.println("Arquivo de áudio não encontrado no caminho: /assets/audio/" + fileName);
                return null;
            }

            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            // AudioSystem.getAudioInputStream tentará decodificar o arquivo de áudio
            // usando os decodificadores nativos do Java Sound.
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
            return audioClip;

        }
        catch
        (UnsupportedAudioFileException e)
        {
            System.err.println("Formato de arquivo de áudio não suportado ou arquivo corrompido: " + fileName);
            e.printStackTrace();
        }
        catch (IOException e)
        {
            System.err.println("Erro de I/O ao carregar áudio: " + fileName);
            e.printStackTrace();
        }
        catch (LineUnavailableException e)
        {
            System.err.println("Linha de áudio não disponível: " + fileName);
            e.printStackTrace();
        }
        return null;
    }
}

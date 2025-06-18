import java.util.List;

public class ConfigJogo {
    private int vidaInicialJogador;
    private int numeroDeFases;
    private List<String> arquivosFase;

    public ConfigJogo(int vidaInicialJogador, int numeroDeFases, List<String> arquivosFase) {
        this.vidaInicialJogador = vidaInicialJogador;
        this.numeroDeFases = numeroDeFases;
        this.arquivosFase = arquivosFase;
    }

    public int getVidaInicialJogador() {
        return vidaInicialJogador;
    }

    public int getNumeroDeFases() {
        return numeroDeFases;
    }

    public List<String> getArquivosFase() {
        return arquivosFase;
    }
}
package Modules.Boss;

import Modules.Enum.EstadosEnum;
import Modules.Others.Projetil;

import java.util.List;

public interface BossInterface {
    /* Interface responsavel pelos atributos de um Boss */

    boolean verificaColisaoComProjetil(Projetil projetil, long currentTime);
    void setLancado(boolean lancado);
    int getVida();
    boolean isDerrotado();
    EstadosEnum getState();
    void update(long currentTime, List<Projetil> e_projetils);
    void desenhaBoss(long currentTime);
}
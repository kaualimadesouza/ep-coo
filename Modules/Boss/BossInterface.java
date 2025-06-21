package Modules.Boss;

import Modules.Enum.EstadosEnum;
import Modules.Others.Projetil;

import java.util.List;

public interface BossInterface {
    boolean verificaColisaoComProjetil(Projetil projetil, long currentTime);
    boolean isLancado();
    void setLancado(boolean lancado);
    int getVida();
    void setVida(int vida);
    long getNextShoot();
    void setNextShoot(long nextShoot);
    boolean estaDerrotado();
    EstadosEnum getState();
    void update(long currentTime, List<Projetil> e_projetils);
    void desenhaBoss(long currentTime);
}
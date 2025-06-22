package Modules.PowerUps;

import Modules.Enum.EstadosEnum;

import java.util.List;

public interface PowerupInterface {
    void atualizaEstadoPowerUp(long delta);
    void desenhaPowerUp();
    double getX();
    double getY();
    double getRadius();
    boolean isEfeitoAplicado();
    double getAumentarStatus();
    void setExpiracao(long l);
    void setEfeitoAplicado(boolean b);
    void setState(EstadosEnum estadosEnum);
}

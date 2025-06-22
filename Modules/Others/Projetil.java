package Modules.Others;

import GameLib.GameLib;
import Modules.Enum.EstadosEnum;

import java.awt.*;

public class Projetil extends Entidade{
    public Projetil(EstadosEnum state, double x, double y, double VX, double VY, double radius) {
        super(state, x, y, VX, VY, radius);
    }

    public static Projetil criaProjetilVazio(double radius) {
        return new Projetil(EstadosEnum.INACTIVE, 0.0, 0.0, 0.0, 0.0, radius);
    }

    public void desenhaProjetilPlayer() {
        if(this.getState() == EstadosEnum.ACTIVE){

            GameLib.setColor(Color.GREEN);
            GameLib.drawLine(this.getX(), this.getY() - 5, this.getX(), this.getY() + 5);
            GameLib.drawLine(this.getX() - 1, this.getY() - 3, this.getX() - 1, this.getY() + 3);
            GameLib.drawLine(this.getX() + 1, this.getY() - 3, this.getX() + 1, this.getY() + 3);
        }
    }

    public void desenhaProjetilInimigo() {
        if(this.getState() == EstadosEnum.ACTIVE){

            GameLib.setColor(Color.RED);
            GameLib.drawCircle(this.getX(), this.getY(), this.getRadius());
        }
    }

    public void atualizaEstadoProjetilInimigo(long delta) {

        if(this.getState() == EstadosEnum.ACTIVE){

            /* verificando se projétil saiu da tela */
            if(this.getY() > GameLib.HEIGHT) {

                this.setState(EstadosEnum.INACTIVE);
            }
            else {

                this.setX(this.getVX() * delta + this.getX());
                this.setY(this.getVY() * delta + this.getY()) ;
            }
        }
    }

    public void atualizaEstadoProjetilPlayer(long delta) {
        if(this.getState() == EstadosEnum.ACTIVE){

            /* verificando se projétil saiu da tela */
            if(this.getY() < 0) {

                this.setState(EstadosEnum.INACTIVE);
            }
            else {

                this.setX(this.getX() + this.getVX() * delta);
                this.setY(this.getY() + this.getVY() * delta);
            }
        }
    }
}

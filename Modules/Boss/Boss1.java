package Modules.Boss;

import GameLib.GameLib;
import Modules.Enemy.EnemyBase;
import Modules.Enum.EstadosEnum;
import Modules.Others.Projetil;

import java.awt.*;
import java.util.List;

public class Boss1 extends EnemyBase implements BossInterface {

    private long nextShoot;
    private int vida;
    private boolean isLancado = false;
    private int tipoTiro = 0;

    public Boss1(EstadosEnum state, double x, double y, double v, double angle, double RV,
                 double explosionStart, double explosionEnd, double radius,
                 long currentTime, int vidaInicial) {
        super(state, x, y, v, angle, RV, explosionStart, explosionEnd, radius);
        this.nextShoot = currentTime;
        this.vida = vidaInicial;
    }

    public static Boss1 criaBoss(long currentTime, double x, double y, int vida) {
        return new Boss1(
                EstadosEnum.ACTIVE,
                x,
                y,
                0.2,
                0.0,
                0.0,
                0.0,
                0.0,
                25,
                currentTime + 2000,
                vida
        );
    }

    @Override
    public boolean verificaColisaoComProjetil(Projetil projetil, long currentTime) {
        if (this.getState() != EstadosEnum.ACTIVE || projetil.getState() != EstadosEnum.ACTIVE) return false;

        boolean colidiu =
                colisaoComCirculo(getX(), getY(), projetil) ||
                        colisaoComCirculo(getX() + 35, getY(), projetil) ||
                        colisaoComCirculo(getX() - 35, getY(), projetil) ||
                        colisaoComCirculo(getX(), getY() + 35, projetil) ||
                        colisaoComCirculo(getX(), getY() - 35, projetil);

        if (colidiu) {
            projetil.setState(EstadosEnum.INACTIVE);
            this.vida--;

            if (this.vida < 1) {
                this.setState(EstadosEnum.EXPLODING);
                this.setExplosionStart(currentTime);
                this.setExplosionEnd(currentTime + 500);
            }
        }

        return colidiu;
    }

    private boolean colisaoComCirculo(double cx, double cy, Projetil p) {
        double dx = cx - p.getX();
        double dy = cy - p.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < (this.getRadius() + p.getRadius());
    }

    public void update(long currentTime, List<Projetil> e_projetils) {
        if (this.getState() == EstadosEnum.EXPLODING && currentTime > this.getExplosionEnd()) {
            this.setState(EstadosEnum.INACTIVE);
        }

        if (this.getState() == EstadosEnum.ACTIVE) {
            // Movimento lateral oscilante
            double novaX = this.getX() + Math.cos(currentTime * 0.001) * 2.5;
            novaX = Math.max(this.getRadius(), Math.min(GameLib.WIDTH - this.getRadius(), novaX));
            this.setX(novaX);

            if (currentTime > this.nextShoot) {
                int freeCount = 0;
                for (Projetil p : e_projetils) {
                    if (p.getState() == EstadosEnum.INACTIVE) freeCount++;
                }

                switch (tipoTiro) {
                    case 0:  // Tiro circular normal
                        int tiros = Math.min(12, freeCount);
                        for (int i = 0, usados = 0; i < e_projetils.size() && usados < tiros; i++) {
                            Projetil p = e_projetils.get(i);
                            if (p.getState() == EstadosEnum.INACTIVE) {
                                double angle = 2 * Math.PI * usados / tiros;
                                p.setX(this.getX());
                                p.setY(this.getY());
                                p.setVX(Math.cos(angle) * 0.3);
                                p.setVY(Math.sin(angle) * 0.3);
                                p.setState(EstadosEnum.ACTIVE);
                                usados++;
                            }
                        }
                        break;

                    case 1: // Tiro espiral
                        int espiralTiros = Math.min(15, freeCount);
                        double t = currentTime * 0.01;
                        for (int i = 0, usados = 0; i < e_projetils.size() && usados < espiralTiros; i++) {
                            Projetil p = e_projetils.get(i);
                            if (p.getState() == EstadosEnum.INACTIVE) {
                                // Ângulo varia com o índice e o tempo para dar efeito espiral
                                double angle = 2 * Math.PI * usados / espiralTiros + t;
                                p.setX(this.getX());
                                p.setY(this.getY());
                                p.setVX(Math.cos(angle) * 0.25);
                                p.setVY(Math.sin(angle) * 0.25);
                                p.setState(EstadosEnum.ACTIVE);
                                usados++;
                            }
                        }
                        break;

                    case 2: // Rajada frontal (tiros retos para baixo em um cone)
                        int rajadaTiros = Math.min(8, freeCount);
                        double baseAngle = Math.PI / 2; // para baixo
                        double spread = Math.PI / 6;  // spread angular total (30°)
                        for (int i = 0, usados = 0; i < e_projetils.size() && usados < rajadaTiros; i++) {
                            Projetil p = e_projetils.get(i);
                            if (p.getState() == EstadosEnum.INACTIVE) {
                                // Ângulo entre baseAngle - spread/2 e baseAngle + spread/2
                                double angle = baseAngle - spread / 2 + spread * usados / (rajadaTiros - 1);
                                p.setX(this.getX());
                                p.setY(this.getY());
                                p.setVX(Math.cos(angle) * 0.35);
                                p.setVY(Math.sin(angle) * 0.35);
                                p.setState(EstadosEnum.ACTIVE);
                                usados++;
                            }
                        }
                        break;
                }

                // Alterna para o próximo tipo de tiro na próxima vez
                tipoTiro = (tipoTiro + 1) % 3;

                // Próximo tiro em 1.5 segundos
                this.nextShoot = currentTime + 1500;
            }
        }
    }

    @Override
    public void desenhaBoss(long currentTime) {
        if (this.getState() == EstadosEnum.ACTIVE) {
            double x = this.getX();
            double y = this.getY();
            double size = this.getRadius() * 2;

            // Corpo principal - cinza escuro
            GameLib.setColor(Color.DARK_GRAY);
            GameLib.fillRect(x, y, size, size);

            // Contorno grosso preto
            GameLib.setColor(Color.BLACK);
            GameLib.drawLine(x - size / 2, y - size / 2, x + size / 2, y - size / 2); // topo
            GameLib.drawLine(x + size / 2, y - size / 2, x + size / 2, y + size / 2); // direita
            GameLib.drawLine(x + size / 2, y + size / 2, x - size / 2, y + size / 2); // base
            GameLib.drawLine(x - size / 2, y + size / 2, x - size / 2, y - size / 2); // esquerda

            // Painéis laterais (retângulos mais claros)
            GameLib.setColor(Color.LIGHT_GRAY);
            double panelWidth = size * 0.2;
            GameLib.fillRect(x - size / 2 + panelWidth / 2, y, panelWidth, size * 0.8);
            GameLib.fillRect(x + size / 2 - panelWidth / 2, y, panelWidth, size * 0.8);

            // Tela frontal (retângulo azul transparente)
            GameLib.setColor(new Color(0, 128, 255, 150)); // azul semi-transparente
            GameLib.fillRect(x, y + size * 0.1, size * 0.6, size * 0.6);

            // Olhos (retângulos vermelhos piscando)
            int blink = (int)(currentTime / 500) % 2;
            GameLib.setColor(blink == 0 ? Color.RED : Color.ORANGE);
            double eyeWidth = size * 0.15;
            double eyeHeight = size * 0.1;
            GameLib.fillRect(x - size * 0.2, y - size * 0.15, eyeWidth, eyeHeight);
            GameLib.fillRect(x + size * 0.2 - eyeWidth / 2, y - size * 0.15, eyeWidth, eyeHeight);

            // Linhas horizontais para textura
            GameLib.setColor(Color.GRAY);
            for (int i = -2; i <= 2; i++) {
                double lineY = y + i * (size * 0.15);
                GameLib.drawLine(x - size / 2, lineY, x + size / 2, lineY);
            }

            // --- RAIO ELÉTRICO EM VOLTA ---

            GameLib.setColor(Color.CYAN);
            double raioMax = size * 1.2;
            int nRaios = 12;
            double t = currentTime * 0.01;

            for (int i = 0; i < nRaios; i++) {
                double angle = 2 * Math.PI * i / nRaios + t;

                // Ponto de origem perto da borda do boss
                double startX = x + Math.cos(angle) * size / 2;
                double startY = y + Math.sin(angle) * size / 2;

                // Ponto final com um pequeno "ziguezague"
                double offset = (Math.sin(t * 10 + i) * 5);
                double endX = x + Math.cos(angle) * raioMax + offset;
                double endY = y + Math.sin(angle) * raioMax + offset;

                GameLib.drawLine(startX, startY, endX, endY);
            }
        }
    }




    @Override
    public boolean isLancado() {
        return isLancado;
    }

    @Override
    public void setLancado(boolean lancado) {
        this.isLancado = lancado;
    }

    @Override
    public int getVida() {
        return vida;
    }

    @Override
    public void setVida(int vida) {
        this.vida = vida;
    }

    @Override
    public long getNextShoot() {
        return nextShoot;
    }

    @Override
    public void setNextShoot(long nextShoot) {
        this.nextShoot = nextShoot;
    }

    @Override
    public boolean estaDerrotado() {
        return this.getState() == EstadosEnum.INACTIVE;
    }
}

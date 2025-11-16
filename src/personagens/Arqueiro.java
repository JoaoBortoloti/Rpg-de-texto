package personagens;

import sistema.Dado;

public class Arqueiro extends Personagem {
    private int flechas;
    private int precisao;

    public Arqueiro(String nome, int pontosVida, int ataque, int defesa, int nivel) {
        super(nome, pontosVida, ataque, defesa, nivel);
        this.flechas = 30;
        this.precisao = 75;
    }

    public Arqueiro() {
        this("Arqueiro", 100, 12, 7, 1);
    }

    public Arqueiro(Arqueiro outro) {
        super(outro);
        this.flechas = outro.flechas;
        this.precisao = outro.precisao;
    }

    @Override
    public int calcularDano(int rolagemDado) {
        if (flechas <= 0) {
            System.out.println("Sem flechas! Usando ataque corpo a corpo...");
            return super.calcularDano(rolagemDado) / 2;
        }

        flechas--;
        int danoBase = super.calcularDano(rolagemDado);

        if (Dado.rolar(100) <= precisao) {
            System.out.println("Tiro preciso!");
            danoBase += 10;
        }

        return danoBase;
    }

    @Override
    public String usarHabilidadeEspecial(Personagem alvo) {
        if (flechas >= 3) {
            flechas -= 3;
            int r1 = sistema.Dado.rolarD6();
            int r2 = sistema.Dado.rolarD6();
            int r3 = sistema.Dado.rolarD6();
            int total = super.calcularDano(r1) + super.calcularDano(r2) + super.calcularDano(r3);
            alvo.receberDano(total);
            return String.format("Rajada de Flechas usada. Flechas: -3 | Rolagens: %d, %d, %d | Dano total: %d", r1, r2, r3, total);
        } else {
            return "Flechas insuficientes para Rajada.";
        }
    }

    public void recarregarFlechas(int quantidade) {
        flechas += quantidade;
    }

    public int getFlechas() {
        return flechas;
    }

    @Override
    public String getStatus() {
        return super.getStatus() + String.format(" | Flechas: %d", flechas);
    }
}
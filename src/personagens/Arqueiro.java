package personagens;

import sistema.Dado;

public class Arqueiro extends Personagem {
    private int flechas;
    private int precisao;

    public Arqueiro(String nome, int pontosVida, int ataque, int defesa, int nivel) {
        super(nome, pontosVida, ataque, defesa, nivel);
        this.flechas = 30;
        this.precisao = 75; // 75% de chance de acerto extra
    }

    // Construtor padrão
    public Arqueiro() {
        this("Arqueiro", 100, 12, 7, 1);
    }

    // Construtor de cópia
    public Arqueiro(Arqueiro outro) {
        super(outro);
        this.flechas = outro.flechas;
        this.precisao = outro.precisao;
    }

    @Override
    public int calcularDano(int rolagemDado) {
        if (flechas <= 0) {
            System.out.println("❌ Sem flechas! Usando ataque corpo a corpo...");
            return super.calcularDano(rolagemDado) / 2;
        }

        flechas--;
        int danoBase = super.calcularDano(rolagemDado);

        // Chance de acerto preciso
        if (Dado.rolar(100) <= precisao) {
            System.out.println("🎯 Tiro preciso!");
            danoBase += 10;
        }

        return danoBase;
    }

    @Override
    public String usarHabilidadeEspecial() {
        if (flechas >= 3) {
            flechas -= 3;
            return "🏹 Rajada de Flechas! Três flechas disparadas! (-3 flechas)";
        } else {
            return "❌ Flechas insuficientes para Rajada!";
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
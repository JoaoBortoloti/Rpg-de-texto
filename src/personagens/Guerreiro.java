package personagens;

import sistema.Dado;

public class Guerreiro extends Personagem {
    private int chanceCritico;
    private boolean furia;

    public Guerreiro(String nome, int pontosVida, int ataque, int defesa, int nivel) {
        super(nome, pontosVida, ataque, defesa, nivel);
        this.chanceCritico = 20; // 20% de chance
        this.furia = false;
    }

    // Construtor padrão
    public Guerreiro() {
        this("Guerreiro", 120, 15, 10, 1);
    }

    // Construtor de cópia
    public Guerreiro(Guerreiro outro) {
        super(outro);
        this.chanceCritico = outro.chanceCritico;
        this.furia = outro.furia;
    }

    @Override
    public int calcularDano(int rolagemDado) {
        int danoBase = super.calcularDano(rolagemDado);
        
        // Verifica crítico
        if (Dado.rolar(100) <= chanceCritico) {
            System.out.println("💥 GOLPE CRÍTICO!");
            danoBase *= 2;
        }

        // Aplica bônus de fúria
        if (furia) {
            danoBase = (int) (danoBase * 1.5);
            System.out.println("🔥 Fúria ativada! Dano aumentado!");
        }

        return danoBase;
    }

    @Override
    public String usarHabilidadeEspecial() {
        furia = true;
        setDefesa(getDefesa() - 3);
        return "⚔️ Guerreiro entra em FÚRIA! Ataque aumentado, defesa reduzida!";
    }

    public void desativarFuria() {
        if (furia) {
            furia = false;
            setDefesa(getDefesa() + 3);
        }
    }

    public boolean isFuria() {
        return furia;
    }
}
package personagens;

import sistema.Dado;

/**
 * Classe concreta de personagem do tipo Guerreiro.
 * <p>
 * Possui chance de golpe crítico e pode entrar em estado de fúria,
 * aumentando o dano às custas da defesa.
 */
public class Guerreiro extends Personagem {
    private int chanceCritico;
    private boolean furia;

    /**
     * Cria um novo Guerreiro com atributos personalizados.
     */
    public Guerreiro(String nome, int pontosVida, int ataque, int defesa, int nivel) {
        super(nome, pontosVida, ataque, defesa, nivel);
        this.chanceCritico = 20; 
        this.furia = false;
    }

    /**
     * Construtor padrão com valores iniciais predefinidos.
     */
    public Guerreiro() {
        this("Guerreiro", 120, 15, 10, 1);
    }

    /**
     * Construtor de cópia.
     */
    public Guerreiro(Guerreiro outro) {
        super(outro);
        this.chanceCritico = outro.chanceCritico;
        this.furia = outro.furia;
    }

    /**
     * Calcula o dano do Guerreiro, considerando:
     * <ul>
     *   <li>Chance de golpe crítico (dano dobrado);</li>
     *   <li>Estado de fúria (dano aumentado em 50%).</li>
     * </ul>
     */
    @Override
    public int calcularDano(int rolagemDado) {
        int danoBase = super.calcularDano(rolagemDado);
        
        if (Dado.rolar(100) <= chanceCritico) {
            System.out.println("GOLPE CRÍTICO!");
            danoBase *= 2;
        }

        if (furia) {
            danoBase = (int) (danoBase * 1.5);
            System.out.println("Fúria ativada! Dano aumentado!");
        }

        return danoBase;
    }

    /**
     * Habilidade especial: entra em fúria.
     * Aumenta o dano (via flag de estado) e reduz a defesa.
     */
    @Override
    public String usarHabilidadeEspecial(Personagem alvo) {
        furia = true;
        setDefesa(getDefesa() - 3);
        return "Guerreiro entra em fúria. Ataque aumentado e defesa reduzida.";
    }

    /**
     * Desativa o estado de fúria, restaurando a defesa perdida.
     */
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
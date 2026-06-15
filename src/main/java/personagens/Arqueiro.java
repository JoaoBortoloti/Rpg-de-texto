package personagens;

import sistema.Dado;

/**
 * Classe concreta de personagem do tipo Arqueiro.
 * <p>
 * Utiliza flechas e possui alta precisão para causar dano à distância.
 */
public class Arqueiro extends Personagem {
    private int flechas;
    private int precisao;

    /**
     * Cria um Arqueiro com atributos personalizados.
     */
    public Arqueiro(String nome, int pontosVida, int ataque, int defesa, int nivel) {
        super(nome, pontosVida, ataque, defesa, nivel);
        this.flechas = 30;
        this.precisao = 75;
    }

    /**
     * Construtor padrão com valores iniciais predefinidos.
     */
    public Arqueiro() {
        this("Arqueiro", 100, 12, 7, 1);
    }

    /**
     * Construtor de cópia.
     */
    public Arqueiro(Arqueiro outro) {
        super(outro);
        this.flechas = outro.flechas;
        this.precisao = outro.precisao;
    }

    /**
     * Calcula o dano do Arqueiro.
     * <ul>
     *   <li>Se não houver flechas, ataca corpo a corpo com dano reduzido.</li>
     *   <li>Se houver flechas, consome 1 e pode aplicar bônus de "tiro preciso".</li>
     * </ul>
     */
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

    /**
     * Habilidade especial: Rajada de Flechas.
     * <p>
     * Consome 3 flechas e realiza três ataques baseados em rolagens de d6.
     */
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

    /**
     * Recarrega o estoque de flechas.
     */
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
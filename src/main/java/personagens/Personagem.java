package personagens;

import itens.Inventario;
import interfaces.Atacavel;

/**
 * Representa um personagem genérico do jogo.
 * <p>
 * É abstrata e serve de base para classes como Guerreiro, Mago, Arqueiro e Inimigo.
 * Implementa a interface {@link Atacavel}, permitindo uso polimórfico em combate.
 */
public abstract class Personagem implements Atacavel {
    private String nome;
    private int pontosVida;
    private int pontosVidaMaximos;
    private int ataque;
    private int defesa;
    private int nivel;
    private Inventario inventario;

    /**
     * Construtor principal de Personagem.
     *
     * @param nome        nome do personagem (não pode ser vazio)
     * @param pontosVida  pontos de vida iniciais (também usados como HP máximo inicial, deve ser &gt; 0)
     * @param ataque      valor base de ataque (não negativo)
     * @param defesa      valor base de defesa (não negativo)
     * @param nivel       nível do personagem (mínimo 1)
     * @throws IllegalArgumentException se algum valor for inválido
     */
    public Personagem(String nome, int pontosVida, int ataque, int defesa, int nivel) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do personagem não pode ser vazio");
        }
        if (pontosVida <= 0) {
            throw new IllegalArgumentException("Pontos de vida devem ser positivos");
        }
        if (ataque < 0 || defesa < 0) {
            throw new IllegalArgumentException("Ataque e defesa não podem ser negativos");
               }
        if (nivel < 1) {
            throw new IllegalArgumentException("Nível deve ser pelo menos 1");
        }

        this.nome = nome;
        this.pontosVida = pontosVida;
        this.pontosVidaMaximos = pontosVida;
        this.ataque = ataque;
        this.defesa = defesa;
        this.nivel = nivel;
        this.inventario = new Inventario(20);
    }

    /**
     * Construtor de cópia. Faz cópia profunda do inventário.
     *
     * @param outro personagem a ser copiado
     */
    public Personagem(Personagem outro) {
        this.nome = outro.nome;
        this.pontosVida = outro.pontosVida;
        this.pontosVidaMaximos = outro.pontosVidaMaximos;
        this.ataque = outro.ataque;
        this.defesa = outro.defesa;
        this.nivel = outro.nivel;
        this.inventario = new Inventario(outro.inventario);
    }

    public String getNome() {
        return nome;
    }

    public int getPontosVida() {
        return pontosVida;
    }

    /**
     * Atualiza o HP atual, limitando entre 0 e HP máximo.
     *
     * @param pontosVida novo valor de HP
     */
    public void setPontosVida(int pontosVida) {
        this.pontosVida = Math.max(0, Math.min(pontosVida, pontosVidaMaximos));
    }

    public int getPontosVidaMaximos() {
        return pontosVidaMaximos;
    }

    /**
     * Altera o HP máximo, garantindo que seja positivo e
     * que o HP atual não ultrapasse o novo máximo.
     *
     * @param pontosVidaMaximos novo HP máximo (&gt; 0)
     */
    public void setPontosVidaMaximos(int pontosVidaMaximos) {
        if (pontosVidaMaximos <= 0) {
            throw new IllegalArgumentException("HP máximo deve ser positivo");
        }
        this.pontosVidaMaximos = pontosVidaMaximos;
        this.pontosVida = Math.min(this.pontosVida, this.pontosVidaMaximos);
    }

    public int getAtaque() {
        return ataque;
    }

    /**
     * Define o valor de ataque, nunca permitindo que seja negativo.
     */
    public void setAtaque(int ataque) {
        this.ataque = Math.max(0, ataque);
    }

    public int getDefesa() {
        return defesa;
    }

    /**
     * Define o valor de defesa, nunca permitindo que seja negativo.
     */
    public void setDefesa(int defesa) {
        this.defesa = Math.max(0, defesa);
    }

    public int getNivel() {
        return nivel;
    }

    /**
     * Define o nível, garantindo valor mínimo 1.
     */
    public void setNivel(int nivel) {
        this.nivel = Math.max(1, nivel);
    }

    public Inventario getInventario() {
        return inventario;
    }

    /**
     * Cálculo padrão de dano: ataque base + rolagem do dado.
     * Subclasses podem sobrescrever para adicionar efeitos especiais.
     *
     * @param rolagemDado valor da rolagem de dado
     * @return dano base causado
     */
    @Override
    public int calcularDano(int rolagemDado) {
        return ataque + rolagemDado;
    }

    /**
     * Recebe dano, considerando a defesa.
     * Dano real = max(0, dano - defesa).
     * HP nunca fica abaixo de 0.
     *
     * @param dano dano bruto recebido
     */
    @Override
    public void receberDano(int dano) {
        int danoReal = Math.max(0, dano - defesa);
        pontosVida = Math.max(0, pontosVida - danoReal);
    }

    @Override
    public boolean estaVivo() {
        return pontosVida > 0;
    }

    /**
     * Cura o personagem até o máximo de HP.
     *
     * @param quantidade quantidade a ser curada (valores menores ou iguais a 0 são ignorados)
     */
    public void curar(int quantidade) {
        if (quantidade <= 0) return;
        pontosVida = Math.min(pontosVida + quantidade, pontosVidaMaximos);
    }

    /**
     * Habilidade especial de cada classe concreta.
     *
     * @param alvo alvo da habilidade (pode ser inimigo ou outro personagem)
     * @return mensagem descrevendo o efeito da habilidade
     */
    public abstract String usarHabilidadeEspecial(Personagem alvo);

    /**
     * Retorna uma representação textual do status atual do personagem.
     */
    public String getStatus() {
        return String.format("%s (Nível %d) - HP: %d/%d | ATK: %d | DEF: %d",
                nome, nivel, pontosVida, pontosVidaMaximos, ataque, defesa);
    }

    @Override
    public String toString() {
        return getStatus();
    }
}
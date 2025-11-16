package personagens;

/**
 * Classe concreta de personagem do tipo Mago.
 * <p>
 * Usa mana para potencializar seus ataques e lançar habilidades mágicas.
 */
public class Mago extends Personagem {
    private int mana;
    private int manaMaxima;
    private int poderMagico;

    /**
     * Cria um Mago com atributos personalizados.
     */
    public Mago(String nome, int pontosVida, int ataque, int defesa, int nivel) {
        super(nome, pontosVida, ataque, defesa, nivel);
        this.manaMaxima = 50 + (nivel * 10);
        this.mana = manaMaxima;
        this.poderMagico = 20;
    }

    /**
     * Construtor padrão com valores iniciais predefinidos.
     */
    public Mago() {
        this("Mago", 80, 10, 5, 1);
    }

    /**
     * Construtor de cópia.
     */
    public Mago(Mago outro) {
        super(outro);
        this.mana = outro.mana;
        this.manaMaxima = outro.manaMaxima;
        this.poderMagico = outro.poderMagico;
    }

    /**
     * Calcula o dano do Mago.
     * <p>
     * Se houver mana suficiente, o Mago gasta 10 de mana e adiciona
     * {@code poderMagico} ao dano base.
     */
    @Override
    public int calcularDano(int rolagemDado) {
        int danoBase = super.calcularDano(rolagemDado);
        
        if (mana >= 10) {
            danoBase += poderMagico;
            mana -= 10;
            System.out.println("Magia conjurada! (-10 mana)");
        }
        
        return danoBase;
    }

    /**
     * Habilidade especial: Bola de Fogo.
     * <p>
     * Custa 30 de mana e causa dano adicional diretamente ao alvo.
     */
    @Override
    public String usarHabilidadeEspecial(Personagem alvo) {
        if (mana >= 30) {
            mana -= 30;
            int rolagem = sistema.Dado.rolarD6();
            int dano = poderMagico + 10 + rolagem;
            alvo.receberDano(dano);
            return String.format("Bola de Fogo usada. Mana: -30 | Rolagem: %d | Dano causado: %d", rolagem, dano);
        } else {
            return "Mana insuficiente para conjurar Bola de Fogo.";
        }
    }

    /**
     * Regenera mana até o máximo.
     */
    public void regenerarMana(int quantidade) {
        mana = Math.min(mana + quantidade, manaMaxima);
    }

    public int getMana() {
        return mana;
    }

    public int getManaMaxima() {
        return manaMaxima;
    }

    @Override
    public String getStatus() {
        return super.getStatus() + String.format(" | MANA: %d/%d", mana, manaMaxima);
    }
}
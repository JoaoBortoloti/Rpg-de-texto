package itens;

/**
 * Enum que representa os tipos de efeito que um item pode ter.
 */
public enum Efeito {
    CURA("Restaura pontos de vida"),
    BUFF_ATAQUE("Aumenta o ataque temporariamente"),
    BUFF_DEFESA("Aumenta a defesa temporariamente"),
    DANO("Causa dano ao alvo"),
    OUTRO("Efeito especial");

    private final String descricao;

    Efeito(String descricao) {
        this.descricao = descricao;
    }

    /**
     * @return descrição textual do efeito.
     */
    public String getDescricao() {
        return descricao;
    }
}
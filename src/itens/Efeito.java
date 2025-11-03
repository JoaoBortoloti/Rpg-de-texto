package itens;

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

    public String getDescricao() {
        return descricao;
    }
}
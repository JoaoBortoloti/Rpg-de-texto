package itens;

import java.util.Objects;

public class Item implements Comparable<Item> {
    private final String nome;
    private final String descricao;
    private final Efeito efeito;
    private int quantidade;
    private final int valorEfeito;

    public Item(String nome, String descricao, Efeito efeito, int quantidade, int valorEfeito) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa");
        }
        this.nome = nome;
        this.descricao = descricao;
        this.efeito = efeito;
        this.quantidade = quantidade;
        this.valorEfeito = valorEfeito;
    }

    // Construtor de cópia
    public Item(Item outro) {
        this.nome = outro.nome;
        this.descricao = outro.descricao;
        this.efeito = outro.efeito;
        this.quantidade = outro.quantidade;
        this.valorEfeito = outro.valorEfeito;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public Efeito getEfeito() {
        return efeito;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public int getValorEfeito() {
        return valorEfeito;
    }

    public void setQuantidade(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa");
        }
        this.quantidade = quantidade;
    }

    public void incrementarQuantidade(int valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("Valor de incremento não pode ser negativo");
        }
        this.quantidade += valor;
    }

    public boolean decrementarQuantidade(int valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("Valor de decremento não pode ser negativo");
        }
        if (this.quantidade >= valor) {
            this.quantidade -= valor;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(nome, item.nome) && efeito == item.efeito;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, efeito);
    }

    @Override
    public int compareTo(Item outro) {
        int comparacaoNome = this.nome.compareTo(outro.nome);
        if (comparacaoNome != 0) {
            return comparacaoNome;
        }
        return this.efeito.compareTo(outro.efeito);
    }

    @Override
    public String toString() {
        return String.format("%s (x%d) - %s [%s: %d]", 
            nome, quantidade, descricao, efeito.name(), valorEfeito);
    }
}
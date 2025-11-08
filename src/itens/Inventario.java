package itens;

import java.util.*;
import java.util.stream.Collectors;

public class Inventario {
    private Map<String, Item> itens;
    private int capacidadeMaxima;

    public Inventario(int capacidadeMaxima) {
        this.itens = new HashMap<>();
        this.capacidadeMaxima = capacidadeMaxima;
    }

    // Construtor de cópia
    public Inventario(Inventario outro) {
        this.capacidadeMaxima = outro.capacidadeMaxima;
        this.itens = new HashMap<>();
        for (Map.Entry<String, Item> entry : outro.itens.entrySet()) {
            this.itens.put(entry.getKey(), new Item(entry.getValue()));
        }
    }

    public void adicionar(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item não pode ser nulo");
        }

        String chave = gerarChave(item);
        
        if (itens.containsKey(chave)) {
            Item itemExistente = itens.get(chave);
            itemExistente.incrementarQuantidade(item.getQuantidade());
        } else {
            if (itens.size() >= capacidadeMaxima) {
                throw new IllegalStateException("Inventário cheio!");
            }
            itens.put(chave, new Item(item));
        }
    }

    public boolean remover(String nome, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }

        Item item = buscarPorNome(nome);
        if (item == null) {
            return false;
        }

        if (item.getQuantidade() < quantidade) {
            return false;
        }

        item.decrementarQuantidade(quantidade);
        
        if (item.getQuantidade() == 0) {
            itens.remove(gerarChave(item));
        }

        return true;
    }

    public Item buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return null;
        }
        
        String nomeBusca = nome.trim();
        
        for (Item item : itens.values()) {
            if (item.getNome().trim().equalsIgnoreCase(nomeBusca)) {
                return item;
            }
        }
        
        return null;
    }
    
    public Item buscarPorIndice(int indice) {
        List<Item> itensOrdenados = listarOrdenado();
        if (indice < 0 || indice >= itensOrdenados.size()) {
            return null;
        }
        
        Item itemCopia = itensOrdenados.get(indice);
        // Retorna o item original do HashMap, não a cópia
        return buscarPorNome(itemCopia.getNome());
    }

    public List<Item> listarOrdenado() {
        return itens.values().stream()
                .map(Item::new)
                .sorted()
                .collect(Collectors.toList());
    }

    public boolean estaVazio() {
        return itens.isEmpty();
    }

    public int getTamanho() {
        return itens.size();
    }

    private String gerarChave(Item item) {
        return item.getNome().toLowerCase() + "_" + item.getEfeito().name();
    }

    @Override
    public String toString() {
        if (estaVazio()) {
            return "Inventário vazio";
        }

        StringBuilder sb = new StringBuilder("=== INVENTÁRIO ===\n");
        List<Item> itensOrdenados = listarOrdenado();
        for (int i = 0; i < itensOrdenados.size(); i++) {
            sb.append(String.format("%d. %s\n", i + 1, itensOrdenados.get(i)));
        }
        return sb.toString();
    }
}
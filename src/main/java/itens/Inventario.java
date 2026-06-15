package itens;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Representa o inventário de um personagem.
 * <p>
 * Armazena itens com múltiplas unidades, agrupando por (nome + efeito).
 * Usa um {@link Map} interno para facilitar o merge de quantidades.
 */
public class Inventario {
    private Map<String, Item> itens;
    private int capacidadeMaxima;

    /**
     * @param capacidadeMaxima número máximo de tipos diferentes de item.
     *                         Não é a soma das quantidades, e sim o número de "slots".
     * @throws IllegalArgumentException se capacidadeMaxima &lt;= 0
     */
    public Inventario(int capacidadeMaxima) {
        if (capacidadeMaxima <= 0) {
            throw new IllegalArgumentException("Capacidade máxima deve ser positiva");
        }
        this.itens = new HashMap<>();
        this.capacidadeMaxima = capacidadeMaxima;
    }

    /**
     * Construtor de cópia.
     * Cria cópias independentes dos itens.
     */
    public Inventario(Inventario outro) {
        this.capacidadeMaxima = outro.capacidadeMaxima;
        this.itens = new HashMap<>();
        for (Map.Entry<String, Item> entry : outro.itens.entrySet()) {
            this.itens.put(entry.getKey(), new Item(entry.getValue()));
        }
    }

    /**
     * Adiciona um item ao inventário. Se já existir um item com o mesmo
     * nome e efeito, apenas incrementa a quantidade.
     *
     * @param item item a ser adicionado (não pode ser nulo)
     * @throws IllegalArgumentException se item for nulo
     * @throws IllegalStateException    se o inventário estiver cheio para novos tipos
     */
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

    /**
     * Remove certa quantidade de um item pelo nome.
     *
     * @param nome       nome lógico do item
     * @param quantidade quantidade a remover (&gt; 0)
     * @return true se conseguiu remover; false se não havia quantidade suficiente
     *         ou o item não existia.
     * @throws IllegalArgumentException se quantidade &lt;= 0
     */
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

    /**
     * Busca item por nome (ignorando maiúsculas/minúsculas).
     *
     * @param nome nome do item
     * @return referência ao item interno (não uma cópia) ou null se não encontrado
     */
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
    
    /**
     * Retorna o item pelo índice na lista ordenada.
     *
     * @param indice posição na lista ordenada (0-based)
     * @return referência ao item interno ou null se índice for inválido
     */
    public Item buscarPorIndice(int indice) {
        List<Item> itensOrdenados = listarOrdenado();
        if (indice < 0 || indice >= itensOrdenados.size()) {
            return null;
        }
        
        Item itemCopia = itensOrdenados.get(indice);
        return buscarPorNome(itemCopia.getNome());
    }

    /**
     * Retorna uma lista de cópias dos itens,
     * ordenada pelo {@link Item#compareTo(Item)}.
     */
    public List<Item> listarOrdenado() {
        return itens.values().stream()
            .map(Item::new)
            .sorted()
            .collect(Collectors.toList());
    }

    public boolean estaVazio() {
        return itens.isEmpty();
    }

    /**
     * @return quantidade de tipos de item (slots ocupados)
     */
    public int getTamanho() {
        return itens.size();
    }

    /**
     * Remove todos os itens do inventário.
     */
    public void limpar() {
        itens.clear();
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
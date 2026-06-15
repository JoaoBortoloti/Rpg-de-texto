package sistema;

import personagens.Inimigo;
import personagens.Personagem;
import itens.Item;
import itens.Efeito;

/**
 * Processa eventos aleatórios de exploração (combate, item, armadilha).
 * <p>
 * Retorna o {@link Inimigo} derrotado quando houver combate vencido,
 * ou {@code null} nos demais casos — o chamador decide o que fazer com
 * o XP e o loot.
 */
public class ExploracaoService {

    private final CombateService combateService;

    public ExploracaoService(CombateService combateService) {
        this.combateService = combateService;
    }

    /**
     * Processa um evento aleatório para o jogador.
     *
     * @param jogador personagem que está explorando
     * @return inimigo derrotado se houver vitória em combate, {@code null} caso contrário
     */
    public Inimigo processarEvento(Personagem jogador) {
        int evento = Dado.rolar(10);

        if (evento <= GameConfig.LIMIAR_INIMIGO) {
            return combateInimigo(jogador);
        } else if (evento <= GameConfig.LIMIAR_ITEM) {
            encontrarItem(jogador);
        } else if (evento == GameConfig.LIMIAR_ARMADILHA) {
            armadilha(jogador);
        } else {
            System.out.println("Você explorou a área mas não encontrou nada interessante.");
        }
        return null;
    }

    private Inimigo combateInimigo(Personagem jogador) {
        Inimigo inimigo = Inimigo.criarInimigoAleatorio(jogador.getNivel());
        System.out.println("\nUm " + inimigo.getNome() + " apareceu!");
        System.out.println(inimigo.getStatus());

        ResultadoCombate resultado = combateService.combater(jogador, inimigo, true, false);
        return resultado == ResultadoCombate.VITORIA ? inimigo : null;
    }

    private void encontrarItem(Personagem jogador) {
        int tipoItem = Dado.rolar(4);
        Item item = switch (tipoItem) {
            case 1 -> new Item("Poção de Vida",   "Restaura 30 HP",   Efeito.CURA,        1, 30);
            case 2 -> new Item("Poção de Força",  "Aumenta ataque",   Efeito.BUFF_ATAQUE, 1, 5);
            case 3 -> new Item("Poção de Defesa", "Aumenta defesa",   Efeito.BUFF_DEFESA, 1, 5);
            default -> new Item("Elixir Raro",    "Restaura 50 HP",   Efeito.CURA,        1, 50);
        };

        System.out.println("Você encontrou: " + item.getNome() + "!");
        try {
            jogador.getInventario().adicionar(item);
        } catch (IllegalStateException e) {
            System.out.println("Inventário cheio! Você não pode carregar: " + item.getNome());
        }
    }

    private void armadilha(Personagem jogador) {
        System.out.println("Você caiu em uma armadilha!");
        int dano = Dado.rolar(GameConfig.FACES_DADO_ARMADILHA);
        jogador.receberDano(dano);
        System.out.println("Você recebeu " + dano + " de dano!");
        System.out.println(jogador.getStatus());
    }
}

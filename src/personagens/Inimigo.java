package personagens;

import itens.Item;
import itens.Efeito;
import sistema.Dado;

public class Inimigo extends Personagem {
    private int recompensaXP;
    private String tipo;

    public Inimigo(String nome, int pontosVida, int ataque, int defesa, int nivel, String tipo) {
        super(nome, pontosVida, ataque, defesa, nivel);
        this.tipo = tipo;
        this.recompensaXP = nivel * 50;
        gerarLoot();
    }

    // Construtor padrão
    public Inimigo() {
        this("Goblin", 50, 8, 3, 1, "Comum");
    }

    // Construtor de cópia
    public Inimigo(Inimigo outro) {
        super(outro);
        this.recompensaXP = outro.recompensaXP;
        this.tipo = outro.tipo;
    }

    private void gerarLoot() {
        // Adiciona itens aleatórios ao inventário do inimigo
        int numeroItens = Dado.rolar(3);
        
        for (int i = 0; i < numeroItens; i++) {
            int tipoItem = Dado.rolar(4);
            Item item = null;
            
            switch (tipoItem) {
                case 1:
                    item = new Item("Poção de Vida", "Restaura HP", Efeito.CURA, 1, 30);
                    break;
                case 2:
                    item = new Item("Poção de Força", "Aumenta ataque", Efeito.BUFF_ATAQUE, 1, 5);
                    break;
                case 3:
                    item = new Item("Poção de Defesa", "Aumenta defesa", Efeito.BUFF_DEFESA, 1, 5);
                    break;
                case 4:
                    item = new Item("Bomba", "Causa dano", Efeito.DANO, 1, 25);
                    break;
            }
            
            if (item != null) {
                getInventario().adicionar(item);
            }
        }
    }

    @Override
    public String usarHabilidadeEspecial() {
        return "👹 " + getNome() + " usa ataque especial!";
    }

    public int getRecompensaXP() {
        return recompensaXP;
    }

    public String getTipo() {
        return tipo;
    }

    public static Inimigo criarInimigoAleatorio(int nivelJogador) {
        int nivel = Math.max(1, nivelJogador + Dado.rolar(3) - 2);
        int tipoInimigo = Dado.rolar(5);
        
        switch (tipoInimigo) {
            case 1:
                return new Inimigo("Goblin", 40 + nivel * 10, 6 + nivel * 2, 2 + nivel, nivel, "Comum");
            case 2:
                return new Inimigo("Orc", 60 + nivel * 15, 8 + nivel * 3, 4 + nivel, nivel, "Forte");
            case 3:
                return new Inimigo("Esqueleto", 35 + nivel * 8, 7 + nivel * 2, 3 + nivel, nivel, "Morto-vivo");
            case 4:
                return new Inimigo("Lobo Selvagem", 45 + nivel * 12, 9 + nivel * 2, 2 + nivel, nivel, "Besta");
            case 5:
                return new Inimigo("Dragão Jovem", 80 + nivel * 20, 10 + nivel * 4, 6 + nivel * 2, nivel, "Chefe");
            default:
                return new Inimigo();
        }
    }
}
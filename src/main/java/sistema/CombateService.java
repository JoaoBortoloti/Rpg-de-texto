package sistema;

import personagens.Inimigo;
import personagens.Personagem;
import itens.Item;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Encapsula toda a lógica de turno de combate.
 * <p>
 * Unifica o combate regular e o combate contra o boss, que diferem apenas
 * na presença da opção de fuga e no ataque especial sombrio do boss.
 */
public class CombateService {

    private final BufferedReader reader;

    public CombateService(BufferedReader reader) {
        this.reader = reader;
    }

    /**
     * Executa o loop de combate até um dos lados morrer ou o jogador fugir.
     *
     * @param jogador     personagem controlado pelo usuário
     * @param inimigo     oponente
     * @param permiteFuga se verdadeiro, o menu oferece a opção de fugir
     * @param ehBoss      se verdadeiro, o inimigo pode usar ATAQUE SOMBRIO (dano duplo)
     * @return {@link ResultadoCombate} indicando o desfecho
     */
    public ResultadoCombate combater(Personagem jogador, Inimigo inimigo,
                                     boolean permiteFuga, boolean ehBoss) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(ehBoss ? "BATALHA FINAL!" : "COMBATE INICIADO!");
        System.out.println("=".repeat(50));

        while (jogador.estaVivo() && inimigo.estaVivo()) {
            System.out.println("\n--- Seu turno ---");
            imprimirMenuCombate(permiteFuga);

            int maxOpcao = permiteFuga ? 4 : 3;
            int acao = lerOpcao(1, maxOpcao);

            boolean pulouTurnoInimigo = false;

            switch (acao) {
                case 1 -> {
                    int rolagem = Dado.rolarD6();
                    System.out.println("Você rolou: " + rolagem);
                    int dano = jogador.calcularDano(rolagem);
                    inimigo.receberDano(dano);
                    System.out.println("Você causou " + dano + " de dano!");
                    System.out.println(inimigo.getStatus());
                }
                case 2 -> {
                    usarItem(jogador, inimigo);
                    pulouTurnoInimigo = true;
                }
                case 3 -> {
                    if (permiteFuga) {
                        if (tentarFugir()) {
                            System.out.println("Você fugiu com sucesso!");
                            return ResultadoCombate.FUGIU;
                        }
                        System.out.println("Não conseguiu fugir!");
                    } else {
                        String resultado = jogador.usarHabilidadeEspecial(inimigo);
                        System.out.println(resultado);
                        System.out.println(inimigo.getStatus());
                    }
                }
                case 4 -> {
                    // só alcançável quando permiteFuga == true, então case 4 = habilidade especial
                    String resultado = jogador.usarHabilidadeEspecial(inimigo);
                    System.out.println(resultado);
                    System.out.println(inimigo.getStatus());
                }
            }

            if (!inimigo.estaVivo()) return ResultadoCombate.VITORIA;
            if (pulouTurnoInimigo) continue;

            // Turno do inimigo
            System.out.println("\n--- Turno do " + inimigo.getNome() + " ---");
            int rolagemInimigo = Dado.rolarD6();
            System.out.println(inimigo.getNome() + " rolou: " + rolagemInimigo);

            if (ehBoss && Dado.rolar(10) >= GameConfig.BOSS_LIMIAR_ESPECIAL) {
                System.out.println(inimigo.getNome() + " usa ATAQUE SOMBRIO!");
                int danoEspecial = inimigo.calcularDano(rolagemInimigo) * 2;
                jogador.receberDano(danoEspecial);
                System.out.println("Você recebeu " + danoEspecial + " de dano devastador!");
            } else {
                int dano = inimigo.calcularDano(rolagemInimigo);
                jogador.receberDano(dano);
                System.out.println("Você recebeu " + dano + " de dano!");
            }

            System.out.println(jogador.getStatus());
            aguardarEnter();
        }

        return jogador.estaVivo() ? ResultadoCombate.VITORIA : ResultadoCombate.DERROTA;
    }

    /**
     * Permite ao jogador selecionar e consumir um item do inventário.
     * Pode ser chamado durante o combate ou no menu principal (alvo null).
     *
     * @param jogador         dono do inventário
     * @param alvoEmCombate   inimigo alvo (null quando fora de combate)
     */
    public void usarItem(Personagem jogador, Inimigo alvoEmCombate) {
        if (jogador.getInventario().estaVazio()) {
            System.out.println("Seu inventário está vazio!");
            return;
        }

        System.out.println("\n" + jogador.getInventario());
        System.out.print("Digite o número do item (ou 0 para cancelar): ");

        int escolha = lerOpcao(0, jogador.getInventario().getTamanho());
        if (escolha == 0) return;

        Item item = jogador.getInventario().buscarPorIndice(escolha - 1);
        if (item == null) {
            System.out.println("Item inválido!");
            return;
        }

        boolean consumiu = aplicarEfeitoItem(jogador, item, alvoEmCombate);
        if (consumiu) {
            jogador.getInventario().remover(item.getNome(), 1);
            System.out.println(item.getNome() + " usado!");
        }
    }

    private boolean aplicarEfeitoItem(Personagem jogador, Item item, Inimigo alvoEmCombate) {
        switch (item.getEfeito()) {
            case CURA -> {
                jogador.curar(item.getValorEfeito());
                System.out.println("Você recuperou " + item.getValorEfeito() + " HP!");
                return true;
            }
            case BUFF_ATAQUE -> {
                jogador.setAtaque(jogador.getAtaque() + item.getValorEfeito());
                System.out.println("Seu ataque aumentou em " + item.getValorEfeito() + "!");
                return true;
            }
            case BUFF_DEFESA -> {
                jogador.setDefesa(jogador.getDefesa() + item.getValorEfeito());
                System.out.println("Sua defesa aumentou em " + item.getValorEfeito() + "!");
                return true;
            }
            case DANO -> {
                if (alvoEmCombate == null) {
                    System.out.println("Este item só pode ser usado em combate!");
                    return false;
                }
                int variacao = Dado.rolarD6() - 3;
                int danoTotal = Math.max(1, item.getValorEfeito() + variacao);
                alvoEmCombate.receberDano(danoTotal);
                System.out.printf("Você usou %s e causou %d de dano em %s!%n",
                        item.getNome(), danoTotal, alvoEmCombate.getNome());
                System.out.println(alvoEmCombate.getStatus());
                return true;
            }
            default -> {
                System.out.println("Efeito especial aplicado!");
                return true;
            }
        }
    }

    private void imprimirMenuCombate(boolean permiteFuga) {
        System.out.println("1. Atacar");
        System.out.println("2. Usar item");
        if (permiteFuga) {
            System.out.println("3. Tentar fugir");
            System.out.println("4. Habilidade especial");
        } else {
            System.out.println("3. Habilidade especial");
        }
    }

    private boolean tentarFugir() {
        return Dado.rolarD20() >= GameConfig.LIMIAR_FUGA;
    }

    private int lerOpcao(int min, int max) {
        while (true) {
            try {
                String linha = reader.readLine();
                int opcao = Integer.parseInt(linha.trim());
                if (opcao >= min && opcao <= max) return opcao;
                System.out.print("Opção inválida! Digite entre " + min + " e " + max + ": ");
            } catch (IOException | NumberFormatException e) {
                System.out.print("Entrada inválida! Digite um número: ");
            }
        }
    }

    private void aguardarEnter() {
        System.out.println("\n[Pressione ENTER para continuar]");
        try {
            reader.readLine();
        } catch (IOException e) {
            // silencioso
        }
    }
}

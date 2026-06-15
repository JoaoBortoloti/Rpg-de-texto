package sistema;

import personagens.*;
import itens.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.nio.charset.StandardCharsets;

/**
 * Orquestrador principal do RPG de texto.
 * <p>
 * Gerencia o loop principal, menus e a progressão da história, delegando
 * responsabilidades específicas para serviços dedicados:
 * <ul>
 *   <li>{@link CombateService} — lógica de turno de combate</li>
 *   <li>{@link ExploracaoService} — eventos aleatórios de exploração</li>
 *   <li>{@link SaveSystem} — persistência em disco</li>
 * </ul>
 */
public class Jogo {
    private Personagem jogador;
    private final BufferedReader reader;
    private final CombateService combateService;
    private final ExploracaoService exploracaoService;
    private final SaveSystem saveSystem;

    private int xpAtual;
    private int xpProximoNivel;
    private boolean jogoAtivo;
    private int exploracoesRealizadas;
    private int capituloAtual;
    private boolean bossDerrotado;

    public Jogo() {
        this.reader            = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        this.combateService    = new CombateService(reader);
        this.exploracaoService = new ExploracaoService(combateService);
        this.saveSystem        = new SaveSystem();

        this.xpAtual               = 0;
        this.xpProximoNivel        = GameConfig.XP_INICIAL_PROXIMO_NIVEL;
        this.jogoAtivo             = true;
        this.exploracoesRealizadas = 0;
        this.capituloAtual         = 1;
        this.bossDerrotado         = false;
    }

    public void iniciar() {
        exibirBanner();
        telaInicial();
        if (jogador == null) {
            System.out.println("Encerrando jogo.");
            return;
        }
        introducaoSeForNovoJogo();
        loopPrincipal();
    }

    // ── Tela inicial ─────────────────────────────────────────────────────────

    private void telaInicial() {
        boolean escolhendo = true;
        while (escolhendo) {
            System.out.println("1. Novo jogo");
            System.out.println("2. Carregar jogo");
            System.out.println("0. Sair");
            System.out.print("Escolha: ");

            switch (lerOpcao(0, 2)) {
                case 1 -> { criarPersonagem(); escolhendo = false; }
                case 2 -> { if (carregarJogo()) escolhendo = false; }
                case 0 -> { jogoAtivo = false; escolhendo = false; }
            }
        }
    }

    private void exibirBanner() {
        System.out.println("╔═══════════════════════════════════╗");
        System.out.println("║    RPG DE TEXTO - AVENTURA POO    ║");
        System.out.println("╚═══════════════════════════════════╝");
        System.out.println();
    }

    private void criarPersonagem() {
        System.out.println("Escolha sua classe:");
        System.out.println("1. Guerreiro - Alto HP e defesa, golpes críticos");
        System.out.println("2. Mago - Magia poderosa, baixa defesa");
        System.out.println("3. Arqueiro - Ataques precisos à distância");

        int escolha = lerOpcao(1, 3);
        System.out.print("\nDigite o nome do seu personagem: ");
        String nome = lerLinha();

        jogador = switch (escolha) {
            case 1 -> new Guerreiro(nome, 120, 15, 10, 1);
            case 2 -> new Mago(nome, 80, 10, 5, 1);
            default -> new Arqueiro(nome, 100, 12, 7, 1);
        };

        jogador.getInventario().adicionar(new Item("Poção de Vida",  "Restaura 30 HP",       Efeito.CURA,        3, 30));
        jogador.getInventario().adicionar(new Item("Poção de Força", "Aumenta ataque em 5",  Efeito.BUFF_ATAQUE, 1, 5));

        System.out.println("\nPersonagem criado com sucesso!");
        System.out.println(jogador.getStatus());
    }

    // ── Loop principal ────────────────────────────────────────────────────────

    private void loopPrincipal() {
        while (jogoAtivo && jogador.estaVivo() && !bossDerrotado) {
            exibirMenu();
            switch (lerOpcao(1, 6)) {
                case 1 -> explorar();
                case 2 -> combateService.usarItem(jogador, null);
                case 3 -> System.out.println("\n" + jogador.getInventario());
                case 4 -> verStatus();
                case 5 -> saveSystem.salvar(jogador, xpAtual, xpProximoNivel,
                                            capituloAtual, exploracoesRealizadas,
                                            bossDerrotado, reader);
                case 6 -> sair();
            }
        }

        if (!jogador.estaVivo()) gameOver();
        else if (bossDerrotado)  finalVitorioso();
    }

    private void exibirMenu() {
        System.out.println("\n" + "─".repeat(50));
        System.out.println("Capítulo " + capituloAtual + " | Explorações: " + exploracoesRealizadas);
        System.out.println("O que deseja fazer?");
        System.out.println("1. Explorar");
        System.out.println("2. Usar item");
        System.out.println("3. Ver inventário");
        System.out.println("4. Ver status");
        System.out.println("5. Salvar jogo");
        System.out.println("6. Sair do jogo");
        System.out.print("Escolha: ");
    }

    // ── Exploração e história ─────────────────────────────────────────────────

    private void explorar() {
        System.out.println("\nExplorando...");
        exploracoesRealizadas++;

        if (exploracoesRealizadas % GameConfig.CADENCIA_HISTORIA == 0) {
            avancarHistoria();
            return;
        }

        Inimigo inimigoVencido = exploracaoService.processarEvento(jogador);
        if (inimigoVencido != null) {
            vitoria(inimigoVencido);
        }
    }

    private void avancarHistoria() {
        capituloAtual++;
        System.out.println("\n" + "=".repeat(50));

        switch (capituloAtual) {
            case 2 -> {
                System.out.println("CAPÍTULO 2: A VILA ABANDONADA");
                System.out.println("=".repeat(50));
                System.out.println("Você encontra uma vila abandonada.");
                System.out.println("Sinais de batalha estão por toda parte.");
                System.out.println("Nas paredes, escritos em sangue: 'Ele vem à noite'.");
                System.out.println("Você sente que está se aproximando do castelo...");
            }
            case 3 -> {
                System.out.println("CAPÍTULO 3: O CEMITÉRIO AMALDIÇOADO");
                System.out.println("=".repeat(50));
                System.out.println("Um cemitério surge à sua frente.");
                System.out.println("Mortos-vivos vagam entre as lápides.");
                System.out.println("Uma energia sombria emana do solo.");
                System.out.println("O castelo está cada vez mais próximo...");
            }
            case 4 -> {
                System.out.println("CAPÍTULO 4: A PONTE QUEBRADA");
                System.out.println("=".repeat(50));
                System.out.println("Você chega a uma ponte sobre um abismo.");
                System.out.println("Do outro lado, o castelo se ergue imponente.");
                System.out.println("Criaturas guardam a passagem.");
                System.out.println("Você está quase lá...");
            }
            case 5 -> {
                System.out.println("CAPÍTULO 5: OS PORTÕES DO CASTELO");
                System.out.println("=".repeat(50));
                System.out.println("Finalmente, você alcança os portões do castelo.");
                System.out.println("Eles se abrem lentamente, rangendo.");
                System.out.println("Uma voz ecoa: 'Bem-vindo, aventureiro...'");
                System.out.println("Prepare-se para o confronto final!");
            }
            default -> {
                if (capituloAtual >= 6) {
                    iniciarBossFight();
                    return;
                }
            }
        }

        System.out.println("=".repeat(50));
        aguardarEnter();
    }

    // ── Boss ──────────────────────────────────────────────────────────────────

    private void iniciarBossFight() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("CAPÍTULO FINAL: VORATH, O ETERNO");
        System.out.println("=".repeat(50));
        System.out.println("Você entra no salão principal do castelo.");
        System.out.println("No trono, uma figura sombria se levanta.");
        System.out.println("'Você chegou longe, " + jogador.getNome() + "...'");
        System.out.println("'Mas sua jornada termina aqui!'");
        System.out.println("=".repeat(50));
        aguardarEnter();

        Inimigo boss = new Inimigo(
            "Vorath, o Eterno",
            GameConfig.BOSS_HP_BASE     + (jogador.getNivel() * GameConfig.BOSS_HP_POR_NIVEL),
            GameConfig.BOSS_ATAQUE_BASE + (jogador.getNivel() * GameConfig.BOSS_ATAQUE_POR_NIVEL),
            GameConfig.BOSS_DEFESA_BASE + jogador.getNivel(),
            jogador.getNivel()          + GameConfig.BOSS_NIVEL_BONUS,
            "Boss Final"
        );
        boss.getInventario().adicionar(new Item("Elixir Lendário",    "Restaura 100 HP",      Efeito.CURA,        2, 100));
        boss.getInventario().adicionar(new Item("Essência das Trevas", "Aumenta ataque em 10", Efeito.BUFF_ATAQUE, 1, 10));

        System.out.println("\n" + boss.getStatus());
        aguardarEnter();

        if (combateService.combater(jogador, boss, false, true) == ResultadoCombate.VITORIA) {
            vitoriaBoss(boss);
        }
    }

    private void vitoriaBoss(Inimigo boss) {
        bossDerrotado = true;
        System.out.println("\n" + "=".repeat(50));
        System.out.println("VITÓRIA ÉPICA!");
        System.out.println("=".repeat(50));
        System.out.println("Você derrotou o " + boss.getNome() + "!");
        System.out.println("O castelo começa a desmoronar...");

        int xpGanho = boss.getRecompensaXP() * GameConfig.XP_BOSS_MULTIPLICADOR;
        xpAtual += xpGanho;
        System.out.println("\n" + xpGanho + " XP ganhos!");

        saquearInimigo(boss);
        aguardarEnter();
    }

    private void finalVitorioso() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("FINAL - A LUZ RETORNA");
        System.out.println("=".repeat(50));
        System.out.println("Com o Vorath, o Eterno derrotado,");
        System.out.println("a paz retorna às terras.");
        System.out.println("Você é aclamado como herói!");
        System.out.printf("%nNível alcançado: %d%nXP total: %d%nExplorações: %d%n",
                jogador.getNivel(), xpAtual, exploracoesRealizadas);
        System.out.println("\nParabéns, " + jogador.getNome() + "!");
        System.out.println("=".repeat(50));
    }

    // ── Pós-combate ───────────────────────────────────────────────────────────

    private void vitoria(Inimigo inimigo) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("VITÓRIA!");
        System.out.println("=".repeat(50));
        System.out.println("Você derrotou " + inimigo.getNome() + "!");

        xpAtual += inimigo.getRecompensaXP();
        System.out.println(inimigo.getRecompensaXP() + " XP");

        if (xpAtual >= xpProximoNivel) levelUp();

        saquearInimigo(inimigo);
        aguardarEnter();
    }

    private void levelUp() {
        jogador.setNivel(jogador.getNivel() + 1);
        xpAtual -= xpProximoNivel;
        xpProximoNivel = (int) (xpProximoNivel * GameConfig.MULTIPLICADOR_XP_NIVEL);

        jogador.setPontosVidaMaximos(jogador.getPontosVidaMaximos() + GameConfig.BONUS_HP_LEVEL_UP);
        jogador.setPontosVida(jogador.getPontosVidaMaximos());
        jogador.setAtaque(jogador.getAtaque() + GameConfig.BONUS_ATAQUE_LEVEL_UP);
        jogador.setDefesa(jogador.getDefesa() + GameConfig.BONUS_DEFESA_LEVEL_UP);

        System.out.println("\nLEVEL UP! Agora você é nível " + jogador.getNivel());
        System.out.println("HP máximo, ataque e defesa aumentados!");
    }

    private void saquearInimigo(Inimigo inimigo) {
        List<Item> itens = inimigo.getInventario().listarOrdenado();
        if (itens.isEmpty()) {
            System.out.println("O inimigo não tinha itens.");
            return;
        }
        System.out.println("\nItens encontrados:");
        for (Item item : itens) {
            System.out.println("  - " + item.getNome() + " (x" + item.getQuantidade() + ")");
            try {
                jogador.getInventario().adicionar(item);
            } catch (IllegalStateException e) {
                System.out.println("Inventário cheio! Você não pode carregar: " + item.getNome());
            }
        }
    }

    // ── Carregamento de save ──────────────────────────────────────────────────

    private boolean carregarJogo() {
        SaveSystem.EstadoSalvo estado = saveSystem.carregar(reader);
        if (estado == null) return false;

        this.jogador               = estado.jogador;
        this.xpAtual               = estado.xpAtual;
        this.xpProximoNivel        = estado.xpProximoNivel;
        this.capituloAtual         = estado.capituloAtual;
        this.exploracoesRealizadas = estado.exploracoesRealizadas;
        this.bossDerrotado         = estado.bossDerrotado;

        System.out.println("\nSave carregado com sucesso!");
        System.out.println(jogador.getStatus());
        System.out.printf("Capítulo: %d | Explorações: %d%n", capituloAtual, exploracoesRealizadas);
        return true;
    }

    // ── Utilitários ───────────────────────────────────────────────────────────

    private void introducaoSeForNovoJogo() {
        if (capituloAtual <= 1 && exploracoesRealizadas == 0 && !bossDerrotado) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("CAPÍTULO 1: O DESPERTAR");
            System.out.println("=".repeat(50));
            System.out.println("Você acorda em uma floresta escura...");
            System.out.println("Não se lembra de como chegou aqui.");
            System.out.println("Ao longe, você vê as ruínas de um castelo antigo.");
            System.out.println("Dizem que um poderoso ser habita lá...");
            System.out.println("Sua jornada começa agora!");
            System.out.println("=".repeat(50) + "\n");
            aguardarEnter();
        }
    }

    private void verStatus() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(jogador.getStatus());
        System.out.printf("XP: %d/%d | Capítulo: %d | Explorações: %d%n",
                xpAtual, xpProximoNivel, capituloAtual, exploracoesRealizadas);
        System.out.println("=".repeat(50));
    }

    private void sair() {
        System.out.println("\nTem certeza que deseja sair? (s/n)");
        if (lerLinha().equalsIgnoreCase("s")) {
            jogoAtivo = false;
            System.out.println("\nObrigado por jogar!");
        }
    }

    private void gameOver() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("GAME OVER");
        System.out.println("=".repeat(50));
        System.out.printf("Você foi derrotado...%nNível: %d | XP: %d | Capítulo: %d%n",
                jogador.getNivel(), xpAtual, capituloAtual);
    }

    private int lerOpcao(int min, int max) {
        while (true) {
            try {
                int opcao = Integer.parseInt(reader.readLine().trim());
                if (opcao >= min && opcao <= max) return opcao;
                System.out.print("Opção inválida! Digite entre " + min + " e " + max + ": ");
            } catch (IOException | NumberFormatException e) {
                System.out.print("Entrada inválida! Digite um número: ");
            }
        }
    }

    private String lerLinha() {
        try {
            String linha = reader.readLine();
            return linha == null ? "" : linha.trim();
        } catch (IOException e) {
            return "";
        }
    }

    private void aguardarEnter() {
        System.out.println("\n[Pressione ENTER para continuar]");
        try { reader.readLine(); } catch (IOException ignored) {}
    }
}

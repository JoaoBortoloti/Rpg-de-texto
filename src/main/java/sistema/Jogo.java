package sistema;

import personagens.*;
import itens.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;

/**
 * Classe principal de controle do RPG de texto.
 * <p>
 * Gerencia o loop principal do jogo, menus, exploração, combate,
 * save/load e progresso da história.
 */
public class Jogo {
    private Personagem jogador;
    private BufferedReader reader;
    private int xpAtual;
    private int xpProximoNivel;
    private boolean jogoAtivo;
    private int exploracoesRealizadas;
    private int capituloAtual;
    private boolean bossDerrotado;

    /**
     * Cria uma instância de jogo pronta para ser iniciada via {@link #iniciar()}.
     */
    public Jogo() {
        this.reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        this.xpAtual = 0;
        this.xpProximoNivel = 100;
        this.jogoAtivo = true;
        this.exploracoesRealizadas = 0;
        this.capituloAtual = 1;
        this.bossDerrotado = false;
    }

    /**
     * Inicia o jogo, exibindo a tela inicial e o loop principal.
     */
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

    private void telaInicial() {
        boolean escolhendo = true;

        while (escolhendo) {
            System.out.println("1. Novo jogo");
            System.out.println("2. Carregar jogo");
            System.out.println("0. Sair");
            System.out.print("Escolha: ");

            int opcao = lerOpcao(0, 2);

            switch (opcao) {
                case 1:
                    criarPersonagem();
                    escolhendo = false;
                    break;
                case 2:
                    boolean carregou = carregarJogo(true);
                    if (carregou) {
                        escolhendo = false;
                    }
                    break;
                case 0:
                    jogoAtivo = false;
                    escolhendo = false;
                    break;
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
        
        switch (escolha) {
            case 1:
                jogador = new Guerreiro(nome, 120, 15, 10, 1);
                break;
            case 2:
                jogador = new Mago(nome, 80, 10, 5, 1);
                break;
            case 3:
                jogador = new Arqueiro(nome, 100, 12, 7, 1);
                break;
        }
        
        jogador.getInventario().adicionar(new Item("Poção de Vida", "Restaura 30 HP", Efeito.CURA, 3, 30));
        jogador.getInventario().adicionar(new Item("Poção de Força", "Aumenta ataque em 5", Efeito.BUFF_ATAQUE, 1, 5));
        
        System.out.println("\nPersonagem criado com sucesso!");
        System.out.println(jogador.getStatus());
    }

    private void introducaoSeForNovoJogo() {
        if (capituloAtual <= 1 && exploracoesRealizadas == 0 && !bossDerrotado) {
            introducao();
        }
    }

    private void introducao() {
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

    private void loopPrincipal() {
        while (jogoAtivo && jogador.estaVivo() && !bossDerrotado) {
            exibirMenu();
            int opcao = lerOpcao(1, 6);
            
            switch (opcao) {
                case 1:
                    explorar();
                    break;
                case 2:
                    usarItem(null);
                    break;
                case 3:
                    verInventario();
                    break;
                case 4:
                    verStatus();
                    break;
                case 5:
                    salvarJogo();
                    break;
                case 6:
                    sair();
                    break;
            }
        }
        
        if (!jogador.estaVivo()) {
            gameOver();
        } else if (bossDerrotado) {
            finalVitorioso();
        }
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

    private void explorar() {
        System.out.println("\nExplorando...");
        exploracoesRealizadas++;
        
        if (exploracoesRealizadas % 2 == 0) {
            avancarHistoria();
            return;
        }
        
        int evento = Dado.rolar(10);
        
        if (evento <= 5) {
            encontrarInimigo();
        } else if (evento <= 7) {
            encontrarItem();
        } else if (evento == 8) {
            armadilha();
        } else {
            System.out.println("Você explorou a área mas não encontrou nada interessante.");
        }
    }

    private void avancarHistoria() {
        capituloAtual++;
        
        System.out.println("\n" + "=".repeat(50));
        
        switch (capituloAtual) {
            case 2:
                System.out.println("CAPÍTULO 2: A VILA ABANDONADA");
                System.out.println("=".repeat(50));
                System.out.println("Você encontra uma vila abandonada.");
                System.out.println("Sinais de batalha estão por toda parte.");
                System.out.println("Nas paredes, escritos em sangue: 'Ele vem à noite'.");
                System.out.println("Você sente que está se aproximando do castelo...");
                break;
            
            case 3:
                System.out.println("CAPÍTULO 3: O CEMITÉRIO AMALDIÇOADO");
                System.out.println("=".repeat(50));
                System.out.println("Um cemitério surge à sua frente.");
                System.out.println("Mortos-vivos vagam entre as lápides.");
                System.out.println("Uma energia sombria emana do solo.");
                System.out.println("O castelo está cada vez mais próximo...");
                break;
            
            case 4:
                System.out.println("CAPÍTULO 4: A PONTE QUEBRADA");
                System.out.println("=".repeat(50));
                System.out.println("Você chega a uma ponte sobre um abismo.");
                System.out.println("Do outro lado, o castelo se ergue imponente.");
                System.out.println("Criaturas guardam a passagem.");
                System.out.println("Você está quase lá...");
                break;
            
            case 5:
                System.out.println("CAPÍTULO 5: OS PORTÕES DO CASTELO");
                System.out.println("=".repeat(50));
                System.out.println("Finalmente, você alcança os portões do castelo.");
                System.out.println("Eles se abrem lentamente, rangendo.");
                System.out.println("Uma voz ecoa: 'Bem-vindo, aventureiro...'");
                System.out.println("Prepare-se para o confronto final!");
                break;
            
            default:
                if (capituloAtual >= 6) {
                    iniciarBossFight();
                    return;
                }
        }
        
        System.out.println("=".repeat(50));
        aguardarEnter();
    }

    private void iniciarBossFight() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("CAPÍTULO FINAL: O VORATH O ETERNO");
        System.out.println("=".repeat(50));
        System.out.println("Você entra no salão principal do castelo.");
        System.out.println("No trono, uma figura sombria se levanta.");
        System.out.println("'Você chegou longe, " + jogador.getNome() + "...'");
        System.out.println("'Mas sua jornada termina aqui!'");
        System.out.println("=".repeat(50));
        aguardarEnter();
        
        Inimigo boss = new Inimigo(
            "Vorath, o Eterno",
            200 + (jogador.getNivel() * 20),
            20 + (jogador.getNivel() * 2),
            15 + jogador.getNivel(),
            jogador.getNivel() + 2,
            "Boss Final" 
        );
        
        boss.getInventario().adicionar(new Item("Elixir Lendário", "Restaura 100 HP", Efeito.CURA, 2, 100));
        boss.getInventario().adicionar(new Item("Essência das Trevas", "Aumenta ataque em 10", Efeito.BUFF_ATAQUE, 1, 10));
        
        System.out.println("\n" + boss.getStatus());
        aguardarEnter();
        
        batalharBoss(boss);
    }

    private void batalharBoss(Inimigo boss) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("BATALHA FINAL!");
        System.out.println("=".repeat(50));
        
        while (jogador.estaVivo() && boss.estaVivo()) {
            System.out.println("\n--- Seu turno ---");
            System.out.println("1. Atacar");
            System.out.println("2. Usar item");
            System.out.println("3. Habilidade especial");
            
            int acao = lerOpcao(1, 3);
            
            if (acao == 1) {
                int rolagemJogador = Dado.rolarD6();
                System.out.println("Você rolou: " + rolagemJogador);
                
                int danoJogador = jogador.calcularDano(rolagemJogador);
                boss.receberDano(danoJogador);
                
                System.out.println("Você causou " + danoJogador + " de dano!");
                System.out.println(boss.getStatus());
                
            } else if (acao == 2) {
                usarItem(boss);
                continue;
            } else if (acao == 3) {
                String resultado = jogador.usarHabilidadeEspecial(boss);
                System.out.println(resultado);
                System.out.println(boss.getStatus());
            }
            
            if (!boss.estaVivo()) {
                vitoriaBoss(boss);
                return;
            }
            
            System.out.println("\n--- Turno do " + boss.getNome() + " ---");
            int rolagemBoss = Dado.rolarD6();
            System.out.println(boss.getNome() + " rolou: " + rolagemBoss);
            
            if (Dado.rolar(10) >= 7) {
                System.out.println(boss.getNome() + " usa ATAQUE SOMBRIO!");
                int danoEspecial = boss.calcularDano(rolagemBoss) * 2;
                jogador.receberDano(danoEspecial);
                System.out.println("Você recebeu " + danoEspecial + " de dano devastador!");
            } else {
                int danoBoss = boss.calcularDano(rolagemBoss);
                jogador.receberDano(danoBoss);
                System.out.println("Você recebeu " + danoBoss + " de dano!");
            }
            
            System.out.println(jogador.getStatus());
            aguardarEnter();
        }
    }

    private void vitoriaBoss(Inimigo boss) {
        bossDerrotado = true;
        System.out.println("\n" + "=".repeat(50));
        System.out.println("VITÓRIA ÉPICA!");
        System.out.println("=".repeat(50));
        System.out.println("Você derrotou o " + boss.getNome() + "!");
        System.out.println("O castelo começa a desmoronar...");
        System.out.println("A escuridão se dissipa...");
        
        int xpGanho = boss.getRecompensaXP() * 3;
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
        System.out.println("\nEstatísticas finais:");
        System.out.println("Nível alcançado: " + jogador.getNivel());
        System.out.println("XP total: " + xpAtual);
        System.out.println("Explorações realizadas: " + exploracoesRealizadas);
        System.out.println("\nParabéns, " + jogador.getNome() + "!");
        System.out.println("=".repeat(50));
    }

    private void encontrarInimigo() {
        Inimigo inimigo = Inimigo.criarInimigoAleatorio(jogador.getNivel());
        System.out.println("\nUm " + inimigo.getNome() + " apareceu!");
        System.out.println(inimigo.getStatus());
        
        batalhar(inimigo);
    }

    private void batalhar(Inimigo inimigo) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("COMBATE INICIADO!");
        System.out.println("=".repeat(50));
        
        while (jogador.estaVivo() && inimigo.estaVivo()) {
            System.out.println("\n--- Seu turno ---");
            System.out.println("1. Atacar");
            System.out.println("2. Usar item");
            System.out.println("3. Tentar fugir");
            System.out.println("4. Habilidade especial");
            
            int acao = lerOpcao(1, 4);
            
            if (acao == 1) {
                int rolagemJogador = Dado.rolarD6();
                System.out.println("Você rolou: " + rolagemJogador);
                
                int danoJogador = jogador.calcularDano(rolagemJogador);
                inimigo.receberDano(danoJogador);
                
                System.out.println("Você causou " + danoJogador + " de dano!");
                System.out.println(inimigo.getStatus());
                
            } else if (acao == 2) {
                usarItem(inimigo);
                continue;
            } else if (acao == 3) {
                if (tentarFugir()) {
                    System.out.println("Você fugiu com sucesso!");
                    return;
                } else {
                    System.out.println("Não conseguiu fugir!");
                }
            } else if (acao == 4) {
                String resultado = jogador.usarHabilidadeEspecial(inimigo);
                System.out.println(resultado);
                System.out.println(inimigo.getStatus());
            }
            
            if (!inimigo.estaVivo()) {
                vitoria(inimigo);
                return;
            }
            
            System.out.println("\n--- Turno do inimigo ---");
            int rolagemInimigo = Dado.rolarD6();
            System.out.println(inimigo.getNome() + " rolou: " + rolagemInimigo);
            
            int danoInimigo = inimigo.calcularDano(rolagemInimigo);
            jogador.receberDano(danoInimigo);
            
            System.out.println("Você recebeu " + danoInimigo + " de dano!");
            System.out.println(jogador.getStatus());
            
            aguardarEnter();
        }
    }

    private boolean tentarFugir() {
        int rolagem = Dado.rolarD20();
        return rolagem >= 12;
    }

    private void vitoria(Inimigo inimigo) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("VITÓRIA!");
        System.out.println("=".repeat(50));
        System.out.println("Você derrotou " + inimigo.getNome() + "!");
        
        int xpGanho = inimigo.getRecompensaXP();
        xpAtual += xpGanho;
        System.out.println(xpGanho + " XP");
        
        if (xpAtual >= xpProximoNivel) {
            levelUp();
        }
        
        saquearInimigo(inimigo);
        aguardarEnter();
    }

    private void levelUp() {
        jogador.setNivel(jogador.getNivel() + 1);
        xpAtual -= xpProximoNivel;
        xpProximoNivel = (int) (xpProximoNivel * 1.5);
        
        jogador.setPontosVidaMaximos(jogador.getPontosVidaMaximos() + 20);
        jogador.setPontosVida(jogador.getPontosVidaMaximos());
        jogador.setAtaque(jogador.getAtaque() + 3);
        jogador.setDefesa(jogador.getDefesa() + 2);
        
        System.out.println("\nLEVEL UP! Agora você é nível " + jogador.getNivel());
        System.out.println("HP máximo aumentado!");
        System.out.println("Ataque e defesa aumentados!");
    }

    /**
     * Transfere os itens do inimigo para o inventário do jogador.
     * Se o inventário estiver cheio, imprime uma mensagem e ignora o item.
     */
    private void saquearInimigo(Inimigo inimigo) {
        List<Item> itensInimigo = inimigo.getInventario().listarOrdenado();
        
        if (itensInimigo.isEmpty()) {
            System.out.println("O inimigo não tinha itens.");
            return;
        }
        
        System.out.println("\nItens encontrados:");
        for (Item item : itensInimigo) {
            System.out.println("  - " + item.getNome() + " (x" + item.getQuantidade() + ")");
            try {
                jogador.getInventario().adicionar(item);
            } catch (IllegalStateException e) {
                System.out.println("Inventário cheio! Você não pode carregar: " + item.getNome());
            }
        }
    }

    private void encontrarItem() {
        int tipoItem = Dado.rolar(4);
        Item item = null;
        
        switch (tipoItem) {
            case 1:
                item = new Item("Poção de Vida", "Restaura 30 HP", Efeito.CURA, 1, 30);
                break;
            case 2:
                item = new Item("Poção de Força", "Aumenta ataque em 5", Efeito.BUFF_ATAQUE, 1, 5);
                break;
            case 3:
                item = new Item("Poção de Defesa", "Aumenta defesa em 5", Efeito.BUFF_DEFESA, 1, 5);
                break;
            case 4:
                item = new Item("Elixir Raro", "Restaura 50 HP", Efeito.CURA, 1, 50);
                break;
        }
        
        System.out.println("Você encontrou: " + item.getNome() + "!");
        jogador.getInventario().adicionar(item);
    }

    private void armadilha() {
        System.out.println("Você caiu em uma armadilha!");
        int dano = Dado.rolar(15);
        jogador.receberDano(dano);
        System.out.println("Você recebeu " + dano + " de dano!");
        System.out.println(jogador.getStatus());
    }

    private void usarItem(Inimigo alvoEmCombate) {
        if (jogador.getInventario().estaVazio()) {
            System.out.println("Seu inventário está vazio!");
            return;
        }
        
        System.out.println("\n" + jogador.getInventario());
        System.out.print("Digite o número do item (ou 0 para cancelar): ");
        
        int escolha = lerOpcao(0, jogador.getInventario().getTamanho());
        
        if (escolha == 0) {
            return;
        }
        
        Item item = jogador.getInventario().buscarPorIndice(escolha - 1);

        if (item == null) {
            System.out.println("Item inválido!");
            return;
        }
        
        boolean consumiu = aplicarEfeitoItem(item, alvoEmCombate);
        if (consumiu) {
            jogador.getInventario().remover(item.getNome(), 1);
            System.out.println(item.getNome() + " usado!");
        }
    }

    private boolean aplicarEfeitoItem(Item item, Inimigo alvoEmCombate) {
        switch (item.getEfeito()) {
            case CURA:
                jogador.curar(item.getValorEfeito());
                System.out.println("Você recuperou " + item.getValorEfeito() + " HP!");
                return true;

            case BUFF_ATAQUE:
                jogador.setAtaque(jogador.getAtaque() + item.getValorEfeito());
                System.out.println("Seu ataque aumentou em " + item.getValorEfeito() + "!");
                return true;

            case BUFF_DEFESA:
                jogador.setDefesa(jogador.getDefesa() + item.getValorEfeito());
                System.out.println("Sua defesa aumentou em " + item.getValorEfeito() + "!");
                return true;

            case DANO:
                if (alvoEmCombate == null) {
                    System.out.println("Este item só pode ser usado em combate!");
                    return false;
                }
                int danoBase = item.getValorEfeito();
                int variacao = Dado.rolarD6() - 3;
                int danoTotal = Math.max(1, danoBase + variacao);
                alvoEmCombate.receberDano(danoTotal);
                System.out.println("Você usou " + item.getNome() + " e causou " + danoTotal + " de dano em " + alvoEmCombate.getNome() + "!");
                System.out.println(alvoEmCombate.getStatus());
                return true;

            default:
                System.out.println("Efeito especial aplicado!");
                return true;
        }
    }

    private void verInventario() {
        System.out.println("\n" + jogador.getInventario());
    }

    private void verStatus() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(jogador.getStatus());
        System.out.println("XP: " + xpAtual + "/" + xpProximoNivel);
        System.out.println("Capítulo: " + capituloAtual);
        System.out.println("Explorações: " + exploracoesRealizadas);
        System.out.println("=".repeat(50));
    }

    private void salvarJogo() {
        if (jogador == null) {
            System.out.println("Não há jogo em andamento para salvar.");
            return;
        }

        System.out.print("Digite o nome do save: ");
        String nomeSave = lerLinha();
        if (nomeSave == null || nomeSave.trim().isEmpty()) {
            System.out.println("Nome de save inválido!");
            return;
        }

        nomeSave = nomeSave.trim();

        try {
            File pastaSaves = new File("src/saves");
            if (!pastaSaves.exists()) {
                pastaSaves.mkdirs();
            }

            File arquivoSave = new File(pastaSaves, nomeSave + ".txt");
            try (PrintWriter pw = new PrintWriter(new FileWriter(arquivoSave))) {
                pw.println("CLASSE=" + jogador.getClass().getSimpleName());
                pw.println("NOME=" + jogador.getNome());
                pw.println("NIVEL=" + jogador.getNivel());
                pw.println("HP_ATUAL=" + jogador.getPontosVida());
                pw.println("HP_MAX=" + jogador.getPontosVidaMaximos());
                pw.println("ATAQUE=" + jogador.getAtaque());
                pw.println("DEFESA=" + jogador.getDefesa());
                pw.println("XP_ATUAL=" + xpAtual);
                pw.println("XP_PROX=" + xpProximoNivel);
                pw.println("CAPITULO=" + capituloAtual);
                pw.println("EXPLORACOES=" + exploracoesRealizadas);
                pw.println("BOSS_DERROTADO=" + bossDerrotado);

                pw.println("ITENS_INICIO");
                for (Item item : jogador.getInventario().listarOrdenado()) {
                    pw.println(
                        item.getNome() + ";" +
                        item.getDescricao() + ";" +
                        item.getEfeito().name() + ";" +
                        item.getQuantidade() + ";" +
                        item.getValorEfeito()
                    );
                }
                pw.println("ITENS_FIM");
            }

            System.out.println("Jogo salvo em: " + arquivoSave.getPath());
        } catch (Exception e) {
            System.out.println("Erro ao salvar jogo: " + e.getMessage());
        }
    }

    private boolean carregarJogo(boolean fromTelaInicial) {
        File pastaSaves = new File("src/saves");
        if (!pastaSaves.exists() || !pastaSaves.isDirectory()) {
            System.out.println("Nenhum save encontrado (pasta src/saves não existe).");
            return false;
        }

        File[] arquivos = pastaSaves.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        if (arquivos == null || arquivos.length == 0) {
            System.out.println("Nenhum arquivo de save encontrado em src/saves.");
            return false;
        }

        System.out.println("\n=== Saves disponíveis ===");
        List<File> listaSaves = new ArrayList<>();
        for (File f : arquivos) {
            listaSaves.add(f);
        }
        listaSaves.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        for (int i = 0; i < listaSaves.size(); i++) {
            String nome = listaSaves.get(i).getName();
            if (nome.toLowerCase().endsWith(".txt")) {
                nome = nome.substring(0, nome.length() - 4);
            }
            System.out.printf("%d. %s%n", i + 1, nome);
        }
        System.out.println("0. Cancelar");
        System.out.print("Escolha o save: ");

        int escolha = lerOpcao(0, listaSaves.size());
        if (escolha == 0) {
            System.out.println("Carregamento cancelado.");
            return false;
        }

        File arquivoSave = listaSaves.get(escolha - 1);

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoSave))) {
            String linha;
            String classe = null;
            String nome = null;
            int nivel = 1;
            int hpAtual = 0;
            int hpMax = 0;
            int ataque = 0;
            int defesa = 0;
            int xpAtualLido = 0;
            int xpProxLido = 100;
            int capituloLido = 1;
            int exploracoesLidas = 0;
            boolean bossDerrotadoLido = false;

            List<Item> itensLidos = new ArrayList<>();
            boolean lendoItens = false;

            while ((linha = br.readLine()) != null) {
                if ("ITENS_INICIO".equals(linha)) {
                    lendoItens = true;
                    continue;
                }
                if ("ITENS_FIM".equals(linha)) {
                    lendoItens = false;
                    continue;
                }

                if (lendoItens) {
                    String[] partes = linha.split(";");
                    if (partes.length == 5) {
                        String nItem = partes[0];
                        String desc = partes[1];
                        Efeito efeito = Efeito.valueOf(partes[2]);
                        int qtd = Integer.parseInt(partes[3]);
                        int val = Integer.parseInt(partes[4]);
                        itensLidos.add(new Item(nItem, desc, efeito, qtd, val));
                    }
                } else {
                    String[] partes = linha.split("=", 2);
                    if (partes.length != 2) continue;
                    String chave = partes[0];
                    String valor = partes[1];

                    switch (chave) {
                        case "CLASSE": classe = valor; break;
                        case "NOME": nome = valor; break;
                        case "NIVEL": nivel = Integer.parseInt(valor); break;
                        case "HP_ATUAL": hpAtual = Integer.parseInt(valor); break;
                        case "HP_MAX": hpMax = Integer.parseInt(valor); break;
                        case "ATAQUE": ataque = Integer.parseInt(valor); break;
                        case "DEFESA": defesa = Integer.parseInt(valor); break;
                        case "XP_ATUAL": xpAtualLido = Integer.parseInt(valor); break;
                        case "XP_PROX": xpProxLido = Integer.parseInt(valor); break;
                        case "CAPITULO": capituloLido = Integer.parseInt(valor); break;
                        case "EXPLORACOES": exploracoesLidas = Integer.parseInt(valor); break;
                        case "BOSS_DERROTADO": bossDerrotadoLido = Boolean.parseBoolean(valor); break;
                    }
                }
            }

            if ("Guerreiro".equals(classe)) {
                jogador = new Guerreiro(nome, hpMax, ataque, defesa, nivel);
            } else if ("Mago".equals(classe)) {
                jogador = new Mago(nome, hpMax, ataque, defesa, nivel);
            } else if ("Arqueiro".equals(classe)) {
                jogador = new Arqueiro(nome, hpMax, ataque, defesa, nivel);
            } else {
                System.out.println("Classe inválida no save!");
                return false;
            }

            jogador.setPontosVidaMaximos(hpMax);
            jogador.setPontosVida(hpAtual);
            jogador.setAtaque(ataque);
            jogador.setDefesa(defesa);
            jogador.setNivel(nivel);

            jogador.getInventario().limpar();
            for (Item item : itensLidos) {
                jogador.getInventario().adicionar(item);
            }

            this.xpAtual = xpAtualLido;
            this.xpProximoNivel = xpProxLido;
            this.capituloAtual = capituloLido;
            this.exploracoesRealizadas = exploracoesLidas;
            this.bossDerrotado = bossDerrotadoLido;

            System.out.println("\nSave carregado com sucesso!");
            System.out.println(jogador.getStatus());
            System.out.println("Capítulo: " + capituloAtual + " | Explorações: " + exploracoesRealizadas);

            return true;
        } catch (Exception e) {
            System.out.println("Erro ao carregar jogo: " + e.getMessage());
            return false;
        }
    }

    private void sair() {
        System.out.println("\nTem certeza que deseja sair? (s/n)");
        String resposta = lerLinha();
        if (resposta.equalsIgnoreCase("s")) {
            jogoAtivo = false;
            System.out.println("\nObrigado por jogar!");
        }
    }

    private void gameOver() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("GAME OVER");
        System.out.println("=".repeat(50));
        System.out.println("Você foi derrotado...");
        System.out.println("Nível alcançado: " + jogador.getNivel());
        System.out.println("XP total: " + xpAtual);
        System.out.println("Capítulo alcançado: " + capituloAtual);
    }

    private int lerOpcao(int min, int max) {
        while (true) {
            try {
                String linha = reader.readLine();
                int opcao = Integer.parseInt(linha.trim());
                if (opcao >= min && opcao <= max) {
                    return opcao;
                }
                System.out.print("Opção inválida! Digite entre " + min + " e " + max + ": ");
            } catch (IOException | NumberFormatException e) {
                System.out.print("Entrada inválida! Digite um número: ");
            }
        }
    }

    private String lerLinha() {
        try {
            String linha = reader.readLine();
            if (linha == null) {
                return "";
            }
            return linha.trim();
        } catch (IOException e) {
            return "";
        }
    }

    private void aguardarEnter() {
        System.out.println("\n[Pressione ENTER para continuar]");
        try {
            reader.readLine();
        } catch (IOException e) {
        }
    }
}
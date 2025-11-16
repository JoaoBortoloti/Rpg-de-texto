package sistema;

import personagens.*;
import itens.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class Jogo {
    private Personagem jogador;
    private BufferedReader reader;
    private int xpAtual;
    private int xpProximoNivel;
    private boolean jogoAtivo;
    private Personagem savePoint;
    private int exploracoesRealizadas;
    private int capituloAtual;
    private boolean bossDerrotado;

    public Jogo() {
        this.reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        this.xpAtual = 0;
        this.xpProximoNivel = 100;
        this.jogoAtivo = true;
        this.exploracoesRealizadas = 0;
        this.capituloAtual = 1;
        this.bossDerrotado = false;
    }

    public void iniciar() {
        exibirBanner();
        criarPersonagem();
        introducao();
        loopPrincipal();
    }

    private void exibirBanner() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║     RPG DE TEXTO - AVENTURA POO       ║");
        System.out.println("╚════════════════════════════════════════╝");
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
        
        // Adiciona itens iniciais
        jogador.getInventario().adicionar(new Item("Poção de Vida", "Restaura 30 HP", Efeito.CURA, 3, 30));
        jogador.getInventario().adicionar(new Item("Poção de Força", "Aumenta ataque em 5", Efeito.BUFF_ATAQUE, 1, 5));
        
        System.out.println("\nPersonagem criado com sucesso!");
        System.out.println(jogador.getStatus());
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
            int opcao = lerOpcao(1, 7);
            
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
                    criarSavePoint();
                    break;
                case 6:
                    restaurarSavePoint();
                    break;
                case 7:
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
        System.out.println("5. Criar save point");
        System.out.println("6. Restaurar save point");
        System.out.println("7. Sair do jogo");
        System.out.print("Escolha: ");
    }

    private void explorar() {
        System.out.println("\nExplorando...");
        System.out.println("[DEBUG] Explorações realizadas: " + exploracoesRealizadas);
        System.out.println("\nExplorando...");
        exploracoesRealizadas++;
        
        // Verifica progressão da história
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
        
        // Cria o boss com stats aumentados
        Inimigo boss = new Inimigo(
            "Vorath, o Eterno",
            200 + (jogador.getNivel() * 20),
            20 + (jogador.getNivel() * 2),
            15 + jogador.getNivel(),
            jogador.getNivel() + 2,
            "Boss Final" 
        );
        
        // Adiciona itens raros ao boss
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
            
            // Turno do boss (ataque mais forte)
            System.out.println("\n--- Turno do " + boss.getNome() + " ---");
            int rolagemBoss = Dado.rolarD6();
            System.out.println(boss.getNome() + " rolou: " + rolagemBoss);
            
            // Boss tem chance de ataque especial
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

    private void saquearInimigo(Inimigo inimigo) {
        List<Item> itensInimigo = inimigo.getInventario().listarOrdenado();
        
        if (itensInimigo.isEmpty()) {
            System.out.println("O inimigo não tinha itens.");
            return;
        }
        
        System.out.println("\nItens encontrados:");
        for (Item item : itensInimigo) {
            System.out.println("  - " + item.getNome() + " (x" + item.getQuantidade() + ")");
            jogador.getInventario().adicionar(item);
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
                int variacao = Dado.rolarD6() - 3; // pequena variação (-2 a +3)
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

    private void criarSavePoint() {
        if (jogador instanceof Guerreiro) {
            savePoint = new Guerreiro((Guerreiro) jogador);
        } else if (jogador instanceof Mago) {
            savePoint = new Mago((Mago) jogador);
        } else if (jogador instanceof Arqueiro) {
            savePoint = new Arqueiro((Arqueiro) jogador);
        }
        
        System.out.println("Save point criado! Você pode restaurar este estado mais tarde.");
    }
    
    private void restaurarSavePoint() {
        if (savePoint == null) {
            System.out.println("Nenhum save point disponível!");
            return;
        }

        if (savePoint instanceof Guerreiro) {
            jogador = new Guerreiro((Guerreiro) savePoint);
        } else if (savePoint instanceof Mago) {
            jogador = new Mago((Mago) savePoint);
        } else if (savePoint instanceof Arqueiro) {
            jogador = new Arqueiro((Arqueiro) savePoint);
        }

        System.out.println("Save point restaurado! Seu progresso foi revertido para o estado salvo.");
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
            return reader.readLine().trim();
        } catch (IOException e) {
            return "";
        }
    }

    private void aguardarEnter() {
        System.out.println("\n[Pressione ENTER para continuar]");
        try {
            reader.readLine();
        } catch (IOException e) {
            // Ignora
        }
    }
}
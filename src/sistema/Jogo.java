package sistema;

import personagens.*;
import itens.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Jogo {
    private Personagem jogador;
    private BufferedReader reader;
    private int xpAtual;
    private int xpProximoNivel;
    private boolean jogoAtivo;
    private Personagem savePoint;

    public Jogo() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.xpAtual = 0;
        this.xpProximoNivel = 100;
        this.jogoAtivo = true;
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
        
        System.out.println("\n✅ Personagem criado com sucesso!");
        System.out.println(jogador.getStatus());
    }

    private void introducao() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Você acorda em uma floresta escura...");
        System.out.println("Não se lembra de como chegou aqui.");
        System.out.println("Ao longe, você vê as ruínas de um castelo antigo.");
        System.out.println("Sua jornada começa agora!");
        System.out.println("=".repeat(50) + "\n");
        aguardarEnter();
    }

    private void loopPrincipal() {
        while (jogoAtivo && jogador.estaVivo()) {
            exibirMenu();
            int opcao = lerOpcao(1, 7);
            
            switch (opcao) {
                case 1:
                    explorar();
                    break;
                case 2:
                    usarItem();
                    break;
                case 3:
                    verInventario();
                    break;
                case 4:
                    verStatus();
                    break;
                case 5:
                    usarHabilidadeEspecial();
                    break;
                case 6:
                    criarSavePoint();
                    break;
                case 7:
                    sair();
                    break;
            }
        }
        
        if (!jogador.estaVivo()) {
            gameOver();
        }
    }

    private void exibirMenu() {
        System.out.println("\n" + "─".repeat(50));
        System.out.println("O que deseja fazer?");
        System.out.println("1. Explorar");
        System.out.println("2. Usar item");
        System.out.println("3. Ver inventário");
        System.out.println("4. Ver status");
        System.out.println("5. Usar habilidade especial");
        System.out.println("6. Criar save point");
        System.out.println("7. Sair do jogo");
        System.out.print("Escolha: ");
    }

    private void explorar() {
        System.out.println("\n🔍 Explorando...");
        int evento = Dado.rolar(10);
        
        if (evento <= 5) {
            // Encontrou inimigo
            encontrarInimigo();
        } else if (evento <= 7) {
            // Encontrou item
            encontrarItem();
        } else if (evento == 8) {
            // Armadilha
            armadilha();
        } else {
            // Nada aconteceu
            System.out.println("Você explorou a área mas não encontrou nada interessante.");
        }
    }

    private void encontrarInimigo() {
        Inimigo inimigo = Inimigo.criarInimigoAleatorio(jogador.getNivel());
        System.out.println("\n⚔️ Um " + inimigo.getNome() + " apareceu!");
        System.out.println(inimigo.getStatus());
        
        batalhar(inimigo);
    }

    private void batalhar(Inimigo inimigo) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("⚔️ COMBATE INICIADO!");
        System.out.println("=".repeat(50));
        
        while (jogador.estaVivo() && inimigo.estaVivo()) {
            System.out.println("\n--- Seu turno ---");
            System.out.println("1. Atacar");
            System.out.println("2. Usar item");
            System.out.println("3. Tentar fugir");
            
            int acao = lerOpcao(1, 3);
            
            if (acao == 1) {
                // Jogador ataca
                int rolagemJogador = Dado.rolarD6();
                System.out.println("🎲 Você rolou: " + rolagemJogador);
                
                int danoJogador = jogador.calcularDano(rolagemJogador);
                inimigo.receberDano(danoJogador);
                
                System.out.println("💥 Você causou " + danoJogador + " de dano!");
                System.out.println(inimigo.getStatus());
                
            } else if (acao == 2) {
                usarItem();
                continue; // Não conta como turno de ataque
            } else {
                // Tentar fugir
                if (tentarFugir()) {
                    System.out.println("🏃 Você fugiu com sucesso!");
                    return;
                } else {
                    System.out.println("❌ Não conseguiu fugir!");
                }
            }
            
            if (!inimigo.estaVivo()) {
                vitoria(inimigo);
                return;
            }
            
            // Turno do inimigo
            System.out.println("\n--- Turno do inimigo ---");
            int rolagemInimigo = Dado.rolarD6();
            System.out.println("🎲 " + inimigo.getNome() + " rolou: " + rolagemInimigo);
            
            int danoInimigo = inimigo.calcularDano(rolagemInimigo);
            jogador.receberDano(danoInimigo);
            
            System.out.println("💥 Você recebeu " + danoInimigo + " de dano!");
            System.out.println(jogador.getStatus());
            
            aguardarEnter();
        }
    }

    private boolean tentarFugir() {
        int rolagem = Dado.rolarD20();
        return rolagem >= 12; // 45% de chance
    }

    private void vitoria(Inimigo inimigo) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🎉 VITÓRIA!");
        System.out.println("=".repeat(50));
        System.out.println("Você derrotou " + inimigo.getNome() + "!");
        
        // Ganhar XP
        int xpGanho = inimigo.getRecompensaXP();
        xpAtual += xpGanho;
        System.out.println("📈 +" + xpGanho + " XP");
        
        // Verificar level up
        if (xpAtual >= xpProximoNivel) {
            levelUp();
        }
        
        // Saquear itens
        saquearInimigo(inimigo);
        
        aguardarEnter();
    }

    private void levelUp() {
        jogador.setNivel(jogador.getNivel() + 1);
        xpAtual -= xpProximoNivel;
        xpProximoNivel = (int) (xpProximoNivel * 1.5);
        
        // Aumentar atributos
        jogador.setPontosVidaMaximos(jogador.getPontosVidaMaximos() + 20);
        jogador.setPontosVida(jogador.getPontosVidaMaximos());
        jogador.setAtaque(jogador.getAtaque() + 3);
        jogador.setDefesa(jogador.getDefesa() + 2);
        
        System.out.println("\n⭐ LEVEL UP! Agora você é nível " + jogador.getNivel());
        System.out.println("HP máximo aumentado!");
        System.out.println("Ataque e defesa aumentados!");
    }

    private void saquearInimigo(Inimigo inimigo) {
        List<Item> itensInimigo = inimigo.getInventario().listarOrdenado();
        
        if (itensInimigo.isEmpty()) {
            System.out.println("O inimigo não tinha itens.");
            return;
        }
        
        System.out.println("\n💰 Itens encontrados:");
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
        
        System.out.println("✨ Você encontrou: " + item.getNome() + "!");
        jogador.getInventario().adicionar(item);
    }

    private void armadilha() {
        System.out.println("⚠️ Você caiu em uma armadilha!");
        int dano = Dado.rolar(15);
        jogador.receberDano(dano);
        System.out.println("💥 Você recebeu " + dano + " de dano!");
        System.out.println(jogador.getStatus());
    }

    private void usarItem() {
        if (jogador.getInventario().estaVazio()) {
            System.out.println("❌ Seu inventário está vazio!");
            return;
        }
        
        System.out.println("\n" + jogador.getInventario());
        System.out.print("Digite o nome do item (ou 'cancelar'): ");
        String nomeItem = lerLinha();
        
        if (nomeItem.equalsIgnoreCase("cancelar")) {
            return;
        }
        
        Item item = jogador.getInventario().buscarPorNome(nomeItem);
        
        if (item == null) {
            System.out.println("❌ Item não encontrado!");
            return;
        }
        
        aplicarEfeitoItem(item);
        jogador.getInventario().remover(item.getNome(), 1);
        System.out.println("✅ " + item.getNome() + " usado!");
    }

    private void aplicarEfeitoItem(Item item) {
        switch (item.getEfeito()) {
            case CURA:
                jogador.curar(item.getValorEfeito());
                System.out.println("💚 Você recuperou " + item.getValorEfeito() + " HP!");
                break;
            case BUFF_ATAQUE:
                jogador.setAtaque(jogador.getAtaque() + item.getValorEfeito());
                System.out.println("⚔️ Seu ataque aumentou em " + item.getValorEfeito() + "!");
                break;
            case BUFF_DEFESA:
                jogador.setDefesa(jogador.getDefesa() + item.getValorEfeito());
                System.out.println("🛡️ Sua defesa aumentou em " + item.getValorEfeito() + "!");
                break;
            case DANO:
                System.out.println("💣 Item de dano! Use em combate.");
                break;
            default:
                System.out.println("✨ Efeito especial aplicado!");
        }
    }

    private void verInventario() {
        System.out.println("\n" + jogador.getInventario());
    }

    private void verStatus() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(jogador.getStatus());
        System.out.println("XP: " + xpAtual + "/" + xpProximoNivel);
        System.out.println("=".repeat(50));
    }

    private void usarHabilidadeEspecial() {
        String resultado = jogador.usarHabilidadeEspecial();
        System.out.println("\n" + resultado);
    }

    private void criarSavePoint() {
        if (jogador instanceof Guerreiro) {
            savePoint = new Guerreiro((Guerreiro) jogador);
        } else if (jogador instanceof Mago) {
            savePoint = new Mago((Mago) jogador);
        } else if (jogador instanceof Arqueiro) {
            savePoint = new Arqueiro((Arqueiro) jogador);
        }
        
        System.out.println("💾 Save point criado! Você pode restaurar este estado mais tarde.");
    }

    private void sair() {
        System.out.println("\nTem certeza que deseja sair? (s/n)");
        String resposta = lerLinha();
        if (resposta.equalsIgnoreCase("s")) {
            jogoAtivo = false;
            System.out.println("\n👋 Obrigado por jogar!");
        }
    }

    private void gameOver() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("💀 GAME OVER");
        System.out.println("=".repeat(50));
        System.out.println("Você foi derrotado...");
        System.out.println("Nível alcançado: " + jogador.getNivel());
        System.out.println("XP total: " + xpAtual);
    }

    private int lerOpcao(int min, int max) {
        while (true) {
            try {
                String linha = reader.readLine();
                int opcao = Integer.parseInt(linha.trim());
                if (opcao >= min && opcao <= max) {
                    return opcao;
                }
                System.out.print("❌ Opção inválida! Digite entre " + min + " e " + max + ": ");
            } catch (IOException | NumberFormatException e) {
                System.out.print("❌ Entrada inválida! Digite um número: ");
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
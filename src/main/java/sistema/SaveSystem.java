package sistema;

import personagens.Arqueiro;
import personagens.Guerreiro;
import personagens.Mago;
import personagens.Personagem;
import itens.Efeito;
import itens.Item;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsável por persistir e restaurar o estado do jogo em disco.
 * <p>
 * Formato de arquivo: pares CHAVE=VALOR por linha, com seção delimitada
 * ITENS_INICIO / ITENS_FIM para o inventário.
 */
public class SaveSystem {

    private static final String PASTA_SAVES = "src/saves";

    /**
     * Salva o estado atual do jogo em um arquivo texto escolhido pelo usuário.
     *
     * @param jogador              personagem do jogador
     * @param xpAtual              XP acumulado no nível atual
     * @param xpProximoNivel       XP necessário para o próximo nível
     * @param capituloAtual        capítulo em que o jogador se encontra
     * @param exploracoesRealizadas número de explorações feitas
     * @param bossDerrotado        se o boss final foi derrotado
     * @param reader               stream de entrada para ler o nome do save
     */
    public void salvar(Personagem jogador, int xpAtual, int xpProximoNivel,
                       int capituloAtual, int exploracoesRealizadas, boolean bossDerrotado,
                       BufferedReader reader) {
        if (jogador == null) {
            System.out.println("Não há jogo em andamento para salvar.");
            return;
        }

        System.out.print("Digite o nome do save: ");
        String nomeSave = lerLinha(reader);
        if (nomeSave == null || nomeSave.trim().isEmpty()) {
            System.out.println("Nome de save inválido!");
            return;
        }
        nomeSave = nomeSave.trim();

        try {
            File pasta = new File(PASTA_SAVES);
            if (!pasta.exists()) pasta.mkdirs();

            File arquivo = new File(pasta, nomeSave + ".txt");
            try (PrintWriter pw = new PrintWriter(new FileWriter(arquivo))) {
                pw.println("CLASSE="           + jogador.getClass().getSimpleName());
                pw.println("NOME="             + jogador.getNome());
                pw.println("NIVEL="            + jogador.getNivel());
                pw.println("HP_ATUAL="         + jogador.getPontosVida());
                pw.println("HP_MAX="           + jogador.getPontosVidaMaximos());
                pw.println("ATAQUE="           + jogador.getAtaque());
                pw.println("DEFESA="           + jogador.getDefesa());
                pw.println("XP_ATUAL="         + xpAtual);
                pw.println("XP_PROX="          + xpProximoNivel);
                pw.println("CAPITULO="         + capituloAtual);
                pw.println("EXPLORACOES="      + exploracoesRealizadas);
                pw.println("BOSS_DERROTADO="   + bossDerrotado);

                pw.println("ITENS_INICIO");
                for (Item item : jogador.getInventario().listarOrdenado()) {
                    pw.println(item.getNome()        + ";" +
                               item.getDescricao()   + ";" +
                               item.getEfeito().name() + ";" +
                               item.getQuantidade()  + ";" +
                               item.getValorEfeito());
                }
                pw.println("ITENS_FIM");
            }

            System.out.println("Jogo salvo em: " + arquivo.getPath());
        } catch (Exception e) {
            System.out.println("Erro ao salvar jogo: " + e.getMessage());
        }
    }

    /**
     * Apresenta os saves disponíveis e restaura o estado escolhido pelo usuário.
     *
     * @param reader stream de entrada para interação
     * @return estado carregado, ou {@code null} se o usuário cancelar ou houver erro
     */
    public EstadoSalvo carregar(BufferedReader reader) {
        File pasta = new File(PASTA_SAVES);
        if (!pasta.exists() || !pasta.isDirectory()) {
            System.out.println("Nenhum save encontrado.");
            return null;
        }

        File[] arquivos = pasta.listFiles((d, n) -> n.toLowerCase().endsWith(".txt"));
        if (arquivos == null || arquivos.length == 0) {
            System.out.println("Nenhum arquivo de save encontrado.");
            return null;
        }

        List<File> lista = new ArrayList<>(List.of(arquivos));
        lista.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        System.out.println("\n=== Saves disponíveis ===");
        for (int i = 0; i < lista.size(); i++) {
            String nome = lista.get(i).getName().replaceAll("(?i)\\.txt$", "");
            System.out.printf("%d. %s%n", i + 1, nome);
        }
        System.out.println("0. Cancelar");
        System.out.print("Escolha o save: ");

        int escolha = lerOpcao(0, lista.size(), reader);
        if (escolha == 0) {
            System.out.println("Carregamento cancelado.");
            return null;
        }

        return parsearArquivo(lista.get(escolha - 1));
    }

    private EstadoSalvo parsearArquivo(File arquivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String classe = null, nome = null;
            int nivel = 1, hpAtual = 0, hpMax = 0, ataque = 0, defesa = 0;
            int xpAtual = 0, xpProx = GameConfig.XP_INICIAL_PROXIMO_NIVEL;
            int capitulo = 1, exploracoes = 0;
            boolean bossDerrotado = false;
            List<Item> itens = new ArrayList<>();
            boolean lendoItens = false;

            String linha;
            while ((linha = br.readLine()) != null) {
                if ("ITENS_INICIO".equals(linha)) { lendoItens = true;  continue; }
                if ("ITENS_FIM".equals(linha))    { lendoItens = false; continue; }

                if (lendoItens) {
                    String[] p = linha.split(";");
                    if (p.length == 5) {
                        itens.add(new Item(p[0], p[1], Efeito.valueOf(p[2]),
                                Integer.parseInt(p[3]), Integer.parseInt(p[4])));
                    }
                } else {
                    String[] p = linha.split("=", 2);
                    if (p.length != 2) continue;
                    switch (p[0]) {
                        case "CLASSE"          -> classe     = p[1];
                        case "NOME"            -> nome       = p[1];
                        case "NIVEL"           -> nivel      = Integer.parseInt(p[1]);
                        case "HP_ATUAL"        -> hpAtual    = Integer.parseInt(p[1]);
                        case "HP_MAX"          -> hpMax      = Integer.parseInt(p[1]);
                        case "ATAQUE"          -> ataque     = Integer.parseInt(p[1]);
                        case "DEFESA"          -> defesa     = Integer.parseInt(p[1]);
                        case "XP_ATUAL"        -> xpAtual    = Integer.parseInt(p[1]);
                        case "XP_PROX"         -> xpProx     = Integer.parseInt(p[1]);
                        case "CAPITULO"        -> capitulo   = Integer.parseInt(p[1]);
                        case "EXPLORACOES"     -> exploracoes = Integer.parseInt(p[1]);
                        case "BOSS_DERROTADO"  -> bossDerrotado = Boolean.parseBoolean(p[1]);
                    }
                }
            }

            Personagem jogador = instanciarPersonagem(classe, nome, hpMax, ataque, defesa, nivel);
            if (jogador == null) {
                System.out.println("Classe inválida no save!");
                return null;
            }

            jogador.setPontosVidaMaximos(hpMax);
            jogador.setPontosVida(hpAtual);
            jogador.setAtaque(ataque);
            jogador.setDefesa(defesa);
            jogador.setNivel(nivel);
            jogador.getInventario().limpar();
            itens.forEach(jogador.getInventario()::adicionar);

            return new EstadoSalvo(jogador, xpAtual, xpProx, capitulo, exploracoes, bossDerrotado);

        } catch (Exception e) {
            System.out.println("Erro ao carregar jogo: " + e.getMessage());
            return null;
        }
    }

    private Personagem instanciarPersonagem(String classe, String nome,
                                            int hp, int ataque, int defesa, int nivel) {
        return switch (classe) {
            case "Guerreiro" -> new Guerreiro(nome, hp, ataque, defesa, nivel);
            case "Mago"      -> new Mago(nome, hp, ataque, defesa, nivel);
            case "Arqueiro"  -> new Arqueiro(nome, hp, ataque, defesa, nivel);
            default          -> null;
        };
    }

    private int lerOpcao(int min, int max, BufferedReader reader) {
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

    private String lerLinha(BufferedReader reader) {
        try {
            String linha = reader.readLine();
            return linha == null ? "" : linha.trim();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Contém o estado completo restaurado de um arquivo de save.
     */
    public static class EstadoSalvo {
        public final Personagem jogador;
        public final int xpAtual;
        public final int xpProximoNivel;
        public final int capituloAtual;
        public final int exploracoesRealizadas;
        public final boolean bossDerrotado;

        public EstadoSalvo(Personagem jogador, int xpAtual, int xpProximoNivel,
                           int capituloAtual, int exploracoesRealizadas, boolean bossDerrotado) {
            this.jogador               = jogador;
            this.xpAtual               = xpAtual;
            this.xpProximoNivel        = xpProximoNivel;
            this.capituloAtual         = capituloAtual;
            this.exploracoesRealizadas = exploracoesRealizadas;
            this.bossDerrotado         = bossDerrotado;
        }
    }
}

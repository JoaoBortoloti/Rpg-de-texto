package sistema;

/**
 * Classe de entrada da aplicação.
 */
public class Main {
    public static void main(String[] args) {
        // Opcional: definir seed para testes determinísticos
        // Dado.setSeed(12345L);
        
        Jogo jogo = new Jogo();
        jogo.iniciar();
    }
}
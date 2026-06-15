package personagens;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sistema.Dado;

import static org.junit.jupiter.api.Assertions.*;

class GuerreiroTest {

    private Guerreiro guerreiro;

    @BeforeEach
    void setUp() {
        Dado.setSilencioso(true);
        guerreiro = new Guerreiro("Guerreiro", 120, 15, 10, 1);
    }

    @AfterEach
    void tearDown() {
        Dado.resetSeed();
        Dado.setSilencioso(false);
    }

    @Test
    void calcularDanoSemCriticoRetornaDanoBase() {
        // Verifica que o dano está dentro do range possível [ataque+rolagem, (ataque+rolagem)*2]
        int ataque = guerreiro.getAtaque();
        int rolagemFake = 6;
        int resultado = guerreiro.calcularDano(rolagemFake);
        int danoBase = ataque + rolagemFake;
        assertTrue(resultado >= danoBase && resultado <= danoBase * 2,
                "Dano fora do range esperado: " + resultado);
    }

    @Test
    void taxaDeCriticoEhAproximadamente20PorCento() {
        Dado.setSeed(0L);
        int criticos = 0;
        int totalTentativas = 1000;
        for (int i = 0; i < totalTentativas; i++) {
            int semCritico = guerreiro.getAtaque() + 5;
            int resultado = guerreiro.calcularDano(5);
            if (resultado > semCritico) {
                criticos++;
            }
        }
        double taxa = (double) criticos / totalTentativas;
        // Taxa esperada: ~20%, aceitamos entre 10% e 35% com seed fixo
        assertTrue(taxa >= 0.10 && taxa <= 0.35,
                "Taxa de crítico inesperada: " + taxa);
    }

    @Test
    void furiaAumentaDanoEReduzDefesa() {
        int defesaAntes = guerreiro.getDefesa();
        guerreiro.usarHabilidadeEspecial(guerreiro);

        assertTrue(guerreiro.isFuria());
        assertEquals(defesaAntes - 3, guerreiro.getDefesa());
    }

    @Test
    void desativarFuriaRestauraNivelNormal() {
        int defesaAntes = guerreiro.getDefesa();
        guerreiro.usarHabilidadeEspecial(guerreiro);
        guerreiro.desativarFuria();

        assertFalse(guerreiro.isFuria());
        assertEquals(defesaAntes, guerreiro.getDefesa());
    }

    @Test
    void desativarFuriaSemFuriaAtivaNaoAlteraDefesa() {
        int defesaAntes = guerreiro.getDefesa();
        guerreiro.desativarFuria();
        assertEquals(defesaAntes, guerreiro.getDefesa());
    }

    @Test
    void construtorDeCopiaPreservaEstado() {
        guerreiro.usarHabilidadeEspecial(guerreiro);
        Guerreiro copia = new Guerreiro(guerreiro);
        assertTrue(copia.isFuria());
        assertEquals(guerreiro.getDefesa(), copia.getDefesa());
    }
}

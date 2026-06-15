package personagens;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sistema.Dado;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa o comportamento herdado de Personagem usando Guerreiro como
 * implementação concreta (não testamos Personagem diretamente pois é abstrata).
 */
class PersonagemBaseTest {

    private Guerreiro guerreiro;

    @BeforeEach
    void setUp() {
        Dado.setSilencioso(true);
        guerreiro = new Guerreiro("Herói", 100, 15, 10, 1);
    }

    @AfterEach
    void tearDown() {
        Dado.resetSeed();
        Dado.setSilencioso(false);
    }

    @Test
    void construtorComNomeVazioLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> new Guerreiro("", 100, 15, 10, 1));
    }

    @Test
    void construtorComHpZeroLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> new Guerreiro("X", 0, 15, 10, 1));
    }

    @Test
    void construtorComNivelZeroLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> new Guerreiro("X", 100, 15, 10, 0));
    }

    @Test
    void receberDanoReduzHp() {
        guerreiro.receberDano(20);
        assertEquals(90, guerreiro.getPontosVida());
    }

    @Test
    void receberDanoComDefesaAbsorveParte() {
        // ataque 10, defesa 10 → dano real = 0
        guerreiro.receberDano(10);
        assertEquals(100, guerreiro.getPontosVida());
    }

    @Test
    void receberDanoNaoVaiAbaixoDeZero() {
        guerreiro.receberDano(999);
        assertEquals(0, guerreiro.getPontosVida());
    }

    @Test
    void estaVivoRetornaTrueComHpPositivo() {
        assertTrue(guerreiro.estaVivo());
    }

    @Test
    void estaVivoRetornaFalseComHpZero() {
        guerreiro.receberDano(999);
        assertFalse(guerreiro.estaVivo());
    }

    @Test
    void curarAumentaHp() {
        guerreiro.receberDano(50);
        guerreiro.curar(20);
        assertEquals(70, guerreiro.getPontosVida());
    }

    @Test
    void curarNaoUltrapassaHpMaximo() {
        guerreiro.curar(999);
        assertEquals(100, guerreiro.getPontosVida());
    }

    @Test
    void setAtaqueNaoPermiteNegativo() {
        guerreiro.setAtaque(-5);
        assertEquals(0, guerreiro.getAtaque());
    }

    @Test
    void setDefesaNaoPermiteNegativo() {
        guerreiro.setDefesa(-5);
        assertEquals(0, guerreiro.getDefesa());
    }

    @Test
    void construtorDeCopiaEhIndependente() {
        Guerreiro copia = new Guerreiro(guerreiro);
        copia.receberDano(999);
        assertTrue(guerreiro.estaVivo());
        assertFalse(copia.estaVivo());
    }
}

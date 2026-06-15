package sistema;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DadoTest {

    @BeforeEach
    void setUp() {
        Dado.setSilencioso(true);
    }

    @AfterEach
    void tearDown() {
        Dado.resetSeed();
        Dado.setSilencioso(false);
    }

    @Test
    void rolarDeveRetornarValorDentroDoRange() {
        for (int i = 0; i < 1000; i++) {
            int resultado = Dado.rolar(6);
            assertTrue(resultado >= 1 && resultado <= 6,
                    "Esperado entre 1 e 6, obtido: " + resultado);
        }
    }

    @Test
    void rolarD6DeveUsarSeissFaces() {
        for (int i = 0; i < 500; i++) {
            int resultado = Dado.rolarD6();
            assertTrue(resultado >= 1 && resultado <= 6);
        }
    }

    @Test
    void rolarD20DeveUsarVinteFaces() {
        for (int i = 0; i < 500; i++) {
            int resultado = Dado.rolarD20();
            assertTrue(resultado >= 1 && resultado <= 20);
        }
    }

    @Test
    void seedDeterministicaProduzesMesmaSequencia() {
        Dado.setSeed(42L);
        int a1 = Dado.rolar(20);
        int a2 = Dado.rolar(20);

        Dado.setSeed(42L);
        int b1 = Dado.rolar(20);
        int b2 = Dado.rolar(20);

        assertEquals(a1, b1);
        assertEquals(a2, b2);
    }

    @Test
    void resetSeedVoltaAoModoAleatorio() {
        Dado.setSeed(42L);
        assertNotNull(Dado.getSeed());

        Dado.resetSeed();
        assertNull(Dado.getSeed());
    }

    @Test
    void facesInvalidasLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> Dado.rolar(0));
        assertThrows(IllegalArgumentException.class, () -> Dado.rolar(-1));
    }
}

package personagens;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sistema.Dado;

import static org.junit.jupiter.api.Assertions.*;

class InimigoTest {

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
    void construtorPadraoGeraGoblinNivel1() {
        Inimigo goblin = new Inimigo();
        assertEquals("Goblin", goblin.getNome());
        assertEquals(1, goblin.getNivel());
        assertEquals("Comum", goblin.getTipo());
    }

    @Test
    void recompensaXpEscalaComNivel() {
        Inimigo nivel1 = new Inimigo("X", 50, 5, 2, 1, "Comum");
        Inimigo nivel3 = new Inimigo("X", 50, 5, 2, 3, "Comum");
        assertEquals(50, nivel1.getRecompensaXP());
        assertEquals(150, nivel3.getRecompensaXP());
    }

    @Test
    void gerarLootPreencheInventario() {
        Dado.setSeed(0L);
        Inimigo inimigo = new Inimigo("Orc", 80, 10, 5, 2, "Forte");
        assertFalse(inimigo.getInventario().estaVazio());
    }

    @Test
    void criarInimigoAleatorioComSeedRetornaInstanciaValida() {
        Dado.setSeed(42L);
        Inimigo inimigo = Inimigo.criarInimigoAleatorio(1);
        assertNotNull(inimigo);
        assertTrue(inimigo.estaVivo());
        assertNotNull(inimigo.getNome());
        assertFalse(inimigo.getNome().isEmpty());
    }

    @Test
    void criarInimigoAleatorioEscalaAtributosComNivel() {
        Dado.setSeed(1L);
        Inimigo nivel1 = Inimigo.criarInimigoAleatorio(1);

        Dado.setSeed(1L);
        Inimigo nivel5 = Inimigo.criarInimigoAleatorio(5);

        assertTrue(nivel5.getPontosVidaMaximos() > nivel1.getPontosVidaMaximos(),
                "Inimigo de nível maior deve ter mais HP");
    }

    @Test
    void nivelMinimoDaFactoryEhUm() {
        Dado.setSeed(0L);
        // nivelJogador = 0 → nivel = max(1, 0 + algo - 2), nunca menor que 1
        Inimigo inimigo = Inimigo.criarInimigoAleatorio(0);
        assertTrue(inimigo.getNivel() >= 1);
    }

    @Test
    void usarHabilidadeEspecialRetornaMensagemNaoNula() {
        Inimigo inimigo = new Inimigo();
        Guerreiro alvo = new Guerreiro();
        String resultado = inimigo.usarHabilidadeEspecial(alvo);
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    void construtorDeCopiaEhIndependente() {
        Dado.setSeed(0L);
        Inimigo original = new Inimigo("Orc", 80, 10, 5, 2, "Forte");
        Inimigo copia = new Inimigo(original);

        copia.receberDano(999);
        assertTrue(original.estaVivo());
        assertFalse(copia.estaVivo());
    }
}

package personagens;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sistema.Dado;

import static org.junit.jupiter.api.Assertions.*;

class ArqueiroTest {

    private Arqueiro arqueiro;

    @BeforeEach
    void setUp() {
        Dado.setSilencioso(true);
        arqueiro = new Arqueiro("Arqueiro", 100, 12, 7, 1);
    }

    @AfterEach
    void tearDown() {
        Dado.resetSeed();
        Dado.setSilencioso(false);
    }

    @Test
    void flechasIniciaisSaoTrinta() {
        assertEquals(30, arqueiro.getFlechas());
    }

    @Test
    void calcularDanoConsumeUmaFlecha() {
        arqueiro.calcularDano(5);
        assertEquals(29, arqueiro.getFlechas());
    }

    @Test
    void semFlechasUsaMeleComMetadeDoDano() {
        // Remove todas as flechas
        while (arqueiro.getFlechas() > 0) {
            arqueiro.calcularDano(1);
        }
        int ataque = arqueiro.getAtaque();
        int rolagem = 10;
        int resultado = arqueiro.calcularDano(rolagem);
        int danoMeleeEsperado = (ataque + rolagem) / 2;
        assertEquals(danoMeleeEsperado, resultado);
    }

    @Test
    void danoComFlechasEhMaiorQueMetadeDoDanoBase() {
        int ataque = arqueiro.getAtaque();
        int rolagem = 10;
        int danoBase = ataque + rolagem;
        int resultado = arqueiro.calcularDano(rolagem);
        assertTrue(resultado >= danoBase,
                "Dano com flecha deve ser pelo menos igual ao dano base");
    }

    @Test
    void habilidadeEspecialConsumesTresFlechas() {
        Guerreiro alvo = new Guerreiro("Alvo", 500, 10, 0, 1);
        int flechasAntes = arqueiro.getFlechas();
        arqueiro.usarHabilidadeEspecial(alvo);
        assertEquals(flechasAntes - 3, arqueiro.getFlechas());
    }

    @Test
    void habilidadeEspecialCausaDanoNoAlvo() {
        Dado.setSeed(1L);
        Guerreiro alvo = new Guerreiro("Alvo", 500, 10, 0, 1);
        int hpAntes = alvo.getPontosVida();
        arqueiro.usarHabilidadeEspecial(alvo);
        assertTrue(alvo.getPontosVida() < hpAntes);
    }

    @Test
    void habilidadeEspecialSemFlechasSuficientesNaoCausaDano() {
        while (arqueiro.getFlechas() >= 3) {
            arqueiro.calcularDano(1);
        }
        Guerreiro alvo = new Guerreiro("Alvo", 200, 10, 0, 1);
        int hpAntes = alvo.getPontosVida();
        String resultado = arqueiro.usarHabilidadeEspecial(alvo);
        assertEquals(hpAntes, alvo.getPontosVida());
        assertTrue(resultado.contains("insuficientes"));
    }

    @Test
    void recarregarFlechasAumentaEstoque() {
        arqueiro.calcularDano(1);
        arqueiro.recarregarFlechas(5);
        assertEquals(34, arqueiro.getFlechas());
    }

    @Test
    void construtorDeCopiaPreservaFlechas() {
        arqueiro.calcularDano(1);
        Arqueiro copia = new Arqueiro(arqueiro);
        assertEquals(arqueiro.getFlechas(), copia.getFlechas());
    }
}

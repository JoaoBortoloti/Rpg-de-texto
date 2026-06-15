package personagens;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sistema.Dado;

import static org.junit.jupiter.api.Assertions.*;

class MagoTest {

    private Mago mago;

    @BeforeEach
    void setUp() {
        Dado.setSilencioso(true);
        mago = new Mago("Mago", 80, 10, 5, 1);
    }

    @AfterEach
    void tearDown() {
        Dado.resetSeed();
        Dado.setSilencioso(false);
    }

    @Test
    void manaInicialEscalaComNivel() {
        Mago nivel3 = new Mago("Mago", 80, 10, 5, 3);
        assertEquals(50 + (3 * 10), nivel3.getManaMaxima());
        assertEquals(nivel3.getManaMaxima(), nivel3.getMana());
    }

    @Test
    void calcularDanoConsome10DeMana() {
        int manaAntes = mago.getMana();
        mago.calcularDano(5);
        assertEquals(manaAntes - 10, mago.getMana());
    }

    @Test
    void calcularDanoComManaSuficienteAdicionaPoderMagico() {
        int ataque = mago.getAtaque();
        int rolagem = 5;
        // dano = ataque + rolagem + poderMagico (quando tem mana)
        // poderMagico base não é acessível diretamente, mas sabemos que é > 0
        int resultado = mago.calcularDano(rolagem);
        assertTrue(resultado > ataque + rolagem,
                "Dano com mana deve ser maior que ataque + rolagem");
    }

    @Test
    void calcularDanoSemManaUsaApenasDanoBase() {
        // Zera mana
        while (mago.getMana() >= 10) {
            mago.calcularDano(1);
        }
        int ataque = mago.getAtaque();
        int rolagem = 5;
        int resultado = mago.calcularDano(rolagem);
        assertEquals(ataque + rolagem, resultado);
    }

    @Test
    void habilidadeEspecialConsome30DeMana() {
        Guerreiro alvo = new Guerreiro("Alvo", 200, 10, 0, 1);
        int manaAntes = mago.getMana();
        mago.usarHabilidadeEspecial(alvo);
        assertEquals(manaAntes - 30, mago.getMana());
    }

    @Test
    void habilidadeEspecialCausaDanoNoAlvo() {
        Dado.setSeed(42L);
        Guerreiro alvo = new Guerreiro("Alvo", 200, 10, 0, 1);
        int hpAntes = alvo.getPontosVida();
        mago.usarHabilidadeEspecial(alvo);
        assertTrue(alvo.getPontosVida() < hpAntes);
    }

    @Test
    void habilidadeEspecialSemManaSuficienteNaoCausaDano() {
        // Gasta mana até ficar com menos de 30
        while (mago.getMana() >= 30) {
            mago.calcularDano(1);
        }
        Guerreiro alvo = new Guerreiro("Alvo", 200, 10, 0, 1);
        int hpAntes = alvo.getPontosVida();
        String resultado = mago.usarHabilidadeEspecial(alvo);
        assertEquals(hpAntes, alvo.getPontosVida());
        assertTrue(resultado.contains("insuficiente"));
    }

    @Test
    void regenerarManaRestauraSemUltrapassarMaximo() {
        mago.calcularDano(1);
        mago.regenerarMana(999);
        assertEquals(mago.getManaMaxima(), mago.getMana());
    }

    @Test
    void construtorDeCopiaPreservaMana() {
        mago.calcularDano(1);
        int manaEsperada = mago.getMana();
        Mago copia = new Mago(mago);
        assertEquals(manaEsperada, copia.getMana());
    }
}

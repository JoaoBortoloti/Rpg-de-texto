package itens;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    private Item cura() {
        return new Item("Poção de Cura", "Recupera HP", Efeito.CURA, 3, 30);
    }

    @Test
    void itensComMesmoNomeEEfeitoSaoIguais() {
        Item a = new Item("Poção de Cura", "Descrição A", Efeito.CURA, 1, 30);
        Item b = new Item("Poção de Cura", "Descrição B", Efeito.CURA, 99, 50);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void itensComEfeitosDistintosNaoSaoIguais() {
        Item cura = new Item("Elixir", "desc", Efeito.CURA, 1, 10);
        Item dano = new Item("Elixir", "desc", Efeito.DANO, 1, 10);
        assertNotEquals(cura, dano);
    }

    @Test
    void compareToOrdenaAlfabeticamentePorNome() {
        Item a = new Item("Antídoto", "desc", Efeito.CURA, 1, 10);
        Item b = new Item("Poção", "desc", Efeito.CURA, 1, 10);
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }

    @Test
    void compareToDesempataPorEfeito() {
        Item buff = new Item("Elixir", "desc", Efeito.BUFF_ATAQUE, 1, 10);
        Item cura = new Item("Elixir", "desc", Efeito.CURA, 1, 10);
        assertNotEquals(0, buff.compareTo(cura));
    }

    @Test
    void compareToRetornaZeroParaItensIguais() {
        Item a = new Item("Poção", "desc1", Efeito.CURA, 5, 20);
        Item b = new Item("Poção", "desc2", Efeito.CURA, 1, 99);
        assertEquals(0, a.compareTo(b));
    }

    @Test
    void incrementarAumentaQuantidade() {
        Item item = cura();
        item.incrementarQuantidade(2);
        assertEquals(5, item.getQuantidade());
    }

    @Test
    void incrementarComValorNegativoLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> cura().incrementarQuantidade(-1));
    }

    @Test
    void decrementarRetornaTrueQuandoHaSaldo() {
        Item item = cura();
        assertTrue(item.decrementarQuantidade(2));
        assertEquals(1, item.getQuantidade());
    }

    @Test
    void decrementarRetornaFalseQuandoSemSaldo() {
        Item item = cura();
        assertFalse(item.decrementarQuantidade(10));
        assertEquals(3, item.getQuantidade());
    }

    @Test
    void decrementarNaoVaiAbaixoDeZero() {
        Item item = cura();
        item.decrementarQuantidade(3);
        assertEquals(0, item.getQuantidade());
        assertFalse(item.decrementarQuantidade(1));
    }

    @Test
    void construtorDeCopiaGeraInstanciaIndependente() {
        Item original = cura();
        Item copia = new Item(original);
        copia.incrementarQuantidade(10);
        assertEquals(3, original.getQuantidade());
        assertEquals(13, copia.getQuantidade());
    }

    @Test
    void construtorComQuantidadeNegativaLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> new Item("X", "desc", Efeito.CURA, -1, 10));
    }
}

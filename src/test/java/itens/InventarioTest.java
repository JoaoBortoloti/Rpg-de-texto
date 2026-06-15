package itens;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventarioTest {

    private Inventario inventario;

    @BeforeEach
    void setUp() {
        inventario = new Inventario(5);
    }

    private Item pocaoCura(int qtd) {
        return new Item("Poção de Cura", "Recupera HP", Efeito.CURA, qtd, 30);
    }

    private Item pocaoMana(int qtd) {
        return new Item("Poção de Mana", "Recupera mana", Efeito.BUFF_ATAQUE, qtd, 20);
    }

    @Test
    void adicionarNovoItemOcupaSlot() {
        inventario.adicionar(pocaoCura(1));
        assertEquals(1, inventario.getTamanho());
    }

    @Test
    void adicionarItemExistenteFazMerge() {
        inventario.adicionar(pocaoCura(2));
        inventario.adicionar(pocaoCura(3));
        assertEquals(1, inventario.getTamanho());
        assertEquals(5, inventario.buscarPorNome("Poção de Cura").getQuantidade());
    }

    @Test
    void inventarioCheioLancaExcecao() {
        Inventario cheio = new Inventario(2);
        cheio.adicionar(pocaoCura(1));
        cheio.adicionar(pocaoMana(1));
        Item terceiro = new Item("Antídoto", "desc", Efeito.OUTRO, 1, 0);
        assertThrows(IllegalStateException.class, () -> cheio.adicionar(terceiro));
    }

    @Test
    void adicionarItemNuloLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> inventario.adicionar(null));
    }

    @Test
    void removerItemExistenteRetornaTrue() {
        inventario.adicionar(pocaoCura(3));
        assertTrue(inventario.remover("Poção de Cura", 2));
        assertEquals(1, inventario.buscarPorNome("Poção de Cura").getQuantidade());
    }

    @Test
    void removerTudoLiberaSlot() {
        inventario.adicionar(pocaoCura(1));
        inventario.remover("Poção de Cura", 1);
        assertEquals(0, inventario.getTamanho());
        assertTrue(inventario.estaVazio());
    }

    @Test
    void removerItemInexistenteRetornaFalse() {
        assertFalse(inventario.remover("Espada Mágica", 1));
    }

    @Test
    void removerQuantidadeMaiorQueEstoqueRetornaFalse() {
        inventario.adicionar(pocaoCura(2));
        assertFalse(inventario.remover("Poção de Cura", 5));
        assertEquals(2, inventario.buscarPorNome("Poção de Cura").getQuantidade());
    }

    @Test
    void buscarPorIndiceRetornaItemCerto() {
        inventario.adicionar(pocaoCura(1));
        inventario.adicionar(pocaoMana(1));
        List<Item> ordenados = inventario.listarOrdenado();
        Item porIndice = inventario.buscarPorIndice(0);
        assertEquals(ordenados.get(0).getNome(), porIndice.getNome());
    }

    @Test
    void buscarPorIndiceInvalidoRetornaNull() {
        assertNull(inventario.buscarPorIndice(-1));
        assertNull(inventario.buscarPorIndice(0));
    }

    @Test
    void copiaFundaEhIndependenteDoOriginal() {
        inventario.adicionar(pocaoCura(5));
        Inventario copia = new Inventario(inventario);

        copia.remover("Poção de Cura", 3);

        assertEquals(5, inventario.buscarPorNome("Poção de Cura").getQuantidade());
        assertEquals(2, copia.buscarPorNome("Poção de Cura").getQuantidade());
    }

    @Test
    void listarOrdenadoRetornaCopiasNaoReferencias() {
        inventario.adicionar(pocaoCura(3));
        List<Item> lista = inventario.listarOrdenado();
        lista.get(0).incrementarQuantidade(100);
        assertEquals(3, inventario.buscarPorNome("Poção de Cura").getQuantidade());
    }

    @Test
    void capacidadeInvalidaLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Inventario(0));
        assertThrows(IllegalArgumentException.class, () -> new Inventario(-1));
    }
}

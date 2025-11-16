package personagens;

import itens.Inventario;
import interfaces.Atacavel;

public abstract class Personagem implements Atacavel {
    private String nome;
    private int pontosVida;
    private int pontosVidaMaximos;
    private int ataque;
    private int defesa;
    private int nivel;
    private Inventario inventario;

    public Personagem(String nome, int pontosVida, int ataque, int defesa, int nivel) {
        this.nome = nome;
        this.pontosVida = pontosVida;
        this.pontosVidaMaximos = pontosVida;
        this.ataque = ataque;
        this.defesa = defesa;
        this.nivel = nivel;
        this.inventario = new Inventario(20);
    }

    public Personagem(Personagem outro) {
        this.nome = outro.nome;
        this.pontosVida = outro.pontosVida;
        this.pontosVidaMaximos = outro.pontosVidaMaximos;
        this.ataque = outro.ataque;
        this.defesa = outro.defesa;
        this.nivel = outro.nivel;
        this.inventario = new Inventario(outro.inventario);
    }

    public String getNome() {
        return nome;
    }

    public int getPontosVida() {
        return pontosVida;
    }

    public void setPontosVida(int pontosVida) {
        this.pontosVida = Math.max(0, Math.min(pontosVida, pontosVidaMaximos));
    }

    public int getPontosVidaMaximos() {
        return pontosVidaMaximos;
    }

    public void setPontosVidaMaximos(int pontosVidaMaximos) {
        this.pontosVidaMaximos = pontosVidaMaximos;
    }

    public int getAtaque() {
        return ataque;
    }

    public void setAtaque(int ataque) {
        this.ataque = Math.max(0, ataque);
    }

    public int getDefesa() {
        return defesa;
    }

    public void setDefesa(int defesa) {
        this.defesa = Math.max(0, defesa);
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = Math.max(1, nivel);
    }

    public Inventario getInventario() {
        return inventario;
    }

    @Override
    public int calcularDano(int rolagemDado) {
        return ataque + rolagemDado;
    }

    @Override
    public void receberDano(int dano) {
        int danoReal = Math.max(0, dano - defesa);
        pontosVida = Math.max(0, pontosVida - danoReal);
    }

    @Override
    public boolean estaVivo() {
        return pontosVida > 0;
    }

    public void curar(int quantidade) {
        pontosVida = Math.min(pontosVida + quantidade, pontosVidaMaximos);
    }

    public abstract String usarHabilidadeEspecial(Personagem alvo);

    public String getStatus() {
        return String.format("%s (Nível %d) - HP: %d/%d | ATK: %d | DEF: %d",
                nome, nivel, pontosVida, pontosVidaMaximos, ataque, defesa);
    }

    @Override
    public String toString() {
        return getStatus();
    }
}
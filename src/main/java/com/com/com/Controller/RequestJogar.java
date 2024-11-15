package com.com.com.Controller;

public class RequestJogar {
    private String simbolo;
    private int posicao;

    public RequestJogar() {
    }

    public RequestJogar(String simbolo, int posicao) {
        this.simbolo = simbolo;
        this.posicao = posicao;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(int posicao) {
        this.posicao = posicao;
    }
}

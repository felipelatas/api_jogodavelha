package com.com.com.Controller;

import lombok.Getter;
import lombok.Setter;

public class RequestJogar {
    @Getter @Setter
    private String simbolo;
    @Getter @Setter
    private int posicao;

    public RequestJogar() {
    }

    public RequestJogar(String simbolo, int posicao) {
        this.simbolo = simbolo;
        this.posicao = posicao;
    }
}

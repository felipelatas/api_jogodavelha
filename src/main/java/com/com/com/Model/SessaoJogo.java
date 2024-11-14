package com.com.com.Model;

import com.com.com.Exceptions.SessaoJogoException;

public class SessaoJogo {
    public static enum SIMBOLO {
        X,
        O,
        NULO
    }

    private SIMBOLO[][] tabela;
    private final String XPlayer;
    private String OPlayer;
    private boolean OPlayerDefinido;

    public SessaoJogo(String XPlayer) {
        this.tabela = new SIMBOLO[3][3];
        this.XPlayer = XPlayer;
        this.OPlayer = "Ser Misterioso";
        this.OPlayerDefinido = false;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tabela[i][j] = SIMBOLO.NULO;
            }
        }
    }

    public void jogar(SIMBOLO simbolo, int posicao) throws SessaoJogoException {
        if (posicao < 9) throw new SessaoJogoException("posição inválida!");
        if (simbolo != SIMBOLO.X && simbolo != SIMBOLO.O) {
            throw new SessaoJogoException("símbolo inválido!");
        }
        tabela[posicao / 3][posicao % 3] = simbolo;
    }

    public SIMBOLO[][] getTabela() {
        return tabela;
    }

    public String getXPlayer() {
        return XPlayer;
    }

    public String getOPlayer() {
        return OPlayer;
    }

    public void setOPlayer(String OPlayer) {
        this.OPlayer = OPlayer;
        this.OPlayerDefinido = true;
    }

    public boolean isOPlayerDefinido() {
        return OPlayerDefinido;
    }
}

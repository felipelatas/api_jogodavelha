package com.com.com.Model;

import com.com.com.Exceptions.SessaoJogoException;
import lombok.Getter;

public class SessaoJogo {
    public enum SIMBOLO {
        X,
        O,
        NULO
    }

    @Getter
    private final SIMBOLO[][] tabela;
    @Getter
    private final String XPlayer;
    @Getter
    private String OPlayer;
    @Getter
    private boolean OPlayerDefinido;
    @Getter
    private SIMBOLO vezDeQuem;
    @Getter
    private SIMBOLO ganhador = null;

    private final static boolean[][][] combinacoesVitoria = {
            {
                    {true, false, false},
                    {false, true, false},
                    {false, false, true}
            },
            {
                    {false, false, true},
                    {false, true, false},
                    {true, false, false}
            },
            {
                    {true, false, false},
                    {true, false, false},
                    {true, false, false}
            },
            {
                    {false, true, false},
                    {false, true, false},
                    {false, true, false}
            },
            {
                    {false, false, true},
                    {false, false, true},
                    {false, false, true}
            },
            {
                    {true, true, true},
                    {false, false, false},
                    {false, false, false}
            },
            {
                    {false, false, false},
                    {true, true, true},
                    {false, false, false}
            },
            {
                    {false, false, false},
                    {false, false, false},
                    {true, true, true}
            },
    };

    private boolean verificarEmpate() {
        boolean empate = true;
        for (SIMBOLO[] simbolos : tabela) {
            for (SIMBOLO simbolo : simbolos) {
                if (simbolo == SIMBOLO.NULO) {
                    empate = false;
                    break;
                }
            }
            if (!empate) break;
        }
        return empate;
    }

    private SIMBOLO verificarGanhador() {
        for (boolean[][] combinacao : combinacoesVitoria) {
            boolean ganhaste = true;
            SIMBOLO ultimaOcorrencia = null;
            for (int i = 0; i < tabela.length; i++) {
                for (int j = 0; j < tabela[i].length; j++) {
                    if (!combinacao[i][j]) continue;
                    if (ultimaOcorrencia == null) {
                        ultimaOcorrencia = tabela[i][j];
                        continue;
                    }
                    if (tabela[i][j] == SIMBOLO.NULO || tabela[i][j] != ultimaOcorrencia) {
                        // parar o loop por completo e partir para verificar outro
                        ganhaste = false;
                        i = tabela.length;
                        break;
                    }
                }
            }
            if (ganhaste) return ultimaOcorrencia;
        }
        if (verificarEmpate()) {
            return SIMBOLO.NULO;
        }
        return null;
    }

    public SessaoJogo(String XPlayer) {
        this.tabela = new SIMBOLO[3][3];
        this.XPlayer = XPlayer;
        this.OPlayer = "Ser Misterioso";
        this.OPlayerDefinido = false;
        this.vezDeQuem = SIMBOLO.X;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tabela[i][j] = SIMBOLO.NULO;
            }
        }
    }

    public void jogar(SIMBOLO simbolo, int posicao) throws SessaoJogoException {
        if (ganhador != null) throw new SessaoJogoException("O jogo já acabou");
        if (simbolo != vezDeQuem) throw new SessaoJogoException("Não é a vez de " + simbolo + "!!!!");
        if (posicao >= 9) throw new SessaoJogoException("Posição inválida!");
        if (simbolo != SIMBOLO.X && simbolo != SIMBOLO.O) {
            throw new SessaoJogoException("Símbolo inválido!");
        }

        int coluna = posicao / 3;
        int linha = posicao % 3;

        if (tabela[coluna][linha] != SIMBOLO.NULO) throw new SessaoJogoException("A posição já está ocupada!!!");

        tabela[coluna][linha] = simbolo;

        vezDeQuem = vezDeQuem == SIMBOLO.X ? SIMBOLO.O : SIMBOLO.X;

        ganhador = verificarGanhador();
    }

    public void setOPlayer(String OPlayer) {
        this.OPlayer = OPlayer;
        this.OPlayerDefinido = true;
    }
}

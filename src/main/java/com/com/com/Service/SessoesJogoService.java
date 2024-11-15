package com.com.com.Service;

import com.com.com.Exceptions.SessaoJogoException;
import com.com.com.Model.SessaoJogo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class SessoesJogoService {
    private static final Map<String, SessaoJogo> sessoesJogo = new HashMap<>();
    public static final int tamanhoCod = 4;
    private static final ScheduledExecutorService temporizador = Executors.newScheduledThreadPool(1);

    public static ResponseEntity<String> erro(String msg) {
        return new ResponseEntity<>("erro: " + msg, HttpStatus.BAD_REQUEST);
    }

    private static String gerarCodAleatorio() {
        final String caracteresPossiveis = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUV123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        while (sb.length() < tamanhoCod) {
            int index = random.nextInt(caracteresPossiveis.length());
            sb.append(caracteresPossiveis.charAt(index));
        }
        return sb.toString();
    }

    public static String novoJogo(String xPlayer) {
        String strGerada;
        // O hashMap usa o .equals do String,
        // ao invés de comparar ponteiros, então funciona
        do {
            strGerada = gerarCodAleatorio();
        } while (sessoesJogo.containsKey(strGerada));
        sessoesJogo.put(strGerada, new SessaoJogo(xPlayer));
        return strGerada;
    }

    public static SessaoJogo obterSessao(String cod) throws SessaoJogoException {
        SessaoJogo sessao = sessoesJogo.get(cod);
        if (sessao == null) throw new SessaoJogoException("a sessão informada não existe");
        return sessao;
    }

    // inexiste o jogo depois de 5 minutos
    public static ResponseEntity<String> inexistirJogo(String cod) {
        try {
            obterSessao(cod);
        } catch (SessaoJogoException e) {
            return erro(e.getMessage());
        }
        temporizador.schedule(() -> {
            sessoesJogo.remove(cod);
        }, 5, TimeUnit.MINUTES);
        return new ResponseEntity<>("Sucesso! O jogo acabou, logo, a sessão do jogo será excluída em 5 minutos", HttpStatus.ACCEPTED);
    }

    public static ResponseEntity<String> setarNomeBolinha(String cod, String oPlayer) {
        SessaoJogo sessao;
        try {
            sessao = obterSessao(cod);
        } catch (SessaoJogoException e) {
            return erro(e.getMessage());
        }
        if (sessao.isOPlayerDefinido()) return erro("nome já definido!");
        sessao.setOPlayer(oPlayer);
        return new ResponseEntity<>("sucesso!", HttpStatus.ACCEPTED);
    }

    private static SessaoJogo.SIMBOLO[][] obterTabela(String cod) {
        return sessoesJogo.get(cod).getTabela();
    }

    private static SessaoJogo.SIMBOLO[] obterTabelaFormatada(SessaoJogo sessao) throws SessaoJogoException {
        SessaoJogo.SIMBOLO[][] tabela = sessao.getTabela();
        SessaoJogo.SIMBOLO[] tabelaArr = new SessaoJogo.SIMBOLO[9];
        int arrIndex = 0;
        for (SessaoJogo.SIMBOLO[] simbolos : tabela) {
            for (SessaoJogo.SIMBOLO simbolo : simbolos) {
                tabelaArr[arrIndex++] = simbolo;
            }
        }
        return tabelaArr;
    }

    public static ResponseEntity<String> obterJsonEstadoJogo(String cod) {
        StringBuilder sb = new StringBuilder("{ \"tabela\": [ ");
        SessaoJogo.SIMBOLO[] tabelaFormatada;
        SessaoJogo sessao;
        try {
            sessao = obterSessao(cod);
            tabelaFormatada = obterTabelaFormatada(sessao);
        } catch (SessaoJogoException e) {
            return erro(e.getMessage());
        }

        for (SessaoJogo.SIMBOLO simbolo: tabelaFormatada) {
            sb.append("\"");
            sb.append(simbolo.toString());
            sb.append("\" ");
            sb.append(", ");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));

        sb.append("], \"vez\": \"");
        sb.append(sessao.getVezDeQuem());
        sb.append("\", ");

        sb.append("\"vencedor\": ");
        SessaoJogo.SIMBOLO ganhador = sessao.getGanhador();
        if (ganhador != null) {
            if (ganhador.equals(SessaoJogo.SIMBOLO.NULO)) {
                sb.append("\"EMPATE\"");
            } else {
                sb.append("\"");
                sb.append(ganhador);
                sb.append("\"");
            }
        } else {
            sb.append("\"NULO\"");
        }
        sb.append(" }");

        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }

    public static ResponseEntity<String> obterNome(String cod, SessaoJogo.SIMBOLO simbolo) {
        SessaoJogo sessao;
        try {
            sessao = obterSessao(cod);
        } catch (SessaoJogoException e) {
            return erro(e.getMessage());
        }
        if (simbolo == null || simbolo == SessaoJogo.SIMBOLO.NULO) return erro(
                "O simbolo informado deve ser O ou X, mas \"" + simbolo + "\" for informado no lugar"
        );
        return new ResponseEntity<>("{ \"nome\": \"" + (simbolo == SessaoJogo.SIMBOLO.X ? sessao.getXPlayer() : sessao.getOPlayer()) + "\" }", HttpStatus.OK);
    }

    public static ResponseEntity<String> jogar(String cod, String simbolo, int posicao) {
        SessaoJogo.SIMBOLO simboloManeiro;
        switch (simbolo) {
            case "X": simboloManeiro = SessaoJogo.SIMBOLO.X; break;
            case "O": simboloManeiro = SessaoJogo.SIMBOLO.O; break;
            default: return erro("simbolo inválido!");
        }


        SessaoJogo sessaoJogo;
        try {
            sessaoJogo = obterSessao(cod);
            sessaoJogo.jogar(simboloManeiro, posicao);
        } catch (SessaoJogoException e) {
            return erro(e.getMessage());
        }

        if (sessaoJogo.getGanhador() != null) return inexistirJogo(cod);

        return new ResponseEntity<>("sucesso!", HttpStatus.ACCEPTED);
    }
}

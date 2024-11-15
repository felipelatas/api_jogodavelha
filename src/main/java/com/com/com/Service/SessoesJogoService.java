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
        String strGerada = null;
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
    public static String inexistirJogo(String cod) {
        try {
            obterSessao(cod);
        } catch (SessaoJogoException e) {
            return "erro: " + e.getMessage();
        }
        temporizador.schedule(() -> {
            sessoesJogo.remove(cod);
        }, 5, TimeUnit.MINUTES);
        return "A sessão do jogo será excluída em 5 minutos!";
    }

    public static String setarNomeBolinha(String cod, String oPlayer) {
        SessaoJogo sessao;
        try {
            sessao = obterSessao(cod);
        } catch (SessaoJogoException e) {
            return "erro: " + e.getMessage();
        }
        if (sessao.isOPlayerDefinido()) return "Nome já deinido!";
        sessao.setOPlayer(oPlayer);
        return "sucesso!";
    }

    private static SessaoJogo.SIMBOLO[][] obterTabela(String cod) {
        return sessoesJogo.get(cod).getTabela();
    }

    private static SessaoJogo.SIMBOLO[] obterTabelaFormatada(String cod) throws SessaoJogoException {
        SessaoJogo.SIMBOLO[][] tabela = obterSessao(cod).getTabela();
        SessaoJogo.SIMBOLO[] tabelaArr = new SessaoJogo.SIMBOLO[9];
        int arrIndex = 0;
        for (SessaoJogo.SIMBOLO[] simbolos : tabela) {
            for (SessaoJogo.SIMBOLO simbolo : simbolos) {
                tabelaArr[arrIndex++] = simbolo;
            }
        }
        return tabelaArr;
    }

//    private static boolean[][] combinacoesVitoria = {
//            {true, true, true, true, true, true, true, true, true},
//    }
//        {
//    }
//    private static String checarPorVitoria() {
//
//    }

    public static String obterJsonEstadoJogo(String cod) {
        StringBuilder sb = new StringBuilder("{ \"tabela\": [ ");
        SessaoJogo.SIMBOLO[] tabelaFormatada = null;
        try {
            tabelaFormatada = obterTabelaFormatada(cod);
        } catch (SessaoJogoException e) {
            return "erro: " + e.getMessage();
        }

        for (SessaoJogo.SIMBOLO simbolo: tabelaFormatada) {
            sb.append("\"");
            sb.append(simbolo.toString());
            sb.append("\" ");
            sb.append(", ");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("] }");
        return sb.toString();
    }

    public static String obterNome(String cod, SessaoJogo.SIMBOLO simbolo) {
        SessaoJogo sessao;
        try {
            sessao = obterSessao(cod);
        } catch (SessaoJogoException e) {
            return e.getMessage();
        }
        if (simbolo == SessaoJogo.SIMBOLO.X) return sessao.getXPlayer();
        if (simbolo == SessaoJogo.SIMBOLO.O) return sessao.getOPlayer();
        return "O simbolo informado deve ser O ou X, mas \"" + simbolo.toString() + "\" for informado no lugar";
    }

    public static ResponseEntity<String> jogar(String cod, String simbolo, int posicao) {
        SessaoJogo.SIMBOLO simboloManeiro;
        switch (simbolo) {
            case "X": simboloManeiro = SessaoJogo.SIMBOLO.X; break;
            case "O": simboloManeiro = SessaoJogo.SIMBOLO.O; break;
            default: return erro("simbolo inválido!");
        }
        try {
            obterSessao(cod).jogar(simboloManeiro, posicao);
        } catch (SessaoJogoException e) {
            return erro(e.getMessage());
        }
        return new ResponseEntity<>("sucesso!", HttpStatus.OK);
    }
}

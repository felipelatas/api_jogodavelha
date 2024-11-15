package com.com.com.Controller;

import com.com.com.Model.SessaoJogo;
import com.com.com.Service.SessoesJogoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.com.com.Service.SessoesJogoService;

import java.util.Map;

@RestController
public class SessaoJogoController {

    private ResponseEntity<String> erroCod(String cod) {
        return SessoesJogoService.erro(("o tamanho do codigo é sempre "
                + SessoesJogoService.tamanhoCod
                + ", fornecido: " + cod.length()));
    }

    @GetMapping("/consultar/{cod}")
    public ResponseEntity<String> consultar(@PathVariable String cod) {
        if (cod.length() != SessoesJogoService.tamanhoCod)
            return erroCod(cod);
        return new ResponseEntity<>(SessoesJogoService.obterJsonEstadoJogo(cod),
                HttpStatus.OK);
    }


    @PostMapping("/criar")
    public ResponseEntity<String> criar(@RequestBody(required = false) Map<String, Object> coisas) {
        String nome;
        if (coisas == null || coisas.get("nome") == null) nome = "Ser X Misterioso";
        else nome = coisas.get("nome").toString();

        return new ResponseEntity<>("{ \"cod\": \""
                + SessoesJogoService.novoJogo(nome) + "\" }",
                HttpStatus.CREATED);
    }

    @PostMapping("/nome_circulo/{cod}")
    public ResponseEntity<String> nomeCirculo(@PathVariable String cod, @RequestBody(required = false) Map<String, Object> coisas) {
        if (cod.length() != SessoesJogoService.tamanhoCod)
            return erroCod(cod);

        if (coisas == null || coisas.get("nome") == null
                || coisas.get("nome").toString().isEmpty())
            return SessoesJogoService.erro( "você precisa informar o nome para colocar no player \"circulo\"!!!!");

        return new ResponseEntity<>(SessoesJogoService.setarNomeBolinha(cod, coisas.get("nome").toString()), HttpStatus.OK);
    }

    @GetMapping("/obter_nome/{cod}")
    public ResponseEntity<String> nome(@PathVariable String cod, @RequestParam(required = false) String simbolo) {
        if (simbolo == null) return SessoesJogoService.erro("informe um simbolo por favor! "
                + "Você pode fazer isso colocando ?simbolo=X ou ?simbolo=O na url");

        if (cod.length() != SessoesJogoService.tamanhoCod)
            return erroCod(cod);
        SessaoJogo.SIMBOLO simboloManeiro;
        switch (simbolo) {
            case "X":
                simboloManeiro = SessaoJogo.SIMBOLO.X;
                break;
            case "O":
                simboloManeiro = SessaoJogo.SIMBOLO.O;
                break;
            default:
                return SessoesJogoService.erro("Esse símbolo nem existe! tem que ser \"O\" ou \"X\"");
        }
        return new ResponseEntity<>(SessoesJogoService.obterNome(cod, simboloManeiro), HttpStatus.OK);
    }


    @PostMapping("/jogar/{cod}")
    public ResponseEntity<String> jogar(@PathVariable String cod, @RequestBody(required = true) RequestJogar req) {
        if (cod.length() != SessoesJogoService.tamanhoCod) return erroCod(cod);
        if (!(req.getSimbolo().equals("X") || req.getSimbolo().equals("O")))
            return SessoesJogoService.erro("o símbolo precisa ser \"O\" ou \"X\"");

        return SessoesJogoService.jogar(cod, req.getSimbolo(), req.getPosicao());
    }

    @RequestMapping("/**")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String notFound() {
        return "erro: poxa vida esse endpoint não existe :(";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String errorHandler(Exception e) {
        return "erro: poxa vida meus perdões viu, "
                + "alguma coisa deu errado aqui dentro "
                + "do meu pobrezinho servidor :(\n"
                + "O erro em questão é esse aqui caso lhe for útil meu broto:\n"
                + e.getMessage();
    }
}

package com.chatambiental.comum;

import java.util.HashMap;
import java.util.Map;

public class MensagemComando extends Mensagem {
    private static final long serialVersionUID = 1L;

    private TipoComando comando;
    private Map<String, Object> parametros;
    private String mensagemTexto;

    public MensagemComando(TipoComando comando, String remetente) {
        super(TipoMensagem.COMANDO, remetente);
        this.comando = comando;
        this.parametros = new HashMap<>();
        this.mensagemTexto = "";
    }

    public MensagemComando(TipoComando comando, String remetente, String mensagemTexto) {
        super(TipoMensagem.COMANDO, remetente);
        this.comando = comando;
        this.parametros = new HashMap<>();
        this.mensagemTexto = mensagemTexto;
    }

    public void adicionarParametro(String chave, Object valor) {
        parametros.put(chave, valor);
    }

    public Object getParametro(String chave) {
        return parametros.get(chave);
    }

    public TipoComando getComando() {
        return comando;
    }

    public String getMensagemTexto() {
        return mensagemTexto;
    }
}
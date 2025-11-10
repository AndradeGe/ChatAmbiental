package com.chatambiental.comum;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class Mensagem implements Serializable {
    private static final long serialVersionUID = 1L;

    protected TipoMensagem tipo;
    protected String remetente;
    protected LocalDateTime timestamp;

    public Mensagem(TipoMensagem tipo, String remetente) {
        this.tipo = tipo;
        this.remetente = remetente;
        this.timestamp = LocalDateTime.now();
    }

    public TipoMensagem getTipo() {
        return tipo;
    }

    public String getRemetente() {
        return remetente;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setRemetente(String remetente) {
        this.remetente = remetente;
    }
}
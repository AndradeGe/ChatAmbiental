package com.chatambiental.comum;

public class MensagemTexto extends Mensagem {
    private static final long serialVersionUID = 1L;

    private String texto;
    private String destinatario;

    public MensagemTexto(String remetente, String texto) {
        super(TipoMensagem.TEXTO, remetente);
        this.texto = texto;
        this.destinatario = null;
    }

    public MensagemTexto(String remetente, String destinatario, String texto) {
        super(TipoMensagem.TEXTO_PRIVADO, remetente);
        this.texto = texto;
        this.destinatario = destinatario;
    }

    public String getTexto() {
        return texto;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
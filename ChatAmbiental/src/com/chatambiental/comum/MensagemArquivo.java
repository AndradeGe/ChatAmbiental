package com.chatambiental.comum;

public class MensagemArquivo extends Mensagem {
    private static final long serialVersionUID = 1L;

    private String nomeArquivo;
    private long tamanhoArquivo;
    private byte[] dados;
    private String destinatario;

    public MensagemArquivo(String remetente, String nomeArquivo, byte[] dados) {
        super(TipoMensagem.ARQUIVO, remetente);
        this.nomeArquivo = nomeArquivo;
        this.tamanhoArquivo = dados.length;
        this.dados = dados;
        this.destinatario = null;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public long getTamanhoArquivo() {
        return tamanhoArquivo;
    }

    public byte[] getDados() {
        return dados;
    }

    public String getDestinatario() {
        return destinatario;
    }
}
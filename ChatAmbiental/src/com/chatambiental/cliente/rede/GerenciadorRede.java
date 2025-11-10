package com.chatambiental.cliente.rede;

import com.chatambiental.comum.*;
import com.chatambiental.cliente.ui.JanelaPrincipal;

import java.io.*;
import java.net.Socket;

public class GerenciadorRede {
    private Socket socket;
    private ObjectOutputStream saida;
    private ObjectInputStream entrada;
    private String nomeUsuario;
    private JanelaPrincipal janelaPrincipal;
    private ReceptorMensagens receptorMensagens;

    public GerenciadorRede(String servidor, int porta, String nomeUsuario) throws IOException {
        this.nomeUsuario = nomeUsuario;
        this.socket = new Socket(servidor, porta);
        this.saida = new ObjectOutputStream(socket.getOutputStream());
        this.saida.flush();
        this.entrada = new ObjectInputStream(socket.getInputStream());

        // Envia mensagem de conexão
        MensagemComando msgConexao = new MensagemComando(TipoComando.CONECTAR, nomeUsuario);
        saida.writeObject(msgConexao);
        saida.flush();
    }

    public void iniciarRecepcao(JanelaPrincipal janela) {
        this.janelaPrincipal = janela;
        this.receptorMensagens = new ReceptorMensagens(entrada, janelaPrincipal);
        receptorMensagens.start();
    }

    public void enviarMensagem(String texto) {
        try {
            MensagemTexto mensagem = new MensagemTexto(nomeUsuario, texto);
            saida.writeObject(mensagem);
            saida.flush();
        } catch (IOException e) {
            System.err.println("Erro ao enviar mensagem: " + e.getMessage());
        }
    }

    public void enviarArquivo(File arquivo) {
        try {
            FileInputStream fis = new FileInputStream(arquivo);
            byte[] dados = new byte[(int) arquivo.length()];
            fis.read(dados);
            fis.close();

            MensagemArquivo msgArquivo = new MensagemArquivo(nomeUsuario, arquivo.getName(), dados);

            saida.writeObject(msgArquivo);
            saida.flush();

            janelaPrincipal.exibirMensagemSistema("Arquivo enviado: " + arquivo.getName());

        } catch (IOException e) {
            System.err.println("Erro ao enviar arquivo: " + e.getMessage());
        }
    }

    public void desconectar() {
        try {
            MensagemComando msgDesconectar = new MensagemComando(TipoComando.DESCONECTAR, nomeUsuario);
            saida.writeObject(msgDesconectar);
            saida.flush();

            if (receptorMensagens != null) {
                receptorMensagens.parar();
            }

            entrada.close();
            saida.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Erro ao desconectar: " + e.getMessage());
        }
    }
}
package com.chatambiental.servidor.cliente;

import com.chatambiental.comum.*;
import com.chatambiental.servidor.core.GerenciadorConexoes;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ThreadCliente extends Thread {
    private Socket socket;
    private GerenciadorConexoes gerenciador;
    private ObjectInputStream entrada;
    private ObjectOutputStream saida;
    private String nomeUsuario;
    private boolean conectado;

    public ThreadCliente(Socket socket, GerenciadorConexoes gerenciador) {
        this.socket = socket;
        this.gerenciador = gerenciador;
        this.conectado = true;

        try {
            this.saida = new ObjectOutputStream(socket.getOutputStream());
            this.saida.flush();
            this.entrada = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            log("Erro ao criar streams: " + e.getMessage());
            conectado = false;
        }
    }

    @Override
    public void run() {
        try {
            Mensagem msgConexao = (Mensagem) entrada.readObject();

            if (msgConexao instanceof MensagemComando) {
                MensagemComando comando = (MensagemComando) msgConexao;
                this.nomeUsuario = comando.getRemetente();

                if (!gerenciador.adicionarCliente(nomeUsuario, this)) {
                    MensagemComando falha = new MensagemComando(
                            TipoComando.CONECTAR_FALHA, "SERVIDOR", "Nome já em uso");
                    enviarMensagem(falha);
                    socket.close();
                    return;
                }

                MensagemComando confirmacao = new MensagemComando(
                        TipoComando.CONECTAR_SUCESSO, "SERVIDOR", "Conectado!");
                confirmacao.adicionarParametro("usuarios", gerenciador.obterUsuariosOnline());
                enviarMensagem(confirmacao);

                MensagemTexto notificacao = new MensagemTexto("SISTEMA", nomeUsuario + " entrou na sala");
                gerenciador.broadcast(notificacao, nomeUsuario);

                MensagemComando atualizacao = new MensagemComando(TipoComando.ATUALIZAR_USUARIOS, "SERVIDOR");
                atualizacao.adicionarParametro("usuarios", gerenciador.obterUsuariosOnline());
                gerenciador.broadcast(atualizacao);
            }

            while (conectado) {
                Mensagem mensagem = (Mensagem) entrada.readObject();
                processarMensagem(mensagem);
            }

        } catch (Exception e) {
            log("Cliente desconectado: " + nomeUsuario);
        } finally {
            desconectar();
        }
    }

    private void processarMensagem(Mensagem mensagem) {
        if (mensagem instanceof MensagemTexto) {
            MensagemTexto msgTexto = (MensagemTexto) mensagem;
            log(nomeUsuario + " enviou: " + msgTexto.getTexto());

            if (msgTexto.getDestinatario() == null) {
                gerenciador.broadcast(msgTexto);
            } else {
                gerenciador.enviarMensagemPrivada(msgTexto.getDestinatario(), msgTexto);
                enviarMensagem(msgTexto);
            }
        } else if (mensagem instanceof MensagemArquivo) {
            MensagemArquivo msgArquivo = (MensagemArquivo) mensagem;
            log(nomeUsuario + " enviou arquivo: " + msgArquivo.getNomeArquivo());
            gerenciador.broadcast(msgArquivo, nomeUsuario);
        }
    }

    public void enviarMensagem(Mensagem mensagem) {
        try {
            synchronized (saida) {
                saida.writeObject(mensagem);
                saida.flush();
                saida.reset();
            }
        } catch (IOException e) {
            conectado = false;
        }
    }

    private void desconectar() {
        if (nomeUsuario != null) {
            gerenciador.removerCliente(nomeUsuario);

            MensagemTexto notificacao = new MensagemTexto("SISTEMA", nomeUsuario + " saiu da sala");
            gerenciador.broadcast(notificacao);

            MensagemComando atualizacao = new MensagemComando(TipoComando.ATUALIZAR_USUARIOS, "SERVIDOR");
            atualizacao.adicionarParametro("usuarios", gerenciador.obterUsuariosOnline());
            gerenciador.broadcast(atualizacao);
        }

        try {
            if (entrada != null) entrada.close();
            if (saida != null) saida.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            // Ignora
        }
    }

    private void log(String mensagem) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("[" + timestamp + "] " + mensagem);
    }
}
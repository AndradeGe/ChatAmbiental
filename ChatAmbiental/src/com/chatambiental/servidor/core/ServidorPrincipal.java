package com.chatambiental.servidor.core;

import com.chatambiental.servidor.cliente.ThreadCliente;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServidorPrincipal {
    private static final int PORTA_PADRAO = 5000;
    private ServerSocket serverSocket;
    private GerenciadorConexoes gerenciadorConexoes;
    private boolean executando;

    public ServidorPrincipal(int porta) throws IOException {
        this.serverSocket = new ServerSocket(porta);
        this.gerenciadorConexoes = new GerenciadorConexoes();
        this.executando = true;

        log("===================================");
        log("SERVIDOR CHAT AMBIENTAL INICIADO");
        log("Porta: " + porta);
        log("===================================");
        log("Aguardando conexões...");
    }

    public void iniciar() {
        while (executando) {
            try {
                Socket socketCliente = serverSocket.accept();
                log("Nova conexão de: " + socketCliente.getInetAddress().getHostAddress());

                ThreadCliente threadCliente = new ThreadCliente(socketCliente, gerenciadorConexoes);
                threadCliente.start();

            } catch (IOException e) {
                if (executando) {
                    log("Erro ao aceitar conexão: " + e.getMessage());
                }
            }
        }
    }

    private void log(String mensagem) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        System.out.println("[" + timestamp + "] " + mensagem);
    }

    public static void main(String[] args) {
        int porta = PORTA_PADRAO;

        if (args.length > 0) {
            try {
                porta = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Porta inválida. Usando porta padrão: " + PORTA_PADRAO);
            }
        }

        try {
            ServidorPrincipal servidor = new ServidorPrincipal(porta);
            servidor.iniciar();
        } catch (IOException e) {
            System.err.println("ERRO FATAL: Não foi possível iniciar o servidor!");
            System.err.println("Motivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
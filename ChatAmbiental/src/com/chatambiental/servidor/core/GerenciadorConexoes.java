package com.chatambiental.servidor.core;

import com.chatambiental.comum.Mensagem;
import com.chatambiental.servidor.cliente.ThreadCliente;

import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorConexoes {
    private ConcurrentHashMap<String, ThreadCliente> clientesConectados;

    public GerenciadorConexoes() {
        this.clientesConectados = new ConcurrentHashMap<>();
    }

    public synchronized boolean adicionarCliente(String nomeUsuario, ThreadCliente threadCliente) {
        if (clientesConectados.containsKey(nomeUsuario)) {
            return false;
        }
        clientesConectados.put(nomeUsuario, threadCliente);
        System.out.println("Cliente adicionado: " + nomeUsuario + " (Total: " + clientesConectados.size() + ")");
        return true;
    }

    public synchronized void removerCliente(String nomeUsuario) {
        clientesConectados.remove(nomeUsuario);
        System.out.println("Cliente removido: " + nomeUsuario + " (Total: " + clientesConectados.size() + ")");
    }

    public void broadcast(Mensagem mensagem) {
        broadcast(mensagem, null);
    }

    public void broadcast(Mensagem mensagem, String exceto) {
        for (String nomeUsuario : clientesConectados.keySet()) {
            if (exceto == null || !nomeUsuario.equals(exceto)) {
                ThreadCliente cliente = clientesConectados.get(nomeUsuario);
                if (cliente != null) {
                    cliente.enviarMensagem(mensagem);
                }
            }
        }
    }

    public void enviarMensagemPrivada(String destinatario, Mensagem mensagem) {
        ThreadCliente cliente = clientesConectados.get(destinatario);
        if (cliente != null) {
            cliente.enviarMensagem(mensagem);
        }
    }

    public List<String> obterUsuariosOnline() {
        return new ArrayList<>(clientesConectados.keySet());
    }
}
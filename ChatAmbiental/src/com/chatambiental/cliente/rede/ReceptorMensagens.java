package com.chatambiental.cliente.rede;

import com.chatambiental.comum.*;
import com.chatambiental.cliente.ui.JanelaPrincipal;

import javax.swing.*;
import java.io.*;
import java.util.List;

public class ReceptorMensagens extends Thread {
    private ObjectInputStream entrada;
    private JanelaPrincipal janelaPrincipal;
    private boolean executando;

    public ReceptorMensagens(ObjectInputStream entrada, JanelaPrincipal janelaPrincipal) {
        this.entrada = entrada;
        this.janelaPrincipal = janelaPrincipal;
        this.executando = true;
    }

    @Override
    public void run() {
        while (executando) {
            try {
                Mensagem mensagem = (Mensagem) entrada.readObject();
                processarMensagem(mensagem);
            } catch (EOFException e) {
                executando = false;
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(janelaPrincipal,
                            "Conexão com o servidor foi perdida.",
                            "Desconectado",
                            JOptionPane.WARNING_MESSAGE);
                });
                break;
            } catch (Exception e) {
                if (executando) {
                    System.err.println("Erro ao receber mensagem: " + e.getMessage());
                    break;
                }
            }
        }
    }

    private void processarMensagem(Mensagem mensagem) {
        if (mensagem instanceof MensagemTexto) {
            MensagemTexto msgTexto = (MensagemTexto) mensagem;
            janelaPrincipal.exibirMensagem(msgTexto);

        } else if (mensagem instanceof MensagemComando) {
            MensagemComando msgComando = (MensagemComando) mensagem;
            processarComando(msgComando);

        } else if (mensagem instanceof MensagemArquivo) {
            MensagemArquivo msgArquivo = (MensagemArquivo) mensagem;
            receberArquivo(msgArquivo);
        }
    }

    @SuppressWarnings("unchecked")
    private void processarComando(MensagemComando comando) {
        switch (comando.getComando()) {
            case CONECTAR_SUCESSO:
                List<String> usuarios = (List<String>) comando.getParametro("usuarios");
                janelaPrincipal.atualizarUsuarios(usuarios);
                janelaPrincipal.exibirMensagemSistema("Conectado ao servidor com sucesso!");
                break;

            case ATUALIZAR_USUARIOS:
                usuarios = (List<String>) comando.getParametro("usuarios");
                janelaPrincipal.atualizarUsuarios(usuarios);
                break;

            case CONECTAR_FALHA:
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(janelaPrincipal,
                            comando.getMensagemTexto(),
                            "Erro de Conexão",
                            JOptionPane.ERROR_MESSAGE);
                });
                break;
        }
    }

    private void receberArquivo(MensagemArquivo msgArquivo) {
        SwingUtilities.invokeLater(() -> {
            int opcao = JOptionPane.showConfirmDialog(
                    janelaPrincipal,
                    msgArquivo.getRemetente() + " enviou o arquivo:\n" +
                            msgArquivo.getNomeArquivo() + " (" + msgArquivo.getTamanhoArquivo() + " bytes)\n\n" +
                            "Deseja salvar?",
                    "Arquivo Recebido",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (opcao == JOptionPane.YES_OPTION) {
                JFileChooser seletor = new JFileChooser();
                seletor.setSelectedFile(new File(msgArquivo.getNomeArquivo()));

                int resultado = seletor.showSaveDialog(janelaPrincipal);
                if (resultado == JFileChooser.APPROVE_OPTION) {
                    try {
                        FileOutputStream fos = new FileOutputStream(seletor.getSelectedFile());
                        fos.write(msgArquivo.getDados());
                        fos.close();

                        janelaPrincipal.exibirMensagemSistema(
                                "Arquivo salvo: " + seletor.getSelectedFile().getName());
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(janelaPrincipal,
                                "Erro ao salvar arquivo: " + e.getMessage(),
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    public void parar() {
        executando = false;
        interrupt();
    }
}
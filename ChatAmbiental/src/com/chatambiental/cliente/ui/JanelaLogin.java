package com.chatambiental.cliente.ui;

import com.chatambiental.cliente.rede.GerenciadorRede;

import javax.swing.*;
import java.awt.*;

public class JanelaLogin extends JFrame {
    private JTextField campoNomeUsuario;
    private JTextField campoServidor;
    private JTextField campoPorta;
    private JButton botaoConectar;
    private JButton botaoCancelar;

    public JanelaLogin() {
        setTitle("Chat Ambiental - Login");
        setSize(450, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        inicializarComponentes();
        configurarLayout();
        configurarEventos();
    }

    private void inicializarComponentes() {
        campoNomeUsuario = new JTextField(20);
        campoServidor = new JTextField("localhost", 20);
        campoPorta = new JTextField("5000", 20);

        botaoConectar = new JButton("Conectar");
        botaoCancelar = new JButton("Cancelar");

        botaoConectar.setBackground(new Color(46, 125, 50));
        botaoConectar.setForeground(Color.WHITE);
        botaoConectar.setFocusPainted(false);

        botaoCancelar.setBackground(new Color(211, 47, 47));
        botaoCancelar.setForeground(Color.WHITE);
        botaoCancelar.setFocusPainted(false);
    }

    private void configurarLayout() {
        JPanel painelPrincipal = new JPanel(new GridBagLayout());
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painelPrincipal.setBackground(new Color(250, 250, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titulo = new JLabel("Sistema de Chat Ambiental");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(new Color(33, 150, 243));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        painelPrincipal.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;

        // Nome de usuário
        gbc.gridx = 0;
        gbc.gridy = 1;
        painelPrincipal.add(new JLabel("Nome de Usuário:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        painelPrincipal.add(campoNomeUsuario, gbc);

        // Servidor
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        painelPrincipal.add(new JLabel("Servidor:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        painelPrincipal.add(campoServidor, gbc);

        // Porta
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        painelPrincipal.add(new JLabel("Porta:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        painelPrincipal.add(campoPorta, gbc);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        painelBotoes.setOpaque(false);
        painelBotoes.add(botaoConectar);
        painelBotoes.add(botaoCancelar);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        painelPrincipal.add(painelBotoes, gbc);

        add(painelPrincipal);
    }

    private void configurarEventos() {
        botaoConectar.addActionListener(e -> conectar());
        botaoCancelar.addActionListener(e -> System.exit(0));

        campoPorta.addActionListener(e -> conectar());
        campoNomeUsuario.addActionListener(e -> conectar());
    }

    private void conectar() {
        String nomeUsuario = campoNomeUsuario.getText().trim();
        String servidor = campoServidor.getText().trim();
        String portaTexto = campoPorta.getText().trim();

        if (nomeUsuario.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, insira um nome de usuário.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            campoNomeUsuario.requestFocus();
            return;
        }

        if (!nomeUsuario.matches("[a-zA-Z0-9_]+")) {
            JOptionPane.showMessageDialog(this,
                    "Nome de usuário deve conter apenas letras, números e underscore.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            campoNomeUsuario.requestFocus();
            return;
        }

        if (servidor.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, insira o endereço do servidor.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            campoServidor.requestFocus();
            return;
        }

        int porta;
        try {
            porta = Integer.parseInt(portaTexto);
            if (porta < 1024 || porta > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Porta deve ser um número entre 1024 e 65535.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            campoPorta.requestFocus();
            return;
        }

        botaoConectar.setEnabled(false);
        botaoConectar.setText("Conectando...");

        new Thread(() -> {
            try {
                GerenciadorRede gerenciadorRede = new GerenciadorRede(servidor, porta, nomeUsuario);

                SwingUtilities.invokeLater(() -> {
                    JanelaPrincipal janelaPrincipal = new JanelaPrincipal(gerenciadorRede, nomeUsuario);
                    gerenciadorRede.iniciarRecepcao(janelaPrincipal);
                    janelaPrincipal.setVisible(true);
                    dispose();
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Erro ao conectar ao servidor:\n" + e.getMessage() +
                                    "\n\nVerifique se o servidor está rodando.",
                            "Erro de Conexão",
                            JOptionPane.ERROR_MESSAGE);

                    botaoConectar.setEnabled(true);
                    botaoConectar.setText("Conectar");
                });
            }
        }).start();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JanelaLogin janela = new JanelaLogin();
            janela.setVisible(true);
        });
    }
}
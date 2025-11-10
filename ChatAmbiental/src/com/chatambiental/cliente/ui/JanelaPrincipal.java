package com.chatambiental.cliente.ui;

import com.chatambiental.cliente.rede.GerenciadorRede;
import com.chatambiental.comum.MensagemTexto;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

public class JanelaPrincipal extends JFrame {
    private JTextPane areaChat;
    private StyledDocument documentoChat;
    private JTextField campoTexto;
    private JButton botaoEnviar;
    private JList<String> listaUsuarios;
    private DefaultListModel<String> modeloUsuarios;
    private GerenciadorRede gerenciadorRede;
    private String nomeUsuario;

    public JanelaPrincipal(GerenciadorRede gerenciadorRede, String nomeUsuario) {
        this.gerenciadorRede = gerenciadorRede;
        this.nomeUsuario = nomeUsuario;

        setTitle("Chat Ambiental - " + nomeUsuario);
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
        configurarLayout();
        configurarEventos();
    }

    private void inicializarComponentes() {
        areaChat = new JTextPane();
        areaChat.setEditable(false);
        documentoChat = areaChat.getStyledDocument();
        areaChat.setFont(new Font("Arial", Font.PLAIN, 13));

        campoTexto = new JTextField();
        campoTexto.setFont(new Font("Arial", Font.PLAIN, 13));

        botaoEnviar = new JButton("Enviar");
        botaoEnviar.setBackground(new Color(33, 150, 243));
        botaoEnviar.setForeground(Color.WHITE);
        botaoEnviar.setFocusPainted(false);
        botaoEnviar.setFont(new Font("Arial", Font.BOLD, 12));

        modeloUsuarios = new DefaultListModel<>();
        listaUsuarios = new JList<>(modeloUsuarios);
        listaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaUsuarios.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private void configurarLayout() {
        setLayout(new BorderLayout(5, 5));

        // Painel central com área de chat
        JScrollPane scrollChat = new JScrollPane(areaChat);
        scrollChat.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollChat.setBorder(BorderFactory.createTitledBorder("Mensagens"));
        add(scrollChat, BorderLayout.CENTER);

        // Painel inferior com entrada de texto
        JPanel painelInferior = new JPanel(new BorderLayout(5, 5));
        painelInferior.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        painelInferior.add(campoTexto, BorderLayout.CENTER);
        painelInferior.add(botaoEnviar, BorderLayout.EAST);
        add(painelInferior, BorderLayout.SOUTH);

        // Painel lateral com lista de usuários
        JPanel painelLateral = new JPanel(new BorderLayout());
        painelLateral.setPreferredSize(new Dimension(180, 0));
        painelLateral.setBorder(BorderFactory.createTitledBorder("Usuários Online"));
        JScrollPane scrollUsuarios = new JScrollPane(listaUsuarios);
        painelLateral.add(scrollUsuarios, BorderLayout.CENTER);
        add(painelLateral, BorderLayout.EAST);

        criarBarraMenu();

        exibirMensagemSistema("Bem-vindo ao Chat Ambiental!");
    }

    private void criarBarraMenu() {
        JMenuBar barraMenu = new JMenuBar();

        JMenu menuArquivo = new JMenu("Arquivo");
        JMenuItem itemEnviarArquivo = new JMenuItem("Enviar Arquivo");
        JMenuItem itemDesconectar = new JMenuItem("Desconectar");

        itemEnviarArquivo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        itemDesconectar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));

        menuArquivo.add(itemEnviarArquivo);
        menuArquivo.addSeparator();
        menuArquivo.add(itemDesconectar);

        JMenu menuAjuda = new JMenu("Ajuda");
        JMenuItem itemSobre = new JMenuItem("Sobre");
        menuAjuda.add(itemSobre);

        barraMenu.add(menuArquivo);
        barraMenu.add(menuAjuda);
        setJMenuBar(barraMenu);

        itemDesconectar.addActionListener(e -> desconectar());
        itemEnviarArquivo.addActionListener(e -> enviarArquivo());
        itemSobre.addActionListener(e -> mostrarSobre());
    }

    private void configurarEventos() {
        botaoEnviar.addActionListener(e -> enviarMensagem());
        campoTexto.addActionListener(e -> enviarMensagem());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                desconectar();
            }
        });
    }

    private void enviarMensagem() {
        String texto = campoTexto.getText().trim();
        if (!texto.isEmpty()) {
            gerenciadorRede.enviarMensagem(texto);
            campoTexto.setText("");
            campoTexto.requestFocus();
        }
    }

    public void exibirMensagem(MensagemTexto mensagem) {
        SwingUtilities.invokeLater(() -> {
            try {
                String timestamp = String.format("%02d:%02d:%02d",
                        mensagem.getTimestamp().getHour(),
                        mensagem.getTimestamp().getMinute(),
                        mensagem.getTimestamp().getSecond());

                Style estiloTimestamp = areaChat.addStyle("Timestamp", null);
                StyleConstants.setForeground(estiloTimestamp, Color.GRAY);
                StyleConstants.setFontSize(estiloTimestamp, 11);

                Style estiloRemetente = areaChat.addStyle("Remetente", null);
                StyleConstants.setBold(estiloRemetente, true);

                if (mensagem.getRemetente().equals("SISTEMA")) {
                    StyleConstants.setForeground(estiloRemetente, new Color(0, 150, 136));
                } else if (mensagem.getRemetente().equals(nomeUsuario)) {
                    StyleConstants.setForeground(estiloRemetente, new Color(33, 150, 243));
                } else {
                    StyleConstants.setForeground(estiloRemetente, new Color(76, 175, 80));
                }

                Style estiloTexto = areaChat.addStyle("Texto", null);
                StyleConstants.setForeground(estiloTexto, Color.BLACK);

                documentoChat.insertString(documentoChat.getLength(),
                        "[" + timestamp + "] ", estiloTimestamp);
                documentoChat.insertString(documentoChat.getLength(),
                        mensagem.getRemetente() + ": ", estiloRemetente);
                documentoChat.insertString(documentoChat.getLength(),
                        mensagem.getTexto() + "\n", estiloTexto);

                areaChat.setCaretPosition(documentoChat.getLength());

            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    public void exibirMensagemSistema(String texto) {
        MensagemTexto msg = new MensagemTexto("SISTEMA", texto);
        exibirMensagem(msg);
    }

    public void atualizarUsuarios(List<String> usuarios) {
        SwingUtilities.invokeLater(() -> {
            modeloUsuarios.clear();
            for (String usuario : usuarios) {
                String exibicao = usuario.equals(nomeUsuario) ? usuario + " (Você)" : usuario;
                modeloUsuarios.addElement(exibicao);
            }
        });
    }

    private void enviarArquivo() {
        JFileChooser seletor = new JFileChooser();
        seletor.setDialogTitle("Selecionar Arquivo para Enviar");
        int resultado = seletor.showOpenDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File arquivo = seletor.getSelectedFile();

            if (arquivo.length() > 10 * 1024 * 1024) { // 10 MB
                JOptionPane.showMessageDialog(this,
                        "Arquivo muito grande! Tamanho máximo: 10 MB",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            gerenciadorRede.enviarArquivo(arquivo);
        }
    }

    private void mostrarSobre() {
        JOptionPane.showMessageDialog(this,
                "Sistema de Chat Ambiental\n\n" +
                        "Desenvolvido para monitoramento do Rio Tietê\n" +
                        "Secretaria de Estado do Meio Ambiente\n\n" +
                        "Versão 1.0 - 2025\n" +
                        "Trabalho de APS - Ciência da Computação",
                "Sobre",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void desconectar() {
        int opcao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente desconectar?",
                "Confirmar Desconexão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcao == JOptionPane.YES_OPTION) {
            gerenciadorRede.desconectar();
            dispose();
            System.exit(0);
        }
    }
}

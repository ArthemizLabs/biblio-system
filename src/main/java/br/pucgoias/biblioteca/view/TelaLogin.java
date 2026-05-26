package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.controller.UsuarioController;
import br.pucgoias.biblioteca.model.Usuario;
import br.pucgoias.biblioteca.util.Mensagens;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;
import br.pucgoias.biblioteca.util.exceptions.ValidacaoException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Tela de login do BiblioSystem.
 * Primeira janela exibida ao iniciar o sistema — autentica o usuário via banco de dados.
 */
public class TelaLogin extends JFrame {

    private final UsuarioController controller = new UsuarioController();

    private JTextField campoLogin;
    private JPasswordField campoSenha;
    private JButton btnEntrar;
    private JLabel labelLogin;
    private JLabel labelSenha;

    public TelaLogin() {
        inicializarComponentes();
        configurarJanela();
        aplicarIdioma();
    }

    private void inicializarComponentes() {
        // Painel principal com padding
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        painel.setBackground(new Color(245, 245, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel labelTitulo = new JLabel("BiblioSystem", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        labelTitulo.setForeground(new Color(33, 97, 140));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 24, 8);
        painel.add(labelTitulo, gbc);

        // Label Login
        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 8, 6, 8);
        labelLogin = new JLabel();
        labelLogin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(labelLogin, gbc);

        // Campo Login
        campoLogin = new JTextField(18);
        campoLogin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.gridy = 1;
        painel.add(campoLogin, gbc);

        // Label Senha
        labelSenha = new JLabel();
        labelSenha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 2;
        painel.add(labelSenha, gbc);

        // Campo Senha
        campoSenha = new JPasswordField(18);
        campoSenha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.gridy = 2;
        painel.add(campoSenha, gbc);

        // Botão Entrar
        btnEntrar = new JButton();
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEntrar.setBackground(new Color(33, 97, 140));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 0, 8);
        painel.add(btnEntrar, gbc);

        // Ação do botão
        btnEntrar.addActionListener(e -> realizarLogin());

        // Enter nos campos também dispara o login
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) realizarLogin();
            }
        };
        campoLogin.addKeyListener(enterListener);
        campoSenha.addKeyListener(enterListener);

        setContentPane(painel);
    }

    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null); // centraliza na tela
    }

    private void aplicarIdioma() {
        setTitle(Mensagens.get("login.titulo"));
        labelLogin.setText(Mensagens.get("login.usuario"));
        labelSenha.setText(Mensagens.get("login.senha"));
        btnEntrar.setText(Mensagens.get("login.btn.entrar"));
    }

    private void realizarLogin() {
        try {
            String login = campoLogin.getText().trim();
            String senha = new String(campoSenha.getPassword()).trim();

            Usuario usuario = controller.autenticar(login, senha);

            if (usuario == null) {
                JOptionPane.showMessageDialog(this,
                        Mensagens.get("login.erro.invalido"),
                        Mensagens.get("login.titulo"),
                        JOptionPane.WARNING_MESSAGE);
                campoSenha.setText("");
                campoSenha.requestFocus();
                return;
            }

            // Login bem-sucedido — abre o menu principal
            new TelaMenu(usuario).setVisible(true);
            dispose();

        } catch (ValidacaoException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    Mensagens.get("login.titulo"),
                    JOptionPane.WARNING_MESSAGE);

        } catch (BancoDadosException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao conectar ao banco de dados.\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
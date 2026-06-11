package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.util.Mensagens;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * Diálogo de configuração de idioma do sistema.
 * Permite alternar entre português e inglês via ResourceBundle (requisito AED i18n).
 */
public class ConfiguracaoIdiomaDialog extends JDialog {

    private JRadioButton radioIngles;

    public ConfiguracaoIdiomaDialog(JFrame parent) {
        super(parent, Mensagens.get("dialogo.idioma.titulo"), true);
        inicializarComponentes();
        configurarJanela();
    }

    private void inicializarComponentes() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel labelTitulo = new JLabel(Mensagens.get("dialogo.idioma.label"));
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        painel.add(labelTitulo, gbc);

        JRadioButton radioPortugues = new JRadioButton("Português (Brasil)");
        radioPortugues.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        radioIngles = new JRadioButton("English (US)");
        radioIngles.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Marca o idioma atual
        String lang = Mensagens.getLocalAtual().getLanguage();
        if (lang.equals("en")) radioIngles.setSelected(true);
        else radioPortugues.setSelected(true);

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(radioPortugues);
        grupo.add(radioIngles);

        gbc.gridwidth = 1;
        gbc.gridy = 1; painel.add(radioPortugues, gbc);
        gbc.gridy = 2; painel.add(radioIngles, gbc);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnConfirmar = new JButton(Mensagens.get("btn.confirmar"));
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnConfirmar.setBackground(new Color(39, 174, 96));
        btnConfirmar.setForeground(Color.BLACK);
        btnConfirmar.setFocusPainted(false);

        JButton btnCancelar = new JButton(Mensagens.get("btn.cancelar"));
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancelar.setBackground(new Color(127, 140, 141));
        btnCancelar.setForeground(Color.BLACK);
        btnCancelar.setFocusPainted(false);

        painelBotoes.add(btnConfirmar);
        painelBotoes.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        painel.add(painelBotoes, gbc);

        btnConfirmar.addActionListener(e -> confirmar());
        btnCancelar.addActionListener(e  -> dispose());

        setContentPane(painel);
    }

    private void confirmar() {
        Locale novoLocale = radioIngles.isSelected()
                ? new Locale("en", "US")
                : new Locale("pt", "BR");
        Mensagens.setIdioma(novoLocale);
        dispose();
    }

    private void configurarJanela() {
        setResizable(false);
        pack();
        setLocationRelativeTo(getParent());
    }
}
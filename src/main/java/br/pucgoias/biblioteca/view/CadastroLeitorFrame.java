package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.controller.LeitorController;
import br.pucgoias.biblioteca.model.Leitor;
import br.pucgoias.biblioteca.util.Mensagens;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;
import br.pucgoias.biblioteca.util.exceptions.ValidacaoException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.util.List;

/**
 * Janela de cadastro de Leitores.
 * Cadastro intermediário com pesquisa por código ou nome e validação de CPF.
 */
public class CadastroLeitorFrame extends JInternalFrame {

    private final LeitorController controller = new LeitorController();

    private JTextField campoId, campoNome, campoCpf, campoEmail, campoTelefone;

    // Pesquisa complexa
    private JComboBox<String> comboFiltro;
    private JTextField campoPesquisa;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public CadastroLeitorFrame() {
        inicializarComponentes();
        configurarJanela();
    }

    private void inicializarComponentes() {
        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        abas.addTab(Mensagens.get("aba.cadastro"), criarPainelCadastro());
        abas.addTab(Mensagens.get("aba.pesquisa"), criarPainelPesquisa());
        add(abas);
    }

    // ----------------------------------------------------------------
    // ABA CADASTRO
    // ----------------------------------------------------------------
    private JPanel criarPainelCadastro() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Código
        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Código:"), gbc);
        campoId = new JTextField(8);
        campoId.setEditable(false);
        campoId.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1; painel.add(campoId, gbc);

        // Nome
        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(new JLabel("Nome: *"), gbc);
        campoNome = new JTextField(25);
        gbc.gridx = 1; painel.add(campoNome, gbc);

        // CPF com máscara
        gbc.gridx = 0; gbc.gridy = 2;
        painel.add(new JLabel("CPF: *"), gbc);
        campoCpf = new JTextField(25);
        aplicarMascaraCpf(campoCpf);
        gbc.gridx = 1; painel.add(campoCpf, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 3;
        painel.add(new JLabel("E-mail:"), gbc);
        campoEmail = new JTextField(25);
        gbc.gridx = 1; painel.add(campoEmail, gbc);

        // Telefone
        gbc.gridx = 0; gbc.gridy = 4;
        painel.add(new JLabel("Telefone:"), gbc);
        campoTelefone = new JTextField(25);
        gbc.gridx = 1; painel.add(campoTelefone, gbc);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnSalvar = criarBotao(Mensagens.get("btn.salvar"), new Color(39, 174, 96));
        JButton btnAlterar = criarBotao(Mensagens.get("btn.alterar"), new Color(41, 128, 185));
        JButton btnExcluir = criarBotao(Mensagens.get("btn.excluir"), new Color(192, 57, 43));
        JButton btnLimpar = criarBotao(Mensagens.get("btn.limpar"), new Color(127, 140, 141));
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnAlterar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 6, 6, 6);
        painel.add(painelBotoes, gbc);

        btnSalvar.addActionListener(e  -> salvar());
        btnAlterar.addActionListener(e -> alterar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e  -> limparCampos());

        return painel;
    }

    // ----------------------------------------------------------------
    // ABA PESQUISA COMPLEXA
    // ----------------------------------------------------------------
    private JPanel criarPainelPesquisa() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBusca.add(new JLabel("Pesquisar por:"));

        comboFiltro = new JComboBox<>(new String[]{"Nome", "Código"});
        comboFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelBusca.add(comboFiltro);

        campoPesquisa = new JTextField(18);
        painelBusca.add(campoPesquisa);

        JButton btnPesquisar = criarBotao(Mensagens.get("btn.pesquisar"), new Color(41, 128, 185));
        painelBusca.add(btnPesquisar);

        // Validação dinâmica: só números quando filtro for "Código"
        comboFiltro.addActionListener(e -> {
            campoPesquisa.setText("");
            if (comboFiltro.getSelectedIndex() == 1) {
                aplicarFiltroNumerico(campoPesquisa);
            } else {
                ((AbstractDocument) campoPesquisa.getDocument()).setDocumentFilter(null);
            }
        });

        modeloTabela = new DefaultTableModel(
                new String[]{"Código", "Nome", "CPF", "E-mail", "Telefone"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) carregarDaTabela();
        });

        painel.add(painelBusca, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        btnPesquisar.addActionListener(e -> pesquisar());
        campoPesquisa.addActionListener(e -> pesquisar());

        return painel;
    }

    // ----------------------------------------------------------------
    // AÇÕES
    // ----------------------------------------------------------------
    private void salvar() {
        try {
            Leitor leitor = new Leitor(0,
                    campoNome.getText(), campoCpf.getText(),
                    campoEmail.getText(), campoTelefone.getText());
            controller.salvar(leitor);
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.sucesso.salvar"));
            limparCampos(); pesquisar();
        } catch (ValidacaoException | BancoDadosException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void alterar() {
        if (campoId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.selecionar"), "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Leitor leitor = new Leitor(
                    Integer.parseInt(campoId.getText()),
                    campoNome.getText(), campoCpf.getText(),
                    campoEmail.getText(), campoTelefone.getText());
            controller.atualizar(leitor);
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.sucesso.alterar"));
            limparCampos(); pesquisar();
        } catch (ValidacaoException | BancoDadosException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluir() {
        if (campoId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.selecionar"), "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, Mensagens.get("msg.confirmar.excluir"),
                "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                controller.deletar(Integer.parseInt(campoId.getText()));
                JOptionPane.showMessageDialog(this, Mensagens.get("msg.sucesso.excluir"));
                limparCampos(); pesquisar();
            } catch (BancoDadosException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void pesquisar() {
        modeloTabela.setRowCount(0);
        List<Leitor> lista;

        if (comboFiltro.getSelectedIndex() == 1) {
            // Pesquisa por código
            String texto = campoPesquisa.getText().trim();
            if (texto.isEmpty()) {
                lista = controller.listarTodos();
            } else {
                try {
                    Leitor leitor = controller.buscarPorId(Integer.parseInt(texto));
                    lista = leitor != null ? List.of(leitor) : List.of();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Informe um código numérico válido.",
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        } else {
            // Pesquisa por nome
            lista = controller.buscarPorNome(campoPesquisa.getText());
        }

        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.nao.encontrado"));
            return;
        }
        for (Leitor l : lista) {
            modeloTabela.addRow(new Object[]{
                    l.getId(), l.getNome(), l.getCpf(), l.getEmail(), l.getTelefone()
            });
        }
    }

    private void carregarDaTabela() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        campoId.setText(modeloTabela.getValueAt(linha, 0).toString());
        campoNome.setText(modeloTabela.getValueAt(linha, 1).toString());
        campoCpf.setText(modeloTabela.getValueAt(linha, 2).toString());
        Object email = modeloTabela.getValueAt(linha, 3);
        campoEmail.setText(email != null ? email.toString() : "");
        Object tel = modeloTabela.getValueAt(linha, 4);
        campoTelefone.setText(tel != null ? tel.toString() : "");
        JTabbedPane abas = (JTabbedPane) getContentPane().getComponent(0);
        abas.setSelectedIndex(0);
    }

    private void limparCampos() {
        campoId.setText(""); campoNome.setText("");
        campoCpf.setText(""); campoEmail.setText("");
        campoTelefone.setText("");
        campoNome.requestFocus();
    }

    // ----------------------------------------------------------------
    // UTILITÁRIOS
    // ----------------------------------------------------------------

    /** Aplica máscara de CPF: 000.000.000-00 */
    private void aplicarMascaraCpf(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                    throws BadLocationException {
                String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String novo = atual.substring(0, offset) + text + atual.substring(offset);
                if (novo.replaceAll("[^0-9]", "").length() <= 11) {
                    super.insertString(fb, offset, text, attr);
                    formatarCpf(fb);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr)
                    throws BadLocationException {
                String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String novo = atual.substring(0, offset) + (text != null ? text : "") + atual.substring(offset + length);
                if (novo.replaceAll("[^0-9]", "").length() <= 11) {
                    super.replace(fb, offset, length, text, attr);
                    formatarCpf(fb);
                }
            }

            private void formatarCpf(FilterBypass fb) throws BadLocationException {
                String texto = fb.getDocument().getText(0, fb.getDocument().getLength());
                String digits = texto.replaceAll("[^0-9]", "");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < digits.length(); i++) {
                    if (i == 3 || i == 6) sb.append('.');
                    else if (i == 9) sb.append('-');
                    sb.append(digits.charAt(i));
                }
                fb.getDocument().remove(0, fb.getDocument().getLength());
                ((AbstractDocument) fb.getDocument()).setDocumentFilter(null);
                fb.insertString(0, sb.toString(), null);
                ((AbstractDocument) fb.getDocument()).setDocumentFilter(this);
            }
        });
    }

    /** Permite apenas dígitos no campo */
    private void aplicarFiltroNumerico(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                    throws BadLocationException {
                if (text.matches("\\d+")) super.insertString(fb, offset, text, attr);
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr)
                    throws BadLocationException {
                if (text == null || text.matches("\\d*")) super.replace(fb, offset, length, text, attr);
            }
        });
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(cor);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void configurarJanela() {
        setTitle(Mensagens.get("menu.leitores"));
        setSize(640, 500);
        setClosable(true); setMaximizable(true);
        setIconifiable(true); setResizable(true);
        setLocation(120, 30);
    }
}
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

public class CadastroLeitorFrame extends GenericFrame {

    private final LeitorController controller = new LeitorController();

    private JTextField campoNome, campoCpf, campoEmail, campoTelefone;
    private JComboBox<String> comboFiltro;
    private JLabel labelCodigo, labelNome, labelCpf, labelEmail, labelTelefone, labelPesquisarPor;

    public CadastroLeitorFrame() {
        configurarJanela(Mensagens.get("menu.leitores"), 640, 500, 120, 30);
    }

    @Override
    protected JPanel criarPainelCadastro() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        labelCodigo = new JLabel(Mensagens.get("label.codigo"));
        painel.add(labelCodigo, gbc);
        campoId = new JTextField(8);
        campoId.setEditable(false);
        campoId.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1; painel.add(campoId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        labelNome = new JLabel(Mensagens.get("label.nome"));
        painel.add(labelNome, gbc);
        campoNome = new JTextField(25);
        gbc.gridx = 1; painel.add(campoNome, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        labelCpf = new JLabel(Mensagens.get("label.cpf"));
        painel.add(labelCpf, gbc);
        campoCpf = new JTextField(25);
        aplicarMascaraCpf(campoCpf);
        gbc.gridx = 1; painel.add(campoCpf, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        labelEmail = new JLabel(Mensagens.get("label.email"));
        painel.add(labelEmail, gbc);
        campoEmail = new JTextField(25);
        gbc.gridx = 1; painel.add(campoEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        labelTelefone = new JLabel(Mensagens.get("label.telefone"));
        painel.add(labelTelefone, gbc);
        campoTelefone = new JTextField(25);
        gbc.gridx = 1; painel.add(campoTelefone, gbc);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnSalvar = criarBotao(Mensagens.get("btn.salvar"), new Color(39, 174, 96));
        JButton btnAlterar = criarBotao(Mensagens.get("btn.alterar"), new Color(41, 128, 185));
        JButton btnExcluir = criarBotao(Mensagens.get("btn.excluir"), new Color(192, 57, 43));
        JButton btnLimpar  = criarBotao(Mensagens.get("btn.limpar"),  new Color(127, 140, 141));
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

    @Override
    protected JPanel criarPainelPesquisa() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPesquisarPor = new JLabel(Mensagens.get("label.pesquisar.por"));
        painelBusca.add(labelPesquisarPor);

        comboFiltro = new JComboBox<>(new String[]{Mensagens.get("col.nome"), Mensagens.get("col.codigo")});
        comboFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelBusca.add(comboFiltro);

        campoPesquisa = new JTextField(18);
        painelBusca.add(campoPesquisa);

        JButton btnPesquisar = criarBotao(Mensagens.get("btn.pesquisar"), new Color(41, 128, 185));
        painelBusca.add(btnPesquisar);

        comboFiltro.addActionListener(e -> {
            campoPesquisa.setText("");
            if (comboFiltro.getSelectedIndex() == 1) {
                aplicarFiltroNumerico(campoPesquisa);
            } else {
                ((AbstractDocument) campoPesquisa.getDocument()).setDocumentFilter(null);
            }
        });

        modeloTabela = new DefaultTableModel(
                new String[]{Mensagens.get("col.codigo"), Mensagens.get("col.nome"), Mensagens.get("col.cpf"),
                             Mensagens.get("col.email"), Mensagens.get("col.telefone")}, 0) {
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

    @Override
    protected void atualizarTextos() {
        setTitle(Mensagens.get("menu.leitores"));
        labelCodigo.setText(Mensagens.get("label.codigo"));
        labelNome.setText(Mensagens.get("label.nome"));
        labelCpf.setText(Mensagens.get("label.cpf"));
        labelEmail.setText(Mensagens.get("label.email"));
        labelTelefone.setText(Mensagens.get("label.telefone"));
        labelPesquisarPor.setText(Mensagens.get("label.pesquisar.por"));
        int sel = comboFiltro.getSelectedIndex();
        comboFiltro.removeAllItems();
        comboFiltro.addItem(Mensagens.get("col.nome"));
        comboFiltro.addItem(Mensagens.get("col.codigo"));
        comboFiltro.setSelectedIndex(sel);
        modeloTabela.setColumnIdentifiers(new String[]{
            Mensagens.get("col.codigo"), Mensagens.get("col.nome"), Mensagens.get("col.cpf"),
            Mensagens.get("col.email"), Mensagens.get("col.telefone")
        });
    }

    @Override
    protected void salvar() {
        try {
            controller.salvar(new Leitor(0, campoNome.getText(), campoCpf.getText(), campoEmail.getText(), campoTelefone.getText()));
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.sucesso.salvar"));
            limparCampos(); pesquisar();
        } catch (ValidacaoException | BancoDadosException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    protected void alterar() {
        if (campoId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.selecionar"), "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            controller.atualizar(new Leitor(Integer.parseInt(campoId.getText()),
                    campoNome.getText(), campoCpf.getText(), campoEmail.getText(), campoTelefone.getText()));
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.sucesso.alterar"));
            limparCampos(); pesquisar();
        } catch (ValidacaoException | BancoDadosException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    protected void excluir() {
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

    @Override
    protected void pesquisar() {
        modeloTabela.setRowCount(0);
        List<Leitor> lista;

        if (comboFiltro.getSelectedIndex() == 1) {
            String texto = campoPesquisa.getText().trim();
            if (texto.isEmpty()) {
                lista = controller.listarTodos();
            } else {
                try {
                    Leitor leitor = controller.buscarPorId(Integer.parseInt(texto));
                    lista = leitor != null ? List.of(leitor) : List.of();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Informe um código numérico válido.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        } else {
            lista = controller.buscarPorNome(campoPesquisa.getText());
        }

        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.nao.encontrado"));
            return;
        }
        for (Leitor l : lista) {
            modeloTabela.addRow(new Object[]{l.getId(), l.getNome(), l.getCpf(), l.getEmail(), l.getTelefone()});
        }
    }

    @Override
    protected void carregarDaTabela() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        campoId.setText(modeloTabela.getValueAt(linha, 0).toString());
        campoNome.setText(modeloTabela.getValueAt(linha, 1).toString());
        campoCpf.setText(modeloTabela.getValueAt(linha, 2).toString());
        Object email = modeloTabela.getValueAt(linha, 3);
        campoEmail.setText(email != null ? email.toString() : "");
        Object tel = modeloTabela.getValueAt(linha, 4);
        campoTelefone.setText(tel != null ? tel.toString() : "");
        abas.setSelectedIndex(0);
    }

    @Override
    protected void limparCampos() {
        campoId.setText(""); campoNome.setText(""); campoCpf.setText("");
        campoEmail.setText(""); campoTelefone.setText("");
        campoNome.requestFocus();
    }

    private void aplicarMascaraCpf(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
                String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String novo = atual.substring(0, offset) + text + atual.substring(offset);
                if (novo.replaceAll("[^0-9]", "").length() <= 11) {
                    super.insertString(fb, offset, text, attr);
                    formatarCpf(fb);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr) throws BadLocationException {
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

    private void aplicarFiltroNumerico(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
                if (text.matches("\\d+")) super.insertString(fb, offset, text, attr);
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr) throws BadLocationException {
                if (text == null || text.matches("\\d*")) super.replace(fb, offset, length, text, attr);
            }
        });
    }
}

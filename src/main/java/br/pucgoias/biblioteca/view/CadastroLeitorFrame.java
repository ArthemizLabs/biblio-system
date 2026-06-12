package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.controller.LeitorController;
import br.pucgoias.biblioteca.model.Leitor;
import br.pucgoias.biblioteca.util.Mensagens;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.List;

public class CadastroLeitorFrame extends GenericFrame<Leitor> {

    private JTextField campoNome, campoCpf, campoEmail, campoTelefone;
    private JComboBox<String> comboFiltro;
    private JLabel labelNome, labelCpf, labelEmail, labelTelefone;

    public CadastroLeitorFrame() {
        super(new LeitorController());
        configurarJanela(Mensagens.get("menu.leitores"), 640, 500, 120, 30);
    }

    @Override
    protected int adicionarCamposEspecificos(JPanel painel, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = 1;
        labelNome = new JLabel(Mensagens.get("label.nome"));
        painel.add(labelNome, gbc);
        campoNome = new JTextField(25);
        gbc.gridx = 1; painel.add(campoNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1 + 1;
        labelCpf = new JLabel(Mensagens.get("label.cpf"));
        painel.add(labelCpf, gbc);
        campoCpf = new JTextField(25);
        aplicarMascaraCpf(campoCpf);
        gbc.gridx = 1; painel.add(campoCpf, gbc);

        gbc.gridx = 0; gbc.gridy = 1 + 2;
        labelEmail = new JLabel(Mensagens.get("label.email"));
        painel.add(labelEmail, gbc);
        campoEmail = new JTextField(25);
        gbc.gridx = 1; painel.add(campoEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 1 + 3;
        labelTelefone = new JLabel(Mensagens.get("label.telefone"));
        painel.add(labelTelefone, gbc);
        campoTelefone = new JTextField(25);
        gbc.gridx = 1; painel.add(campoTelefone, gbc);

        return 1 + 4;
    }

    @Override
    protected JComponent componenteFiltroExtra() {
        comboFiltro = new JComboBox<>(new String[]{Mensagens.get("col.nome"), Mensagens.get("col.codigo")});
        comboFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboFiltro.addActionListener(e -> {
            campoPesquisa.setText("");
            if (comboFiltro.getSelectedIndex() == 1) {
                aplicarFiltroNumerico(campoPesquisa);
            } else {
                ((AbstractDocument) campoPesquisa.getDocument()).setDocumentFilter(null);
            }
        });
        return comboFiltro;
    }

    @Override
    protected Leitor construirEntidade(int id) {
        return new Leitor(id, campoNome.getText(), campoCpf.getText(), campoEmail.getText(), campoTelefone.getText());
    }

    @Override
    protected List<Leitor> executarBusca() {
        LeitorController c = (LeitorController) controller;
        if (comboFiltro.getSelectedIndex() == 1) {
            String texto = campoPesquisa.getText().trim();
            if (texto.isEmpty()) return c.listarTodos();
            try {
                Leitor l = c.buscarPorId(Integer.parseInt(texto));
                return l != null ? List.of(l) : List.of();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Informe um código numérico válido.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }
        return c.buscarPorNome(campoPesquisa.getText());
    }

    @Override
    protected Object[] linhaParaTabela(Leitor l) {
        return new Object[]{l.getId(), l.getNome(), l.getCpf(), l.getEmail(), l.getTelefone()};
    }

    @Override
    protected void preencherCampos(int linha) {
        campoNome.setText(modeloTabela.getValueAt(linha, 1).toString());
        campoCpf.setText(modeloTabela.getValueAt(linha, 2).toString());
        Object email = modeloTabela.getValueAt(linha, 3);
        campoEmail.setText(email != null ? email.toString() : "");
        Object tel = modeloTabela.getValueAt(linha, 4);
        campoTelefone.setText(tel != null ? tel.toString() : "");
    }

    @Override
    protected void limparCamposEspecificos() {
        campoNome.setText(""); campoCpf.setText("");
        campoEmail.setText(""); campoTelefone.setText("");
        campoNome.requestFocus();
    }

    @Override
    protected String[] chavesColunasTabela() {
        return new String[]{"col.codigo", "col.nome", "col.cpf", "col.email", "col.telefone"};
    }

    @Override
    protected String chaveLabelPesquisa() {
        return "label.pesquisar.por";
    }

    @Override
    protected void atualizarTextos() {
        setTitle(Mensagens.get("menu.leitores"));
        labelNome.setText(Mensagens.get("label.nome"));
        labelCpf.setText(Mensagens.get("label.cpf"));
        labelEmail.setText(Mensagens.get("label.email"));
        labelTelefone.setText(Mensagens.get("label.telefone"));
        int sel = comboFiltro.getSelectedIndex();
        comboFiltro.removeAllItems();
        comboFiltro.addItem(Mensagens.get("col.nome"));
        comboFiltro.addItem(Mensagens.get("col.codigo"));
        comboFiltro.setSelectedIndex(sel);
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

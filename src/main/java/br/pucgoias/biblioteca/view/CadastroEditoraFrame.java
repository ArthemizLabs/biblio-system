package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.controller.EditoraController;
import br.pucgoias.biblioteca.model.Editora;
import br.pucgoias.biblioteca.util.Mensagens;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CadastroEditoraFrame extends GenericFrame<Editora> {

    private JTextField campoNome;
    private JTextField campoCidade;
    private JLabel labelNome, labelCidade;

    public CadastroEditoraFrame() {
        super(new EditoraController());
        configurarJanela(Mensagens.get("menu.editoras"), 550, 420, 60, 60);
    }

    @Override
    protected int adicionarCamposEspecificos(JPanel painel, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = 1;
        labelNome = new JLabel(Mensagens.get("label.nome"));
        painel.add(labelNome, gbc);
        campoNome = new JTextField(25);
        gbc.gridx = 1; painel.add(campoNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1 + 1;
        labelCidade = new JLabel(Mensagens.get("label.cidade"));
        painel.add(labelCidade, gbc);
        campoCidade = new JTextField(25);
        gbc.gridx = 1; painel.add(campoCidade, gbc);

        return 1 + 2;
    }

    @Override
    protected Editora construirEntidade(int id) {
        return new Editora(id, campoNome.getText(), campoCidade.getText());
    }

    @Override
    protected List<Editora> executarBusca() {
        return ((EditoraController) controller).buscarPorNome(campoPesquisa.getText());
    }

    @Override
    protected Object[] linhaParaTabela(Editora e) {
        return new Object[]{e.getId(), e.getNome(), e.getCidade()};
    }

    @Override
    protected void preencherCampos(int linha) {
        campoNome.setText(modeloTabela.getValueAt(linha, 1).toString());
        Object cidade = modeloTabela.getValueAt(linha, 2);
        campoCidade.setText(cidade != null ? cidade.toString() : "");
    }

    @Override
    protected void limparCamposEspecificos() {
        campoNome.setText("");
        campoCidade.setText("");
        campoNome.requestFocus();
    }

    @Override
    protected String[] chavesColunasTabela() {
        return new String[]{"col.codigo", "col.nome", "col.cidade"};
    }

    @Override
    protected String chaveLabelPesquisa() {
        return "label.nome";
    }

    @Override
    protected void atualizarTextos() {
        setTitle(Mensagens.get("menu.editoras"));
        labelNome.setText(Mensagens.get("label.nome"));
        labelCidade.setText(Mensagens.get("label.cidade"));
    }
}

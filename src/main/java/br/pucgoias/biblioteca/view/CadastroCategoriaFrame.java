package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.controller.CategoriaController;
import br.pucgoias.biblioteca.model.Categoria;
import br.pucgoias.biblioteca.util.Mensagens;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CadastroCategoriaFrame extends GenericFrame<Categoria> {

    private JTextField campoNome;
    private JTextArea campoDescricao;
    private JLabel labelNome, labelDescricao;

    public CadastroCategoriaFrame() {
        super(new CategoriaController());
        configurarJanela(Mensagens.get("menu.categorias"), 550, 450, 90, 90);
    }

    @Override
    protected int adicionarCamposEspecificos(JPanel painel, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = 1;
        labelNome = new JLabel(Mensagens.get("label.nome"));
        painel.add(labelNome, gbc);
        campoNome = new JTextField(25);
        gbc.gridx = 1; painel.add(campoNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1 + 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        labelDescricao = new JLabel(Mensagens.get("label.descricao"));
        painel.add(labelDescricao, gbc);
        campoDescricao = new JTextArea(3, 25);
        campoDescricao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoDescricao.setLineWrap(true);
        campoDescricao.setWrapStyleWord(true);
        gbc.gridx = 1; painel.add(new JScrollPane(campoDescricao), gbc);

        return 1 + 2;
    }

    @Override
    protected Categoria construirEntidade(int id) {
        return new Categoria(id, campoNome.getText(), campoDescricao.getText());
    }

    @Override
    protected List<Categoria> executarBusca() {
        return controller.listarTodos();
    }

    @Override
    protected Object[] linhaParaTabela(Categoria c) {
        return new Object[]{c.getId(), c.getNome(), c.getDescricao()};
    }

    @Override
    protected void preencherCampos(int linha) {
        campoNome.setText(modeloTabela.getValueAt(linha, 1).toString());
        Object desc = modeloTabela.getValueAt(linha, 2);
        campoDescricao.setText(desc != null ? desc.toString() : "");
    }

    @Override
    protected void limparCamposEspecificos() {
        campoNome.setText("");
        campoDescricao.setText("");
        campoNome.requestFocus();
    }

    @Override
    protected String[] chavesColunasTabela() {
        return new String[]{"col.codigo", "col.nome", "col.descricao"};
    }

    @Override
    protected String chaveLabelPesquisa() {
        return "label.nome";
    }

    @Override
    protected void atualizarTextos() {
        setTitle(Mensagens.get("menu.categorias"));
        labelNome.setText(Mensagens.get("label.nome"));
        labelDescricao.setText(Mensagens.get("label.descricao"));
    }
}

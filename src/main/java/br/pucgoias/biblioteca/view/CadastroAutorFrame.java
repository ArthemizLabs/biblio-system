package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.controller.AutorController;
import br.pucgoias.biblioteca.model.Autor;
import br.pucgoias.biblioteca.util.Mensagens;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CadastroAutorFrame extends GenericFrame<Autor> {

    private JTextField campoNome, campoNacionalidade;
    private JLabel labelNome, labelNacionalidade;

    public CadastroAutorFrame() {
        super(new AutorController());
        configurarJanela(Mensagens.get("menu.autores"), 550, 420, 30, 30);
    }

    @Override
    protected int adicionarCamposEspecificos(JPanel painel, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = 1;
        labelNome = new JLabel(Mensagens.get("label.nome"));
        painel.add(labelNome, gbc);
        campoNome = new JTextField(25);
        gbc.gridx = 1; painel.add(campoNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1 + 1;
        labelNacionalidade = new JLabel(Mensagens.get("label.nacionalidade"));
        painel.add(labelNacionalidade, gbc);
        campoNacionalidade = new JTextField(25);
        gbc.gridx = 1; painel.add(campoNacionalidade, gbc);

        return 1 + 2;
    }

    @Override
    protected Autor construirEntidade(int id) {
        return new Autor(id, campoNome.getText(), campoNacionalidade.getText());
    }

    @Override
    protected List<Autor> executarBusca() {
        return ((AutorController) controller).buscarPorNome(campoPesquisa.getText());
    }

    @Override
    protected Object[] linhaParaTabela(Autor a) {
        return new Object[]{a.getId(), a.getNome(), a.getNacionalidade()};
    }

    @Override
    protected void preencherCampos(int linha) {
        campoNome.setText(modeloTabela.getValueAt(linha, 1).toString());
        Object nac = modeloTabela.getValueAt(linha, 2);
        campoNacionalidade.setText(nac != null ? nac.toString() : "");
    }

    @Override
    protected void limparCamposEspecificos() {
        campoNome.setText("");
        campoNacionalidade.setText("");
        campoNome.requestFocus();
    }

    @Override
    protected String[] chavesColunasTabela() {
        return new String[]{"col.codigo", "col.nome", "col.nacionalidade"};
    }

    @Override
    protected String chaveLabelPesquisa() {
        return "label.nome";
    }

    @Override
    protected void atualizarTextos() {
        setTitle(Mensagens.get("menu.autores"));
        labelNome.setText(Mensagens.get("label.nome"));
        labelNacionalidade.setText(Mensagens.get("label.nacionalidade"));
    }
}

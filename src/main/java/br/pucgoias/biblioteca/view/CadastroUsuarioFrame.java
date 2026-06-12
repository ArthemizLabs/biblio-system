package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.controller.UsuarioController;
import br.pucgoias.biblioteca.model.Usuario;
import br.pucgoias.biblioteca.util.Mensagens;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CadastroUsuarioFrame extends GenericFrame<Usuario> {

    private JTextField campoLogin;
    private JPasswordField campoSenha;
    private JComboBox<String> comboPerfil;
    private JLabel labelLogin, labelSenha, labelPerfil;

    public CadastroUsuarioFrame() {
        super(new UsuarioController());
        configurarJanela(Mensagens.get("menu.usuarios"), 560, 460, 150, 60);
    }

    @Override
    protected int adicionarCamposEspecificos(JPanel painel, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = 1;
        labelLogin = new JLabel(Mensagens.get("label.login"));
        painel.add(labelLogin, gbc);
        campoLogin = new JTextField(25);
        gbc.gridx = 1; painel.add(campoLogin, gbc);

        gbc.gridx = 0; gbc.gridy = 1 + 1;
        labelSenha = new JLabel(Mensagens.get("label.senha"));
        painel.add(labelSenha, gbc);
        campoSenha = new JPasswordField(25);
        gbc.gridx = 1; painel.add(campoSenha, gbc);

        gbc.gridx = 0; gbc.gridy = 1 + 2;
        labelPerfil = new JLabel(Mensagens.get("label.perfil"));
        painel.add(labelPerfil, gbc);
        comboPerfil = new JComboBox<>(new String[]{"ADMIN", "FUNCIONÁRIO"});
        comboPerfil.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; painel.add(comboPerfil, gbc);

        return 1 + 3;
    }

    @Override
    protected Usuario construirEntidade(int id) {
        return new Usuario(id, campoLogin.getText(), new String(campoSenha.getPassword()), perfilSelecionado());
    }

    @Override
    protected List<Usuario> executarBusca() {
        String filtro = campoPesquisa.getText().trim().toLowerCase();
        List<Usuario> todos = controller.listarTodos();
        if (filtro.isEmpty()) return todos;
        List<Usuario> filtrados = new ArrayList<>();
        for (Usuario u : todos) {
            if (u.getLogin().toLowerCase().contains(filtro)) filtrados.add(u);
        }
        return filtrados;
    }

    @Override
    protected Object[] linhaParaTabela(Usuario u) {
        String display = u.getPerfil() == Usuario.Perfil.FUNCIONARIO ? "FUNCIONÁRIO" : u.getPerfil().name();
        return new Object[]{u.getId(), u.getLogin(), display};
    }

    @Override
    protected void preencherCampos(int linha) {
        campoLogin.setText(modeloTabela.getValueAt(linha, 1).toString());
        campoSenha.setText("");
        comboPerfil.setSelectedItem(modeloTabela.getValueAt(linha, 2).toString());
    }

    @Override
    protected void limparCamposEspecificos() {
        campoLogin.setText(""); campoSenha.setText("");
        comboPerfil.setSelectedIndex(0);
        campoLogin.requestFocus();
    }

    @Override
    protected String[] chavesColunasTabela() {
        return new String[]{"col.codigo", "col.login", "col.perfil"};
    }

    @Override
    protected String chaveLabelPesquisa() {
        return "label.login";
    }

    @Override
    protected void atualizarTextos() {
        setTitle(Mensagens.get("menu.usuarios"));
        labelLogin.setText(Mensagens.get("label.login"));
        labelSenha.setText(Mensagens.get("label.senha"));
        labelPerfil.setText(Mensagens.get("label.perfil"));
    }

    private Usuario.Perfil perfilSelecionado() {
        return "FUNCIONÁRIO".equals(comboPerfil.getSelectedItem())
                ? Usuario.Perfil.FUNCIONARIO
                : Usuario.Perfil.ADMIN;
    }
}

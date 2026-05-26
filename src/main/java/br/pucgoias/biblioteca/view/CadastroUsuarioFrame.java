package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.controller.UsuarioController;
import br.pucgoias.biblioteca.model.Usuario;
import br.pucgoias.biblioteca.util.Mensagens;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;
import br.pucgoias.biblioteca.util.exceptions.ValidacaoException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Janela de cadastro de Usuários do sistema.
 * Acessível apenas por administradores. Gerencia login, senha e perfil.
 */
public class CadastroUsuarioFrame extends JInternalFrame {

    private final UsuarioController controller = new UsuarioController();

    private JTextField campoId, campoLogin;
    private JPasswordField campoSenha;
    private JComboBox<String> comboPerfil;

    private JTextField campoPesquisa;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public CadastroUsuarioFrame() {
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

    private JPanel criarPainelCadastro() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Código:"), gbc);
        campoId = new JTextField(8);
        campoId.setEditable(false);
        campoId.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1; painel.add(campoId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(new JLabel("Login: *"), gbc);
        campoLogin = new JTextField(25);
        gbc.gridx = 1; painel.add(campoLogin, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        painel.add(new JLabel("Senha: *"), gbc);
        campoSenha = new JPasswordField(25);
        gbc.gridx = 1; painel.add(campoSenha, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        painel.add(new JLabel("Perfil: *"), gbc);
        comboPerfil = new JComboBox<>(new String[]{"FUNCIONÁRIO", "ADMIN"});
        comboPerfil.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; painel.add(comboPerfil, gbc);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnSalvar = criarBotao(Mensagens.get("btn.salvar"), new Color(39, 174, 96));
        JButton btnAlterar = criarBotao(Mensagens.get("btn.alterar"), new Color(41, 128, 185));
        JButton btnExcluir = criarBotao(Mensagens.get("btn.excluir"), new Color(192, 57, 43));
        JButton btnLimpar = criarBotao(Mensagens.get("btn.limpar"), new Color(127, 140, 141));
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnAlterar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 6, 6, 6);
        painel.add(painelBotoes, gbc);

        btnSalvar.addActionListener(e  -> salvar());
        btnAlterar.addActionListener(e -> alterar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e  -> limparCampos());

        return painel;
    }

    private JPanel criarPainelPesquisa() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBusca.add(new JLabel("Login:"));
        campoPesquisa = new JTextField(20);
        JButton btnPesquisar = criarBotao(Mensagens.get("btn.pesquisar"), new Color(41, 128, 185));
        painelBusca.add(campoPesquisa);
        painelBusca.add(btnPesquisar);

        modeloTabela = new DefaultTableModel(
                new String[]{"Código", "Login", "Perfil"}, 0) {
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

    private void salvar() {
        try {
            Usuario usuario = new Usuario(0,
                    campoLogin.getText(),
                    new String(campoSenha.getPassword()),
                    Usuario.Perfil.valueOf((String) comboPerfil.getSelectedItem()));
            controller.salvar(usuario);
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
            Usuario usuario = new Usuario(
                    Integer.parseInt(campoId.getText()),
                    campoLogin.getText(),
                    new String(campoSenha.getPassword()),
                    Usuario.Perfil.valueOf((String) comboPerfil.getSelectedItem()));
            controller.atualizar(usuario);
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
        List<Usuario> lista = controller.listarTodos();
        modeloTabela.setRowCount(0);
        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.nao.encontrado"));
            return;
        }
        String filtro = campoPesquisa.getText().trim().toLowerCase();
        for (Usuario u : lista) {
            if (filtro.isEmpty() || u.getLogin().toLowerCase().contains(filtro)) {
                modeloTabela.addRow(new Object[]{u.getId(), u.getLogin(), u.getPerfil()});
            }
        }
    }

    private void carregarDaTabela() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        campoId.setText(modeloTabela.getValueAt(linha, 0).toString());
        campoLogin.setText(modeloTabela.getValueAt(linha, 1).toString());
        campoSenha.setText("");
        comboPerfil.setSelectedItem(modeloTabela.getValueAt(linha, 2).toString());
        JTabbedPane abas = (JTabbedPane) getContentPane().getComponent(0);
        abas.setSelectedIndex(0);
    }

    private void limparCampos() {
        campoId.setText(""); campoLogin.setText("");
        campoSenha.setText("");
        comboPerfil.setSelectedIndex(0);
        campoLogin.requestFocus();
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void configurarJanela() {
        setTitle(Mensagens.get("menu.usuarios"));
        setSize(560, 460);
        setClosable(true); setMaximizable(true);
        setIconifiable(true); setResizable(true);
        setLocation(150, 60);
    }
}
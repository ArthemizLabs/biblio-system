package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.controller.UsuarioController;
import br.pucgoias.biblioteca.model.Usuario;
import br.pucgoias.biblioteca.util.IdiomaListener;
import br.pucgoias.biblioteca.util.Mensagens;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;
import br.pucgoias.biblioteca.util.exceptions.ValidacaoException;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CadastroUsuarioFrame extends JInternalFrame implements IdiomaListener {

    private final UsuarioController controller = new UsuarioController();

    private JTextField campoId, campoLogin;
    private JPasswordField campoSenha;
    private JComboBox<String> comboPerfil;

    private JTextField campoPesquisa;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private JTabbedPane abas;
    private JLabel labelCodigo, labelLogin, labelSenha, labelPerfil, labelPesquisaLogin;

    public CadastroUsuarioFrame() {
        inicializarComponentes();
        configurarJanela();
        Mensagens.addIdiomaListener(this);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override public void internalFrameClosed(InternalFrameEvent e) {
                Mensagens.removeIdiomaListener(CadastroUsuarioFrame.this);
            }
        });
    }

    private void inicializarComponentes() {
        abas = new JTabbedPane();
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
        labelCodigo = new JLabel(Mensagens.get("label.codigo"));
        painel.add(labelCodigo, gbc);
        campoId = new JTextField(8);
        campoId.setEditable(false);
        campoId.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1; painel.add(campoId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        labelLogin = new JLabel(Mensagens.get("label.login"));
        painel.add(labelLogin, gbc);
        campoLogin = new JTextField(25);
        gbc.gridx = 1; painel.add(campoLogin, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        labelSenha = new JLabel(Mensagens.get("label.senha"));
        painel.add(labelSenha, gbc);
        campoSenha = new JPasswordField(25);
        gbc.gridx = 1; painel.add(campoSenha, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        labelPerfil = new JLabel(Mensagens.get("label.perfil"));
        painel.add(labelPerfil, gbc);
        comboPerfil = new JComboBox<>(new String[]{"ADMIN", "FUNCIONÁRIO"});
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
        labelPesquisaLogin = new JLabel(Mensagens.get("label.login"));
        painelBusca.add(labelPesquisaLogin);
        campoPesquisa = new JTextField(20);
        JButton btnPesquisar = criarBotao(Mensagens.get("btn.pesquisar"), new Color(41, 128, 185));
        painelBusca.add(campoPesquisa);
        painelBusca.add(btnPesquisar);

        modeloTabela = new DefaultTableModel(
                new String[]{Mensagens.get("col.codigo"), Mensagens.get("col.login"), Mensagens.get("col.perfil")}, 0) {
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
    public void onIdiomaChanged() {
        setTitle(Mensagens.get("menu.usuarios"));
        abas.setTitleAt(0, Mensagens.get("aba.cadastro"));
        abas.setTitleAt(1, Mensagens.get("aba.pesquisa"));
        labelCodigo.setText(Mensagens.get("label.codigo"));
        labelLogin.setText(Mensagens.get("label.login"));
        labelSenha.setText(Mensagens.get("label.senha"));
        labelPerfil.setText(Mensagens.get("label.perfil"));
        labelPesquisaLogin.setText(Mensagens.get("label.login"));
        modeloTabela.setColumnIdentifiers(new String[]{
            Mensagens.get("col.codigo"), Mensagens.get("col.login"), Mensagens.get("col.perfil")
        });
    }

    private void salvar() {
        try {
            Usuario usuario = new Usuario(0,
                    campoLogin.getText(),
                    new String(campoSenha.getPassword()),
                    perfilSelecionado());
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
                    perfilSelecionado());
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
        try {
            List<Usuario> lista = controller.listarTodos();
            modeloTabela.setRowCount(0);
            String filtro = campoPesquisa.getText().trim().toLowerCase();
            for (Usuario u : lista) {
                if (filtro.isEmpty() || u.getLogin().toLowerCase().contains(filtro)) {
                    String display = u.getPerfil() == Usuario.Perfil.FUNCIONARIO ? "FUNCIONÁRIO" : u.getPerfil().name();
                    modeloTabela.addRow(new Object[]{u.getId(), u.getLogin(), display});
                }
            }
            if (modeloTabela.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.nao.encontrado"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao pesquisar: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarDaTabela() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        campoId.setText(modeloTabela.getValueAt(linha, 0).toString());
        campoLogin.setText(modeloTabela.getValueAt(linha, 1).toString());
        campoSenha.setText("");
        comboPerfil.setSelectedItem(modeloTabela.getValueAt(linha, 2).toString());
        abas.setSelectedIndex(0);
    }

    private void limparCampos() {
        campoId.setText(""); campoLogin.setText("");
        campoSenha.setText("");
        comboPerfil.setSelectedIndex(0);
        campoLogin.requestFocus();
    }

    private Usuario.Perfil perfilSelecionado() {
        return "FUNCIONÁRIO".equals(comboPerfil.getSelectedItem())
                ? Usuario.Perfil.FUNCIONARIO
                : Usuario.Perfil.ADMIN;
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
        setTitle(Mensagens.get("menu.usuarios"));
        setSize(560, 460);
        setClosable(true); setMaximizable(true);
        setIconifiable(true); setResizable(true);
        setLocation(150, 60);
    }
}

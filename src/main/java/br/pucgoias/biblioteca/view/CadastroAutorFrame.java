package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.controller.AutorController;
import br.pucgoias.biblioteca.model.Autor;
import br.pucgoias.biblioteca.util.Mensagens;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;
import br.pucgoias.biblioteca.util.exceptions.ValidacaoException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Janela de cadastro de Autores.
 * Possui abas de Cadastro e Pesquisa com CRUD completo via AutorController.
 */
public class CadastroAutorFrame extends JInternalFrame {

    private final AutorController controller = new AutorController();

    // Aba Cadastro
    private JTextField campoId;
    private JTextField campoNome;
    private JTextField campoNacionalidade;

    // Aba Pesquisa
    private JTextField campoPesquisa;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public CadastroAutorFrame() {
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

        // Código (somente leitura)
        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Código:"), gbc);
        campoId = new JTextField(8);
        campoId.setEditable(false);
        campoId.setBackground(new Color(230, 230, 230));
        gbc.gridx = 1;
        painel.add(campoId, gbc);

        // Nome
        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(new JLabel("Nome: *"), gbc);
        campoNome = new JTextField(25);
        gbc.gridx = 1;
        painel.add(campoNome, gbc);

        // Nacionalidade
        gbc.gridx = 0; gbc.gridy = 2;
        painel.add(new JLabel("Nacionalidade:"), gbc);
        campoNacionalidade = new JTextField(25);
        gbc.gridx = 1;
        painel.add(campoNacionalidade, gbc);

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

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 6, 6, 6);
        painel.add(painelBotoes, gbc);

        // Ações dos botões
        btnSalvar.addActionListener(e  -> salvar());
        btnAlterar.addActionListener(e -> alterar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e  -> limparCampos());

        return painel;
    }

    // ----------------------------------------------------------------
    // ABA PESQUISA
    // ----------------------------------------------------------------
    private JPanel criarPainelPesquisa() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Painel de busca
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBusca.add(new JLabel("Nome:"));
        campoPesquisa = new JTextField(20);
        JButton btnPesquisar = criarBotao(Mensagens.get("btn.pesquisar"), new Color(41, 128, 185));
        painelBusca.add(campoPesquisa);
        painelBusca.add(btnPesquisar);

        // Tabela
        modeloTabela = new DefaultTableModel(
                new String[]{"Código", "Nome", "Nacionalidade"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Seleciona linha → preenche aba cadastro
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) carregarDaTabela();
        });

        painel.add(painelBusca, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        btnPesquisar.addActionListener(e -> pesquisar());
        // Enter no campo também pesquisa
        campoPesquisa.addActionListener(e -> pesquisar());

        return painel;
    }

    // ----------------------------------------------------------------
    // AÇÕES
    // ----------------------------------------------------------------
    private void salvar() {
        try {
            Autor autor = new Autor(0, campoNome.getText(), campoNacionalidade.getText());
            controller.salvar(autor);
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.sucesso.salvar"));
            limparCampos();
            pesquisar();
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
            Autor autor = new Autor(
                    Integer.parseInt(campoId.getText()),
                    campoNome.getText(),
                    campoNacionalidade.getText()
            );
            controller.atualizar(autor);
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.sucesso.alterar"));
            limparCampos();
            pesquisar();
        } catch (ValidacaoException | BancoDadosException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluir() {
        if (campoId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.selecionar"), "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirma = JOptionPane.showConfirmDialog(this,
                Mensagens.get("msg.confirmar.excluir"), "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            try {
                controller.deletar(Integer.parseInt(campoId.getText()));
                JOptionPane.showMessageDialog(this, Mensagens.get("msg.sucesso.excluir"));
                limparCampos();
                pesquisar();
            } catch (BancoDadosException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void pesquisar() {
        List<Autor> lista = controller.buscarPorNome(campoPesquisa.getText());
        modeloTabela.setRowCount(0);
        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.nao.encontrado"));
            return;
        }
        for (Autor a : lista) {
            modeloTabela.addRow(new Object[]{a.getId(), a.getNome(), a.getNacionalidade()});
        }
    }

    private void carregarDaTabela() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        campoId.setText(modeloTabela.getValueAt(linha, 0).toString());
        campoNome.setText(modeloTabela.getValueAt(linha, 1).toString());
        Object nac = modeloTabela.getValueAt(linha, 2);
        campoNacionalidade.setText(nac != null ? nac.toString() : "");

        // Vai para aba de cadastro automaticamente
        JTabbedPane abas = (JTabbedPane) getContentPane().getComponent(0);
        abas.setSelectedIndex(0);
    }

    private void limparCampos() {
        campoId.setText("");
        campoNome.setText("");
        campoNacionalidade.setText("");
        campoNome.requestFocus();
    }

    // ----------------------------------------------------------------
    // UTILITÁRIOS
    // ----------------------------------------------------------------
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
        setTitle(Mensagens.get("menu.autores"));
        setSize(550, 420);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setResizable(true);
        setLocation(30, 30);
    }
}
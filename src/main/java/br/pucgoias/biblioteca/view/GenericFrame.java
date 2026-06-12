package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.controller.interfaces.ICrudController;
import br.pucgoias.biblioteca.view.interfaces.IdiomaListener;
import br.pucgoias.biblioteca.util.Mensagens;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;
import br.pucgoias.biblioteca.util.exceptions.ValidacaoException;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public abstract class GenericFrame<T> extends JInternalFrame implements IdiomaListener {

    protected final ICrudController<T> controller;

    protected JTabbedPane abas;
    protected JTable tabela;
    protected DefaultTableModel modeloTabela;
    protected JTextField campoId;
    protected JTextField campoPesquisa;
    protected JLabel labelCodigo;
    protected JLabel labelPesquisa;

    protected GenericFrame(ICrudController<T> controller) {
        this.controller = controller;
        inicializarComponentes();
        Mensagens.addIdiomaListener(this);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override public void internalFrameClosed(InternalFrameEvent e) {
                Mensagens.removeIdiomaListener(GenericFrame.this);
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

    protected final JPanel criarPainelCadastro() {
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
        gbc.gridx = 1;
        painel.add(campoId, gbc);

        int proximaLinha = adicionarCamposEspecificos(painel, gbc);

        gbc.gridx = 0; gbc.gridy = proximaLinha;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 6, 6, 6);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnSalvar  = criarBotao(Mensagens.get("btn.salvar"),  new Color(39, 174, 96));
        JButton btnAlterar = criarBotao(Mensagens.get("btn.alterar"), new Color(41, 128, 185));
        JButton btnExcluir = criarBotao(Mensagens.get("btn.excluir"), new Color(192, 57, 43));
        JButton btnLimpar  = criarBotao(Mensagens.get("btn.limpar"),  new Color(127, 140, 141));
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnAlterar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);
        painel.add(painelBotoes, gbc);

        btnSalvar.addActionListener(e  -> salvar());
        btnAlterar.addActionListener(e -> alterar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e  -> limparCampos());

        return painel;
    }

    protected final JPanel criarPainelPesquisa() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPesquisa = new JLabel(Mensagens.get(chaveLabelPesquisa()));
        painelBusca.add(labelPesquisa);

        JComponent filtroExtra = componenteFiltroExtra();
        if (filtroExtra != null) painelBusca.add(filtroExtra);

        campoPesquisa = new JTextField(20);
        painelBusca.add(campoPesquisa);

        JButton btnPesquisar = criarBotao(Mensagens.get("btn.pesquisar"), new Color(41, 128, 185));
        painelBusca.add(btnPesquisar);

        modeloTabela = new DefaultTableModel(resolverColunas(), 0) {
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

        aposCriarTabela(tabela);

        painel.add(painelBusca, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        btnPesquisar.addActionListener(e -> pesquisar());
        campoPesquisa.addActionListener(e -> pesquisar());

        return painel;
    }

    private String[] resolverColunas() {
        String[] keys = chavesColunasTabela();
        String[] out = new String[keys.length];
        for (int i = 0; i < keys.length; i++) out[i] = Mensagens.get(keys[i]);
        return out;
    }

    protected final void salvar() {
        try {
            controller.salvar(construirEntidade(0));
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.sucesso.salvar"));
            limparCampos();
            pesquisar();
        } catch (ValidacaoException | BancoDadosException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }

    protected final void alterar() {
        if (campoId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.selecionar"), "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            controller.atualizar(construirEntidade(Integer.parseInt(campoId.getText())));
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.sucesso.alterar"));
            limparCampos();
            pesquisar();
        } catch (ValidacaoException | BancoDadosException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }

    protected final void excluir() {
        if (campoId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.selecionar"), "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, Mensagens.get("msg.confirmar.excluir"),
                "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
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

    protected final void pesquisar() {
        modeloTabela.setRowCount(0);
        List<T> lista;
        try {
            lista = executarBusca();
        } catch (BancoDadosException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (lista == null) return;  // subclasse já tratou (ex.: aviso de input inválido)
        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.nao.encontrado"));
            return;
        }
        for (T item : lista) modeloTabela.addRow(linhaParaTabela(item));
    }

    protected final void carregarDaTabela() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        campoId.setText(modeloTabela.getValueAt(linha, 0).toString());
        preencherCampos(linha);
        abas.setSelectedIndex(0);
    }

    protected final void limparCampos() {
        campoId.setText("");
        limparCamposEspecificos();
    }

    protected void configurarJanela(String titulo, int w, int h, int x, int y) {
        setTitle(titulo);
        setSize(w, h);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setResizable(true);
        setLocation(x, y);
    }

    protected JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(cor);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    @Override
    public final void onIdiomaChanged() {
        abas.setTitleAt(0, Mensagens.get("aba.cadastro"));
        abas.setTitleAt(1, Mensagens.get("aba.pesquisa"));
        labelCodigo.setText(Mensagens.get("label.codigo"));
        labelPesquisa.setText(Mensagens.get(chaveLabelPesquisa()));
        modeloTabela.setColumnIdentifiers(resolverColunas());
        atualizarTextos();
    }

    protected abstract int adicionarCamposEspecificos(JPanel painel, GridBagConstraints gbc);
    protected abstract T construirEntidade(int id);
    protected abstract List<T> executarBusca();
    protected abstract Object[] linhaParaTabela(T entidade);
    protected abstract void preencherCampos(int linha);
    protected abstract void limparCamposEspecificos();
    protected abstract String[] chavesColunasTabela();
    protected abstract String chaveLabelPesquisa();
    protected abstract void atualizarTextos();

    protected JComponent componenteFiltroExtra() { return null; }
    protected void aposCriarTabela(JTable tabela) { }
}

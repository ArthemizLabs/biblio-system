package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.controller.AutorController;
import br.pucgoias.biblioteca.controller.CategoriaController;
import br.pucgoias.biblioteca.controller.EditoraController;
import br.pucgoias.biblioteca.controller.LivroController;
import br.pucgoias.biblioteca.model.*;
import br.pucgoias.biblioteca.util.Mensagens;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;
import br.pucgoias.biblioteca.util.exceptions.ValidacaoException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.List;

public class CadastroLivroFrame extends GenericFrame {

    private final LivroController livroController         = new LivroController();
    private final AutorController autorController         = new AutorController();
    private final EditoraController editoraController     = new EditoraController();
    private final CategoriaController categoriaController = new CategoriaController();

    private JTextField campoTitulo, campoIsbn, campoAno, campoQtd;
    private JComboBox<Autor>     comboAutor;
    private JComboBox<Editora>   comboEditora;
    private JComboBox<Categoria> comboCategoria;
    private JComboBox<String>    comboFiltro;

    private JLabel labelCodigo, labelTitulo, labelIsbn, labelAno, labelQtd;
    private JLabel labelAutor, labelEditora, labelCategoria, labelPesquisarPor;

    public CadastroLivroFrame() {
        configurarJanela(Mensagens.get("menu.livros"), 700, 580, 20, 20);
        carregarCombos();
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
        labelTitulo = new JLabel(Mensagens.get("label.titulo"));
        painel.add(labelTitulo, gbc);
        campoTitulo = new JTextField(30);
        gbc.gridx = 1; painel.add(campoTitulo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        labelIsbn = new JLabel(Mensagens.get("label.isbn"));
        painel.add(labelIsbn, gbc);
        campoIsbn = new JTextField(20);
        aplicarFiltroNumerico(campoIsbn);
        gbc.gridx = 1; painel.add(campoIsbn, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        labelAno = new JLabel(Mensagens.get("label.ano"));
        painel.add(labelAno, gbc);
        campoAno = new JTextField(6);
        aplicarFiltroAno(campoAno);
        gbc.gridx = 1; painel.add(campoAno, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        labelQtd = new JLabel(Mensagens.get("label.quantidade"));
        painel.add(labelQtd, gbc);
        campoQtd = new JTextField(6);
        aplicarFiltroNumerico(campoQtd);
        gbc.gridx = 1; painel.add(campoQtd, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        labelAutor = new JLabel(Mensagens.get("label.autor"));
        painel.add(labelAutor, gbc);
        comboAutor = new JComboBox<>();
        comboAutor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; painel.add(comboAutor, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        labelEditora = new JLabel(Mensagens.get("label.editora"));
        painel.add(labelEditora, gbc);
        comboEditora = new JComboBox<>();
        comboEditora.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; painel.add(comboEditora, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        labelCategoria = new JLabel(Mensagens.get("label.categoria"));
        painel.add(labelCategoria, gbc);
        comboCategoria = new JComboBox<>();
        comboCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; painel.add(comboCategoria, gbc);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnSalvar = criarBotao(Mensagens.get("btn.salvar"), new Color(39, 174, 96));
        JButton btnAlterar = criarBotao(Mensagens.get("btn.alterar"), new Color(41, 128, 185));
        JButton btnExcluir = criarBotao(Mensagens.get("btn.excluir"), new Color(192, 57, 43));
        JButton btnLimpar  = criarBotao(Mensagens.get("btn.limpar"),  new Color(127, 140, 141));
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnAlterar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 8;
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

        comboFiltro = new JComboBox<>(new String[]{
            Mensagens.get("col.titulo"), Mensagens.get("col.codigo"), Mensagens.get("col.isbn")
        });
        comboFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelBusca.add(comboFiltro);

        campoPesquisa = new JTextField(20);
        painelBusca.add(campoPesquisa);

        JButton btnPesquisar = criarBotao(Mensagens.get("btn.pesquisar"), new Color(41, 128, 185));
        painelBusca.add(btnPesquisar);

        comboFiltro.addActionListener(e -> {
            campoPesquisa.setText("");
            int idx = comboFiltro.getSelectedIndex();
            if (idx == 1 || idx == 2) {
                aplicarFiltroNumerico(campoPesquisa);
            } else {
                ((AbstractDocument) campoPesquisa.getDocument()).setDocumentFilter(null);
            }
        });

        modeloTabela = new DefaultTableModel(
                new String[]{Mensagens.get("col.codigo"), Mensagens.get("col.titulo"), Mensagens.get("col.isbn"),
                             Mensagens.get("col.ano"), Mensagens.get("col.quantidade"),
                             Mensagens.get("col.autor"), Mensagens.get("col.categoria")}, 0) {
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

        tabela.getColumnModel().getColumn(0).setPreferredWidth(55);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(45);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(120);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(100);

        painel.add(painelBusca, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        btnPesquisar.addActionListener(e -> pesquisar());
        campoPesquisa.addActionListener(e -> pesquisar());

        return painel;
    }

    @Override
    protected void atualizarTextos() {
        setTitle(Mensagens.get("menu.livros"));
        labelCodigo.setText(Mensagens.get("label.codigo"));
        labelTitulo.setText(Mensagens.get("label.titulo"));
        labelIsbn.setText(Mensagens.get("label.isbn"));
        labelAno.setText(Mensagens.get("label.ano"));
        labelQtd.setText(Mensagens.get("label.quantidade"));
        labelAutor.setText(Mensagens.get("label.autor"));
        labelEditora.setText(Mensagens.get("label.editora"));
        labelCategoria.setText(Mensagens.get("label.categoria"));
        labelPesquisarPor.setText(Mensagens.get("label.pesquisar.por"));
        int sel = comboFiltro.getSelectedIndex();
        comboFiltro.removeAllItems();
        comboFiltro.addItem(Mensagens.get("col.titulo"));
        comboFiltro.addItem(Mensagens.get("col.codigo"));
        comboFiltro.addItem(Mensagens.get("col.isbn"));
        comboFiltro.setSelectedIndex(sel);
        modeloTabela.setColumnIdentifiers(new String[]{
            Mensagens.get("col.codigo"), Mensagens.get("col.titulo"), Mensagens.get("col.isbn"),
            Mensagens.get("col.ano"), Mensagens.get("col.quantidade"),
            Mensagens.get("col.autor"), Mensagens.get("col.categoria")
        });
    }

    @Override
    protected void salvar() {
        try {
            livroController.salvar(montarLivro());
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
            Livro livro = montarLivro();
            livro.setId(Integer.parseInt(campoId.getText()));
            livroController.atualizar(livro);
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
                livroController.deletar(Integer.parseInt(campoId.getText()));
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
        List<Livro> lista;
        String texto = campoPesquisa.getText().trim();
        int filtro = comboFiltro.getSelectedIndex();

        try {
            if (filtro == 1) {
                lista = texto.isEmpty() ? livroController.listarTodos()
                        : (livroController.buscarPorId(Integer.parseInt(texto)) != null
                           ? List.of(livroController.buscarPorId(Integer.parseInt(texto))) : List.of());
            } else if (filtro == 2) {
                lista = texto.isEmpty() ? livroController.listarTodos()
                        : (livroController.buscarPorIsbn(texto) != null
                           ? List.of(livroController.buscarPorIsbn(texto)) : List.of());
            } else {
                lista = livroController.buscarPorTitulo(texto);
            }
        } catch (BancoDadosException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.nao.encontrado"));
            return;
        }
        for (Livro l : lista) {
            modeloTabela.addRow(new Object[]{
                l.getId(), l.getTitulo(), l.getIsbn(), l.getAnoPublicacao(), l.getQuantidade(),
                l.getAutor() != null ? l.getAutor().getNome() : "",
                l.getCategoria() != null ? l.getCategoria().getNome() : ""
            });
        }
    }

    @Override
    protected void carregarDaTabela() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;

        int id = (int) modeloTabela.getValueAt(linha, 0);
        Livro livro = livroController.buscarPorId(id);
        if (livro == null) return;

        campoId.setText(String.valueOf(livro.getId()));
        campoTitulo.setText(livro.getTitulo());
        campoIsbn.setText(livro.getIsbn());
        campoAno.setText(String.valueOf(livro.getAnoPublicacao()));
        campoQtd.setText(String.valueOf(livro.getQuantidade()));

        selecionarCombo(comboAutor, livro.getAutor() != null ? livro.getAutor().getId() : -1);
        selecionarCombo(comboEditora, livro.getEditora() != null ? livro.getEditora().getId() : -1);
        selecionarCombo(comboCategoria, livro.getCategoria() != null ? livro.getCategoria().getId() : -1);

        abas.setSelectedIndex(0);
    }

    @Override
    protected void limparCampos() {
        campoId.setText(""); campoTitulo.setText(""); campoIsbn.setText("");
        campoAno.setText(""); campoQtd.setText("");
        if (comboAutor.getItemCount()    > 0) comboAutor.setSelectedIndex(0);
        if (comboEditora.getItemCount()  > 0) comboEditora.setSelectedIndex(0);
        if (comboCategoria.getItemCount()> 0) comboCategoria.setSelectedIndex(0);
        campoTitulo.requestFocus();
    }

    private Livro montarLivro() {
        Livro livro = new Livro();
        livro.setTitulo(campoTitulo.getText());
        livro.setIsbn(campoIsbn.getText());
        livro.setAnoPublicacao(campoAno.getText().isEmpty() ? 0 : Integer.parseInt(campoAno.getText()));
        livro.setQuantidade(campoQtd.getText().isEmpty() ? 0 : Integer.parseInt(campoQtd.getText()));
        livro.setAutor((Autor) comboAutor.getSelectedItem());
        livro.setEditora((Editora) comboEditora.getSelectedItem());
        livro.setCategoria((Categoria) comboCategoria.getSelectedItem());
        return livro;
    }

    private void carregarCombos() {
        try {
            comboAutor.removeAllItems();
            autorController.listarTodos().forEach(comboAutor::addItem);

            comboEditora.removeAllItems();
            editoraController.listarTodos().forEach(comboEditora::addItem);

            comboCategoria.removeAllItems();
            categoriaController.listarTodos().forEach(comboCategoria::addItem);
        } catch (BancoDadosException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private <T> void selecionarCombo(JComboBox<T> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            int itemId = 0;
            if (item instanceof Autor)     itemId = ((Autor) item).getId();
            if (item instanceof Editora)   itemId = ((Editora) item).getId();
            if (item instanceof Categoria) itemId = ((Categoria) item).getId();
            if (itemId == id) { combo.setSelectedIndex(i); return; }
        }
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

    private void aplicarFiltroAno(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
                String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
                if (text.matches("\\d+") && (atual + text).length() <= 4)
                    super.insertString(fb, offset, text, attr);
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr) throws BadLocationException {
                String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String resultado = atual.substring(0, offset) + (text != null ? text : "") + atual.substring(offset + length);
                if (resultado.matches("\\d{0,4}")) super.replace(fb, offset, length, text, attr);
            }
        });
    }
}

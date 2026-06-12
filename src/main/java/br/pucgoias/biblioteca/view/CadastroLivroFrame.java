package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.controller.AutorController;
import br.pucgoias.biblioteca.controller.CategoriaController;
import br.pucgoias.biblioteca.controller.EditoraController;
import br.pucgoias.biblioteca.controller.LivroController;
import br.pucgoias.biblioteca.model.*;
import br.pucgoias.biblioteca.util.Mensagens;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.List;

public class CadastroLivroFrame extends GenericFrame<Livro> {

    private final AutorController autorController         = new AutorController();
    private final EditoraController editoraController     = new EditoraController();
    private final CategoriaController categoriaController = new CategoriaController();

    private JTextField campoTitulo, campoIsbn, campoAno, campoQtd;
    private JComboBox<Autor>     comboAutor;
    private JComboBox<Editora>   comboEditora;
    private JComboBox<Categoria> comboCategoria;
    private JComboBox<String>    comboFiltro;

    private JLabel labelTitulo, labelIsbn, labelAno, labelQtd;
    private JLabel labelAutor, labelEditora, labelCategoria;

    public CadastroLivroFrame() {
        super(new LivroController());
        configurarJanela(Mensagens.get("menu.livros"), 700, 580, 20, 20);
        carregarCombos();
    }

    @Override
    protected int adicionarCamposEspecificos(JPanel painel, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = 1;
        labelTitulo = new JLabel(Mensagens.get("label.titulo"));
        painel.add(labelTitulo, gbc);
        campoTitulo = new JTextField(30);
        gbc.gridx = 1; painel.add(campoTitulo, gbc);

        gbc.gridx = 0; gbc.gridy = 1 + 1;
        labelIsbn = new JLabel(Mensagens.get("label.isbn"));
        painel.add(labelIsbn, gbc);
        campoIsbn = new JTextField(20);
        aplicarFiltroNumerico(campoIsbn);
        gbc.gridx = 1; painel.add(campoIsbn, gbc);

        gbc.gridx = 0; gbc.gridy = 1 + 2;
        labelAno = new JLabel(Mensagens.get("label.ano"));
        painel.add(labelAno, gbc);
        campoAno = new JTextField(6);
        aplicarFiltroAno(campoAno);
        gbc.gridx = 1; painel.add(campoAno, gbc);

        gbc.gridx = 0; gbc.gridy = 1 + 3;
        labelQtd = new JLabel(Mensagens.get("label.quantidade"));
        painel.add(labelQtd, gbc);
        campoQtd = new JTextField(6);
        aplicarFiltroNumerico(campoQtd);
        gbc.gridx = 1; painel.add(campoQtd, gbc);

        gbc.gridx = 0; gbc.gridy = 1 + 4;
        labelAutor = new JLabel(Mensagens.get("label.autor"));
        painel.add(labelAutor, gbc);
        comboAutor = new JComboBox<>();
        comboAutor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; painel.add(comboAutor, gbc);

        gbc.gridx = 0; gbc.gridy = 1 + 5;
        labelEditora = new JLabel(Mensagens.get("label.editora"));
        painel.add(labelEditora, gbc);
        comboEditora = new JComboBox<>();
        comboEditora.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; painel.add(comboEditora, gbc);

        gbc.gridx = 0; gbc.gridy = 1 + 6;
        labelCategoria = new JLabel(Mensagens.get("label.categoria"));
        painel.add(labelCategoria, gbc);
        comboCategoria = new JComboBox<>();
        comboCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; painel.add(comboCategoria, gbc);

        return 1 + 7;
    }

    @Override
    protected JComponent componenteFiltroExtra() {
        comboFiltro = new JComboBox<>(new String[]{
            Mensagens.get("col.titulo"), Mensagens.get("col.codigo"), Mensagens.get("col.isbn")
        });
        comboFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboFiltro.addActionListener(e -> {
            campoPesquisa.setText("");
            int idx = comboFiltro.getSelectedIndex();
            if (idx == 1 || idx == 2) {
                aplicarFiltroNumerico(campoPesquisa);
            } else {
                ((AbstractDocument) campoPesquisa.getDocument()).setDocumentFilter(null);
            }
        });
        return comboFiltro;
    }

    @Override
    protected void aposCriarTabela(JTable t) {
        t.getColumnModel().getColumn(0).setPreferredWidth(55);
        t.getColumnModel().getColumn(1).setPreferredWidth(200);
        t.getColumnModel().getColumn(2).setPreferredWidth(120);
        t.getColumnModel().getColumn(3).setPreferredWidth(45);
        t.getColumnModel().getColumn(4).setPreferredWidth(40);
        t.getColumnModel().getColumn(5).setPreferredWidth(120);
        t.getColumnModel().getColumn(6).setPreferredWidth(100);
    }

    @Override
    protected Livro construirEntidade(int id) {
        Livro livro = montarLivro();
        if (id != 0) livro.setId(id);
        return livro;
    }

    @Override
    protected List<Livro> executarBusca() {
        LivroController c = (LivroController) controller;
        String texto = campoPesquisa.getText().trim();
        int filtro = comboFiltro.getSelectedIndex();
        if (filtro == 1) {
            if (texto.isEmpty()) return c.listarTodos();
            Livro l = c.buscarPorId(Integer.parseInt(texto));
            return l != null ? List.of(l) : List.of();
        }
        if (filtro == 2) {
            if (texto.isEmpty()) return c.listarTodos();
            Livro l = c.buscarPorIsbn(texto);
            return l != null ? List.of(l) : List.of();
        }
        return c.buscarPorTitulo(texto);
    }

    @Override
    protected Object[] linhaParaTabela(Livro l) {
        return new Object[]{
            l.getId(), l.getTitulo(), l.getIsbn(), l.getAnoPublicacao(), l.getQuantidade(),
            l.getAutor() != null ? l.getAutor().getNome() : "",
            l.getCategoria() != null ? l.getCategoria().getNome() : ""
        };
    }

    @Override
    protected void preencherCampos(int linha) {
        int id = (int) modeloTabela.getValueAt(linha, 0);
        Livro livro = controller.buscarPorId(id);
        if (livro == null) return;

        campoTitulo.setText(livro.getTitulo());
        campoIsbn.setText(livro.getIsbn());
        campoAno.setText(String.valueOf(livro.getAnoPublicacao()));
        campoQtd.setText(String.valueOf(livro.getQuantidade()));

        selecionarCombo(comboAutor, livro.getAutor() != null ? livro.getAutor().getId() : -1);
        selecionarCombo(comboEditora, livro.getEditora() != null ? livro.getEditora().getId() : -1);
        selecionarCombo(comboCategoria, livro.getCategoria() != null ? livro.getCategoria().getId() : -1);
    }

    @Override
    protected void limparCamposEspecificos() {
        campoTitulo.setText(""); campoIsbn.setText("");
        campoAno.setText(""); campoQtd.setText("");
        if (comboAutor.getItemCount()    > 0) comboAutor.setSelectedIndex(0);
        if (comboEditora.getItemCount()  > 0) comboEditora.setSelectedIndex(0);
        if (comboCategoria.getItemCount()> 0) comboCategoria.setSelectedIndex(0);
        campoTitulo.requestFocus();
    }

    @Override
    protected String[] chavesColunasTabela() {
        return new String[]{
            "col.codigo", "col.titulo", "col.isbn", "col.ano",
            "col.quantidade", "col.autor", "col.categoria"
        };
    }

    @Override
    protected String chaveLabelPesquisa() {
        return "label.pesquisar.por";
    }

    @Override
    protected void atualizarTextos() {
        setTitle(Mensagens.get("menu.livros"));
        labelTitulo.setText(Mensagens.get("label.titulo"));
        labelIsbn.setText(Mensagens.get("label.isbn"));
        labelAno.setText(Mensagens.get("label.ano"));
        labelQtd.setText(Mensagens.get("label.quantidade"));
        labelAutor.setText(Mensagens.get("label.autor"));
        labelEditora.setText(Mensagens.get("label.editora"));
        labelCategoria.setText(Mensagens.get("label.categoria"));
        int sel = comboFiltro.getSelectedIndex();
        comboFiltro.removeAllItems();
        comboFiltro.addItem(Mensagens.get("col.titulo"));
        comboFiltro.addItem(Mensagens.get("col.codigo"));
        comboFiltro.addItem(Mensagens.get("col.isbn"));
        comboFiltro.setSelectedIndex(sel);
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

    private <E> void selecionarCombo(JComboBox<E> combo, int id) {
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

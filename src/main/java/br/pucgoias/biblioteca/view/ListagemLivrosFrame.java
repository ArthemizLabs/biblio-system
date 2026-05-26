package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.controller.LivroController;
import br.pucgoias.biblioteca.model.Livro;
import br.pucgoias.biblioteca.util.Mensagens;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Janela de listagem geral de livros do acervo.
 * Permite ordenação por Título ou por Categoria conforme exigido pelo requisito VIII.
 */
public class ListagemLivrosFrame extends JFrame {

    private final LivroController controller = new LivroController();

    private JComboBox<String> comboOrdenacao;
    private DefaultTableModel modeloTabela;
    private JLabel labelTotal;

    public ListagemLivrosFrame() {
        inicializarComponentes();
        configurarJanela();
        carregar("titulo");
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // Painel superior — ordenação
        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        painelTopo.add(new JLabel("Ordenar por:"));

        comboOrdenacao = new JComboBox<>(new String[]{"Título", "Categoria"});
        comboOrdenacao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelTopo.add(comboOrdenacao);

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAtualizar.setBackground(new Color(41, 128, 185));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setFocusPainted(false);
        painelTopo.add(btnAtualizar);

        labelTotal = new JLabel("Total: 0 livros");
        labelTotal.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        painelTopo.add(Box.createHorizontalStrut(20));
        painelTopo.add(labelTotal);

        // Tabela
        modeloTabela = new DefaultTableModel(
                new String[]{"Código", "Título", "ISBN", "Ano", "Qtd", "Autor", "Editora", "Categoria"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabela = new JTable(modeloTabela);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.setRowHeight(24);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Ajuste de largura
        tabela.getColumnModel().getColumn(0).setPreferredWidth(55);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(220);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(45);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(7).setPreferredWidth(110);

        add(painelTopo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Rodapé
        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnFechar = new JButton(Mensagens.get("btn.fechar"));
        btnFechar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnFechar.addActionListener(e -> dispose());
        painelRodape.add(btnFechar);
        add(painelRodape, BorderLayout.SOUTH);

        // Ações
        btnAtualizar.addActionListener(e -> {
            String ordem = comboOrdenacao.getSelectedIndex() == 1 ? "categoria" : "titulo";
            carregar(ordem);
        });
        comboOrdenacao.addActionListener(e -> {
            String ordem = comboOrdenacao.getSelectedIndex() == 1 ? "categoria" : "titulo";
            carregar(ordem);
        });
    }

    private void carregar(String ordenarPor) {
        modeloTabela.setRowCount(0);
        try {
            List<Livro> lista = controller.listarOrdenado(ordenarPor);
            for (Livro l : lista) {
                modeloTabela.addRow(new Object[]{
                        l.getId(), l.getTitulo(), l.getIsbn(), l.getAnoPublicacao(),
                        l.getQuantidade(),
                        l.getAutor()    != null ? l.getAutor().getNome()    : "",
                        l.getEditora()  != null ? l.getEditora().getNome()  : "",
                        l.getCategoria()!= null ? l.getCategoria().getNome(): ""
                });
            }
            labelTotal.setText("Total: " + lista.size() + " livro(s)");
        } catch (BancoDadosException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configurarJanela() {
        setTitle(Mensagens.get("menu.listagem"));
        setSize(950, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
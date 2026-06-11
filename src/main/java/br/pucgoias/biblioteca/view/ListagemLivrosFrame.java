package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.controller.LivroController;
import br.pucgoias.biblioteca.model.Livro;
import br.pucgoias.biblioteca.util.IdiomaListener;
import br.pucgoias.biblioteca.util.Mensagens;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class ListagemLivrosFrame extends JFrame implements IdiomaListener {

    private final LivroController controller = new LivroController();

    private JComboBox<String> comboOrdenacao;
    private DefaultTableModel modeloTabela;
    private JLabel labelTotal, labelOrdenarPor;
    private JButton btnAtualizar, btnFechar;

    public ListagemLivrosFrame() {
        inicializarComponentes();
        configurarJanela();
        carregar("titulo");
        Mensagens.addIdiomaListener(this);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                Mensagens.removeIdiomaListener(ListagemLivrosFrame.this);
            }
        });
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        labelOrdenarPor = new JLabel(Mensagens.get("col.ordenar.por"));
        painelTopo.add(labelOrdenarPor);

        comboOrdenacao = new JComboBox<>(new String[]{Mensagens.get("col.titulo"), Mensagens.get("col.categoria")});
        comboOrdenacao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelTopo.add(comboOrdenacao);

        btnAtualizar = new JButton(Mensagens.get("btn.atualizar"));
        btnAtualizar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAtualizar.setBackground(new Color(41, 128, 185));
        btnAtualizar.setForeground(Color.BLACK);
        btnAtualizar.setFocusPainted(false);
        painelTopo.add(btnAtualizar);

        labelTotal = new JLabel(Mensagens.get("col.total") + " 0");
        labelTotal.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        painelTopo.add(Box.createHorizontalStrut(20));
        painelTopo.add(labelTotal);

        modeloTabela = new DefaultTableModel(
                new String[]{Mensagens.get("col.codigo"), Mensagens.get("col.titulo"), Mensagens.get("col.isbn"),
                             Mensagens.get("col.ano"), Mensagens.get("col.quantidade"),
                             Mensagens.get("col.autor"), Mensagens.get("col.editora"), Mensagens.get("col.categoria")}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabela = new JTable(modeloTabela);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.setRowHeight(24);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

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

        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnFechar = new JButton(Mensagens.get("btn.fechar"));
        btnFechar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnFechar.addActionListener(e -> dispose());
        painelRodape.add(btnFechar);
        add(painelRodape, BorderLayout.SOUTH);

        btnAtualizar.addActionListener(e -> {
            String ordem = comboOrdenacao.getSelectedIndex() == 1 ? "categoria" : "titulo";
            carregar(ordem);
        });
        comboOrdenacao.addActionListener(e -> {
            String ordem = comboOrdenacao.getSelectedIndex() == 1 ? "categoria" : "titulo";
            carregar(ordem);
        });
    }

    @Override
    public void onIdiomaChanged() {
        setTitle(Mensagens.get("menu.listagem"));
        labelOrdenarPor.setText(Mensagens.get("col.ordenar.por"));
        btnAtualizar.setText(Mensagens.get("btn.atualizar"));
        btnFechar.setText(Mensagens.get("btn.fechar"));
        int sel = comboOrdenacao.getSelectedIndex();
        comboOrdenacao.removeAllItems();
        comboOrdenacao.addItem(Mensagens.get("col.titulo"));
        comboOrdenacao.addItem(Mensagens.get("col.categoria"));
        comboOrdenacao.setSelectedIndex(sel);
        modeloTabela.setColumnIdentifiers(new String[]{
            Mensagens.get("col.codigo"), Mensagens.get("col.titulo"), Mensagens.get("col.isbn"),
            Mensagens.get("col.ano"), Mensagens.get("col.quantidade"),
            Mensagens.get("col.autor"), Mensagens.get("col.editora"), Mensagens.get("col.categoria")
        });
        String totalText = labelTotal.getText();
        String numPart = totalText.replaceAll("[^0-9]", "");
        labelTotal.setText(Mensagens.get("col.total") + " " + numPart);
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
            labelTotal.setText(Mensagens.get("col.total") + " " + lista.size());
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

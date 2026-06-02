package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.dao.EmprestimoDAO;
import br.pucgoias.biblioteca.controller.LeitorController;
import br.pucgoias.biblioteca.controller.LivroController;
import br.pucgoias.biblioteca.model.*;
import br.pucgoias.biblioteca.util.Mensagens;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Janela de registro e controle de Empréstimos.
 * Permite registrar empréstimos, registrar devoluções e listar empréstimos ativos.
 */
public class EmprestimoFrame extends JInternalFrame {

    private final EmprestimoDAO dao         = new EmprestimoDAO();
    private final LeitorController leitorCtrl = new LeitorController();
    private final LivroController livroCtrl   = new LivroController();

    private JComboBox<Leitor> comboLeitor;
    private JComboBox<Livro>  comboLivro;
    private JTextField campoDevolucaoPrevista;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public EmprestimoFrame() {
        inicializarComponentes();
        configurarJanela();
        carregarCombos();
        listarAtivos();
    }

    private void inicializarComponentes() {
        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        abas.addTab("Registrar Empréstimo", criarPainelRegistro());
        abas.addTab("Empréstimos Ativos",   criarPainelAtivos());
        add(abas);
    }

    private JPanel criarPainelRegistro() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Leitor: *"), gbc);
        comboLeitor = new JComboBox<>();
        comboLeitor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; painel.add(comboLeitor, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(new JLabel("Livro: *"), gbc);
        comboLivro = new JComboBox<>();
        comboLivro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; painel.add(comboLivro, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        painel.add(new JLabel("Devolução Prevista: *"), gbc);
        campoDevolucaoPrevista = new JTextField(12);
        campoDevolucaoPrevista.setToolTipText("Formato: AAAA-MM-DD");
        // Pré-preenche com 14 dias a partir de hoje
        campoDevolucaoPrevista.setText(LocalDate.now().plusDays(14).toString());
        gbc.gridx = 1; painel.add(campoDevolucaoPrevista, gbc);

        JButton btnRegistrar = criarBotao("Registrar Empréstimo", new Color(39, 174, 96));
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 6, 6, 6);
        painel.add(btnRegistrar, gbc);

        btnRegistrar.addActionListener(e -> registrarEmprestimo());

        return painel;
    }

    private JPanel criarPainelAtivos() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        modeloTabela = new DefaultTableModel(
                new String[]{"ID", "Leitor", "Livro", "Empréstimo", "Prev. Devolução", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnDevolver = criarBotao("Registrar Devolução", new Color(41, 128, 185));
        JButton btnAtualizar = criarBotao("Atualizar Lista", new Color(127, 140, 141));
        painelBotoes.add(btnDevolver);
        painelBotoes.add(btnAtualizar);

        painel.add(painelBotoes, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        btnDevolver.addActionListener(e  -> registrarDevolucao());
        btnAtualizar.addActionListener(e -> listarAtivos());

        return painel;
    }

    private void registrarEmprestimo() {
        Leitor leitor = (Leitor) comboLeitor.getSelectedItem();
        Livro livro   = (Livro)  comboLivro.getSelectedItem();

        if (leitor == null || livro == null) {
            JOptionPane.showMessageDialog(this, "Selecione o leitor e o livro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate.parse(campoDevolucaoPrevista.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato AAAA-MM-DD.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Emprestimo emp = new Emprestimo();
            emp.setLeitor(leitor);
            emp.setLivro(livro);
            emp.setDataEmprestimo(LocalDate.now());
            emp.setDataDevolucaoPrevista(LocalDate.parse(campoDevolucaoPrevista.getText().trim()));
            emp.setStatus(Emprestimo.Status.ATIVO);
            dao.inserir(emp);
            JOptionPane.showMessageDialog(this, "Empréstimo registrado com sucesso!");
            campoDevolucaoPrevista.setText(LocalDate.now().plusDays(14).toString());
            listarAtivos();
        } catch (BancoDadosException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarDevolucao() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.selecionar"), "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabela.getValueAt(linha, 0);
        try {
            Emprestimo emp = dao.buscarPorId(id);
            if (emp == null) return;
            emp.setDataDevolucaoReal(LocalDate.now());
            emp.setStatus(Emprestimo.Status.DEVOLVIDO);
            dao.atualizar(emp);
            JOptionPane.showMessageDialog(this, "Devolução registrada com sucesso!");
            listarAtivos();
        } catch (BancoDadosException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarAtivos() {
        modeloTabela.setRowCount(0);
        try {
            List<Emprestimo> lista = dao.listarAtivos();
            for (Emprestimo emp : lista) {
                modeloTabela.addRow(new Object[]{
                        emp.getId(),
                        emp.getLeitor() != null ? emp.getLeitor().getNome() : "",
                        emp.getLivro()  != null ? emp.getLivro().getTitulo() : "",
                        emp.getDataEmprestimo(),
                        emp.getDataDevolucaoPrevista(),
                        emp.getStatus()
                });
            }
        } catch (BancoDadosException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarCombos() {
        try {
            comboLeitor.removeAllItems();
            leitorCtrl.listarTodos().forEach(comboLeitor::addItem);
            comboLivro.removeAllItems();
            livroCtrl.listarTodos().forEach(comboLivro::addItem);
        } catch (BancoDadosException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
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
        setTitle(Mensagens.get("menu.emprestimos"));
        setSize(720, 500);
        setClosable(true); setMaximizable(true);
        setIconifiable(true); setResizable(true);
        setLocation(50, 50);
    }
}
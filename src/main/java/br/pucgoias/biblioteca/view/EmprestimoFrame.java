package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.dao.EmprestimoDAO;
import br.pucgoias.biblioteca.controller.LeitorController;
import br.pucgoias.biblioteca.controller.LivroController;
import br.pucgoias.biblioteca.model.*;
import br.pucgoias.biblioteca.view.interfaces.IdiomaListener;
import br.pucgoias.biblioteca.util.Mensagens;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EmprestimoFrame extends JInternalFrame implements IdiomaListener {

    private final EmprestimoDAO dao          = new EmprestimoDAO();
    private final LeitorController leitorCtrl = new LeitorController();
    private final LivroController livroCtrl   = new LivroController();

    private JComboBox<Leitor> comboLeitor;
    private JComboBox<Livro>  comboLivro;
    private JTextField campoDevolucaoPrevista;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private JTabbedPane abas;
    private JLabel labelLeitor, labelLivro, labelDevolucao;
    private JButton btnRegistrarEmprestimo, btnDevolver, btnAtualizarLista;
    private LocalDate devolucaoPrevistaPadrao = LocalDate.now().plusDays(14);

    public EmprestimoFrame() {
        inicializarComponentes();
        configurarJanela();
        carregarCombos();
        listarAtivos();
        Mensagens.addIdiomaListener(this);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override public void internalFrameClosed(InternalFrameEvent e) {
                Mensagens.removeIdiomaListener(EmprestimoFrame.this);
            }
        });
    }

    private void inicializarComponentes() {
        abas = new JTabbedPane();
        abas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        abas.addTab(Mensagens.get("aba.registrar.emprestimo"), criarPainelRegistro());
        abas.addTab(Mensagens.get("aba.emprestimos.ativos"),   criarPainelAtivos());
        add(abas);
    }

    private JPanel criarPainelRegistro() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        labelLeitor = new JLabel(Mensagens.get("label.leitor"));
        painel.add(labelLeitor, gbc);
        comboLeitor = new JComboBox<>();
        comboLeitor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; painel.add(comboLeitor, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        labelLivro = new JLabel(Mensagens.get("label.livro"));
        painel.add(labelLivro, gbc);
        comboLivro = new JComboBox<>();
        comboLivro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; painel.add(comboLivro, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        labelDevolucao = new JLabel(Mensagens.get("label.devolucao"));
        painel.add(labelDevolucao, gbc);
        campoDevolucaoPrevista = new JTextField(12);
        campoDevolucaoPrevista.setToolTipText(Mensagens.get("label.data.formato"));
        campoDevolucaoPrevista.setText(formatarData(devolucaoPrevistaPadrao));
        gbc.gridx = 1; painel.add(campoDevolucaoPrevista, gbc);

        btnRegistrarEmprestimo = criarBotao(Mensagens.get("btn.registrar.emprestimo"), new Color(39, 174, 96));
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 6, 6, 6);
        painel.add(btnRegistrarEmprestimo, gbc);

        btnRegistrarEmprestimo.addActionListener(e -> registrarEmprestimo());

        return painel;
    }

    private JPanel criarPainelAtivos() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        modeloTabela = new DefaultTableModel(
                new String[]{"ID", Mensagens.get("col.leitor"), Mensagens.get("col.livro"),
                             Mensagens.get("col.data.emprestimo"), Mensagens.get("col.devolucao.prevista"),
                             Mensagens.get("col.status")}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnDevolver = criarBotao(Mensagens.get("btn.registrar.devolucao"), new Color(41, 128, 185));
        btnAtualizarLista = criarBotao(Mensagens.get("btn.atualizar"), new Color(127, 140, 141));
        painelBotoes.add(btnDevolver);
        painelBotoes.add(btnAtualizarLista);

        painel.add(painelBotoes, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        btnDevolver.addActionListener(e  -> registrarDevolucao());
        btnAtualizarLista.addActionListener(e -> listarAtivos());

        return painel;
    }

    @Override
    public void onIdiomaChanged() {
        setTitle(Mensagens.get("menu.emprestimos"));
        abas.setTitleAt(0, Mensagens.get("aba.registrar.emprestimo"));
        abas.setTitleAt(1, Mensagens.get("aba.emprestimos.ativos"));
        labelLeitor.setText(Mensagens.get("label.leitor"));
        labelLivro.setText(Mensagens.get("label.livro"));
        labelDevolucao.setText(Mensagens.get("label.devolucao"));
        btnRegistrarEmprestimo.setText(Mensagens.get("btn.registrar.emprestimo"));
        btnDevolver.setText(Mensagens.get("btn.registrar.devolucao"));
        btnAtualizarLista.setText(Mensagens.get("btn.atualizar"));
        campoDevolucaoPrevista.setToolTipText(Mensagens.get("label.data.formato"));
        atualizarCampoDataParaIdiomaAtual();
        modeloTabela.setColumnIdentifiers(new String[]{
            "ID", Mensagens.get("col.leitor"), Mensagens.get("col.livro"),
            Mensagens.get("col.data.emprestimo"), Mensagens.get("col.devolucao.prevista"),
            Mensagens.get("col.status")
        });
    }

    private void registrarEmprestimo() {
        Leitor leitor = (Leitor) comboLeitor.getSelectedItem();
        Livro livro   = (Livro)  comboLivro.getSelectedItem();

        if (leitor == null || livro == null) {
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.selecionar"), Mensagens.get("label.aviso.title"), JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate dataDevolucaoPrevista;
        try {
            dataDevolucaoPrevista = parseData(campoDevolucaoPrevista.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,  Mensagens.get("msg.erro.data"), Mensagens.get("label.aviso.title"), JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Emprestimo emp = new Emprestimo();
            emp.setLeitor(leitor);
            emp.setLivro(livro);
            emp.setDataEmprestimo(LocalDate.now());
            emp.setDataDevolucaoPrevista(dataDevolucaoPrevista);
            emp.setStatus(Emprestimo.Status.ATIVO);
            dao.inserir(emp);
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.sucesso.salvar"));
            devolucaoPrevistaPadrao = LocalDate.now().plusDays(14);
            campoDevolucaoPrevista.setText(formatarData(devolucaoPrevistaPadrao));
            listarAtivos();
        } catch (BancoDadosException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), Mensagens.get("label.erro.title"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarDevolucao() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.selecionar"), Mensagens.get("label.aviso.title"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabela.getValueAt(linha, 0);
        try {
            Emprestimo emp = dao.buscarPorId(id);
            if (emp == null) return;
            emp.setDataDevolucaoReal(LocalDate.now());
            emp.setStatus(Emprestimo.Status.DEVOLVIDO);
            dao.atualizar(emp);
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.sucesso.alterar"));
            listarAtivos();
        } catch (BancoDadosException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), Mensagens.get("label.erro.title"), JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, e.getMessage(), Mensagens.get("label.erro.title"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarCombos() {
        try {
            comboLeitor.removeAllItems();
            leitorCtrl.listarTodos().forEach(comboLeitor::addItem);
            comboLivro.removeAllItems();
            livroCtrl.listarTodos().forEach(comboLivro::addItem);
        } catch (BancoDadosException e) {
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.dados ") + e.getMessage(),
                    Mensagens.get("label.erro.title"), JOptionPane.ERROR_MESSAGE);
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

    private DateTimeFormatter formatadorDataAtual() {
        return DateTimeFormatter.ofPattern(Mensagens.get("date.pattern"));
    }

    private String formatarData(LocalDate data) {
        return data.format(formatadorDataAtual());
    }

    private LocalDate parseData(String texto) {
        return LocalDate.parse(texto, formatadorDataAtual());
    }

    private void atualizarCampoDataParaIdiomaAtual() {
        String textoAtual = campoDevolucaoPrevista.getText().trim();
        LocalDate data = tentarParseDataFlexivel(textoAtual);
        if (data != null) {
            devolucaoPrevistaPadrao = data;
            campoDevolucaoPrevista.setText(formatarData(data));
        }
    }

    private LocalDate tentarParseDataFlexivel(String texto) {
        if (texto.isEmpty()) return null;
        try {
            return parseData(texto);
        } catch (DateTimeParseException ignored) {}
        try {
            return LocalDate.parse(texto, DateTimeFormatter.ofPattern("dd-MM-uuuu"));
        } catch (DateTimeParseException ignored) {}
        try {
            return LocalDate.parse(texto, DateTimeFormatter.ofPattern("MM-dd-uuuu"));
        } catch (DateTimeParseException ignored) {}
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private void configurarJanela() {
        setTitle(Mensagens.get("menu.emprestimos"));
        setSize(720, 500);
        setClosable(true); setMaximizable(true);
        setIconifiable(true); setResizable(true);
        setLocation(50, 50);
    }
}

package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.dao.ReservaDAO;
import br.pucgoias.biblioteca.controller.LeitorController;
import br.pucgoias.biblioteca.controller.LivroController;
import br.pucgoias.biblioteca.model.*;
import br.pucgoias.biblioteca.dao.interfaces.IdiomaListener;
import br.pucgoias.biblioteca.util.Mensagens;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ReservaFrame extends JInternalFrame implements IdiomaListener {

    private final ReservaDAO dao              = new ReservaDAO();
    private final LeitorController leitorCtrl = new LeitorController();
    private final LivroController livroCtrl   = new LivroController();

    private JComboBox<Leitor> comboLeitor;
    private JComboBox<Livro>  comboLivro;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private JTabbedPane abas;
    private JLabel labelLeitor, labelLivro;
    private JButton btnReservar, btnCancelar, btnAtualizar;

    public ReservaFrame() {
        inicializarComponentes();
        configurarJanela();
        carregarCombos();
        listarReservas();
        Mensagens.addIdiomaListener(this);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override public void internalFrameClosed(InternalFrameEvent e) {
                Mensagens.removeIdiomaListener(ReservaFrame.this);
            }
        });
    }

    private void inicializarComponentes() {
        abas = new JTabbedPane();
        abas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        abas.addTab(Mensagens.get("aba.reservas.novas"),  criarPainelReserva());
        abas.addTab(Mensagens.get("aba.reservas.abertas"), criarPainelLista());
        add(abas);
    }

    private JPanel criarPainelReserva() {
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

        btnReservar = criarBotao(Mensagens.get("btn.registrar.reserva"), new Color(39, 174, 96));
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 6, 6, 6);
        painel.add(btnReservar, gbc);

        btnReservar.addActionListener(e -> registrarReserva());

        return painel;
    }

    private JPanel criarPainelLista() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        modeloTabela = new DefaultTableModel(
                new String[]{"ID", Mensagens.get("col.leitor"), Mensagens.get("col.livro"),
                             Mensagens.get("col.data.reserva"), Mensagens.get("col.status")}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnCancelar = criarBotao(Mensagens.get("btn.cancelar.reserva"), new Color(192, 57, 43));
        btnAtualizar = criarBotao(Mensagens.get("btn.atualizar"), new Color(127, 140, 141));
        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnAtualizar);

        painel.add(painelBotoes, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        btnCancelar.addActionListener(e  -> cancelarReserva());
        btnAtualizar.addActionListener(e -> listarReservas());

        return painel;
    }

    @Override
    public void onIdiomaChanged() {
        setTitle(Mensagens.get("menu.reservas"));
        abas.setTitleAt(0, Mensagens.get("aba.reservas.novas"));
        abas.setTitleAt(1, Mensagens.get("aba.reservas.abertas"));
        labelLeitor.setText(Mensagens.get("label.leitor"));
        labelLivro.setText(Mensagens.get("label.livro"));
        btnReservar.setText(Mensagens.get("btn.registrar.reserva"));
        btnCancelar.setText(Mensagens.get("btn.cancelar.reserva"));
        btnAtualizar.setText(Mensagens.get("btn.atualizar"));
        modeloTabela.setColumnIdentifiers(new String[]{
            "ID", Mensagens.get("col.leitor"), Mensagens.get("col.livro"),
            Mensagens.get("col.data.reserva"), Mensagens.get("col.status")
        });
    }

    private void registrarReserva() {
        Leitor leitor = (Leitor) comboLeitor.getSelectedItem();
        Livro livro   = (Livro)  comboLivro.getSelectedItem();

        if (leitor == null || livro == null) {
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.selecionar"), "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (!dao.verificarDisponibilidade(livro.getId())) {
                JOptionPane.showMessageDialog(this,
                        "Livro indisponível para reserva (sem exemplares ou já reservado).",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Reserva reserva = new Reserva();
            reserva.setLeitor(leitor);
            reserva.setLivro(livro);
            reserva.setDataReserva(LocalDate.now());
            reserva.setStatus(Reserva.Status.ABERTA);
            dao.inserir(reserva);
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.sucesso.salvar"));
            listarReservas();
        } catch (BancoDadosException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarReserva() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.erro.selecionar"), "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabela.getValueAt(linha, 0);
        try {
            Reserva reserva = dao.buscarPorId(id);
            if (reserva == null) return;
            reserva.setStatus(Reserva.Status.CANCELADA);
            dao.atualizar(reserva);
            JOptionPane.showMessageDialog(this, Mensagens.get("msg.sucesso.alterar"));
            listarReservas();
        } catch (BancoDadosException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarReservas() {
        modeloTabela.setRowCount(0);
        try {
            List<Reserva> lista = dao.listarTodos();
            for (Reserva r : lista) {
                modeloTabela.addRow(new Object[]{
                        r.getId(),
                        r.getLeitor() != null ? r.getLeitor().getNome() : "",
                        r.getLivro()  != null ? r.getLivro().getTitulo() : "",
                        r.getDataReserva(),
                        r.getStatus()
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
        setTitle(Mensagens.get("menu.reservas"));
        setSize(680, 460);
        setClosable(true); setMaximizable(true);
        setIconifiable(true); setResizable(true);
        setLocation(80, 80);
    }
}

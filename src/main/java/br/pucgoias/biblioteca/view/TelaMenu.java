package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.dao.*;
import br.pucgoias.biblioteca.model.Usuario;
import br.pucgoias.biblioteca.view.interfaces.IdiomaListener;
import br.pucgoias.biblioteca.util.Mensagens;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class TelaMenu extends JFrame implements IdiomaListener {

    private final Usuario usuarioLogado;
    private JDesktopPane desktopPane;

    // Cards de estatísticas (painel + label de valor)
    private JLabel statLivrosValor, statLeitoresValor, statEmprestimosValor, statReservasValor;
    private JLabel statLivrosLabel, statLeitoresLabel, statEmprestimosLabel, statReservasLabel;

    // Seções da sidebar
    private JLabel labelSecaoAcervo, labelSecaoUsuarios, labelSecaoMovimentacoes, labelSecaoSistema;

    // Botões da sidebar
    private JButton btnLivros, btnAutores, btnEditoras, btnCategorias;
    private JButton btnLeitores;
    private JButton btnEmprestimos, btnReservas, btnListagem;
    private JButton btnUsuarios, btnIdioma, btnSair;

    public TelaMenu(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        inicializarComponentes();
        configurarJanela();
        atualizarEstatisticas();
        Mensagens.addIdiomaListener(this);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        add(criarTopBar(),  BorderLayout.NORTH);
        add(criarSidebar(), BorderLayout.WEST);
        add(criarCentro(),  BorderLayout.CENTER);
    }

    // ----------------------------------------------------------------
    // TOP BAR
    // ----------------------------------------------------------------
    private JPanel criarTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(22, 33, 52));
        top.setPreferredSize(new Dimension(0, 48));
        top.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel logo = new JLabel("BiblioSystem");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(new Color(100, 180, 255));
        top.add(logo, BorderLayout.WEST);

        String perfil = usuarioLogado.getPerfil() == Usuario.Perfil.ADMIN ? "ADMIN" : "FUNCIONARIO";
        JLabel labelUsuario = new JLabel(usuarioLogado.getLogin() + "  |  " + perfil);
        labelUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelUsuario.setForeground(new Color(180, 200, 220));
        top.add(labelUsuario, BorderLayout.EAST);

        return top;
    }

    // ----------------------------------------------------------------
    // SIDEBAR
    // ----------------------------------------------------------------
    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(28, 40, 58));
        sidebar.setPreferredSize(new Dimension(185, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        labelSecaoAcervo = criarSecaoLabel(Mensagens.get("secao.acervo"));
        sidebar.add(labelSecaoAcervo);
        btnLivros     = criarBotaoSidebar(Mensagens.get("menu.livros"),      () -> abrirJanela(new LivroFrame()));
        btnAutores    = criarBotaoSidebar(Mensagens.get("menu.autores"),     () -> abrirJanela(new AutorFrame()));
        btnEditoras   = criarBotaoSidebar(Mensagens.get("menu.editoras"),    () -> abrirJanela(new EditoraFrame()));
        btnCategorias = criarBotaoSidebar(Mensagens.get("menu.categorias"),  () -> abrirJanela(new CategoriaFrame()));
        sidebar.add(btnLivros);
        sidebar.add(btnAutores);
        sidebar.add(btnEditoras);
        sidebar.add(btnCategorias);

        sidebar.add(criarSeparador());

        labelSecaoUsuarios = criarSecaoLabel(Mensagens.get("secao.usuarios"));
        sidebar.add(labelSecaoUsuarios);
        btnLeitores = criarBotaoSidebar(Mensagens.get("menu.leitores"), () -> abrirJanela(new LeitorFrame()));
        sidebar.add(btnLeitores);

        sidebar.add(criarSeparador());

        labelSecaoMovimentacoes = criarSecaoLabel(Mensagens.get("secao.movimentacoes"));
        sidebar.add(labelSecaoMovimentacoes);
        btnEmprestimos = criarBotaoSidebar(Mensagens.get("menu.emprestimos"), () -> abrirJanela(new EmprestimoFrame()));
        btnReservas    = criarBotaoSidebar(Mensagens.get("menu.reservas"),    () -> abrirJanela(new ReservaFrame()));
        btnListagem    = criarBotaoSidebar(Mensagens.get("menu.listagem"),    () -> new ListagemLivrosFrame().setVisible(true));
        sidebar.add(btnEmprestimos);
        sidebar.add(btnReservas);
        sidebar.add(btnListagem);

        sidebar.add(criarSeparador());

        labelSecaoSistema = criarSecaoLabel(Mensagens.get("secao.sistema"));
        sidebar.add(labelSecaoSistema);

        if (usuarioLogado.getPerfil() == Usuario.Perfil.ADMIN) {
            btnUsuarios = criarBotaoSidebar(Mensagens.get("menu.usuarios"), () -> abrirJanela(new UsuarioFrame()));
            sidebar.add(btnUsuarios);
        }
        btnIdioma = criarBotaoSidebar(Mensagens.get("menu.idioma"), () -> new ConfiguracaoIdiomaDialog(this).setVisible(true));
        sidebar.add(btnIdioma);

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(criarSeparador());

        btnSair = criarBotaoSair();
        sidebar.add(btnSair);

        return sidebar;
    }

    // ----------------------------------------------------------------
    // CENTRO — Stats cards + Desktop
    // ----------------------------------------------------------------
    private JPanel criarCentro() {
        JPanel centro = new JPanel(new BorderLayout());
        centro.add(criarBarraStats(), BorderLayout.NORTH);

        desktopPane = new JDesktopPane();
        desktopPane.setBackground(new Color(230, 235, 245));
        centro.add(desktopPane, BorderLayout.CENTER);

        return centro;
    }

    private JPanel criarBarraStats() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        barra.setBackground(new Color(240, 243, 248));
        barra.setBorder(new MatteBorder(0, 0, 1, 0, new Color(210, 215, 225)));

        Color[] cores = {
            new Color(41, 128, 185),
            new Color(39, 174, 96),
            new Color(230, 126, 34),
            new Color(142, 68, 173)
        };

        statLivrosValor      = new JLabel("--");
        statLeitoresValor    = new JLabel("--");
        statEmprestimosValor = new JLabel("--");
        statReservasValor    = new JLabel("--");

        statLivrosLabel      = new JLabel(Mensagens.get("stat.livros"));
        statLeitoresLabel    = new JLabel(Mensagens.get("stat.leitores"));
        statEmprestimosLabel = new JLabel(Mensagens.get("stat.emprestimos"));
        statReservasLabel    = new JLabel(Mensagens.get("stat.reservas"));

        barra.add(criarStatCard(statLivrosLabel,      statLivrosValor,      cores[0]));
        barra.add(criarStatCard(statLeitoresLabel,    statLeitoresValor,    cores[1]));
        barra.add(criarStatCard(statEmprestimosLabel, statEmprestimosValor, cores[2]));
        barra.add(criarStatCard(statReservasLabel,    statReservasValor,    cores[3]));

        return barra;
    }

    private JPanel criarStatCard(JLabel labelTexto, JLabel labelValor, Color acento) {
        JPanel card = new JPanel(new BorderLayout(4, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 232), 1),
            new CompoundBorder(
                new MatteBorder(0, 3, 0, 0, acento),
                BorderFactory.createEmptyBorder(6, 10, 6, 16)
            )
        ));

        labelTexto.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        labelTexto.setForeground(new Color(100, 110, 130));

        labelValor.setFont(new Font("Segoe UI", Font.BOLD, 20));
        labelValor.setForeground(acento);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 0));
        textos.setOpaque(false);
        textos.add(labelTexto);
        textos.add(labelValor);

        card.add(textos, BorderLayout.CENTER);
        return card;
    }

    // ----------------------------------------------------------------
    // LISTENER DE IDIOMA
    // ----------------------------------------------------------------
    @Override
    public void onIdiomaChanged() {
        labelSecaoAcervo.setText("  " + Mensagens.get("secao.acervo"));
        labelSecaoUsuarios.setText("  " + Mensagens.get("secao.usuarios"));
        labelSecaoMovimentacoes.setText("  " + Mensagens.get("secao.movimentacoes"));
        labelSecaoSistema.setText("  " + Mensagens.get("secao.sistema"));

        btnLivros.setText(Mensagens.get("menu.livros"));
        btnAutores.setText(Mensagens.get("menu.autores"));
        btnEditoras.setText(Mensagens.get("menu.editoras"));
        btnCategorias.setText(Mensagens.get("menu.categorias"));
        btnLeitores.setText(Mensagens.get("menu.leitores"));
        btnEmprestimos.setText(Mensagens.get("menu.emprestimos"));
        btnReservas.setText(Mensagens.get("menu.reservas"));
        btnListagem.setText(Mensagens.get("menu.listagem"));
        if (btnUsuarios != null) btnUsuarios.setText(Mensagens.get("menu.usuarios"));
        btnIdioma.setText(Mensagens.get("menu.idioma"));
        btnSair.setText(Mensagens.get("menu.sair"));

        statLivrosLabel.setText(Mensagens.get("stat.livros"));
        statLeitoresLabel.setText(Mensagens.get("stat.leitores"));
        statEmprestimosLabel.setText(Mensagens.get("stat.emprestimos"));
        statReservasLabel.setText(Mensagens.get("stat.reservas"));

        atualizarEstatisticas();
    }

    // ----------------------------------------------------------------
    // COMPONENTES SIDEBAR
    // ----------------------------------------------------------------
    private JLabel criarSecaoLabel(String texto) {
        JLabel label = new JLabel("  " + texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(new Color(100, 140, 180));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setMaximumSize(new Dimension(185, 28));
        label.setBorder(BorderFactory.createEmptyBorder(12, 12, 4, 0));
        return label;
    }

    private JButton criarBotaoSidebar(String texto, Runnable acao) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(200, 215, 235));
        btn.setBackground(new Color(28, 40, 58));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(185, 36));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(45, 65, 95));
                btn.setForeground(Color.WHITE);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(28, 40, 58));
                btn.setForeground(new Color(200, 215, 235));
            }
        });

        btn.addActionListener(e -> acao.run());
        return btn;
    }

    private JButton criarBotaoSair() {
        JButton btn = new JButton(Mensagens.get("menu.sair"));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(220, 100, 100));
        btn.setBackground(new Color(28, 40, 58));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(185, 36));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(80, 30, 30));
                btn.setForeground(new Color(255, 120, 120));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(28, 40, 58));
                btn.setForeground(new Color(220, 100, 100));
            }
        });

        btn.addActionListener(e -> confirmarSaida());
        return btn;
    }

    private JSeparator criarSeparador() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(45, 62, 85));
        sep.setBackground(new Color(28, 40, 58));
        sep.setMaximumSize(new Dimension(185, 1));
        return sep;
    }

    // ----------------------------------------------------------------
    // ESTATÍSTICAS
    // ----------------------------------------------------------------
    private void atualizarEstatisticas() {
        SwingUtilities.invokeLater(() -> {
            try {
                int totalLivros      = new LivroDAO().listarTodos().size();
                int totalLeitores    = new LeitorDAO().listarTodos().size();
                int totalEmprestimos = new EmprestimoDAO().listarAtivos().size();
                int totalReservas    = new ReservaDAO().listarTodos().stream()
                        .filter(r -> r.getStatus() == br.pucgoias.biblioteca.model.Reserva.Status.ABERTA)
                        .toList().size();

                statLivrosValor.setText(String.valueOf(totalLivros));
                statLeitoresValor.setText(String.valueOf(totalLeitores));
                statEmprestimosValor.setText(String.valueOf(totalEmprestimos));
                statReservasValor.setText(String.valueOf(totalReservas));

            } catch (BancoDadosException e) {
                // Silencioso — stats não críticas
            }
        });
    }

    // ----------------------------------------------------------------
    // UTILITÁRIOS
    // ----------------------------------------------------------------
    private void abrirJanela(JInternalFrame frame) {
        for (JInternalFrame f : desktopPane.getAllFrames()) {
            if (f.getClass().equals(frame.getClass())) {
                f.toFront();
                try { f.setSelected(true); } catch (Exception ignored) {}
                return;
            }
        }
        desktopPane.add(frame);
        frame.setVisible(true);
        atualizarEstatisticas();
    }

    private void configurarJanela() {
        setTitle("BiblioSystem");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmarSaida();
            }
        });
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 600));
    }

    private void confirmarSaida() {
        int opcao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente sair do sistema?", "Sair",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcao == JOptionPane.YES_OPTION) System.exit(0);
    }
}

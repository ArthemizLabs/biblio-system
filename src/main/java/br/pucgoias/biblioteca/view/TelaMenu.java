package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.dao.*;
import br.pucgoias.biblioteca.model.Usuario;
import br.pucgoias.biblioteca.util.Mensagens;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Janela principal do BiblioSystem após o login.
 * Layout com TopBar, Sidebar de navegação e JDesktopPane central.
 */
public class TelaMenu extends JFrame {

    private final Usuario usuarioLogado;
    private JDesktopPane desktopPane;

    // Labels das estatísticas
    private JLabel statLivros, statLeitores, statEmprestimos, statReservas;

    public TelaMenu(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        inicializarComponentes();
        configurarJanela();
        atualizarEstatisticas();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        add(criarTopBar(),   BorderLayout.NORTH);
        add(criarSidebar(),  BorderLayout.WEST);
        add(criarCentro(),   BorderLayout.CENTER);
    }

    // ----------------------------------------------------------------
    // TOP BAR
    // ----------------------------------------------------------------
    private JPanel criarTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(28, 40, 60));
        top.setPreferredSize(new Dimension(0, 48));
        top.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        // Logo
        JLabel logo = new JLabel("🏛️  BiblioSystem");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);
        top.add(logo, BorderLayout.WEST);

        // Usuário logado
        JLabel labelUsuario = new JLabel(
                "👤  " + usuarioLogado.getLogin() + "  (" + usuarioLogado.getPerfil() + ")"
        );
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
        sidebar.setBackground(new Color(36, 50, 70));
        sidebar.setPreferredSize(new Dimension(180, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        // Seção Acervo
        sidebar.add(criarSecaoLabel("ACERVO"));
        sidebar.add(criarBotaoSidebar("📚  Livros",      () -> abrirJanela(new CadastroLivroFrame())));
        sidebar.add(criarBotaoSidebar("👤  Autores",     () -> abrirJanela(new CadastroAutorFrame())));
        sidebar.add(criarBotaoSidebar("🏢  Editoras",    () -> abrirJanela(new CadastroEditoraFrame())));
        sidebar.add(criarBotaoSidebar("🏷️  Categorias",  () -> abrirJanela(new CadastroCategoriaFrame())));

        // Separador
        sidebar.add(criarSeparador());

        // Seção Usuários
        sidebar.add(criarSecaoLabel("USUÁRIOS"));
        sidebar.add(criarBotaoSidebar("👥  Leitores",    () -> abrirJanela(new CadastroLeitorFrame())));

        // Separador
        sidebar.add(criarSeparador());

        // Seção Movimentações
        sidebar.add(criarSecaoLabel("MOVIMENTAÇÕES"));
        sidebar.add(criarBotaoSidebar("📋  Empréstimos", () -> abrirJanela(new EmprestimoFrame())));
        sidebar.add(criarBotaoSidebar("🔖  Reservas",    () -> abrirJanela(new ReservaFrame())));
        sidebar.add(criarBotaoSidebar("📊  Listagem",    () -> new ListagemLivrosFrame().setVisible(true)));

        // Separador
        sidebar.add(criarSeparador());

        // Seção Sistema
        sidebar.add(criarSecaoLabel("SISTEMA"));

        // Usuários só para ADMIN
        if (usuarioLogado.getPerfil() == Usuario.Perfil.ADMIN) {
            sidebar.add(criarBotaoSidebar("🔑  Usuários", () -> abrirJanela(new CadastroUsuarioFrame())));
        }
        sidebar.add(criarBotaoSidebar("🌐  Idioma",  () -> new ConfiguracaoIdiomaDialog(this).setVisible(true)));

        // Espaço flexível empurra o Sair para baixo
        sidebar.add(Box.createVerticalGlue());

        sidebar.add(criarSeparador());
        sidebar.add(criarBotaoSair());

        return sidebar;
    }

    // ----------------------------------------------------------------
    // CENTRO — Stats + Desktop
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
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barra.setBackground(new Color(60, 70, 85));
        barra.setPreferredSize(new Dimension(0, 38));

        statLivros      = criarStatLabel("📚  Livros: --");
        statLeitores    = criarStatLabel("👥  Leitores: --");
        statEmprestimos = criarStatLabel("📋  Empréstimos ativos: --");
        statReservas    = criarStatLabel("🔖  Reservas abertas: --");

        barra.add(statLivros);
        barra.add(criarDivisorStat());
        barra.add(statLeitores);
        barra.add(criarDivisorStat());
        barra.add(statEmprestimos);
        barra.add(criarDivisorStat());
        barra.add(statReservas);

        return barra;
    }

    private JLabel criarStatLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(20, 20, 20));
        label.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        return label;
    }

    private JLabel criarDivisorStat() {
        JLabel div = new JLabel("|");
        div.setForeground(new Color(100, 110, 125));
        div.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return div;
    }

    // ----------------------------------------------------------------
    // COMPONENTES SIDEBAR
    // ----------------------------------------------------------------
    private JLabel criarSecaoLabel(String texto) {
        JLabel label = new JLabel("  " + texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(new Color(120, 150, 180));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setMaximumSize(new Dimension(180, 28));
        label.setBorder(BorderFactory.createEmptyBorder(10, 12, 4, 0));
        return label;
    }

    private JButton criarBotaoSidebar(String texto, Runnable acao) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(210, 220, 235));
        btn.setBackground(new Color(36, 50, 70));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 36));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(52, 73, 100));
                btn.setForeground(Color.WHITE);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(36, 50, 70));
                btn.setForeground(new Color(210, 220, 235));
            }
        });

        btn.addActionListener(e -> acao.run());
        return btn;
    }

    private JButton criarBotaoSair() {
        JButton btn = new JButton("🚪  Sair");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(220, 100, 100));
        btn.setBackground(new Color(36, 50, 70));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 36));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(80, 30, 30));
                btn.setForeground(new Color(255, 120, 120));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(36, 50, 70));
                btn.setForeground(new Color(220, 100, 100));
            }
        });

        btn.addActionListener(e -> confirmarSaida());
        return btn;
    }

    private JSeparator criarSeparador() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(55, 72, 95));
        sep.setBackground(new Color(36, 50, 70));
        sep.setMaximumSize(new Dimension(180, 1));
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

                statLivros.setText("📚  Livros: " + totalLivros);
                statLeitores.setText("👥  Leitores: " + totalLeitores);
                statEmprestimos.setText("📋  Empréstimos ativos: " + totalEmprestimos);
                statReservas.setText("🔖  Reservas abertas: " + totalReservas);

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
        // Atualiza stats ao abrir qualquer janela
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
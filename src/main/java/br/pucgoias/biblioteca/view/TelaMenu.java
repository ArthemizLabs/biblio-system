package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.model.Usuario;
import br.pucgoias.biblioteca.util.Mensagens;

import javax.swing.*;
import java.awt.*;

/**
 * Janela principal do BiblioSystem após o login.
 * Utiliza JDesktopPane para abrigar os JInternalFrames dos cadastros.
 */
public class TelaMenu extends JFrame {

    private final Usuario usuarioLogado;
    private JDesktopPane desktopPane;

    public TelaMenu(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        inicializarComponentes();
        configurarJanela();
        aplicarIdioma();
    }

    private void inicializarComponentes() {
        // Desktop pane — área onde os JInternalFrames serão exibidos
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(new Color(230, 235, 245));
        setContentPane(desktopPane);

        // Barra de menu
        JMenuBar menuBar = new JMenuBar();

        // Menu Cadastros
        JMenu menuCadastros = new JMenu(Mensagens.get("menu.livros") + " / Cadastros");
        menuCadastros.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JMenuItem itemLivros      = criarMenuItem("menu.livros");
        JMenuItem itemAutores     = criarMenuItem("menu.autores");
        JMenuItem itemEditoras    = criarMenuItem("menu.editoras");
        JMenuItem itemCategorias  = criarMenuItem("menu.categorias");
        JMenuItem itemLeitores    = criarMenuItem("menu.leitores");

        itemLivros.addActionListener(e     -> abrirJanela(new CadastroLivroFrame()));
        itemAutores.addActionListener(e    -> abrirJanela(new CadastroAutorFrame()));
        itemEditoras.addActionListener(e   -> abrirJanela(new CadastroEditoraFrame()));
        itemCategorias.addActionListener(e -> abrirJanela(new CadastroCategoriaFrame()));
        itemLeitores.addActionListener(e   -> abrirJanela(new CadastroLeitorFrame()));

        menuCadastros.add(itemLivros);
        menuCadastros.add(itemAutores);
        menuCadastros.add(itemEditoras);
        menuCadastros.add(itemCategorias);
        menuCadastros.addSeparator();
        menuCadastros.add(itemLeitores);

        // Menu Movimentações
        JMenu menuMovimentacoes = new JMenu("Movimentações");
        menuMovimentacoes.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JMenuItem itemEmprestimos = criarMenuItem("menu.emprestimos");
        JMenuItem itemReservas    = criarMenuItem("menu.reservas");
        JMenuItem itemListagem    = criarMenuItem("menu.listagem");

        itemEmprestimos.addActionListener(e -> abrirJanela(new EmprestimoFrame()));
        itemReservas.addActionListener(e    -> abrirJanela(new ReservaFrame()));
        itemListagem.addActionListener(e    -> new ListagemLivrosFrame().setVisible(true));

        menuMovimentacoes.add(itemEmprestimos);
        menuMovimentacoes.add(itemReservas);
        menuMovimentacoes.addSeparator();
        menuMovimentacoes.add(itemListagem);

        // Menu Sistema
        JMenu menuSistema = new JMenu("Sistema");
        menuSistema.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JMenuItem itemUsuarios = criarMenuItem("menu.usuarios");
        JMenuItem itemIdioma   = criarMenuItem("menu.idioma");
        JMenuItem itemSair     = criarMenuItem("menu.sair");

        // Cadastro de usuários apenas para ADMIN
        itemUsuarios.setEnabled(usuarioLogado.getPerfil() == Usuario.Perfil.ADMIN);
        itemUsuarios.addActionListener(e -> abrirJanela(new CadastroUsuarioFrame()));
        itemIdioma.addActionListener(e   -> new ConfiguracaoIdiomaDialog(this).setVisible(true));
        itemSair.addActionListener(e     -> confirmarSaida());

        menuSistema.add(itemUsuarios);
        menuSistema.add(itemIdioma);
        menuSistema.addSeparator();
        menuSistema.add(itemSair);

        menuBar.add(menuCadastros);
        menuBar.add(menuMovimentacoes);
        menuBar.add(menuSistema);

        // Label do usuário logado no canto direito
        JLabel labelUsuario = new JLabel(
                "  Usuário: " + usuarioLogado.getLogin() +
                        " (" + usuarioLogado.getPerfil() + ")  "
        );
        labelUsuario.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        labelUsuario.setForeground(new Color(80, 80, 80));
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(labelUsuario);

        setJMenuBar(menuBar);
    }

    private JMenuItem criarMenuItem(String chave) {
        JMenuItem item = new JMenuItem(Mensagens.get(chave));
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return item;
    }

    private void abrirJanela(JInternalFrame frame) {
        // Evita abrir a mesma janela duas vezes
        for (JInternalFrame f : desktopPane.getAllFrames()) {
            if (f.getClass().equals(frame.getClass())) {
                f.toFront();
                try { f.setSelected(true); } catch (Exception ignored) {}
                return;
            }
        }
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private void configurarJanela() {
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

    private void aplicarIdioma() {
        setTitle(Mensagens.get("app.titulo"));
    }

    private void confirmarSaida() {
        int opcao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente sair do sistema?",
                "Sair",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (opcao == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
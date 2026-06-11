package br.pucgoias.biblioteca.view;

import br.pucgoias.biblioteca.dao.interfaces.IdiomaListener;
import br.pucgoias.biblioteca.util.Mensagens;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public abstract class GenericFrame extends JInternalFrame implements IdiomaListener {

    protected JTabbedPane abas;
    protected JTable tabela;
    protected DefaultTableModel modeloTabela;
    protected JTextField campoId;
    protected JTextField campoPesquisa;

    protected GenericFrame() {
        inicializarComponentes();
        Mensagens.addIdiomaListener(this);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override public void internalFrameClosed(InternalFrameEvent e) {
                Mensagens.removeIdiomaListener(GenericFrame.this);
            }
        });
    }

    private void inicializarComponentes() {
        abas = new JTabbedPane();
        abas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        abas.addTab(Mensagens.get("aba.cadastro"), criarPainelCadastro());
        abas.addTab(Mensagens.get("aba.pesquisa"), criarPainelPesquisa());
        add(abas);
    }

    protected void configurarJanela(String titulo, int w, int h, int x, int y) {
        setTitle(titulo);
        setSize(w, h);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setResizable(true);
        setLocation(x, y);
    }

    protected JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(cor);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    @Override
    public final void onIdiomaChanged() {
        abas.setTitleAt(0, Mensagens.get("aba.cadastro"));
        abas.setTitleAt(1, Mensagens.get("aba.pesquisa"));
        atualizarTextos();
    }

    protected abstract JPanel criarPainelCadastro();
    protected abstract JPanel criarPainelPesquisa();
    protected abstract void salvar();
    protected abstract void alterar();
    protected abstract void excluir();
    protected abstract void pesquisar();
    protected abstract void carregarDaTabela();
    protected abstract void limparCampos();
    protected abstract void atualizarTextos();
}

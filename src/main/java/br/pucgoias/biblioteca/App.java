package br.pucgoias.biblioteca;

import br.pucgoias.biblioteca.view.TelaLogin;

import javax.swing.*;

/**
 * Classe principal do BiblioSystem.
 * Inicializa a interface gráfica na Event Dispatch Thread do Swing.
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new TelaLogin().setVisible(true);
        });
    }
}
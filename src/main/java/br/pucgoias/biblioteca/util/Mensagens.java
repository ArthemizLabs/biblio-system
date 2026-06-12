package br.pucgoias.biblioteca.util;

import br.pucgoias.biblioteca.view.interfaces.IdiomaListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Utilitário de internacionalização (i18n).
 * Gerencia os textos da interface em português e inglês via ResourceBundle.
 */
public class Mensagens {

    private static ResourceBundle bundle;
    private static Locale localAtual = new Locale("pt", "BR"); // padrão: Português
    private static final List<IdiomaListener> listeners = new ArrayList<>();

    // Carrega o bundle ao inicializar
    static {
        carregarBundle();
    }

    private static void carregarBundle() {
        bundle = ResourceBundle.getBundle("messages", localAtual);
    }

    /**
     * Retorna o texto correspondente à chave no idioma atual.
     */
    public static String get(String chave) {
        try {
            return bundle.getString(chave);
        } catch (Exception e) {
            return "[" + chave + "]"; // fallback: exibe a própria chave
        }
    }

    /**
     * Altera o idioma do sistema e recarrega o bundle.
     * @param locale ex: new Locale("en", "US") ou new Locale("pt", "BR")
     */
    public static void setIdioma(Locale locale) {
        localAtual = locale;
        carregarBundle();
        new ArrayList<>(listeners).forEach(IdiomaListener::onIdiomaChanged);
    }

    public static Locale getLocalAtual() {
        return localAtual;
    }

    public static void addIdiomaListener(IdiomaListener l) {
        listeners.add(l);
    }

    public static void removeIdiomaListener(IdiomaListener l) {
        listeners.remove(l);
    }
}
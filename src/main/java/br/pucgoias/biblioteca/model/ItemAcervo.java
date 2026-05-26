package br.pucgoias.biblioteca.model;

/**
 * Classe abstrata base para itens do acervo bibliográfico.
 */
public abstract class ItemAcervo {

    private int id;
    private String titulo;
    private int quantidade;

    public ItemAcervo() {}

    public ItemAcervo(int id, String titulo, int quantidade) {
        this.id = id;
        this.titulo = titulo;
        this.quantidade = quantidade;
    }

    // Método abstrato — cada subclasse define sua própria identificação
    public abstract String getIdentificador();

    // Método abstrato — cada subclasse define sua descrição completa
    public abstract String getDescricaoCompleta();

    // Getters e Setters encapsulados
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    @Override
    public String toString() { return titulo; }
}
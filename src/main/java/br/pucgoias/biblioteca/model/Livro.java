package br.pucgoias.biblioteca.model;

/**
 * Entidade Livro — representa um livro do acervo bibliográfico.
 * Herda de ItemAcervo e implementa os métodos abstratos.
 */
public class Livro extends ItemAcervo {

    private String isbn;
    private int anoPublicacao;
    private Autor autor;
    private Editora editora;
    private Categoria categoria;

    public Livro() {}

    public Livro(int id, String titulo, String isbn, int anoPublicacao,
                 int quantidade, Autor autor, Editora editora, Categoria categoria) {
        super(id, titulo, quantidade);
        this.isbn = isbn;
        this.anoPublicacao = anoPublicacao;
        this.autor = autor;
        this.editora = editora;
        this.categoria = categoria;
    }

    @Override
    public String getIdentificador() {
        return "ISBN: " + isbn;
    }

    @Override
    public String getDescricaoCompleta() {
        return getTitulo() + " | " + getIdentificador()
                + " | Autor: " + (autor != null ? autor.getNome() : "N/A")
                + " | Ano: " + anoPublicacao;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(int anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public Autor getAutor() { return autor; }
    public void setAutor(Autor autor) { this.autor = autor; }

    public Editora getEditora() { return editora; }
    public void setEditora(Editora editora) { this.editora = editora; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}
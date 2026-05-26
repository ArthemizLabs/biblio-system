package br.pucgoias.biblioteca.model;

/**
 * Entidade Editora — representa a editora responsável por um livro.
 */
public class Editora {

    private int id;
    private String nome;
    private String cidade;

    public Editora() {}

    public Editora(int id, String nome, String cidade) {
        this.id = id;
        this.nome = nome;
        this.cidade = cidade;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    @Override
    public String toString() { return nome; }
}
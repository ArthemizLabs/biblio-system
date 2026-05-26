package br.pucgoias.biblioteca.model;

/**
 * Entidade Leitor — representa um usuário que realiza empréstimos e reservas.
 */
public class Leitor {

    private int id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;

    public Leitor() {}

    public Leitor(int id, String nome, String cpf, String email, String telefone) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    @Override
    public String toString() { return nome + " (CPF: " + cpf + ")"; }
}
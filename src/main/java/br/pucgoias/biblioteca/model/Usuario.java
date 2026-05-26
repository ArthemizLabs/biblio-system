package br.pucgoias.biblioteca.model;

/**
 * Entidade Usuario — representa um operador do sistema (funcionário ou admin).
 */
public class Usuario {

    public enum Perfil { ADMIN, FUNCIONARIO }

    private int id;
    private String login;
    private String senha;
    private Perfil perfil;

    public Usuario() {}

    public Usuario(int id, String login, String senha, Perfil perfil) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }

    @Override
    public String toString() { return login + " (" + perfil + ")"; }
}
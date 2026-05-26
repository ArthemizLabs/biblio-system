package br.pucgoias.biblioteca.model;

import java.time.LocalDate;

/**
 * Entidade Emprestimo — registra o empréstimo de um livro a um leitor.
 */
public class Emprestimo {

    public enum Status { ATIVO, DEVOLVIDO }

    private int id;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucaoPrevista;
    private LocalDate dataDevolucaoReal;
    private Status status;
    private Leitor leitor;
    private Livro livro;

    public Emprestimo() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(LocalDate dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }

    public LocalDate getDataDevolucaoPrevista() { return dataDevolucaoPrevista; }
    public void setDataDevolucaoPrevista(LocalDate dataDevolucaoPrevista) { this.dataDevolucaoPrevista = dataDevolucaoPrevista; }

    public LocalDate getDataDevolucaoReal() { return dataDevolucaoReal; }
    public void setDataDevolucaoReal(LocalDate dataDevolucaoReal) { this.dataDevolucaoReal = dataDevolucaoReal; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Leitor getLeitor() { return leitor; }
    public void setLeitor(Leitor leitor) { this.leitor = leitor; }

    public Livro getLivro() { return livro; }
    public void setLivro(Livro livro) { this.livro = livro; }
}
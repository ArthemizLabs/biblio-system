package br.pucgoias.biblioteca.model;

import java.time.LocalDate;

/**
 * Entidade Reserva — registra a reserva de um livro por um leitor.
 */
public class Reserva {

    public enum Status { ABERTA, CANCELADA, ATENDIDA }

    private int id;
    private LocalDate dataReserva;
    private Status status;
    private Leitor leitor;
    private Livro livro;

    public Reserva() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDataReserva() { return dataReserva; }
    public void setDataReserva(LocalDate dataReserva) { this.dataReserva = dataReserva; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Leitor getLeitor() { return leitor; }
    public void setLeitor(Leitor leitor) { this.leitor = leitor; }

    public Livro getLivro() { return livro; }
    public void setLivro(Livro livro) { this.livro = livro; }
}
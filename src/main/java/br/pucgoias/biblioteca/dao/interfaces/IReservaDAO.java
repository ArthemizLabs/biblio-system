package br.pucgoias.biblioteca.dao.interfaces;

import br.pucgoias.biblioteca.model.Reserva;
import java.util.List;

/**
 * Contrato de acesso a dados para a entidade Reserva.
 */
public interface IReservaDAO extends IGenericoDAO<Reserva, Integer> {
    List<Reserva> buscarPorLeitor(int idLeitor);
    boolean verificarDisponibilidade(int idLivro);
}
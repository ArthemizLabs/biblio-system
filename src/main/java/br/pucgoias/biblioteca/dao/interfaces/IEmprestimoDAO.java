package br.pucgoias.biblioteca.dao.interfaces;

import br.pucgoias.biblioteca.model.Emprestimo;
import java.util.List;

/**
 * Contrato de acesso a dados para a entidade Emprestimo.
 */
public interface IEmprestimoDAO extends IGenericoDAO<Emprestimo, Integer> {
    List<Emprestimo> buscarPorLeitor(int idLeitor);
    List<Emprestimo> listarAtivos();
}
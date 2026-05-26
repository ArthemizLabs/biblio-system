package br.pucgoias.biblioteca.dao.interfaces;

import br.pucgoias.biblioteca.model.Leitor;
import java.util.List;

/**
 * Contrato de acesso a dados para a entidade Leitor.
 */
public interface ILeitorDAO extends IGenericoDAO<Leitor, Integer> {
    List<Leitor> buscarPorNome(String nome);
    Leitor buscarPorCpf(String cpf);
}
package br.pucgoias.biblioteca.dao.interfaces;

import br.pucgoias.biblioteca.model.Autor;
import java.util.List;

/**
 * Contrato de acesso a dados para a entidade Autor.
 */
public interface IAutorDAO extends IGenericoDAO<Autor, Integer> {
    List<Autor> buscarPorNome(String nome);
}
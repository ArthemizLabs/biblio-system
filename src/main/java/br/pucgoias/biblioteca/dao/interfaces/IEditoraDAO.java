package br.pucgoias.biblioteca.dao.interfaces;

import br.pucgoias.biblioteca.model.Editora;
import java.util.List;

/**
 * Contrato de acesso a dados para a entidade Editora.
 */
public interface IEditoraDAO extends IGenericoDAO<Editora, Integer> {
    List<Editora> buscarPorNome(String nome);
}
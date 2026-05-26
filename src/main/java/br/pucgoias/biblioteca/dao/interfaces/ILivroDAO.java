package br.pucgoias.biblioteca.dao.interfaces;

import br.pucgoias.biblioteca.model.Livro;
import java.util.List;

/**
 * Contrato de acesso a dados para a entidade Livro.
 * Pesquisa complexa: por código, título ou ISBN.
 */
public interface ILivroDAO extends IGenericoDAO<Livro, Integer> {
    List<Livro> buscarPorTitulo(String titulo);
    Livro buscarPorIsbn(String isbn);
    List<Livro> listarOrdenado(String ordenarPor); // "titulo" ou "categoria"
}
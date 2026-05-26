package br.pucgoias.biblioteca.dao.interfaces;

import java.util.List;

/**
 * Interface genérica que define o contrato CRUD para todos os DAOs do sistema.
 * Uso de Generics garante reaproveitamento e tipagem segura em todas as entidades.
 *
 * @param <T>  Tipo da entidade (ex: Livro, Autor)
 * @param <ID> Tipo da chave primária (geralmente Integer)
 */
public interface IGenericoDAO<T, ID> {

    void inserir(T entidade);
    void atualizar(T entidade);
    void deletar(ID id);
    T buscarPorId(ID id);
    List<T> listarTodos();
}
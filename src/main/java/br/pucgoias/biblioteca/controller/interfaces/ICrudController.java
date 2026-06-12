package br.pucgoias.biblioteca.controller.interfaces;

import java.util.List;

public interface ICrudController<T> {
    void salvar(T entidade);
    void atualizar(T entidade);
    void deletar(int id);
    T buscarPorId(int id);
    List<T> listarTodos();
}

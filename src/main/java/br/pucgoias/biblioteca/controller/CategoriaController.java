package br.pucgoias.biblioteca.controller;

import br.pucgoias.biblioteca.dao.CategoriaDAO;
import br.pucgoias.biblioteca.model.Categoria;
import br.pucgoias.biblioteca.util.exceptions.ValidacaoException;

import java.util.List;

/**
 * Controller da entidade Categoria.
 * Contém as regras de negócio e validações antes de acionar o DAO.
 */
public class CategoriaController {

    private final CategoriaDAO dao = new CategoriaDAO();

    public void salvar(Categoria categoria) {
        validar(categoria);
        dao.inserir(categoria);
    }

    public void atualizar(Categoria categoria) {
        validar(categoria);
        dao.atualizar(categoria);
    }

    public void deletar(int id) {
        dao.deletar(id);
    }

    public Categoria buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public List<Categoria> listarTodos() {
        return dao.listarTodos();
    }

    private void validar(Categoria categoria) {
        if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
            throw new ValidacaoException("O campo \"Nome\" é obrigatório.");
        }
    }
}
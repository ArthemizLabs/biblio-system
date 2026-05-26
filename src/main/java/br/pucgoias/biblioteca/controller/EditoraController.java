package br.pucgoias.biblioteca.controller;

import br.pucgoias.biblioteca.dao.EditoraDAO;
import br.pucgoias.biblioteca.model.Editora;
import br.pucgoias.biblioteca.util.exceptions.ValidacaoException;

import java.util.List;

/**
 * Controller da entidade Editora.
 * Contém as regras de negócio e validações antes de acionar o DAO.
 */
public class EditoraController {

    private final EditoraDAO dao = new EditoraDAO();

    public void salvar(Editora editora) {
        validar(editora);
        dao.inserir(editora);
    }

    public void atualizar(Editora editora) {
        validar(editora);
        dao.atualizar(editora);
    }

    public void deletar(int id) {
        dao.deletar(id);
    }

    public Editora buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public List<Editora> listarTodos() {
        return dao.listarTodos();
    }

    public List<Editora> buscarPorNome(String nome) {
        return dao.buscarPorNome(nome == null ? "" : nome.trim());
    }

    private void validar(Editora editora) {
        if (editora.getNome() == null || editora.getNome().trim().isEmpty()) {
            throw new ValidacaoException("O campo \"Nome\" é obrigatório.");
        }
    }
}
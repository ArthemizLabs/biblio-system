package br.pucgoias.biblioteca.controller;

import br.pucgoias.biblioteca.controller.interfaces.ICrudController;
import br.pucgoias.biblioteca.dao.AutorDAO;
import br.pucgoias.biblioteca.model.Autor;
import br.pucgoias.biblioteca.util.exceptions.ValidacaoException;

import java.util.List;

/**
 * Controller da entidade Autor.
 * Contém as regras de negócio e validações antes de acionar o DAO.
 */
public class AutorController implements ICrudController<Autor> {

    private final AutorDAO dao = new AutorDAO();

    public void salvar(Autor autor) {
        validar(autor);
        dao.inserir(autor);
    }

    public void atualizar(Autor autor) {
        validar(autor);
        dao.atualizar(autor);
    }

    public void deletar(int id) {
        dao.deletar(id);
    }

    public Autor buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public List<Autor> listarTodos() {
        return dao.listarTodos();
    }

    public List<Autor> buscarPorNome(String nome) {
        return dao.buscarPorNome(nome == null ? "" : nome.trim());
    }

    private void validar(Autor autor) {
        if (autor.getNome() == null || autor.getNome().trim().isEmpty()) {
            throw new ValidacaoException("O campo \"Nome\" é obrigatório.");
        }
    }
}
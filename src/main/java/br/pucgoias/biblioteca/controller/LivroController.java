package br.pucgoias.biblioteca.controller;

import br.pucgoias.biblioteca.controller.interfaces.ICrudController;
import br.pucgoias.biblioteca.dao.LivroDAO;
import br.pucgoias.biblioteca.model.Livro;
import br.pucgoias.biblioteca.util.exceptions.ValidacaoException;

import java.util.List;

/**
 * Controller da entidade Livro.
 * Valida ISBN, campos obrigatórios e duplicidade antes de acionar o DAO.
 */
public class LivroController implements ICrudController<Livro> {

    private final LivroDAO dao = new LivroDAO();

    public void salvar(Livro livro) {
        validar(livro);
        if (dao.buscarPorIsbn(livro.getIsbn()) != null) {
            throw new ValidacaoException("ISBN já cadastrado no sistema.");
        }
        dao.inserir(livro);
    }

    public void atualizar(Livro livro) {
        validar(livro);
        Livro existente = dao.buscarPorIsbn(livro.getIsbn());
        if (existente != null && existente.getId() != livro.getId()) {
            throw new ValidacaoException("ISBN já cadastrado para outro livro.");
        }
        dao.atualizar(livro);
    }

    public void deletar(int id) {
        dao.deletar(id);
    }

    public Livro buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public Livro buscarPorIsbn(String isbn) {
        return dao.buscarPorIsbn(isbn);
    }

    public List<Livro> buscarPorTitulo(String titulo) {
        return dao.buscarPorTitulo(titulo == null ? "" : titulo.trim());
    }

    public List<Livro> listarTodos() {
        return dao.listarTodos();
    }

    public List<Livro> listarOrdenado(String ordenarPor) {
        return dao.listarOrdenado(ordenarPor);
    }

    private void validar(Livro livro) {
        if (livro.getTitulo() == null || livro.getTitulo().trim().isEmpty()) {
            throw new ValidacaoException("O campo \"Título\" é obrigatório.");
        }
        if (livro.getIsbn() == null || livro.getIsbn().trim().isEmpty()) {
            throw new ValidacaoException("O campo \"ISBN\" é obrigatório.");
        }
        if (!livro.getIsbn().matches("\\d+")) {
            throw new ValidacaoException("ISBN inválido. Informe apenas números.");
        }
        if (livro.getAutor() == null || livro.getAutor().getId() == 0) {
            throw new ValidacaoException("Selecione um autor.");
        }
        if (livro.getEditora() == null || livro.getEditora().getId() == 0) {
            throw new ValidacaoException("Selecione uma editora.");
        }
        if (livro.getCategoria() == null || livro.getCategoria().getId() == 0) {
            throw new ValidacaoException("Selecione uma categoria.");
        }
        if (livro.getAnoPublicacao() < 1000 || livro.getAnoPublicacao() > 2100) {
            throw new ValidacaoException("Ano de publicação inválido.");
        }
        if (livro.getQuantidade() < 1) {
            throw new ValidacaoException("A quantidade deve ser pelo menos 1.");
        }
    }
}
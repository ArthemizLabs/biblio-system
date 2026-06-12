package br.pucgoias.biblioteca.controller;

import br.pucgoias.biblioteca.controller.interfaces.ICrudController;
import br.pucgoias.biblioteca.dao.LeitorDAO;
import br.pucgoias.biblioteca.model.Leitor;
import br.pucgoias.biblioteca.util.exceptions.ValidacaoException;

import java.util.List;

/**
 * Controller da entidade Leitor.
 * Valida CPF e campos obrigatórios antes de acionar o DAO.
 */
public class LeitorController implements ICrudController<Leitor> {

    private final LeitorDAO dao = new LeitorDAO();

    public void salvar(Leitor leitor) {
        validar(leitor);
        if (dao.buscarPorCpf(leitor.getCpf()) != null) {
            throw new ValidacaoException("CPF já cadastrado no sistema.");
        }
        dao.inserir(leitor);
    }

    public void atualizar(Leitor leitor) {
        validar(leitor);
        Leitor existente = dao.buscarPorCpf(leitor.getCpf());
        if (existente != null && existente.getId() != leitor.getId()) {
            throw new ValidacaoException("CPF já cadastrado para outro leitor.");
        }
        dao.atualizar(leitor);
    }

    public void deletar(int id) {
        dao.deletar(id);
    }

    public Leitor buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public List<Leitor> listarTodos() {
        return dao.listarTodos();
    }

    public List<Leitor> buscarPorNome(String nome) {
        return dao.buscarPorNome(nome == null ? "" : nome.trim());
    }

    private void validar(Leitor leitor) {
        if (leitor.getNome() == null || leitor.getNome().trim().isEmpty()) {
            throw new ValidacaoException("O campo \"Nome\" é obrigatório.");
        }
        if (leitor.getCpf() == null || leitor.getCpf().trim().isEmpty()) {
            throw new ValidacaoException("O campo \"CPF\" é obrigatório.");
        }
        if (!leitor.getCpf().matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            throw new ValidacaoException("CPF inválido. Formato esperado: 000.000.000-00.");
        }
    }
}
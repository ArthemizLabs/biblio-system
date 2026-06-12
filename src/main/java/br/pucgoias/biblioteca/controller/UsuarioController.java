package br.pucgoias.biblioteca.controller;

import br.pucgoias.biblioteca.controller.interfaces.ICrudController;
import br.pucgoias.biblioteca.dao.UsuarioDAO;
import br.pucgoias.biblioteca.model.Usuario;
import br.pucgoias.biblioteca.util.exceptions.ValidacaoException;

import java.util.List;

/**
 * Controller da entidade Usuario.
 * Gerencia autenticação e cadastro de usuários do sistema.
 */
public class UsuarioController implements ICrudController<Usuario> {

    private final UsuarioDAO dao = new UsuarioDAO();

    public Usuario autenticar(String login, String senha) {
        if (login == null || login.trim().isEmpty() ||
                senha == null || senha.trim().isEmpty()) {
            throw new ValidacaoException("Preencha o usuário e a senha.");
        }
        return dao.buscarPorLogin(login.trim(), senha.trim());
    }

    public void salvar(Usuario usuario) {
        validar(usuario);
        if (dao.loginExiste(usuario.getLogin())) {
            throw new ValidacaoException("Login já cadastrado no sistema.");
        }
        dao.inserir(usuario);
    }

    public void atualizar(Usuario usuario) {
        validar(usuario);
        dao.atualizar(usuario);
    }

    public void deletar(int id) {
        dao.deletar(id);
    }

    public Usuario buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public List<Usuario> listarTodos() {
        return dao.listarTodos();
    }

    private void validar(Usuario usuario) {
        if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty()) {
            throw new ValidacaoException("O campo \"Login\" é obrigatório.");
        }
        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            throw new ValidacaoException("O campo \"Senha\" é obrigatório.");
        }
        if (usuario.getPerfil() == null) {
            throw new ValidacaoException("Selecione um perfil para o usuário.");
        }
    }
}
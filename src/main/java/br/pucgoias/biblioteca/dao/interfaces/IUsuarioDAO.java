package br.pucgoias.biblioteca.dao.interfaces;

import br.pucgoias.biblioteca.model.Usuario;

/**
 * Contrato de acesso a dados para a entidade Usuario.
 */
public interface IUsuarioDAO extends IGenericoDAO<Usuario, Integer> {
    Usuario buscarPorLogin(String login, String senha);
    boolean loginExiste(String login);
}
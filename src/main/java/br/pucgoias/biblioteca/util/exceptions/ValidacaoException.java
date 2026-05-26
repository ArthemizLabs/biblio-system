package br.pucgoias.biblioteca.util.exceptions;

/**
 * Exceção customizada para erros de validação de campos.
 * Lançada quando dados informados pelo usuário não atendem às regras de negócio.
 */
public class ValidacaoException extends RuntimeException {

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
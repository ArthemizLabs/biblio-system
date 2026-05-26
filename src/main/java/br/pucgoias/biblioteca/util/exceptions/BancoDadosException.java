package br.pucgoias.biblioteca.util.exceptions;

/**
 * Exceção customizada para erros de acesso ao banco de dados.
 * Encapsula SQLExceptions com mensagens amigáveis ao usuário.
 */
public class BancoDadosException extends RuntimeException {

    public BancoDadosException(String mensagem) {
        super(mensagem);
    }

    public BancoDadosException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
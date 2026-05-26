package br.pucgoias.biblioteca.util;

import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utilitário de conexão JDBC com o banco MySQL.
 * Implementa o padrão Singleton para reutilização da conexão.
 */
public class ConexaoBD {

    private static final String URL = "jdbc:mysql://localhost:3306/bibliosystem?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true";
    private static final String USUARIO  = "root";
    private static final String SENHA    = "root";

    private static Connection conexao;

    // Construtor privado — impede instanciação direta
    private ConexaoBD() {}

    /**
     * Retorna a conexão ativa, criando uma nova se necessário.
     */
    public static Connection getConexao() {
        try {
            if (conexao == null || conexao.isClosed()) {
                conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
            }
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao conectar ao banco de dados: " + e.getMessage(), e);
        }
        return conexao;
    }

    /**
     * Fecha a conexão com o banco de dados.
     */
    public static void fecharConexao() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
            }
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao fechar conexão: " + e.getMessage(), e);
        }
    }
}
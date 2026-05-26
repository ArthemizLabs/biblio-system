package br.pucgoias.biblioteca.dao;

import br.pucgoias.biblioteca.dao.interfaces.IAutorDAO;
import br.pucgoias.biblioteca.model.Autor;
import br.pucgoias.biblioteca.util.ConexaoBD;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do DAO para a entidade Autor.
 * Realiza operações CRUD via JDBC com o banco MySQL.
 */
public class AutorDAO implements IAutorDAO {

    @Override
    public void inserir(Autor autor) {
        String sql = "INSERT INTO autor (nome, nacionalidade) VALUES (?, ?)";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setString(1, autor.getNome());
            ps.setString(2, autor.getNacionalidade());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao inserir autor: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Autor autor) {
        String sql = "UPDATE autor SET nome=?, nacionalidade=? WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setString(1, autor.getNome());
            ps.setString(2, autor.getNacionalidade());
            ps.setInt(3, autor.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao atualizar autor: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletar(Integer id) {
        String sql = "DELETE FROM autor WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao deletar autor: " + e.getMessage(), e);
        }
    }

    @Override
    public Autor buscarPorId(Integer id) {
        String sql = "SELECT * FROM autor WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao buscar autor: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Autor> listarTodos() {
        List<Autor> lista = new ArrayList<>();
        String sql = "SELECT * FROM autor ORDER BY nome";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao listar autores: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Autor> buscarPorNome(String nome) {
        List<Autor> lista = new ArrayList<>();
        String sql = "SELECT * FROM autor WHERE nome LIKE ? ORDER BY nome";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setString(1, "%" + nome + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao buscar autores por nome: " + e.getMessage(), e);
        }
        return lista;
    }

    private Autor mapear(ResultSet rs) throws SQLException {
        return new Autor(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("nacionalidade")
        );
    }
}
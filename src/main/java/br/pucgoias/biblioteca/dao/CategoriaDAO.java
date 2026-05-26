package br.pucgoias.biblioteca.dao;

import br.pucgoias.biblioteca.dao.interfaces.ICategoriaDAO;
import br.pucgoias.biblioteca.model.Categoria;
import br.pucgoias.biblioteca.util.ConexaoBD;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do DAO para a entidade Categoria.
 * Realiza operações CRUD via JDBC com o banco MySQL.
 */
public class CategoriaDAO implements ICategoriaDAO {

    @Override
    public void inserir(Categoria categoria) {
        String sql = "INSERT INTO categoria (nome, descricao) VALUES (?, ?)";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setString(1, categoria.getNome());
            ps.setString(2, categoria.getDescricao());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao inserir categoria: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Categoria categoria) {
        String sql = "UPDATE categoria SET nome=?, descricao=? WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setString(1, categoria.getNome());
            ps.setString(2, categoria.getDescricao());
            ps.setInt(3, categoria.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao atualizar categoria: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletar(Integer id) {
        String sql = "DELETE FROM categoria WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao deletar categoria: " + e.getMessage(), e);
        }
    }

    @Override
    public Categoria buscarPorId(Integer id) {
        String sql = "SELECT * FROM categoria WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao buscar categoria: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Categoria> listarTodos() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categoria ORDER BY nome";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao listar categorias: " + e.getMessage(), e);
        }
        return lista;
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("descricao")
        );
    }
}
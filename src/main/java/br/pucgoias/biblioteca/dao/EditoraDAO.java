package br.pucgoias.biblioteca.dao;

import br.pucgoias.biblioteca.dao.interfaces.IEditoraDAO;
import br.pucgoias.biblioteca.model.Editora;
import br.pucgoias.biblioteca.util.ConexaoBD;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do DAO para a entidade Editora.
 * Realiza operações CRUD via JDBC com o banco MySQL.
 */
public class EditoraDAO implements IEditoraDAO {

    @Override
    public void inserir(Editora editora) {
        String sql = "INSERT INTO editora (nome, cidade) VALUES (?, ?)";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setString(1, editora.getNome());
            ps.setString(2, editora.getCidade());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao inserir editora: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Editora editora) {
        String sql = "UPDATE editora SET nome=?, cidade=? WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setString(1, editora.getNome());
            ps.setString(2, editora.getCidade());
            ps.setInt(3, editora.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao atualizar editora: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletar(Integer id) {
        String sql = "DELETE FROM editora WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao deletar editora: " + e.getMessage(), e);
        }
    }

    @Override
    public Editora buscarPorId(Integer id) {
        String sql = "SELECT * FROM editora WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao buscar editora: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Editora> listarTodos() {
        List<Editora> lista = new ArrayList<>();
        String sql = "SELECT * FROM editora ORDER BY nome";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao listar editoras: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Editora> buscarPorNome(String nome) {
        List<Editora> lista = new ArrayList<>();
        String sql = "SELECT * FROM editora WHERE nome LIKE ? ORDER BY nome";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setString(1, "%" + nome + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao buscar editoras: " + e.getMessage(), e);
        }
        return lista;
    }

    private Editora mapear(ResultSet rs) throws SQLException {
        return new Editora(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("cidade")
        );
    }
}
package br.pucgoias.biblioteca.dao;

import br.pucgoias.biblioteca.dao.interfaces.ILeitorDAO;
import br.pucgoias.biblioteca.model.Leitor;
import br.pucgoias.biblioteca.util.ConexaoBD;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do DAO para a entidade Leitor.
 * Realiza operações CRUD via JDBC com o banco MySQL.
 */
public class LeitorDAO implements ILeitorDAO {

    @Override
    public void inserir(Leitor leitor) {
        String sql = "INSERT INTO leitor (nome, cpf, email, telefone) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setString(1, leitor.getNome());
            ps.setString(2, leitor.getCpf());
            ps.setString(3, leitor.getEmail());
            ps.setString(4, leitor.getTelefone());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao inserir leitor: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Leitor leitor) {
        String sql = "UPDATE leitor SET nome=?, cpf=?, email=?, telefone=? WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setString(1, leitor.getNome());
            ps.setString(2, leitor.getCpf());
            ps.setString(3, leitor.getEmail());
            ps.setString(4, leitor.getTelefone());
            ps.setInt(5, leitor.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao atualizar leitor: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletar(Integer id) {
        String sql = "DELETE FROM leitor WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao deletar leitor: " + e.getMessage(), e);
        }
    }

    @Override
    public Leitor buscarPorId(Integer id) {
        String sql = "SELECT * FROM leitor WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao buscar leitor: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Leitor> listarTodos() {
        List<Leitor> lista = new ArrayList<>();
        String sql = "SELECT * FROM leitor ORDER BY nome";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao listar leitores: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Leitor> buscarPorNome(String nome) {
        List<Leitor> lista = new ArrayList<>();
        String sql = "SELECT * FROM leitor WHERE nome LIKE ? ORDER BY nome";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setString(1, "%" + nome + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao buscar leitores: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Leitor buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM leitor WHERE cpf=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setString(1, cpf);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao buscar leitor por CPF: " + e.getMessage(), e);
        }
        return null;
    }

    private Leitor mapear(ResultSet rs) throws SQLException {
        return new Leitor(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("cpf"),
                rs.getString("email"),
                rs.getString("telefone")
        );
    }
}
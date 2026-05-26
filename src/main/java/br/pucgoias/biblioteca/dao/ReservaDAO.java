package br.pucgoias.biblioteca.dao;

import br.pucgoias.biblioteca.dao.interfaces.IReservaDAO;
import br.pucgoias.biblioteca.model.*;
import br.pucgoias.biblioteca.util.ConexaoBD;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do DAO para a entidade Reserva.
 * Inclui verificação de disponibilidade de livros para reserva.
 */
public class ReservaDAO implements IReservaDAO {

    @Override
    public void inserir(Reserva reserva) {
        String sql = "INSERT INTO reserva (data_reserva, status, id_leitor, id_livro) VALUES (?,?,?,?)";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(reserva.getDataReserva()));
            ps.setString(2, reserva.getStatus().name());
            ps.setInt(3, reserva.getLeitor().getId());
            ps.setInt(4, reserva.getLivro().getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao inserir reserva: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Reserva reserva) {
        String sql = "UPDATE reserva SET status=? WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setString(1, reserva.getStatus().name());
            ps.setInt(2, reserva.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao atualizar reserva: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletar(Integer id) {
        String sql = "DELETE FROM reserva WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao deletar reserva: " + e.getMessage(), e);
        }
    }

    @Override
    public Reserva buscarPorId(Integer id) {
        String sql = "SELECT r.*, l.nome as leitor_nome, li.titulo as livro_titulo " +
                "FROM reserva r " +
                "LEFT JOIN leitor l ON r.id_leitor = l.id " +
                "LEFT JOIN livro li ON r.id_livro = li.id " +
                "WHERE r.id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao buscar reserva: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Reserva> listarTodos() {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT r.*, l.nome as leitor_nome, li.titulo as livro_titulo " +
                "FROM reserva r " +
                "LEFT JOIN leitor l ON r.id_leitor = l.id " +
                "LEFT JOIN livro li ON r.id_livro = li.id " +
                "ORDER BY r.data_reserva DESC";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao listar reservas: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<Reserva> buscarPorLeitor(int idLeitor) {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT r.*, l.nome as leitor_nome, li.titulo as livro_titulo " +
                "FROM reserva r " +
                "LEFT JOIN leitor l ON r.id_leitor = l.id " +
                "LEFT JOIN livro li ON r.id_livro = li.id " +
                "WHERE r.id_leitor=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setInt(1, idLeitor);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao buscar reservas: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public boolean verificarDisponibilidade(int idLivro) {
        // Livro disponível se quantidade > 0 e não há reserva ABERTA para ele
        String sql = "SELECT quantidade FROM livro WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setInt(1, idLivro);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt("quantidade") > 0) return true;
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao verificar disponibilidade: " + e.getMessage(), e);
        }
        return false;
    }

    private Reserva mapear(ResultSet rs) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setId(rs.getInt("id"));
        reserva.setDataReserva(rs.getDate("data_reserva").toLocalDate());
        reserva.setStatus(Reserva.Status.valueOf(rs.getString("status")));

        Leitor leitor = new Leitor();
        leitor.setId(rs.getInt("id_leitor"));
        leitor.setNome(rs.getString("leitor_nome"));
        reserva.setLeitor(leitor);

        Livro livro = new Livro();
        livro.setId(rs.getInt("id_livro"));
        livro.setTitulo(rs.getString("livro_titulo"));
        reserva.setLivro(livro);

        return reserva;
    }
}
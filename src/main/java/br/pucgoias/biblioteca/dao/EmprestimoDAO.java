package br.pucgoias.biblioteca.dao;

import br.pucgoias.biblioteca.dao.interfaces.IEmprestimoDAO;
import br.pucgoias.biblioteca.model.*;
import br.pucgoias.biblioteca.util.ConexaoBD;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do DAO para a entidade Emprestimo.
 * Realiza operações CRUD e listagem de empréstimos ativos via JDBC.
 */
public class EmprestimoDAO implements IEmprestimoDAO {

    @Override
    public void inserir(Emprestimo emp) {
        String sql = "INSERT INTO emprestimo (data_emprestimo, data_devolucao_prevista, status, id_leitor, id_livro) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(emp.getDataEmprestimo()));
            ps.setDate(2, Date.valueOf(emp.getDataDevolucaoPrevista()));
            ps.setString(3, emp.getStatus().name());
            ps.setInt(4, emp.getLeitor().getId());
            ps.setInt(5, emp.getLivro().getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao inserir empréstimo: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Emprestimo emp) {
        String sql = "UPDATE emprestimo SET data_devolucao_real=?, status=? WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setDate(1, emp.getDataDevolucaoReal() != null ? Date.valueOf(emp.getDataDevolucaoReal()) : null);
            ps.setString(2, emp.getStatus().name());
            ps.setInt(3, emp.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao atualizar empréstimo: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletar(Integer id) {
        String sql = "DELETE FROM emprestimo WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao deletar empréstimo: " + e.getMessage(), e);
        }
    }

    @Override
    public Emprestimo buscarPorId(Integer id) {
        String sql = "SELECT e.*, l.nome as leitor_nome, li.titulo as livro_titulo " +
                "FROM emprestimo e " +
                "LEFT JOIN leitor l ON e.id_leitor = l.id " +
                "LEFT JOIN livro li ON e.id_livro = li.id " +
                "WHERE e.id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao buscar empréstimo: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Emprestimo> listarTodos() {
        return listarPorStatus(null);
    }

    @Override
    public List<Emprestimo> listarAtivos() {
        return listarPorStatus("ATIVO");
    }

    @Override
    public List<Emprestimo> buscarPorLeitor(int idLeitor) {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = "SELECT e.*, l.nome as leitor_nome, li.titulo as livro_titulo " +
                "FROM emprestimo e " +
                "LEFT JOIN leitor l ON e.id_leitor = l.id " +
                "LEFT JOIN livro li ON e.id_livro = li.id " +
                "WHERE e.id_leitor=? ORDER BY e.data_emprestimo DESC";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setInt(1, idLeitor);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao buscar empréstimos: " + e.getMessage(), e);
        }
        return lista;
    }

    private List<Emprestimo> listarPorStatus(String status) {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = "SELECT e.*, l.nome as leitor_nome, li.titulo as livro_titulo " +
                "FROM emprestimo e " +
                "LEFT JOIN leitor l ON e.id_leitor = l.id " +
                "LEFT JOIN livro li ON e.id_livro = li.id " +
                (status != null ? "WHERE e.status=? " : "") +
                "ORDER BY e.data_emprestimo DESC";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            if (status != null) ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao listar empréstimos: " + e.getMessage(), e);
        }
        return lista;
    }

    private Emprestimo mapear(ResultSet rs) throws SQLException {
        Emprestimo emp = new Emprestimo();
        emp.setId(rs.getInt("id"));
        emp.setDataEmprestimo(rs.getDate("data_emprestimo").toLocalDate());
        emp.setDataDevolucaoPrevista(rs.getDate("data_devolucao_prevista").toLocalDate());
        Date dataReal = rs.getDate("data_devolucao_real");
        if (dataReal != null) emp.setDataDevolucaoReal(dataReal.toLocalDate());
        emp.setStatus(Emprestimo.Status.valueOf(rs.getString("status")));

        Leitor leitor = new Leitor();
        leitor.setId(rs.getInt("id_leitor"));
        leitor.setNome(rs.getString("leitor_nome"));
        emp.setLeitor(leitor);

        Livro livro = new Livro();
        livro.setId(rs.getInt("id_livro"));
        livro.setTitulo(rs.getString("livro_titulo"));
        emp.setLivro(livro);

        return emp;
    }
}
package br.pucgoias.biblioteca.dao;

import br.pucgoias.biblioteca.dao.interfaces.ILivroDAO;
import br.pucgoias.biblioteca.model.*;
import br.pucgoias.biblioteca.util.ConexaoBD;
import br.pucgoias.biblioteca.util.exceptions.BancoDadosException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do DAO para a entidade Livro.
 * Inclui JOIN com autor, editora e categoria para montar o objeto completo.
 */
public class LivroDAO implements ILivroDAO {

    private static final String SELECT_BASE =
            "SELECT l.*, " +
                    "a.nome AS autor_nome, a.nacionalidade AS autor_nac, " +
                    "e.nome AS editora_nome, e.cidade AS editora_cidade, " +
                    "c.nome AS cat_nome, c.descricao AS cat_desc " +
                    "FROM livro l " +
                    "LEFT JOIN autor    a ON l.id_autor     = a.id " +
                    "LEFT JOIN editora  e ON l.id_editora   = e.id " +
                    "LEFT JOIN categoria c ON l.id_categoria = c.id ";

    @Override
    public void inserir(Livro livro) {
        String sql = "INSERT INTO livro (titulo, isbn, ano_publicacao, quantidade, id_autor, id_editora, id_categoria) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setString(1, livro.getTitulo());
            ps.setString(2, livro.getIsbn());
            ps.setInt(3, livro.getAnoPublicacao());
            ps.setInt(4, livro.getQuantidade());
            ps.setInt(5, livro.getAutor().getId());
            ps.setInt(6, livro.getEditora().getId());
            ps.setInt(7, livro.getCategoria().getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao inserir livro: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Livro livro) {
        String sql = "UPDATE livro SET titulo=?, isbn=?, ano_publicacao=?, quantidade=?, id_autor=?, id_editora=?, id_categoria=? WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setString(1, livro.getTitulo());
            ps.setString(2, livro.getIsbn());
            ps.setInt(3, livro.getAnoPublicacao());
            ps.setInt(4, livro.getQuantidade());
            ps.setInt(5, livro.getAutor().getId());
            ps.setInt(6, livro.getEditora().getId());
            ps.setInt(7, livro.getCategoria().getId());
            ps.setInt(8, livro.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao atualizar livro: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletar(Integer id) {
        String sql = "DELETE FROM livro WHERE id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao deletar livro: " + e.getMessage(), e);
        }
    }

    @Override
    public Livro buscarPorId(Integer id) {
        String sql = SELECT_BASE + "WHERE l.id=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao buscar livro: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Livro> listarTodos() {
        return listarOrdenado("titulo");
    }

    @Override
    public List<Livro> buscarPorTitulo(String titulo) {
        List<Livro> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE l.titulo LIKE ? ORDER BY l.titulo";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setString(1, "%" + titulo + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao buscar livros por título: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Livro buscarPorIsbn(String isbn) {
        String sql = SELECT_BASE + "WHERE l.isbn=?";
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql)) {
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao buscar livro por ISBN: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Livro> listarOrdenado(String ordenarPor) {
        List<Livro> lista = new ArrayList<>();
        String ordem = ordenarPor.equalsIgnoreCase("categoria") ? "c.nome, l.titulo" : "l.titulo";
        String sql = SELECT_BASE + "ORDER BY " + ordem;
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new BancoDadosException("Erro ao listar livros: " + e.getMessage(), e);
        }
        return lista;
    }

    private Livro mapear(ResultSet rs) throws SQLException {
        Autor autor = new Autor(
                rs.getInt("id_autor"),
                rs.getString("autor_nome"),
                rs.getString("autor_nac")
        );
        Editora editora = new Editora(
                rs.getInt("id_editora"),
                rs.getString("editora_nome"),
                rs.getString("editora_cidade")
        );
        Categoria categoria = new Categoria(
                rs.getInt("id_categoria"),
                rs.getString("cat_nome"),
                rs.getString("cat_desc")
        );
        return new Livro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("isbn"),
                rs.getInt("ano_publicacao"),
                rs.getInt("quantidade"),
                autor, editora, categoria
        );
    }
}
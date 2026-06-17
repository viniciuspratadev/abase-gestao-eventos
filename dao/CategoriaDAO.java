package dao;

import modelo.Categoria;
import util.ConexaoFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    public boolean inserir(Categoria categoria) {
        String sql = "INSERT INTO categoria (nome_categoria) VALUES (?)";
        try (Connection conn = ConexaoFactory.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoria.getNomeCategoria());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public List<Categoria> listarTodas() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categoria";
        try (Connection conn = ConexaoFactory.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Categoria c = new Categoria();
                c.setIdCateg(rs.getInt("id_categ"));
                c.setNomeCategoria(rs.getString("nome_categoria"));
                lista.add(c);
            }
        } catch (SQLException e) {}
        return lista;
    }

    public boolean atualizar(Categoria categoria) {
        String sql = "UPDATE categoria SET nome_categoria = ? WHERE id_categ = ?";
        try (Connection conn = ConexaoFactory.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoria.getNomeCategoria());
            stmt.setInt(2, categoria.getIdCateg());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean excluir(int idCateg) {
        String sql = "DELETE FROM categoria WHERE id_categ = ?";
        try (Connection conn = ConexaoFactory.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCateg);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}
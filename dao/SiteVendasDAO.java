package dao;

import modelo.SiteVendas;
import util.ConexaoFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SiteVendasDAO {
    public boolean inserir(SiteVendas site) {
        String sql = "INSERT INTO site_vendas (nome, url_base) VALUES (?, ?)";
        try (Connection conn = ConexaoFactory.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, site.getNome());
            stmt.setString(2, site.getUrlBase());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public List<SiteVendas> listarTodos() {
        List<SiteVendas> lista = new ArrayList<>();
        String sql = "SELECT * FROM site_vendas";
        try (Connection conn = ConexaoFactory.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                SiteVendas s = new SiteVendas();
                s.setIdSite(rs.getInt("id_site"));
                s.setNome(rs.getString("nome"));
                s.setUrlBase(rs.getString("url_base"));
                lista.add(s);
            }
        } catch (SQLException e) {}
        return lista;
    }

    public boolean atualizar(SiteVendas site) {
        String sql = "UPDATE site_vendas SET nome = ?, url_base = ? WHERE id_site = ?";
        try (Connection conn = ConexaoFactory.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, site.getNome());
            stmt.setString(2, site.getUrlBase());
            stmt.setInt(3, site.getIdSite());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean excluir(int idSite) {
        String sql = "DELETE FROM site_vendas WHERE id_site = ?";
        try (Connection conn = ConexaoFactory.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idSite);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}
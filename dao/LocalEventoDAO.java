package dao;

import modelo.LocalEvento;
import util.ConexaoFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocalEventoDAO {

    public boolean inserir(LocalEvento local) {
        String sql = "INSERT INTO local_evento (nome, endereco, latitude, longitude) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoFactory.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, local.getNome());
            stmt.setString(2, local.getEndereco());
            stmt.setBigDecimal(3, local.getLatitude());
            stmt.setBigDecimal(4, local.getLongitude());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir local: " + e.getMessage());
            return false;
        }
    }

    public List<LocalEvento> listarTodos() {
        List<LocalEvento> lista = new ArrayList<>();
        String sql = "SELECT * FROM local_evento";
        try (Connection conn = ConexaoFactory.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                LocalEvento l = new LocalEvento();
                l.setIdLocal(rs.getInt("id_local"));
                l.setNome(rs.getString("nome"));
                l.setEndereco(rs.getString("endereco"));
                l.setLatitude(rs.getBigDecimal("latitude"));
                l.setLongitude(rs.getBigDecimal("longitude"));
                lista.add(l);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar locais: " + e.getMessage());
        }
        return lista;
    }

    public boolean atualizar(LocalEvento local) {
        String sql = "UPDATE local_evento SET nome = ?, endereco = ?, latitude = ?, longitude = ? WHERE id_local = ?";
        try (Connection conn = ConexaoFactory.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, local.getNome());
            stmt.setString(2, local.getEndereco());
            stmt.setBigDecimal(3, local.getLatitude());
            stmt.setBigDecimal(4, local.getLongitude());
            stmt.setInt(5, local.getIdLocal());

            int linhas = stmt.executeUpdate();
            return linhas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar local: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int idLocal) {
        String sql = "DELETE FROM local_evento WHERE id_local = ?";
        try (Connection conn = ConexaoFactory.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idLocal);
            int linhas = stmt.executeUpdate();
            return linhas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir local: " + e.getMessage());
            return false;
        }
    }
}
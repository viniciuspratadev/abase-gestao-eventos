package dao;

import modelo.Checkin;
import modelo.Avaliacao;
import util.ConexaoFactory;
import util.SessaoAtual;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // Retorna todos os eventos ativos que o cliente AINDA NÃO comprou
    public List<Object[]> listarEventosExplorar() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT e.id_evento, e.nome AS evento_nome, e.data_evento, e.hora_evento, " +
                     "l.nome AS local_nome, l.latitude, l.longitude, s.url_base " +
                     "FROM evento e " +
                     "JOIN local_evento l ON e.id_local = l.id_local " +
                     "JOIN site_vendas s ON e.id_site = s.id_site " +
                     "WHERE e.status = 'ativo' AND e.id_evento NOT IN (" +
                     "  SELECT id_evento FROM ingresso WHERE id_usuario = ?" +
                     ")";

        try (Connection conn = ConexaoFactory.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, SessaoAtual.idUsuarioLogado);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getInt("id_evento"), rs.getString("evento_nome"), 
                        rs.getDate("data_evento"), rs.getTime("hora_evento"), 
                        rs.getString("local_nome"), rs.getBigDecimal("latitude"), 
                        rs.getBigDecimal("longitude"), rs.getString("url_base")
                    });
                }
            }
        } catch (SQLException e) { System.out.println("Erro ao listar exploração: " + e.getMessage()); }
        return lista;
    }

    public boolean comprarIngresso(int idEvento) {
        String sql = "INSERT INTO ingresso (id_usuario, id_evento) VALUES (?, ?)";
        try (Connection conn = ConexaoFactory.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, SessaoAtual.idUsuarioLogado);
            stmt.setInt(2, idEvento);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // Lista os eventos que o cliente JÁ COMPROU para fazer check-in
    public List<Object[]> listarMeusEventos() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT e.id_evento, e.nome, e.data_evento, l.nome AS local_nome " +
                     "FROM ingresso i " +
                     "JOIN evento e ON i.id_evento = e.id_evento " +
                     "JOIN local_evento l ON e.id_local = l.id_local " +
                     "WHERE i.id_usuario = ? AND e.id_evento NOT IN (" +
                     "  SELECT id_evento FROM checkin WHERE id_usuario = ?" +
                     ")";

        try (Connection conn = ConexaoFactory.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, SessaoAtual.idUsuarioLogado);
            stmt.setInt(2, SessaoAtual.idUsuarioLogado);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getInt("id_evento"), rs.getString("nome"), rs.getDate("data_evento"), rs.getString("local_nome")
                    });
                }
            }
        } catch (SQLException e) {}
        return lista;
    }

    public boolean fazerCheckin(int idEvento, BigDecimal lat, BigDecimal lon) {
        String sql = "INSERT INTO checkin (latitude_checkin, longitude_checkin, id_usuario, id_evento) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoFactory.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, lat);
            stmt.setBigDecimal(2, lon);
            stmt.setInt(3, SessaoAtual.idUsuarioLogado);
            stmt.setInt(4, idEvento);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public List<Object[]> listarCheckinsPendentesDeAvaliacao() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT c.id_checkin, e.nome, c.data_checkin " +
                     "FROM checkin c " +
                     "JOIN evento e ON c.id_evento = e.id_evento " +
                     "WHERE c.id_usuario = ? AND c.id_checkin NOT IN (SELECT id_checkin FROM avaliacao)";

        try (Connection conn = ConexaoFactory.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, SessaoAtual.idUsuarioLogado);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getInt("id_checkin"), rs.getString("nome"), rs.getTimestamp("data_checkin")
                    });
                }
            }
        } catch (SQLException e) {}
        return lista;
    }

    public boolean avaliar(Avaliacao aval) {
        String sql = "INSERT INTO avaliacao (nota, comentario, id_checkin) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoFactory.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, aval.getNota());
            stmt.setString(2, aval.getComentario());
            stmt.setInt(3, aval.getIdCheckin());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}
package dao;

import modelo.Evento;
import util.ConexaoFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {

    public boolean inserir(Evento evento) {
        String sqlEvento = "INSERT INTO evento (nome, url_evento, data_evento, hora_evento, id_local, id_site, id_produtor) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlCategoria = "INSERT INTO evento_categoria (id_evento, id_categ) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = ConexaoFactory.conectar();
            conn.setAutoCommit(false); // Trava a transação

            int idGerado = -1;

            // Passo 1: Insere o evento e captura o ID gerado pelo Aiven
            try (PreparedStatement stmtEv = conn.prepareStatement(sqlEvento, Statement.RETURN_GENERATED_KEYS)) {
                stmtEv.setString(1, evento.getNome());
                stmtEv.setString(2, evento.getUrlEvento());
                stmtEv.setDate(3, evento.getDataEvento());
                stmtEv.setTime(4, evento.getHoraEvento());
                stmtEv.setInt(5, evento.getIdLocal());
                stmtEv.setInt(6, evento.getIdSite());
                stmtEv.setInt(7, evento.getIdProdutor());
                stmtEv.executeUpdate();

                try (ResultSet rs = stmtEv.getGeneratedKeys()) {
                    if (rs.next()) idGerado = rs.getInt(1);
                    else throw new SQLException("Falha estrutural: ID do evento não gerado.");
                }
            }

            // Passo 2: Associa as categorias usando Batch (Lote)
            if (evento.getIdsCategorias() != null && !evento.getIdsCategorias().isEmpty()) {
                try (PreparedStatement stmtCat = conn.prepareStatement(sqlCategoria)) {
                    for (Integer idCat : evento.getIdsCategorias()) {
                        stmtCat.setInt(1, idGerado);
                        stmtCat.setInt(2, idCat);
                        stmtCat.addBatch(); // Adiciona na fila
                    }
                    stmtCat.executeBatch(); // Dispara todos de uma vez
                }
            }

            conn.commit(); // Confirma a transação
            return true;

        } catch (SQLException e) {
            System.out.println("Falha na transação. Executando Rollback: " + e.getMessage());
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) {}
        }
    }

    public List<Evento> listarPorProdutor(int idProdutor) {
        List<Evento> lista = new ArrayList<>();
        String sql = "SELECT * FROM evento WHERE id_produtor = ?";
        try (Connection conn = ConexaoFactory.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProdutor);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Evento ev = new Evento();
                    ev.setIdEvento(rs.getInt("id_evento"));
                    ev.setNome(rs.getString("nome"));
                    ev.setUrlEvento(rs.getString("url_evento"));
                    ev.setDataEvento(rs.getDate("data_evento"));
                    ev.setHoraEvento(rs.getTime("hora_evento"));
                    ev.setIdLocal(rs.getInt("id_local"));
                    ev.setIdSite(rs.getInt("id_site"));
                    ev.setIdProdutor(rs.getInt("id_produtor"));
                    lista.add(ev);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar eventos: " + e.getMessage());
        }
        return lista;
    }

    public boolean atualizar(Evento evento) {
        String sqlEvento = "UPDATE evento SET nome = ?, url_evento = ?, data_evento = ?, hora_evento = ?, id_local = ?, id_site = ? WHERE id_evento = ? AND id_produtor = ?";
        String sqlLimpaCat = "DELETE FROM evento_categoria WHERE id_evento = ?";
        String sqlNovaCat = "INSERT INTO evento_categoria (id_evento, id_categ) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = ConexaoFactory.conectar();
            conn.setAutoCommit(false);

            // 1. Atualiza dados do evento
            try (PreparedStatement stmtEv = conn.prepareStatement(sqlEvento)) {
                stmtEv.setString(1, evento.getNome());
                stmtEv.setString(2, evento.getUrlEvento());
                stmtEv.setDate(3, evento.getDataEvento());
                stmtEv.setTime(4, evento.getHoraEvento());
                stmtEv.setInt(5, evento.getIdLocal());
                stmtEv.setInt(6, evento.getIdSite());
                stmtEv.setInt(7, evento.getIdEvento());
                stmtEv.setInt(8, evento.getIdProdutor());
                stmtEv.executeUpdate();
            }

            // 2. Apaga as categorias antigas
            try (PreparedStatement stmtDel = conn.prepareStatement(sqlLimpaCat)) {
                stmtDel.setInt(1, evento.getIdEvento());
                stmtDel.executeUpdate();
            }

            // 3. Insere as novas categorias selecionadas
            if (evento.getIdsCategorias() != null && !evento.getIdsCategorias().isEmpty()) {
                try (PreparedStatement stmtIns = conn.prepareStatement(sqlNovaCat)) {
                    for (Integer idCat : evento.getIdsCategorias()) {
                        stmtIns.setInt(1, evento.getIdEvento());
                        stmtIns.setInt(2, idCat);
                        stmtIns.addBatch();
                    }
                    stmtIns.executeBatch();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) {}
        }
    }

    public boolean excluir(int idEvento, int idProdutor) {
        // O ON DELETE CASCADE no banco (se configurado) cuidará de apagar as categorias na tabela associativa
        String sql = "DELETE FROM evento WHERE id_evento = ? AND id_produtor = ?";
        try (Connection conn = ConexaoFactory.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvento);
            stmt.setInt(2, idProdutor);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}
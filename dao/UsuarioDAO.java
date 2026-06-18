package dao;

import modelo.Usuario;
import util.ConexaoFactory;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Usuario autenticar(String email, String senhaPlana) {
        // Correção aplicada: buscando a coluna 'senha_hash' exatamente como está no Aiven
        String sql = "SELECT id_usuario, senha_hash, status_aprov FROM usuario WHERE email = ?";
        
        try (Connection conn = ConexaoFactory.conectar(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Correção aplicada: extraindo a coluna 'senha_hash'
                    String hashBanco = rs.getString("senha_hash");
                    
                    if (BCrypt.checkpw(senhaPlana, hashBanco)) {
                        Usuario user = new Usuario();
                        int idUser = rs.getInt("id_usuario");
                        user.setIdUsuario(idUser);
                        
                        boolean aprovado = rs.getBoolean("status_aprov");
                        user.setStatus(aprovado ? "aprovado" : "pendente");
                        
                        user.setTipoUsuario(descobrirTipoUsuario(idUser, conn));
                        
                        return user; 
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro na base de dados durante autenticação: " + e.getMessage());
        }
        
        return null;
    }

    private String descobrirTipoUsuario(int idUsuario, Connection conn) {
        String sqlTipo = "SELECT 'Admin' AS tipo FROM usuario_admin WHERE id_usuario = ? " +
                         "UNION " +
                         "SELECT 'Produtor' FROM usuario_produtor WHERE id_usuario = ? " +
                         "UNION " +
                         "SELECT 'Cliente' FROM usuario_cliente WHERE id_usuario = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sqlTipo)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idUsuario);
            stmt.setInt(3, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tipo");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao descobrir o tipo de usuário: " + e.getMessage());
        }
        return "Desconhecido";
    }

    public javax.swing.table.DefaultTableModel listarProdutoresPendentes() {
        javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(
                new String[]{"ID", "Razão Social", "CNPJ", "E-mail"}, 0);
        
        String sql = "SELECT u.id_usuario, up.razao_social, up.cnpj, u.email " +
                     "FROM usuario u JOIN usuario_produtor up ON u.id_usuario = up.id_usuario " +
                     "WHERE u.status_aprov = FALSE";
        
        try (Connection conn = ConexaoFactory.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_usuario"),
                    rs.getString("razao_social"),
                    rs.getString("cnpj"),
                    rs.getString("email")
                });
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar produtores: " + e.getMessage());
        }
        return modelo;
    }

    public boolean aprovarUsuario(int idUsuario) {
        String sql = "UPDATE usuario SET status_aprov = TRUE WHERE id_usuario = ?";
        try (Connection conn = ConexaoFactory.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao aprovar: " + e.getMessage());
            return false;
        }
    }
}
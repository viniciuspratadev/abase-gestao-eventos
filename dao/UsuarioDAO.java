package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt; // Requer a biblioteca jBcrypt na pasta lib
import util.ConexaoFactory;
import util.SessaoAtual;

public class UsuarioDAO {

    // Método principal que a tela de Login vai chamar
    public boolean autenticar(String email, String senhaDigitada) {
        // Busca apenas o hash da senha e o status pelo e-mail
        String sqlUsuario = "SELECT id_usuario, senha_hash, status_aprov FROM usuario WHERE email = ?";

        // O bloco 'try-with-resources' garante que a conexão será fechada automaticamente no final
        try (Connection conn = ConexaoFactory.conectar();
             PreparedStatement stmt = conn.prepareStatement(sqlUsuario)) {

            // Previne falhas de segurança do tipo SQL Injection
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashBanco = rs.getString("senha_hash");
                    boolean statusAprov = rs.getBoolean("status_aprov");
                    int idUsuario = rs.getInt("id_usuario");

                    // 1. Verifica se a senha digitada bate com o Hash criptografado do banco
                    if (BCrypt.checkpw(senhaDigitada, hashBanco)) {
                        
                        // 2. Verifica se o cadastro foi aprovado (Exigência RF08 do seu escopo)
                        if (!statusAprov) {
                            System.out.println("Erro: Usuario aguardando aprovacao do Administrador.");
                            return false;
                        }

                        // 3. Se passou em tudo, descobre o tipo de usuário e salva na Sessão
                        definirSessao(idUsuario, conn);
                        return true;

                    } else {
                        System.out.println("Erro: Senha incorreta.");
                        return false;
                    }
                } else {
                    System.out.println("Erro: E-mail nao encontrado.");
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar usuario: " + e.getMessage(), e);
        }
    }

    // Método privado auxiliar para descobrir o papel (Role) do usuário
    private void definirSessao(int idUsuario, Connection conn) throws SQLException {
        // Faz uma busca unificada (UNION) nas três tabelas de perfil para descobrir quem é o usuário
        String sqlTipo = "SELECT 'admin' AS tipo FROM usuario_admin WHERE id_usuario = ? " +
                         "UNION " +
                         "SELECT 'produtor' FROM usuario_produtor WHERE id_usuario = ? " +
                         "UNION " +
                         "SELECT 'cliente' FROM usuario_cliente WHERE id_usuario = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sqlTipo)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idUsuario);
            stmt.setInt(3, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SessaoAtual.idUsuarioLogado = idUsuario;
                    SessaoAtual.tipoUsuarioLogado = rs.getString("tipo");
                }
            }
        }
    }
    public static void main(String[] args) {
        // Gera um hash real para a senha "123456"
        String senhaReal = "123456";
        String hashGerado = BCrypt.hashpw(senhaReal, BCrypt.gensalt());
        System.out.println("Copie o texto abaixo e use no banco de dados:");
        System.out.println(hashGerado);
    }

    // Lista os produtores que ainda estão com status_aprov = FALSE
    public javax.swing.table.DefaultTableModel listarProdutoresPendentes() {
        // Cria a estrutura da tabela
        javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(
                new String[]{"ID", "Razão Social", "CNPJ", "E-mail"}, 0);
        
        String sql = "SELECT u.id_usuario, up.razao_social, up.cnpj, u.email " +
                     "FROM usuario u JOIN usuario_produtor up ON u.id_usuario = up.id_usuario " +
                     "WHERE u.status_aprov = FALSE";
        
        try (Connection conn = ConexaoFactory.conectar();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
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

    // Altera o status do usuário para aprovado
    public boolean aprovarUsuario(int idUsuario) {
        String sql = "UPDATE usuario SET status_aprov = TRUE WHERE id_usuario = ?";
        try (Connection conn = ConexaoFactory.conectar();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            int linhas = stmt.executeUpdate();
            return linhas > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao aprovar: " + e.getMessage());
            return false;
        }
    }
}
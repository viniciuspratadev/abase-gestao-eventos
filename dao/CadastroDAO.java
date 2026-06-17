package dao;

import util.ConexaoFactory;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CadastroDAO {

    public boolean cadastrarCliente(String email, String senha, String cpf) {
        // Clientes entram já aprovados (TRUE)
        String sqlUsuario = "INSERT INTO usuario (email, senha_hash, status_aprov) VALUES (?, ?, TRUE)";
        String sqlCliente = "INSERT INTO usuario_cliente (id_usuario, cpf) VALUES (?, ?)";
        return executarTransacao(email, senha, sqlUsuario, sqlCliente, cpf, null, null);
    }

    public boolean cadastrarProdutor(String email, String senha, String cnpj, String razaoSocial, String nomeFantasia) {
        // Produtores entram bloqueados (FALSE) aguardando o Admin
        String sqlUsuario = "INSERT INTO usuario (email, senha_hash, status_aprov) VALUES (?, ?, FALSE)";
        String sqlProdutor = "INSERT INTO usuario_produtor (id_usuario, cnpj, razao_social, nome_fantasia) VALUES (?, ?, ?, ?)";
        return executarTransacao(email, senha, sqlUsuario, sqlProdutor, cnpj, razaoSocial, nomeFantasia);
    }

    private boolean executarTransacao(String email, String senha, String sqlUser, String sqlPerfil, String doc, String razao, String fantasia) {
        Connection conn = null;
        try {
            conn = ConexaoFactory.conectar();
            // Desliga o salvamento automático para iniciar a transação
            conn.setAutoCommit(false); 

            String hash = BCrypt.hashpw(senha, BCrypt.gensalt());
            int idGerado = -1;

            // Passo 1: Insere na tabela 'usuario' e pede ao banco o ID gerado (SERIAL)
            try (PreparedStatement stmtUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                stmtUser.setString(1, email);
                stmtUser.setString(2, hash);
                stmtUser.executeUpdate();

                try (ResultSet rs = stmtUser.getGeneratedKeys()) {
                    if (rs.next()) idGerado = rs.getInt(1);
                    else throw new SQLException("Falha ao capturar o ID gerado.");
                }
            }

            // Passo 2: Insere na tabela específica usando o ID recém-capturado
            try (PreparedStatement stmtPerfil = conn.prepareStatement(sqlPerfil)) {
                stmtPerfil.setInt(1, idGerado);
                stmtPerfil.setString(2, doc); // CPF ou CNPJ
                if (razao != null) { 
                    stmtPerfil.setString(3, razao);
                    stmtPerfil.setString(4, fantasia);
                }
                stmtPerfil.executeUpdate();
            }

            // Se chegou aqui sem dar erro, confirma a transação no banco
            conn.commit(); 
            return true;

        } catch (SQLException e) {
            System.out.println("Erro. Revertendo transação (Rollback): " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {}
            }
            return false;
        } finally {
            // Devolve a conexão ao estado normal e fecha
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
            }
        }
    }
}
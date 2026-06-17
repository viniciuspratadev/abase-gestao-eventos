package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConexaoFactory {

    public static Connection conectar() {
        try {
            // Lê o arquivo .env nativamente, sem bibliotecas externas
            Properties props = new Properties();
            props.load(new FileInputStream(".env"));
            
            String url = props.getProperty("APP_DB_URL");
            String usuario = props.getProperty("APP_DB_USUARIO");
            String senha = props.getProperty("APP_DB_SENHA");

            // Estabelece a conexão com o PostgreSQL
            return DriverManager.getConnection(url, usuario, senha);
            
        } catch (SQLException e) {
            throw new RuntimeException("Falha na conexao com o banco de dados do Aiven: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo .env. Certifique-se de que o arquivo esta na raiz do projeto (mesmo nivel da pasta src).", e);
        }
    }

    // Método pra testar a conexão
    public static void main(String[] args) {
        System.out.println("Testando conexão...");
        Connection conn = conectar();
        if (conn != null) {
            System.out.println("Conectado com sucesso ao Aiven!");
        }
    }
}
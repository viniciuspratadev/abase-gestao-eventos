package util;

public class SessaoAtual {
    // Guarda o ID numérico do utilizador logado no banco de dados (-1 significa deslogado)
    public static int idUsuarioLogado = -1;
    
    // Guarda o perfil do utilizador ("Cliente", "Produtor" ou "Admin")
    public static String tipoUsuario = null;
}
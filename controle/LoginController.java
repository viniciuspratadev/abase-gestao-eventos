package controle;

import dao.UsuarioDAO;
import modelo.Usuario;
import util.SessaoAtual;

public class LoginController {

    public String autenticar(String email, String senha) {
        if (email.isEmpty() || senha.isEmpty()) {
            return "ERRO_VAZIO";
        }

        UsuarioDAO dao = new UsuarioDAO();
        Usuario user = dao.autenticar(email, senha);

        if (user == null) {
            return "ERRO_CREDENCIAIS";
        }

        // Configura a sessão global aqui (Regra de negócio não deve ficar na visão)
        SessaoAtual.idUsuarioLogado = user.getIdUsuario();
        SessaoAtual.tipoUsuario = user.getTipoUsuario();

        // Lógica de roteamento baseada no tipo de utilizador e status do banco
        if (user.getTipoUsuario().equals("Admin")) {
            return "ADMIN";
        } else if (user.getTipoUsuario().equals("Cliente")) {
            return "CLIENTE";
        } else if (user.getTipoUsuario().equals("Produtor")) {
            if (user.getStatus() != null && user.getStatus().equals("aprovado")) {
                return "PRODUTOR_APROVADO";
            } else {
                // Se não está aprovado, a sessão não deve ser mantida
                SessaoAtual.idUsuarioLogado = -1;
                SessaoAtual.tipoUsuario = null;
                return "PRODUTOR_PENDENTE";
            }
        }
        
        return "ERRO_DESCONHECIDO";
    }
}
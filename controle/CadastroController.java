package controle;

import dao.CadastroDAO;

public class CadastroController {

    // Recebe apenas os dados crus da tela e processa as regras de negócio
    public String registrarUsuario(String tipo, String email, String senha, String cpf, String cnpj, String razao, String fantasia) {
        
        // 1. Validação de Regras de Negócio
        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            return "Erro: Introduza um e-mail válido contendo '@' e domínio.";
        }
        if (senha.length() < 6) {
            return "Erro: A palavra-passe deve ter no mínimo 6 caracteres.";
        }

        boolean isProdutor = tipo.equals("Produtor");
        CadastroDAO dao = new CadastroDAO();

        // 2. Processamento e chamada ao DAO
        if (isProdutor) {
            String cnpjLimpo = cnpj.replaceAll("[^0-9]", "");
            if (cnpjLimpo.length() != 14) return "Erro: O CNPJ deve conter exatamente 14 dígitos numéricos.";
            if (razao.isEmpty()) return "Erro: A Razão Social é obrigatória para o Produtor.";
            if (fantasia.isEmpty()) return "Erro: O Nome Fantasia é obrigatório.";
            
            if (dao.cadastrarProdutor(email, senha, cnpjLimpo, razao, fantasia)) {
                return "SUCESSO_PRODUTOR";
            }
        } else {
            String cpfLimpo = cpf.replaceAll("[^0-9]", "");
            if (cpfLimpo.length() != 11) return "Erro: O CPF deve conter exatamente 11 dígitos numéricos.";
            
            if (dao.cadastrarCliente(email, senha, cpfLimpo)) {
                return "SUCESSO_CLIENTE";
            }
        }
        
        return "Erro: Falha na base de dados. O E-mail, CPF ou CNPJ introduzido já existe no sistema.";
    }
}
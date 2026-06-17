package visao;

import dao.UsuarioDAO;
import util.SessaoAtual;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaLogin extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JButton btnCadastrar;

    public TelaLogin() {
        setTitle("ABase - Login");
        setSize(320, 200); // Largura expandida para acomodar os dois botões
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela
        setLayout(null); // Layout absoluto (manual)

        JLabel lblEmail = new JLabel("E-mail:");
        lblEmail.setBounds(30, 20, 80, 25);
        add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(90, 20, 180, 25); // Caixas de texto ajustadas
        add(txtEmail);

        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setBounds(30, 60, 80, 25);
        add(lblSenha);

        txtSenha = new JPasswordField();
        txtSenha.setBounds(90, 60, 180, 25);
        add(txtSenha);

        // Botão Entrar deslocado para a esquerda
        btnEntrar = new JButton("Entrar");
        btnEntrar.setBounds(40, 100, 100, 30);
        add(btnEntrar);

        // Novo botão de Cadastro posicionado à direita
        btnCadastrar = new JButton("Criar Conta");
        btnCadastrar.setBounds(160, 100, 110, 30); 
        add(btnCadastrar);

        btnEntrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tentarLogin();
            }
        });

        // Ação para abrir a nova tela de cadastro
        btnCadastrar.addActionListener(e -> {
            new TelaCadastro().setVisible(true);
        });
    }

    private void tentarLogin() {
        String email = txtEmail.getText();
        String senha = new String(txtSenha.getPassword());

        UsuarioDAO dao = new UsuarioDAO();
        boolean sucesso = dao.autenticar(email, senha);

        if (sucesso) {
            this.dispose(); // Fecha a tela de login
            
            // Redireciona de acordo com o papel do usuário
            if (SessaoAtual.tipoUsuarioLogado.equals("admin")) {
                new TelaAdmin().setVisible(true);
            } else if (SessaoAtual.tipoUsuarioLogado.equals("produtor")) {
                new TelaProdutor().setVisible(true);
            } else if (SessaoAtual.tipoUsuarioLogado.equals("cliente")) {
                new TelaCliente().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Painel em desenvolvimento para: " + SessaoAtual.tipoUsuarioLogado);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Credenciais inválidas ou acesso não aprovado.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Ponto de partida da interface
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TelaLogin().setVisible(true);
            }
        });
    }
}
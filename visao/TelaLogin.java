package visao;

import controle.LoginController;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TelaLogin extends JFrame {
    
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    
    // A tela agora conhece apenas o Controller
    private LoginController controller = new LoginController();

    public TelaLogin() {
        setTitle("ABase - Acesso ao Sistema");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        int alt = 25;

        add(criarLabel("E-mail:", 50, 40, 80, alt));
        txtEmail = criarTextField(120, 40, 200, alt, this);

        add(criarLabel("Senha:", 50, 80, 80, alt));
        txtSenha = new JPasswordField();
        txtSenha.setBounds(120, 80, 200, alt);
        add(txtSenha);

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setBounds(120, 130, 100, 30);
        add(btnEntrar);

        JLabel lblCriarConta = new JLabel("<html><u>Não tem conta? Criar nova</u></html>");
        lblCriarConta.setBounds(120, 180, 200, alt);
        lblCriarConta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        add(lblCriarConta);

        // AÇÃO DO BOTÃO: Entrega a validação ao Controller e reage à resposta
        btnEntrar.addActionListener(e -> {
            String email = txtEmail.getText().trim();
            String senha = new String(txtSenha.getPassword());

            String resposta = controller.autenticar(email, senha);

            switch (resposta) {
                case "ERRO_VAZIO":
                    JOptionPane.showMessageDialog(this, "Por favor, preencha o e-mail e a senha.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    break;
                case "ERRO_CREDENCIAIS":
                    JOptionPane.showMessageDialog(this, "E-mail não encontrado ou senha incorreta.", "Acesso Negado", JOptionPane.ERROR_MESSAGE);
                    break;
                case "PRODUTOR_PENDENTE":
                    JOptionPane.showMessageDialog(this, "A sua conta de Produtor ainda aguarda a liberação do Administrador.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case "ADMIN":
                    new TelaAdmin().setVisible(true);
                    this.dispose();
                    break;
                case "CLIENTE":
                    new TelaCliente().setVisible(true);
                    this.dispose();
                    break;
                case "PRODUTOR_APROVADO":
                    new TelaProdutor().setVisible(true);
                    this.dispose();
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Erro estrutural crítico ao tentar validar o utilizador.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        // AÇÃO DO TEXTO: Abre o Cadastro
        lblCriarConta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new TelaCadastro().setVisible(true);
            }
        });
    }

    private JLabel criarLabel(String txt, int x, int y, int w, int h) { JLabel l = new JLabel(txt); l.setBounds(x, y, w, h); return l; }
    private JTextField criarTextField(int x, int y, int w, int h, JFrame f) { JTextField t = new JTextField(); t.setBounds(x, y, w, h); f.add(t); return t; }
    
    // Método Main embutido para inicialização (Padrão Swing)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}
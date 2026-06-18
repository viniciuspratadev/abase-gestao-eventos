package visao;

import controle.CadastroController;
import javax.swing.*;
import java.awt.event.ItemEvent;

public class TelaCadastro extends JFrame {
    private JTextField txtEmail, txtCpf, txtCnpj, txtRazao, txtFantasia;
    private JPasswordField txtSenha;
    private JComboBox<String> cbTipo;
    
    // A tela agora conhece apenas o Controller, e nunca o DAO
    private CadastroController controller = new CadastroController();

    public TelaCadastro() {
        setTitle("ABase - Criar Nova Conta");
        setSize(450, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        int xL = 30, xT = 160, alt = 25;

        add(criarLabel("Tipo de Conta:", xL, 20, 120, alt));
        cbTipo = new JComboBox<>(new String[]{"Cliente", "Produtor"});
        cbTipo.setBounds(xT, 20, 200, alt);
        add(cbTipo);

        add(criarLabel("E-mail:", xL, 60, 100, alt));
        txtEmail = criarTextField(xT, 60, 200, alt, this);

        add(criarLabel("Senha (mín 6 carac.):", xL, 100, 130, alt));
        txtSenha = new JPasswordField();
        txtSenha.setBounds(xT, 100, 200, alt);
        add(txtSenha);

        JLabel lblCpf = criarLabel("CPF (11 dígitos):", xL, 140, 120, alt);
        add(lblCpf);
        txtCpf = criarTextField(xT, 140, 200, alt, this);

        JLabel lblCnpj = criarLabel("CNPJ (14 dígitos):", xL, 140, 120, alt);
        add(lblCnpj);
        txtCnpj = criarTextField(xT, 140, 200, alt, this);

        JLabel lblRazao = criarLabel("Razão Social:", xL, 180, 120, alt);
        add(lblRazao);
        txtRazao = criarTextField(xT, 180, 200, alt, this);

        JLabel lblFantasia = criarLabel("Nome Fantasia:", xL, 220, 120, alt);
        add(lblFantasia);
        txtFantasia = criarTextField(xT, 220, 200, alt, this);

        alternarCamposVisiveis(false, lblCnpj, lblRazao, lblFantasia, lblCpf);

        cbTipo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                boolean isProdutor = cbTipo.getSelectedItem().equals("Produtor");
                alternarCamposVisiveis(isProdutor, lblCnpj, lblRazao, lblFantasia, lblCpf);
            }
        });

        JButton btnSalvar = new JButton("Finalizar Cadastro");
        btnSalvar.setBounds(140, 300, 160, 35);
        add(btnSalvar);

        // AÇÃO DO BOTÃO: Apenas delega a tarefa para o Controller
        btnSalvar.addActionListener(e -> {
            String tipo = (String) cbTipo.getSelectedItem();
            String email = txtEmail.getText().trim();
            String senha = new String(txtSenha.getPassword());
            String cpf = txtCpf.getText().trim();
            String cnpj = txtCnpj.getText().trim();
            String razao = txtRazao.getText().trim();
            String fantasia = txtFantasia.getText().trim();

            String resposta = controller.registrarUsuario(tipo, email, senha, cpf, cnpj, razao, fantasia);

            if (resposta.equals("SUCESSO_PRODUTOR")) {
                JOptionPane.showMessageDialog(this, "Cadastro realizado! Aguarde a liberação do Administrador.");
                this.dispose();
            } else if (resposta.equals("SUCESSO_CLIENTE")) {
                JOptionPane.showMessageDialog(this, "Cadastro realizado! Faça o seu login.");
                this.dispose();
            } else {
                // Se não for sucesso, a resposta é a própria mensagem de erro gerada pelo Controller
                JOptionPane.showMessageDialog(this, resposta, "Validação", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void alternarCamposVisiveis(boolean isProd, JLabel lCnpj, JLabel lRazao, JLabel lFan, JLabel lCpf) {
        lCnpj.setVisible(isProd); txtCnpj.setVisible(isProd);
        lRazao.setVisible(isProd); txtRazao.setVisible(isProd);
        lFan.setVisible(isProd); txtFantasia.setVisible(isProd);
        lCpf.setVisible(!isProd); txtCpf.setVisible(!isProd);
    }

    private JLabel criarLabel(String txt, int x, int y, int w, int h) { JLabel l = new JLabel(txt); l.setBounds(x, y, w, h); return l; }
    private JTextField criarTextField(int x, int y, int w, int h, JFrame f) { JTextField t = new JTextField(); t.setBounds(x, y, w, h); f.add(t); return t; }
}
package visao;

import dao.CadastroDAO;
import javax.swing.*;
import java.awt.event.ItemEvent;

public class TelaCadastro extends JFrame {
    private JTextField txtEmail, txtCpf, txtCnpj, txtRazao, txtFantasia;
    private JPasswordField txtSenha;
    private JComboBox<String> cbTipo;

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

        // Aviso visual da regra no próprio campo
        add(criarLabel("Senha (mín 6 carac.):", xL, 100, 130, alt));
        txtSenha = new JPasswordField();
        txtSenha.setBounds(xT, 100, 200, alt);
        add(txtSenha);

        // Cliente
        JLabel lblCpf = criarLabel("CPF (11 dígitos):", xL, 140, 120, alt);
        add(lblCpf);
        txtCpf = criarTextField(xT, 140, 200, alt, this);

        // Produtor
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

        btnSalvar.addActionListener(e -> processarCadastro());
    }

    private void alternarCamposVisiveis(boolean isProd, JLabel lCnpj, JLabel lRazao, JLabel lFan, JLabel lCpf) {
        lCnpj.setVisible(isProd); txtCnpj.setVisible(isProd);
        lRazao.setVisible(isProd); txtRazao.setVisible(isProd);
        lFan.setVisible(isProd); txtFantasia.setVisible(isProd);
        lCpf.setVisible(!isProd); txtCpf.setVisible(!isProd);
    }

    // ==========================================
    // BARREIRA DE BLINDAGEM DE DADOS
    // ==========================================
    private boolean validarCampos() {
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword());
        boolean isProdutor = cbTipo.getSelectedItem().equals("Produtor");

        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Erro: Introduza um e-mail válido contendo '@' e domínio.", "Validação", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (senha.length() < 6) {
            JOptionPane.showMessageDialog(this, "Erro: A palavra-passe deve ter no mínimo 6 caracteres.", "Validação", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (isProdutor) {
            String cnpj = txtCnpj.getText().replaceAll("[^0-9]", ""); // Mantém apenas os números
            if (cnpj.length() != 14) {
                JOptionPane.showMessageDialog(this, "Erro: O CNPJ deve conter exatamente 14 dígitos numéricos.", "Validação", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (txtRazao.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Erro: A Razão Social é obrigatória para o Produtor.", "Validação", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (txtFantasia.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Erro: O Nome Fantasia é obrigatório.", "Validação", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else {
            String cpf = txtCpf.getText().replaceAll("[^0-9]", ""); // Mantém apenas os números
            if (cpf.length() != 11) {
                JOptionPane.showMessageDialog(this, "Erro: O CPF deve conter exatamente 11 dígitos numéricos.", "Validação", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private void processarCadastro() {
        if (!validarCampos()) return; // Interrompe o processo se a validação falhar

        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword());
        boolean isProdutor = cbTipo.getSelectedItem().equals("Produtor");
        CadastroDAO dao = new CadastroDAO();
        boolean sucesso;

        if (isProdutor) {
            // Envia para o banco o CNPJ já purificado de traços e pontos
            String cnpjLimpo = txtCnpj.getText().replaceAll("[^0-9]", "");
            sucesso = dao.cadastrarProdutor(email, senha, cnpjLimpo, txtRazao.getText().trim(), txtFantasia.getText().trim());
        } else {
            // Envia para o banco o CPF purificado
            String cpfLimpo = txtCpf.getText().replaceAll("[^0-9]", "");
            sucesso = dao.cadastrarCliente(email, senha, cpfLimpo);
        }

        if (sucesso) {
            String msg = isProdutor ? "Cadastro realizado! Aguarde a liberação do Administrador." : "Cadastro realizado! Faça o seu login.";
            JOptionPane.showMessageDialog(this, msg);
            this.dispose(); 
        } else {
            JOptionPane.showMessageDialog(this, "Falha na base de dados. O E-mail, CPF ou CNPJ introduzido já existe no sistema.", "Atenção", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel criarLabel(String txt, int x, int y, int w, int h) { JLabel l = new JLabel(txt); l.setBounds(x, y, w, h); return l; }
    private JTextField criarTextField(int x, int y, int w, int h, JFrame f) { JTextField t = new JTextField(); t.setBounds(x, y, w, h); f.add(t); return t; }
}
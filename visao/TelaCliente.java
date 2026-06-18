package visao;

import controle.ClienteController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.List;

public class TelaCliente extends JFrame {

    // A tela conhece unicamente o seu controlador
    private ClienteController controller = new ClienteController();

    // Aba 1: Explorar
    private JTable tabelaExplorar;
    private DefaultTableModel modeloExplorar;
    private JTextField txtBuscaEndereco;
    private int idEventoSelecionado = -1;
    private String urlSelecionada = "";

    // Aba 2: Meus Eventos
    private JTable tabelaMeusEventos;
    private DefaultTableModel modeloMeusEventos;
    private int idMeusEventosSelecionado = -1;
    private JTextField txtLocalCheckin;

    // Aba 3: Avaliações
    private JTable tabelaCheckins;
    private DefaultTableModel modeloCheckins;
    private JTextField txtIdCheckin, txtNota;
    private JTextArea txtComentario;

    public TelaCliente() {
        setTitle("ABase - Área do Cliente");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Explorar Eventos", criarAbaExplorar());
        abas.addTab("Meus Ingressos & Check-in", criarAbaMeusEventos());
        abas.addTab("Avaliações", criarAbaAvaliacoes());
        add(abas, BorderLayout.CENTER);
    }

    // ==================== ABA 1: EXPLORAR ====================
    private JPanel criarAbaExplorar() {
        JPanel painel = new JPanel(null);

        painel.add(criarLabel("Onde você está? (Ex: Jardim da Penha, Vitória):", 20, 20, 300, 25));
        txtBuscaEndereco = criarTextField(300, 20, 250, 25, painel, true);

        JButton btnBuscar = new JButton("Buscar Próximos");
        btnBuscar.setBounds(560, 20, 150, 25);
        painel.add(btnBuscar);

        modeloExplorar = new DefaultTableModel(new String[]{"ID", "Evento", "Data", "Hora", "Local", "Distância (km)", "URL"}, 0);
        tabelaExplorar = new JTable(modeloExplorar);
        JScrollPane scroll = new JScrollPane(tabelaExplorar);
        scroll.setBounds(20, 60, 840, 380);
        painel.add(scroll);

        JButton btnSite = new JButton("Comprar no Site Externo");
        btnSite.setBounds(20, 460, 200, 35);
        painel.add(btnSite);

        JButton btnRegistrarCompra = new JButton("Já Comprei (Salvar Ingresso)");
        btnRegistrarCompra.setBounds(230, 460, 230, 35);
        painel.add(btnRegistrarCompra);

        atualizarEventosExplorar(null); // Carrega padrão inicial

        tabelaExplorar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int l = tabelaExplorar.getSelectedRow();
                if (l >= 0) {
                    idEventoSelecionado = (int) modeloExplorar.getValueAt(l, 0);
                    urlSelecionada = modeloExplorar.getValueAt(l, 6).toString();
                }
            }
        });

        btnBuscar.addActionListener(e -> atualizarEventosExplorar(txtBuscaEndereco.getText()));

        btnSite.addActionListener(e -> {
            if (urlSelecionada == null || urlSelecionada.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um evento na tabela primeiro.");
                return;
            }
            try { Desktop.getDesktop().browse(new URI(urlSelecionada)); } 
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "Erro ao abrir o navegador."); }
        });

        btnRegistrarCompra.addActionListener(e -> {
            String resposta = controller.registrarCompra(idEventoSelecionado);
            if (resposta.equals("SUCESSO")) {
                JOptionPane.showMessageDialog(this, "Ingresso registrado na sua conta!");
                idEventoSelecionado = -1;
                atualizarEventosExplorar(null);
                atualizarMeusEventos();
            } else {
                JOptionPane.showMessageDialog(this, resposta, "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        return painel;
    }

    private void atualizarEventosExplorar(String endereco) {
        modeloExplorar.setRowCount(0);
        // O controlador faz todo o cálculo pesado e devolve a lista pronta
        List<Object[]> eventos = controller.explorarEventos(endereco);
        for (Object[] ev : eventos) {
            modeloExplorar.addRow(new Object[]{ev[0], ev[1], ev[2], ev[3], ev[4], ev[5], ev[7]});
        }
    }

    // ==================== ABA 2: MEUS EVENTOS & CHECKIN ====================
    private JPanel criarAbaMeusEventos() {
        JPanel painel = new JPanel(null);

        painel.add(criarLabel("Local atual para Check-in (Endereço):", 20, 20, 250, 25));
        txtLocalCheckin = criarTextField(250, 20, 300, 25, painel, true);

        modeloMeusEventos = new DefaultTableModel(new String[]{"ID Evento", "Evento", "Data", "Local"}, 0);
        tabelaMeusEventos = new JTable(modeloMeusEventos);
        JScrollPane scroll = new JScrollPane(tabelaMeusEventos);
        scroll.setBounds(20, 60, 840, 350);
        painel.add(scroll);

        JButton btnCheckin = new JButton("Confirmar Presença (Check-in)");
        btnCheckin.setBounds(20, 430, 250, 35);
        painel.add(btnCheckin);

        atualizarMeusEventos();

        tabelaMeusEventos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int l = tabelaMeusEventos.getSelectedRow();
                if (l >= 0) idMeusEventosSelecionado = (int) modeloMeusEventos.getValueAt(l, 0);
            }
        });

        btnCheckin.addActionListener(e -> {
            String resposta = controller.realizarCheckin(idMeusEventosSelecionado, txtLocalCheckin.getText());
            if (resposta.equals("SUCESSO")) {
                JOptionPane.showMessageDialog(this, "Check-in realizado com sucesso!");
                idMeusEventosSelecionado = -1;
                txtLocalCheckin.setText("");
                atualizarMeusEventos();
                atualizarAvaliacoes();
            } else {
                JOptionPane.showMessageDialog(this, resposta, "Atenção", JOptionPane.ERROR_MESSAGE);
            }
        });

        return painel;
    }

    private void atualizarMeusEventos() {
        modeloMeusEventos.setRowCount(0);
        for (Object[] linha : controller.listarMeusEventos()) modeloMeusEventos.addRow(linha);
    }

    // ==================== ABA 3: AVALIAÇÕES ====================
    private JPanel criarAbaAvaliacoes() {
        JPanel painel = new JPanel(null);
        int alt = 25;

        painel.add(criarLabel("ID Check-in:", 20, 20, 80, alt));
        txtIdCheckin = criarTextField(100, 20, 50, alt, painel, false);

        painel.add(criarLabel("Nota (1 a 5):", 170, 20, 80, alt));
        txtNota = criarTextField(250, 20, 50, alt, painel, true);

        painel.add(criarLabel("Comentário:", 20, 60, 80, alt));
        txtComentario = new JTextArea();
        txtComentario.setLineWrap(true);
        JScrollPane scrollComent = new JScrollPane(txtComentario);
        scrollComent.setBounds(100, 60, 760, 60);
        painel.add(scrollComent);

        JButton btnAvaliar = new JButton("Enviar Avaliação");
        btnAvaliar.setBounds(20, 130, 150, 30);
        painel.add(btnAvaliar);

        modeloCheckins = new DefaultTableModel(new String[]{"ID Check-in", "Evento", "Data do Check-in"}, 0);
        tabelaCheckins = new JTable(modeloCheckins);
        JScrollPane scroll = new JScrollPane(tabelaCheckins);
        scroll.setBounds(20, 180, 840, 300);
        painel.add(scroll);

        atualizarAvaliacoes();

        tabelaCheckins.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int l = tabelaCheckins.getSelectedRow();
                if (l >= 0) txtIdCheckin.setText(modeloCheckins.getValueAt(l, 0).toString());
            }
        });

        btnAvaliar.addActionListener(e -> {
            String resposta = controller.enviarAvaliacao(txtIdCheckin.getText(), txtNota.getText(), txtComentario.getText());
            if (resposta.equals("SUCESSO")) {
                JOptionPane.showMessageDialog(this, "Avaliação registrada!");
                txtIdCheckin.setText(""); txtNota.setText(""); txtComentario.setText("");
                atualizarAvaliacoes();
            } else {
                JOptionPane.showMessageDialog(this, resposta, "Validação", JOptionPane.ERROR_MESSAGE);
            }
        });

        return painel;
    }

    private void atualizarAvaliacoes() {
        if (modeloCheckins == null) return;
        modeloCheckins.setRowCount(0);
        for (Object[] linha : controller.listarAvaliacoesPendentes()) modeloCheckins.addRow(linha);
    }

    // Utilitários Visuais
    private JLabel criarLabel(String txt, int x, int y, int w, int h) { JLabel l = new JLabel(txt); l.setBounds(x, y, w, h); return l; }
    private JTextField criarTextField(int x, int y, int w, int h, JPanel p, boolean editavel) { JTextField t = new JTextField(); t.setBounds(x, y, w, h); t.setEditable(editavel); p.add(t); return t; }
}
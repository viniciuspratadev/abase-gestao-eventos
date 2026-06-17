package visao;

import dao.ClienteDAO;
import modelo.Avaliacao;
import util.GeolocalizacaoAPI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Comparator;
import java.util.List;

public class TelaCliente extends JFrame {

    private ClienteDAO dao = new ClienteDAO();

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

        atualizarEventosExplorar(null, null);

        tabelaExplorar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int l = tabelaExplorar.getSelectedRow();
                if (l >= 0) {
                    idEventoSelecionado = (int) modeloExplorar.getValueAt(l, 0);
                    urlSelecionada = modeloExplorar.getValueAt(l, 6).toString(); // A coluna 6 agora é a URL
                }
            }
        });
        btnSite.addActionListener(e -> {
            if (urlSelecionada == null || urlSelecionada.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione um evento na tabela primeiro.");
                return;
            }
            try {
                // Chama o navegador padrão do Windows/Linux/Mac
                Desktop.getDesktop().browse(new URI(urlSelecionada));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Erro ao abrir o navegador. Verifique se a URL é válida.");
            }
        });

        btnBuscar.addActionListener(e -> {
            String endereco = txtBuscaEndereco.getText();
            if (endereco.isEmpty()) return;
            
            BigDecimal[] coords = GeolocalizacaoAPI.buscarCoordenadas(endereco);
            if (coords != null) {
                atualizarEventosExplorar(coords[0], coords[1]);
            } else {
                JOptionPane.showMessageDialog(null, "Não foi possível localizar este endereço.");
            }
        });

        btnRegistrarCompra.addActionListener(e -> {
            if (idEventoSelecionado != -1 && dao.comprarIngresso(idEventoSelecionado)) {
                JOptionPane.showMessageDialog(null, "Ingresso registrado na sua conta!");
                idEventoSelecionado = -1;
                atualizarEventosExplorar(null, null);
                atualizarMeusEventos();
            } else {
                JOptionPane.showMessageDialog(null, "Selecione um evento primeiro.");
            }
        });

        return painel;
    }

    private void atualizarEventosExplorar(BigDecimal latCliente, BigDecimal lonCliente) {
        modeloExplorar.setRowCount(0);
        List<Object[]> eventos = dao.listarEventosExplorar();

        if (latCliente != null && lonCliente != null) {
            // Calcula a distância em KM usando a Fórmula de Haversine
            for (Object[] ev : eventos) {
                BigDecimal latEv = (BigDecimal) ev[5];
                BigDecimal lonEv = (BigDecimal) ev[6];
                double dist = calcularDistancia(latCliente.doubleValue(), lonCliente.doubleValue(), latEv.doubleValue(), lonEv.doubleValue());
                ev[5] = String.format("%.2f", dist); // Substitui a latitude pela distância formatada
            }
            // Ordena a lista do mais perto pro mais longe
            eventos.sort(Comparator.comparingDouble(o -> Double.parseDouble(((String) o[5]).replace(",", "."))));
        } else {
            for (Object[] ev : eventos) ev[5] = "Desconhecida";
        }

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
            if (idMeusEventosSelecionado == -1) {
                JOptionPane.showMessageDialog(null, "Selecione um ingresso.");
                return;
            }
            String end = txtLocalCheckin.getText();
            if (end.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Informe o endereço atual para validar a geolocalização.");
                return;
            }

            BigDecimal[] coords = GeolocalizacaoAPI.buscarCoordenadas(end);
            if (coords != null) {
                if (dao.fazerCheckin(idMeusEventosSelecionado, coords[0], coords[1])) {
                    JOptionPane.showMessageDialog(null, "Check-in realizado com sucesso!");
                    idMeusEventosSelecionado = -1;
                    txtLocalCheckin.setText("");
                    atualizarMeusEventos();
                    atualizarAvaliacoes();
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao processar check-in.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Endereço atual não localizado pelo sistema de GPS.");
            }
        });

        return painel;
    }

    private void atualizarMeusEventos() {
        modeloMeusEventos.setRowCount(0);
        for (Object[] linha : dao.listarMeusEventos()) modeloMeusEventos.addRow(linha);
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
            try {
                Avaliacao aval = new Avaliacao();
                aval.setIdCheckin(Integer.parseInt(txtIdCheckin.getText()));
                aval.setNota(Integer.parseInt(txtNota.getText()));
                aval.setComentario(txtComentario.getText());
                
                if (dao.avaliar(aval)) {
                    JOptionPane.showMessageDialog(null, "Avaliação registrada!");
                    txtIdCheckin.setText(""); txtNota.setText(""); txtComentario.setText("");
                    atualizarAvaliacoes();
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(null, "Dados inválidos."); }
        });

        return painel;
    }

    private void atualizarAvaliacoes() {
        if (modeloCheckins == null) return;
        modeloCheckins.setRowCount(0);
        for (Object[] linha : dao.listarCheckinsPendentesDeAvaliacao()) modeloCheckins.addRow(linha);
    }

    // Utilitários Visuais e Matemáticos
    private JLabel criarLabel(String txt, int x, int y, int w, int h) { JLabel l = new JLabel(txt); l.setBounds(x, y, w, h); return l; }
    private JTextField criarTextField(int x, int y, int w, int h, JPanel p, boolean editavel) { JTextField t = new JTextField(); t.setBounds(x, y, w, h); t.setEditable(editavel); p.add(t); return t; }

    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        int R = 6371; // Raio da terra em KM
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    }
}
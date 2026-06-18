package visao;

import controle.ProdutorController;
import modelo.Evento;
import modelo.LocalEvento;
import modelo.SiteVendas;
import modelo.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class TelaProdutor extends JFrame {

    // A tela comunica-se unicamente com o Controlador
    private ProdutorController controller = new ProdutorController();

    private JTextField txtIdEvento, txtNome, txtUrl, txtData, txtHora;
    private JComboBox<LocalEvento> cbLocal;
    private JComboBox<SiteVendas> cbSite;
    private JList<Categoria> listCategorias;
    private DefaultListModel<Categoria> modeloCategorias;
    private JTable tabelaEventos;
    private DefaultTableModel modeloTabela;

    public TelaProdutor() {
        setTitle("ABase - Painel do Produtor");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        int alt = 25;

        add(criarLabel("ID Evento:", 20, 20, 70, alt));
        txtIdEvento = criarTextField(90, 20, 50, alt, false);

        add(criarLabel("Nome do Evento:", 160, 20, 110, alt));
        txtNome = criarTextField(270, 20, 250, alt, true);

        add(criarLabel("URL Site:", 540, 20, 60, alt));
        txtUrl = criarTextField(600, 20, 210, alt, true);

        add(criarLabel("Data (AAAA-MM-DD):", 20, 60, 130, alt));
        txtData = criarTextField(150, 60, 100, alt, true);

        add(criarLabel("Hora (HH:MM):", 270, 60, 90, alt));
        txtHora = criarTextField(360, 60, 80, alt, true);

        add(criarLabel("Local:", 20, 100, 50, alt));
        cbLocal = new JComboBox<>();
        cbLocal.setBounds(70, 100, 250, alt);
        add(cbLocal);

        add(criarLabel("Site de Vendas:", 340, 100, 100, alt));
        cbSite = new JComboBox<>();
        cbSite.setBounds(440, 100, 250, alt);
        add(cbSite);

        add(criarLabel("Categorias (Segure Ctrl):", 20, 140, 180, alt));
        modeloCategorias = new DefaultListModel<>();
        listCategorias = new JList<>(modeloCategorias);
        listCategorias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollCat = new JScrollPane(listCategorias);
        scrollCat.setBounds(180, 140, 510, 60);
        add(scrollCat);

        // Inicialização de Dados via Controlador
        carregarCombosEListas();

        JButton btnSalvar = new JButton("Salvar Novo");
        btnSalvar.setBounds(20, 220, 120, 30);
        add(btnSalvar);

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.setBounds(150, 220, 100, 30);
        add(btnAtualizar);

        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.setBounds(260, 220, 100, 30);
        add(btnExcluir);

        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.setBounds(370, 220, 100, 30);
        add(btnLimpar);

        modeloTabela = new DefaultTableModel(new String[]{"ID", "Nome", "Data", "Hora", "ID Local", "ID Site"}, 0);
        tabelaEventos = new JTable(modeloTabela);
        JScrollPane scrollTabela = new JScrollPane(tabelaEventos);
        scrollTabela.setBounds(20, 270, 790, 210);
        add(scrollTabela);

        atualizarTabela();

        // Ações da Interface (Mero repasse de dados para o Controller)
        btnLimpar.addActionListener(e -> limparCampos());

        btnSalvar.addActionListener(e -> executarAcao("SALVAR"));
        btnAtualizar.addActionListener(e -> executarAcao("ATUALIZAR"));
        
        btnExcluir.addActionListener(e -> {
            if (txtIdEvento.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um evento para excluir.");
                return;
            }
            int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja apagar este evento permanentemente?", "Aviso", JOptionPane.YES_NO_OPTION);
            if (confirmacao == JOptionPane.YES_OPTION) executarAcao("EXCLUIR");
        });

        tabelaEventos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int linha = tabelaEventos.getSelectedRow();
                if (linha >= 0) {
                    txtIdEvento.setText(modeloTabela.getValueAt(linha, 0).toString());
                    txtNome.setText(modeloTabela.getValueAt(linha, 1).toString());
                    txtData.setText(modeloTabela.getValueAt(linha, 2).toString());
                    txtHora.setText(modeloTabela.getValueAt(linha, 3).toString());
                }
            }
        });
    }

    // Método centralizador de chamadas para manter a UI limpa
    private void executarAcao(String acao) {
        int idLocal = cbLocal.getSelectedItem() != null ? ((LocalEvento) cbLocal.getSelectedItem()).getIdLocal() : 0;
        int idSite = cbSite.getSelectedItem() != null ? ((SiteVendas) cbSite.getSelectedItem()).getIdSite() : 0;
        
        List<Integer> idsCats = new ArrayList<>();
        for (Categoria c : listCategorias.getSelectedValuesList()) {
            idsCats.add(c.getIdCateg());
        }

        String resposta = controller.processarEvento(
            acao, txtIdEvento.getText(), txtNome.getText(), txtUrl.getText(), 
            txtData.getText(), txtHora.getText(), idLocal, idSite, idsCats
        );

        if (resposta.startsWith("ERRO")) {
            JOptionPane.showMessageDialog(this, resposta, "Aviso", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Operação realizada com sucesso!");
            limparCampos();
            atualizarTabela();
        }
    }

    private void carregarCombosEListas() {
        for (LocalEvento l : controller.listarLocais()) cbLocal.addItem(l);
        for (SiteVendas s : controller.listarSites()) cbSite.addItem(s);
        for (Categoria c : controller.listarCategorias()) modeloCategorias.addElement(c);
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        for (Evento ev : controller.listarEventosProdutor()) {
            modeloTabela.addRow(new Object[]{ ev.getIdEvento(), ev.getNome(), ev.getDataEvento(), ev.getHoraEvento(), ev.getIdLocal(), ev.getIdSite() });
        }
    }

    private void limparCampos() {
        txtIdEvento.setText(""); txtNome.setText(""); txtUrl.setText(""); txtData.setText(""); txtHora.setText("");
        if (cbLocal.getItemCount() > 0) cbLocal.setSelectedIndex(0);
        if (cbSite.getItemCount() > 0) cbSite.setSelectedIndex(0);
        listCategorias.clearSelection();
    }

    private JLabel criarLabel(String txt, int x, int y, int w, int h) { JLabel l = new JLabel(txt); l.setBounds(x, y, w, h); return l; }
    private JTextField criarTextField(int x, int y, int w, int h, boolean editavel) { JTextField t = new JTextField(); t.setBounds(x, y, w, h); t.setEditable(editavel); add(t); return t; }
}
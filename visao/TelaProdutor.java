package visao;

import dao.EventoDAO;
import dao.LocalEventoDAO;
import dao.SiteVendasDAO;
import dao.CategoriaDAO;
import modelo.Evento;
import modelo.LocalEvento;
import modelo.SiteVendas;
import modelo.Categoria;
import util.SessaoAtual;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class TelaProdutor extends JFrame {

    private EventoDAO eventoDAO = new EventoDAO();
    private LocalEventoDAO localDAO = new LocalEventoDAO();
    private SiteVendasDAO siteDAO = new SiteVendasDAO();
    private CategoriaDAO categoriaDAO = new CategoriaDAO();

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

        // Linha 1
        add(criarLabel("ID Evento:", 20, 20, 70, alt));
        txtIdEvento = criarTextField(90, 20, 50, alt, false);

        add(criarLabel("Nome do Evento:", 160, 20, 110, alt));
        txtNome = criarTextField(270, 20, 250, alt, true);

        add(criarLabel("URL Site:", 540, 20, 60, alt));
        txtUrl = criarTextField(600, 20, 210, alt, true);

        // Linha 2
        add(criarLabel("Data (AAAA-MM-DD):", 20, 60, 130, alt));
        txtData = criarTextField(150, 60, 100, alt, true);

        add(criarLabel("Hora (HH:MM):", 270, 60, 90, alt));
        txtHora = criarTextField(360, 60, 80, alt, true);

        // Linha 3
        add(criarLabel("Local:", 20, 100, 50, alt));
        cbLocal = new JComboBox<>();
        cbLocal.setBounds(70, 100, 250, alt);
        add(cbLocal);

        add(criarLabel("Site de Vendas:", 340, 100, 100, alt));
        cbSite = new JComboBox<>();
        cbSite.setBounds(440, 100, 250, alt);
        add(cbSite);

        // Linha 4 
        add(criarLabel("Categorias (Segure Ctrl):", 20, 140, 180, alt));
        modeloCategorias = new DefaultListModel<>();
        listCategorias = new JList<>(modeloCategorias);
        listCategorias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollCat = new JScrollPane(listCategorias);
        scrollCat.setBounds(180, 140, 510, 60);
        add(scrollCat);

        carregarLocais();
        carregarSites();
        carregarCategorias();

        // Botões
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

        // Tabela
        modeloTabela = new DefaultTableModel(new String[]{"ID", "Nome", "Data", "Hora", "ID Local", "ID Site"}, 0);
        tabelaEventos = new JTable(modeloTabela);
        JScrollPane scrollTabela = new JScrollPane(tabelaEventos);
        scrollTabela.setBounds(20, 270, 790, 210);
        add(scrollTabela);

        atualizarTabela();

        // Ações
        btnLimpar.addActionListener(e -> limparCampos());

        btnSalvar.addActionListener(e -> {
            if (!validarCampos()) return; // A barreira de blindagem
            try {
                Evento ev = montarEvento();
                if (eventoDAO.inserir(ev)) {
                    limparCampos();
                    atualizarTabela();
                    JOptionPane.showMessageDialog(null, "Evento e categorias registados com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(null, "Erro na transação. Verifique se os dados são válidos no banco.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Erro crítico ao guardar: " + ex.getMessage());
            }
        });

        btnAtualizar.addActionListener(e -> {
            if (txtIdEvento.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um evento na tabela para atualizar.");
                return;
            }
            if (!validarCampos()) return; // A barreira de blindagem
            try {
                Evento ev = montarEvento();
                ev.setIdEvento(Integer.parseInt(txtIdEvento.getText()));
                if (eventoDAO.atualizar(ev)) {
                    limparCampos();
                    atualizarTabela();
                    JOptionPane.showMessageDialog(null, "Evento atualizado com sucesso!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Erro crítico ao atualizar: " + ex.getMessage());
            }
        });

        btnExcluir.addActionListener(e -> {
            if (txtIdEvento.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um evento para excluir.");
                return;
            }
            int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja apagar este evento permanentemente?", "Aviso", JOptionPane.YES_NO_OPTION);
            if (confirmacao == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(txtIdEvento.getText());
                if (eventoDAO.excluir(id, SessaoAtual.idUsuarioLogado)) {
                    limparCampos();
                    atualizarTabela();
                }
            }
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

    // ==========================================
    // MÉTODO DE BLINDAGEM DE DADOS
    // ==========================================
    private boolean validarCampos() {
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Erro: O Nome do evento não pode estar vazio.", "Validação", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtUrl.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Erro: A URL do site é obrigatória.", "Validação", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Tenta converter a data para garantir que o formato é suportado pelo PostgreSQL
        try {
            Date.valueOf(txtData.getText().trim());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Erro: Formato de Data inválido.\nObrigatório o uso do formato AAAA-MM-DD (ex: 2026-10-30).", "Validação", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Tenta converter a hora
        try {
            String horaStr = txtHora.getText().trim();
            if (horaStr.length() == 5) horaStr += ":00";
            Time.valueOf(horaStr);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Erro: Formato de Hora inválido.\nObrigatório o uso do formato HH:MM (ex: 23:30).", "Validação", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (listCategorias.getSelectedValuesList().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Erro: Selecione no mínimo uma categoria para o evento.", "Validação", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void carregarLocais() {
        cbLocal.removeAllItems();
        for (LocalEvento l : localDAO.listarTodos()) cbLocal.addItem(l);
    }

    private void carregarSites() {
        cbSite.removeAllItems();
        for (SiteVendas s : siteDAO.listarTodos()) cbSite.addItem(s);
    }

    private void carregarCategorias() {
        modeloCategorias.clear();
        for (Categoria c : categoriaDAO.listarTodas()) modeloCategorias.addElement(c);
    }

    private Evento montarEvento() {
        Evento ev = new Evento();
        ev.setNome(txtNome.getText().trim());
        ev.setUrlEvento(txtUrl.getText().trim());
        ev.setDataEvento(Date.valueOf(txtData.getText().trim()));
        
        String horaStr = txtHora.getText().trim();
        if (horaStr.length() == 5) horaStr += ":00"; 
        ev.setHoraEvento(Time.valueOf(horaStr));

        if (cbLocal.getSelectedItem() != null) ev.setIdLocal(((LocalEvento) cbLocal.getSelectedItem()).getIdLocal());
        if (cbSite.getSelectedItem() != null) ev.setIdSite(((SiteVendas) cbSite.getSelectedItem()).getIdSite());
        ev.setIdProdutor(SessaoAtual.idUsuarioLogado);

        List<Integer> idsCategorias = new ArrayList<>();
        for (Categoria c : listCategorias.getSelectedValuesList()) {
            idsCategorias.add(c.getIdCateg());
        }
        ev.setIdsCategorias(idsCategorias);

        return ev;
    }

    private void limparCampos() {
        txtIdEvento.setText(""); txtNome.setText(""); txtUrl.setText(""); txtData.setText(""); txtHora.setText("");
        if (cbLocal.getItemCount() > 0) cbLocal.setSelectedIndex(0);
        if (cbSite.getItemCount() > 0) cbSite.setSelectedIndex(0);
        listCategorias.clearSelection();
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        for (Evento ev : eventoDAO.listarPorProdutor(SessaoAtual.idUsuarioLogado)) {
            modeloTabela.addRow(new Object[]{ ev.getIdEvento(), ev.getNome(), ev.getDataEvento(), ev.getHoraEvento(), ev.getIdLocal(), ev.getIdSite() });
        }
    }

    private JLabel criarLabel(String txt, int x, int y, int w, int h) { JLabel l = new JLabel(txt); l.setBounds(x, y, w, h); return l; }
    private JTextField criarTextField(int x, int y, int w, int h, boolean editavel) { JTextField t = new JTextField(); t.setBounds(x, y, w, h); t.setEditable(editavel); add(t); return t; }
}
package visao;

import dao.UsuarioDAO;
import dao.LocalEventoDAO;
import dao.SiteVendasDAO;
import dao.CategoriaDAO;
import modelo.LocalEvento;
import modelo.SiteVendas;
import modelo.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;

public class TelaAdmin extends JFrame {

    // DAOs
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private LocalEventoDAO localDAO = new LocalEventoDAO();
    private SiteVendasDAO siteDAO = new SiteVendasDAO();
    private CategoriaDAO categoriaDAO = new CategoriaDAO();

    // Tabelas e Modelos
    private JTable tabelaPendentes, tabelaLocais, tabelaSites, tabelaCategorias;
    private DefaultTableModel modeloTabelaLocais, modeloTabelaSites, modeloTabelaCategorias;

    // Campos de Entrada
    private JTextField txtIdLocal, txtNomeLocal, txtEndereco, txtLat, txtLon;
    private JTextField txtIdSite, txtNomeSite, txtUrlBase;
    private JTextField txtIdCateg, txtNomeCateg;

    public TelaAdmin() {
        setTitle("ABase - Dashboard Administrativo");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Aprovar Produtores", criarAbaProdutores());
        abas.addTab("Gerenciar Locais", criarAbaLocais());
        abas.addTab("Gerenciar Sites", criarAbaSites());
        abas.addTab("Gerenciar Categorias", criarAbaCategorias());

        add(abas, BorderLayout.CENTER);
    }

    // ==================== ABA 1: PRODUTORES ====================
    private JPanel criarAbaProdutores() {
        JPanel painel = new JPanel(null);
        tabelaPendentes = new JTable();
        JScrollPane scrollPane = new JScrollPane(tabelaPendentes);
        scrollPane.setBounds(20, 20, 780, 350);
        painel.add(scrollPane);

        JButton btnAprovar = new JButton("Aprovar Selecionado");
        btnAprovar.setBounds(20, 390, 180, 30);
        painel.add(btnAprovar);

        atualizarTabelaProdutores();

        btnAprovar.addActionListener(e -> {
            int linha = tabelaPendentes.getSelectedRow();
            if (linha >= 0) {
                int idUsuario = (int) tabelaPendentes.getValueAt(linha, 0);
                if (usuarioDAO.aprovarUsuario(idUsuario)) {
                    JOptionPane.showMessageDialog(null, "Produtor aprovado!");
                    atualizarTabelaProdutores();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Selecione um produtor.");
            }
        });
        return painel;
    }

    private void atualizarTabelaProdutores() {
        tabelaPendentes.setModel(usuarioDAO.listarProdutoresPendentes());
    }

    // ==================== ABA 2: LOCAIS ====================
    private JPanel criarAbaLocais() {
        JPanel painel = new JPanel(null);
        int alt = 25;
        
        painel.add(criarLabel("ID:", 20, 20, 30, alt));
        txtIdLocal = criarTextField(50, 20, 50, alt, painel, false);

        painel.add(criarLabel("Nome:", 120, 20, 50, alt));
        txtNomeLocal = criarTextField(170, 20, 150, alt, painel, true);

        painel.add(criarLabel("Endereço:", 340, 20, 70, alt));
        txtEndereco = criarTextField(410, 20, 340, alt, painel, true);

        painel.add(criarLabel("Latitude:", 20, 60, 60, alt));
        txtLat = criarTextField(80, 60, 120, alt, painel, true);

        painel.add(criarLabel("Longitude:", 220, 60, 70, alt));
        txtLon = criarTextField(290, 60, 120, alt, painel, true);

        JButton btnSalvar = criarBotao("Salvar Novo", 20, 100, 120, 30, painel);
        JButton btnAtualizar = criarBotao("Atualizar", 150, 100, 100, 30, painel);
        JButton btnExcluir = criarBotao("Excluir", 260, 100, 100, 30, painel);
        JButton btnLimpar = criarBotao("Limpar", 370, 100, 100, 30, painel);

        modeloTabelaLocais = new DefaultTableModel(new String[]{"ID", "Nome", "Endereço", "Latitude", "Longitude"}, 0);
        tabelaLocais = new JTable(modeloTabelaLocais);
        JScrollPane scrollPane = new JScrollPane(tabelaLocais);
        scrollPane.setBounds(20, 150, 780, 250);
        painel.add(scrollPane);

        atualizarTabelaLocais();

        tabelaLocais.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int l = tabelaLocais.getSelectedRow();
                if (l >= 0) {
                    txtIdLocal.setText(modeloTabelaLocais.getValueAt(l, 0).toString());
                    txtNomeLocal.setText(modeloTabelaLocais.getValueAt(l, 1).toString());
                    txtEndereco.setText(modeloTabelaLocais.getValueAt(l, 2).toString());
                    txtLat.setText(modeloTabelaLocais.getValueAt(l, 3).toString());
                    txtLon.setText(modeloTabelaLocais.getValueAt(l, 4).toString());
                }
            }
        });

        btnLimpar.addActionListener(e -> limparLocal());
        btnSalvar.addActionListener(e -> {
            try {
                if (localDAO.inserir(montarLocal())) { limparLocal(); atualizarTabelaLocais(); }
            } catch (Exception ex) { JOptionPane.showMessageDialog(null, "Erro nos dados."); }
        });
        btnAtualizar.addActionListener(e -> {
            try {
                LocalEvento loc = montarLocal(); loc.setIdLocal(Integer.parseInt(txtIdLocal.getText()));
                if (localDAO.atualizar(loc)) { limparLocal(); atualizarTabelaLocais(); }
            } catch (Exception ex) {}
        });
        btnExcluir.addActionListener(e -> {
            if (!txtIdLocal.getText().isEmpty() && localDAO.excluir(Integer.parseInt(txtIdLocal.getText()))) {
                limparLocal(); atualizarTabelaLocais();
            }
        });
        return painel;
    }

    private LocalEvento montarLocal() {
        LocalEvento l = new LocalEvento();
        l.setNome(txtNomeLocal.getText()); l.setEndereco(txtEndereco.getText());
        l.setLatitude(new BigDecimal(txtLat.getText().replace(",", ".")));
        l.setLongitude(new BigDecimal(txtLon.getText().replace(",", ".")));
        return l;
    }

    private void limparLocal() { txtIdLocal.setText(""); txtNomeLocal.setText(""); txtEndereco.setText(""); txtLat.setText(""); txtLon.setText(""); }
    private void atualizarTabelaLocais() {
        modeloTabelaLocais.setRowCount(0);
        for (LocalEvento l : localDAO.listarTodos()) modeloTabelaLocais.addRow(new Object[]{l.getIdLocal(), l.getNome(), l.getEndereco(), l.getLatitude(), l.getLongitude()});
    }

    // ==================== ABA 3: SITES ====================
    private JPanel criarAbaSites() {
        JPanel painel = new JPanel(null);
        int alt = 25;
        
        painel.add(criarLabel("ID:", 20, 20, 30, alt));
        txtIdSite = criarTextField(50, 20, 50, alt, painel, false);

        painel.add(criarLabel("Nome:", 120, 20, 50, alt));
        txtNomeSite = criarTextField(170, 20, 150, alt, painel, true);

        painel.add(criarLabel("URL Base:", 340, 20, 70, alt));
        txtUrlBase = criarTextField(410, 20, 340, alt, painel, true);

        JButton btnSalvar = criarBotao("Salvar Novo", 20, 60, 120, 30, painel);
        JButton btnAtualizar = criarBotao("Atualizar", 150, 60, 100, 30, painel);
        JButton btnExcluir = criarBotao("Excluir", 260, 60, 100, 30, painel);
        JButton btnLimpar = criarBotao("Limpar", 370, 60, 100, 30, painel);

        modeloTabelaSites = new DefaultTableModel(new String[]{"ID", "Nome do Site", "URL Base"}, 0);
        tabelaSites = new JTable(modeloTabelaSites);
        JScrollPane scrollPane = new JScrollPane(tabelaSites);
        scrollPane.setBounds(20, 110, 780, 290);
        painel.add(scrollPane);

        atualizarTabelaSites();

        tabelaSites.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int l = tabelaSites.getSelectedRow();
                if (l >= 0) {
                    txtIdSite.setText(modeloTabelaSites.getValueAt(l, 0).toString());
                    txtNomeSite.setText(modeloTabelaSites.getValueAt(l, 1).toString());
                    txtUrlBase.setText(modeloTabelaSites.getValueAt(l, 2).toString());
                }
            }
        });

        btnLimpar.addActionListener(e -> limparSite());
        btnSalvar.addActionListener(e -> {
            SiteVendas s = new SiteVendas(); s.setNome(txtNomeSite.getText()); s.setUrlBase(txtUrlBase.getText());
            if (siteDAO.inserir(s)) { limparSite(); atualizarTabelaSites(); }
        });
        btnAtualizar.addActionListener(e -> {
            if (txtIdSite.getText().isEmpty()) return;
            SiteVendas s = new SiteVendas(); s.setIdSite(Integer.parseInt(txtIdSite.getText())); s.setNome(txtNomeSite.getText()); s.setUrlBase(txtUrlBase.getText());
            if (siteDAO.atualizar(s)) { limparSite(); atualizarTabelaSites(); }
        });
        btnExcluir.addActionListener(e -> {
            if (!txtIdSite.getText().isEmpty() && siteDAO.excluir(Integer.parseInt(txtIdSite.getText()))) { limparSite(); atualizarTabelaSites(); }
        });
        return painel;
    }

    private void limparSite() { txtIdSite.setText(""); txtNomeSite.setText(""); txtUrlBase.setText(""); }
    private void atualizarTabelaSites() {
        modeloTabelaSites.setRowCount(0);
        for (SiteVendas s : siteDAO.listarTodos()) modeloTabelaSites.addRow(new Object[]{s.getIdSite(), s.getNome(), s.getUrlBase()});
    }

    // ==================== ABA 4: CATEGORIAS ====================
    private JPanel criarAbaCategorias() {
        JPanel painel = new JPanel(null);
        int alt = 25;
        
        painel.add(criarLabel("ID:", 20, 20, 30, alt));
        txtIdCateg = criarTextField(50, 20, 50, alt, painel, false);

        painel.add(criarLabel("Nome Categoria:", 120, 20, 100, alt));
        txtNomeCateg = criarTextField(230, 20, 200, alt, painel, true);

        JButton btnSalvar = criarBotao("Salvar Novo", 20, 60, 120, 30, painel);
        JButton btnAtualizar = criarBotao("Atualizar", 150, 60, 100, 30, painel);
        JButton btnExcluir = criarBotao("Excluir", 260, 60, 100, 30, painel);
        JButton btnLimpar = criarBotao("Limpar", 370, 60, 100, 30, painel);

        modeloTabelaCategorias = new DefaultTableModel(new String[]{"ID", "Categoria"}, 0);
        tabelaCategorias = new JTable(modeloTabelaCategorias);
        JScrollPane scrollPane = new JScrollPane(tabelaCategorias);
        scrollPane.setBounds(20, 110, 780, 290);
        painel.add(scrollPane);

        atualizarTabelaCategorias();

        tabelaCategorias.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int l = tabelaCategorias.getSelectedRow();
                if (l >= 0) {
                    txtIdCateg.setText(modeloTabelaCategorias.getValueAt(l, 0).toString());
                    txtNomeCateg.setText(modeloTabelaCategorias.getValueAt(l, 1).toString());
                }
            }
        });

        btnLimpar.addActionListener(e -> limparCateg());
        btnSalvar.addActionListener(e -> {
            Categoria c = new Categoria(); c.setNomeCategoria(txtNomeCateg.getText());
            if (categoriaDAO.inserir(c)) { limparCateg(); atualizarTabelaCategorias(); }
        });
        btnAtualizar.addActionListener(e -> {
            if (txtIdCateg.getText().isEmpty()) return;
            Categoria c = new Categoria(); c.setIdCateg(Integer.parseInt(txtIdCateg.getText())); c.setNomeCategoria(txtNomeCateg.getText());
            if (categoriaDAO.atualizar(c)) { limparCateg(); atualizarTabelaCategorias(); }
        });
        btnExcluir.addActionListener(e -> {
            if (!txtIdCateg.getText().isEmpty() && categoriaDAO.excluir(Integer.parseInt(txtIdCateg.getText()))) { limparCateg(); atualizarTabelaCategorias(); }
        });
        return painel;
    }

    private void limparCateg() { txtIdCateg.setText(""); txtNomeCateg.setText(""); }
    private void atualizarTabelaCategorias() {
        modeloTabelaCategorias.setRowCount(0);
        for (Categoria c : categoriaDAO.listarTodas()) modeloTabelaCategorias.addRow(new Object[]{c.getIdCateg(), c.getNomeCategoria()});
    }

    // Métodos Utilitários Visuais
    private JLabel criarLabel(String txt, int x, int y, int w, int h) { JLabel l = new JLabel(txt); l.setBounds(x, y, w, h); return l; }
    private JTextField criarTextField(int x, int y, int w, int h, JPanel p, boolean editavel) { JTextField t = new JTextField(); t.setBounds(x, y, w, h); t.setEditable(editavel); p.add(t); return t; }
    private JButton criarBotao(String txt, int x, int y, int w, int h, JPanel p) { JButton b = new JButton(txt); b.setBounds(x, y, w, h); p.add(b); return b; }
}
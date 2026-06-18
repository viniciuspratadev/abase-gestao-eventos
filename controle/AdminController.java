package controle;

import dao.UsuarioDAO;
import dao.LocalEventoDAO;
import dao.SiteVendasDAO;
import dao.CategoriaDAO;
import modelo.LocalEvento;
import modelo.SiteVendas;
import modelo.Categoria;

import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.List;

public class AdminController {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private LocalEventoDAO localDAO = new LocalEventoDAO();
    private SiteVendasDAO siteDAO = new SiteVendasDAO();
    private CategoriaDAO categoriaDAO = new CategoriaDAO();

    // ==========================================
    // PRODUTORES
    // ==========================================
    public DefaultTableModel listarProdutoresPendentes() {
        return usuarioDAO.listarProdutoresPendentes();
    }

    public String aprovarProdutor(int idUsuario) {
        if (idUsuario == -1) return "ERRO: Selecione um produtor na tabela para aprovar.";
        if (usuarioDAO.aprovarUsuario(idUsuario)) return "SUCESSO";
        return "ERRO: Falha ao aprovar o produtor na base de dados.";
    }

    // ==========================================
    // LOCAIS
    // ==========================================
    public List<LocalEvento> listarLocais() {
        return localDAO.listarTodos();
    }

    public String processarLocal(String acao, String idStr, String nome, String endereco, String latStr, String lonStr) {
        if (acao.equals("EXCLUIR")) {
            if (idStr.isEmpty()) return "ERRO: Selecione um local para excluir.";
            try {
                if (localDAO.excluir(Integer.parseInt(idStr))) return "SUCESSO";
                return "ERRO: Falha ao excluir local (pode estar vinculado a eventos).";
            } catch (NumberFormatException e) {
                return "ERRO: ID do local inválido.";
            }
        }

        if (nome.trim().isEmpty() || endereco.trim().isEmpty()) {
            return "ERRO: Nome e Endereço são obrigatórios.";
        }

        BigDecimal lat, lon;
        try {
            lat = new BigDecimal(latStr.replace(",", "."));
            lon = new BigDecimal(lonStr.replace(",", "."));
        } catch (NumberFormatException e) {
            return "ERRO: Formato de coordenada inválido. Utilize apenas números e ponto/vírgula.";
        }

        LocalEvento loc = new LocalEvento();
        loc.setNome(nome.trim());
        loc.setEndereco(endereco.trim());
        loc.setLatitude(lat);
        loc.setLongitude(lon);

        if (acao.equals("SALVAR")) {
            if (localDAO.inserir(loc)) return "SUCESSO";
            return "ERRO: Falha ao salvar o novo local no banco.";
        } else if (acao.equals("ATUALIZAR")) {
            if (idStr.isEmpty()) return "ERRO: Selecione um local na tabela para atualizar.";
            loc.setIdLocal(Integer.parseInt(idStr));
            if (localDAO.atualizar(loc)) return "SUCESSO";
            return "ERRO: Falha ao atualizar o local.";
        }

        return "ERRO: Ação não reconhecida.";
    }

    // ==========================================
    // SITES
    // ==========================================
    public List<SiteVendas> listarSites() {
        return siteDAO.listarTodos();
    }

    public String processarSite(String acao, String idStr, String nome, String url) {
        if (acao.equals("EXCLUIR")) {
            if (idStr.isEmpty()) return "ERRO: Selecione um site para excluir.";
            if (siteDAO.excluir(Integer.parseInt(idStr))) return "SUCESSO";
            return "ERRO: Falha ao excluir site (pode estar vinculado a eventos).";
        }

        if (nome.trim().isEmpty() || url.trim().isEmpty()) {
            return "ERRO: O Nome do site e a URL são obrigatórios.";
        }

        SiteVendas s = new SiteVendas();
        s.setNome(nome.trim());
        s.setUrlBase(url.trim());

        if (acao.equals("SALVAR")) {
            if (siteDAO.inserir(s)) return "SUCESSO";
            return "ERRO: Falha ao salvar o site.";
        } else if (acao.equals("ATUALIZAR")) {
            if (idStr.isEmpty()) return "ERRO: Selecione um site para atualizar.";
            s.setIdSite(Integer.parseInt(idStr));
            if (siteDAO.atualizar(s)) return "SUCESSO";
            return "ERRO: Falha ao atualizar o site.";
        }

        return "ERRO: Ação não reconhecida.";
    }

    // ==========================================
    // CATEGORIAS
    // ==========================================
    public List<Categoria> listarCategorias() {
        return categoriaDAO.listarTodas();
    }

    public String processarCategoria(String acao, String idStr, String nome) {
        if (acao.equals("EXCLUIR")) {
            if (idStr.isEmpty()) return "ERRO: Selecione uma categoria para excluir.";
            if (categoriaDAO.excluir(Integer.parseInt(idStr))) return "SUCESSO";
            return "ERRO: Falha ao excluir categoria.";
        }

        if (nome.trim().isEmpty()) return "ERRO: O Nome da categoria é obrigatório.";

        Categoria c = new Categoria();
        c.setNomeCategoria(nome.trim());

        if (acao.equals("SALVAR")) {
            if (categoriaDAO.inserir(c)) return "SUCESSO";
            return "ERRO: Falha ao salvar a categoria.";
        } else if (acao.equals("ATUALIZAR")) {
            if (idStr.isEmpty()) return "ERRO: Selecione uma categoria para atualizar.";
            c.setIdCateg(Integer.parseInt(idStr));
            if (categoriaDAO.atualizar(c)) return "SUCESSO";
            return "ERRO: Falha ao atualizar a categoria.";
        }

        return "ERRO: Ação não reconhecida.";
    }
}
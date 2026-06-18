package controle;

import dao.EventoDAO;
import dao.LocalEventoDAO;
import dao.SiteVendasDAO;
import dao.CategoriaDAO;
import modelo.Evento;
import modelo.LocalEvento;
import modelo.SiteVendas;
import modelo.Categoria;
import util.SessaoAtual;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class ProdutorController {

    private EventoDAO eventoDAO = new EventoDAO();
    private LocalEventoDAO localDAO = new LocalEventoDAO();
    private SiteVendasDAO siteDAO = new SiteVendasDAO();
    private CategoriaDAO categoriaDAO = new CategoriaDAO();

    // Métodos de fornecimento de dados para a Tela
    public List<LocalEvento> listarLocais() { return localDAO.listarTodos(); }
    public List<SiteVendas> listarSites() { return siteDAO.listarTodos(); }
    public List<Categoria> listarCategorias() { return categoriaDAO.listarTodas(); }
    public List<Evento> listarEventosProdutor() { return eventoDAO.listarPorProdutor(SessaoAtual.idUsuarioLogado); }

    // Único ponto de entrada para ações do usuário
    public String processarEvento(String acao, String idStr, String nome, String url, String dataStr, String horaStr, int idLocal, int idSite, List<Integer> idsCategorias) {

        if (acao.equals("EXCLUIR")) {
            if (idStr.isEmpty()) return "ERRO: Selecione um evento para excluir.";
            try {
                int id = Integer.parseInt(idStr);
                if (eventoDAO.excluir(id, SessaoAtual.idUsuarioLogado)) return "SUCESSO_EXCLUIR";
                return "ERRO: Falha ao excluir no banco de dados.";
            } catch (NumberFormatException e) {
                return "ERRO: ID inválido.";
            }
        }

        // Validações de Negócio para Salvar ou Atualizar
        if (nome.trim().isEmpty()) return "ERRO: O Nome do evento não pode estar vazio.";
        if (url.trim().isEmpty()) return "ERRO: A URL do site é obrigatória.";

        Date data;
        try {
            data = Date.valueOf(dataStr.trim());
        } catch (IllegalArgumentException e) {
            return "ERRO: Formato de Data inválido.\nObrigatório o uso do formato AAAA-MM-DD (ex: 2026-10-30).";
        }

        Time hora;
        try {
            String horaFormatada = horaStr.trim();
            if (horaFormatada.length() == 5) horaFormatada += ":00";
            hora = Time.valueOf(horaFormatada);
        } catch (IllegalArgumentException e) {
            return "ERRO: Formato de Hora inválido.\nObrigatório o uso do formato HH:MM (ex: 23:30).";
        }

        if (idsCategorias == null || idsCategorias.isEmpty()) {
            return "ERRO: Selecione no mínimo uma categoria para o evento.";
        }

        // Montagem do Modelo
        Evento ev = new Evento();
        ev.setNome(nome.trim());
        ev.setUrlEvento(url.trim());
        ev.setDataEvento(data);
        ev.setHoraEvento(hora);
        ev.setIdLocal(idLocal);
        ev.setIdSite(idSite);
        ev.setIdProdutor(SessaoAtual.idUsuarioLogado);
        ev.setIdsCategorias(idsCategorias);

        // Comunicação com o Banco
        if (acao.equals("SALVAR")) {
            if (eventoDAO.inserir(ev)) return "SUCESSO_SALVAR";
            return "ERRO: Erro na transação. Verifique se os dados são válidos no banco.";
        } else if (acao.equals("ATUALIZAR")) {
            if (idStr.isEmpty()) return "ERRO: Selecione um evento na tabela para atualizar.";
            try {
                ev.setIdEvento(Integer.parseInt(idStr));
                if (eventoDAO.atualizar(ev)) return "SUCESSO_ATUALIZAR";
                return "ERRO: Erro crítico ao atualizar o evento.";
            } catch (NumberFormatException e) {
                return "ERRO: ID inválido.";
            }
        }

        return "ERRO: Ação não reconhecida pelo sistema.";
    }
}
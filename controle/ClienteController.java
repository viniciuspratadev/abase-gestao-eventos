package controle;

import dao.ClienteDAO;
import modelo.Avaliacao;
import util.GeolocalizacaoAPI;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public class ClienteController {

    private ClienteDAO dao = new ClienteDAO();

    // ==========================================
    // LÓGICA DE EXPLORAÇÃO E MATEMÁTICA
    // ==========================================
    public List<Object[]> explorarEventos(String endereco) {
        List<Object[]> eventos = dao.listarEventosExplorar();

        // Se o utilizador digitou um endereço, tentamos calcular a distância
        if (endereco != null && !endereco.trim().isEmpty()) {
            BigDecimal[] coords = GeolocalizacaoAPI.buscarCoordenadas(endereco);
            
            if (coords != null) {
                double latCliente = coords[0].doubleValue();
                double lonCliente = coords[1].doubleValue();

                // Calcula a distância para cada evento retornado do banco
                for (Object[] ev : eventos) {
                    BigDecimal latEv = (BigDecimal) ev[5];
                    BigDecimal lonEv = (BigDecimal) ev[6];
                    double dist = calcularDistancia(latCliente, lonCliente, latEv.doubleValue(), lonEv.doubleValue());
                    ev[5] = String.format("%.2f", dist); // Substitui a latitude pela distância em KM
                }
                
                // Ordena do mais próximo para o mais distante
                eventos.sort(Comparator.comparingDouble(o -> Double.parseDouble(((String) o[5]).replace(",", "."))));
                return eventos;
            }
        }
        
        // Se falhar ou não tiver endereço, exibe os eventos com distância desconhecida
        for (Object[] ev : eventos) ev[5] = "Desconhecida";
        return eventos;
    }

    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        int R = 6371; // Raio da Terra em KM
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    }

    // ==========================================
    // REGRAS DE COMPRA E CHECK-IN
    // ==========================================
    public String registrarCompra(int idEvento) {
        if (idEvento == -1) return "ERRO: Selecione um evento primeiro.";
        if (dao.comprarIngresso(idEvento)) return "SUCESSO";
        return "ERRO: Falha ao registrar o ingresso na base de dados.";
    }

    public List<Object[]> listarMeusEventos() {
        return dao.listarMeusEventos();
    }

    public String realizarCheckin(int idEvento, String endereco) {
        if (idEvento == -1) return "ERRO: Selecione um ingresso.";
        if (endereco == null || endereco.trim().isEmpty()) return "ERRO: Informe o endereço atual para validar o GPS.";

        BigDecimal[] coords = GeolocalizacaoAPI.buscarCoordenadas(endereco);
        if (coords == null) return "ERRO: Endereço não localizado pelo sistema de GPS.";

        if (dao.fazerCheckin(idEvento, coords[0], coords[1])) return "SUCESSO";
        return "ERRO: Falha ao processar o check-in no banco de dados.";
    }

    // ==========================================
    // REGRAS DE AVALIAÇÃO
    // ==========================================
    public List<Object[]> listarAvaliacoesPendentes() {
        return dao.listarCheckinsPendentesDeAvaliacao();
    }

    public String enviarAvaliacao(String idCheckinStr, String notaStr, String comentario) {
        try {
            int idCheckin = Integer.parseInt(idCheckinStr);
            int nota = Integer.parseInt(notaStr);
            
            if (nota < 1 || nota > 5) return "ERRO: A nota deve ser entre 1 e 5.";
            if (comentario.trim().isEmpty()) return "ERRO: O comentário é obrigatório.";

            Avaliacao aval = new Avaliacao();
            aval.setIdCheckin(idCheckin);
            aval.setNota(nota);
            aval.setComentario(comentario);

            if (dao.avaliar(aval)) return "SUCESSO";
            return "ERRO: Falha ao registar a avaliação.";

        } catch (NumberFormatException e) {
            return "ERRO: Dados numéricos inválidos (Verifique o ID selecionado ou a Nota).";
        }
    }
}
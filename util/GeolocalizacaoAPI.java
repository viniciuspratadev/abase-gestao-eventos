package util;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class GeolocalizacaoAPI {
    public static BigDecimal[] buscarCoordenadas(String endereco) {
        try {
            String enderecoCodificado = URLEncoder.encode(endereco, StandardCharsets.UTF_8);
            String url = "https://nominatim.openstreetmap.org/search?q=" + enderecoCodificado + "&format=json&limit=1";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "ABaseApp/1.0 (Trabalho Final de Banco de Dados)")
                    .GET()
                    .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    String json = response.body();

                    if (json == null || json.trim().equals("[]") || json.isEmpty()) {
                        return null;
                    }
                    String latStr = extrairValorJSON(json, "lat");
                    String lonStr = extrairValorJSON(json, "lon");

                    if (latStr != null && lonStr != null) {
                        return new BigDecimal[] {new BigDecimal(latStr), new BigDecimal(lonStr)};
                    }
        } catch (Exception e) {
            System.out.println("Falha ao comunicar com OpenStreetMap: " + e.getMessage());
        } return null;
    }
    private static String extrairValorJSON(String json, String chave) {
        String busca = "\"" + chave + "\":\"";
        int inicio = json.indexOf(busca);
        if (inicio == -1) return null;
        inicio += busca.length();
        int fim = json.indexOf("\"", inicio);
        return json.substring(inicio, fim);
    }
    public static void main(String[] args) {
        System.out.println("iniciando requisição de teste na API...");
        String localBusca = "Jardim da Penha, Vitória - ES";
        BigDecimal[] coordenadas= buscarCoordenadas(localBusca);

        if (coordenadas != null) {
            System.out.println("Sucesso! Coordenadas localizadas:");
            System.out.println("Latitude: " + coordenadas[0]);
            System.out.println("Longitude: " + coordenadas[1]);
        } else {
            System.out.println("O OpenStreetMap não conseguiu localizar as coordenadas para o endereço: " + localBusca);
        }
    }
}
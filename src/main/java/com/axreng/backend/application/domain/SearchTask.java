package com.axreng.backend.application.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchTask implements Runnable {
    private String searchId;
    private String keyword;

    private SearchManager searchManager;

    private List<String> partialResults; // Lista para armazenar os resultados parciais

    public SearchTask(String searchId, String keyword, SearchManager searchManager) {
        this.searchId = searchId;
        this.keyword = keyword;
        this.searchManager = searchManager;
        this.partialResults = new ArrayList<>();
    }

    @Override
    public void run() {
        // Implemente a lógica de busca aqui
        // Isso pode incluir o uso de Jsoup para fazer web scraping e buscar resultados

        // Suponha que você encontre resultados parciais como URLs correspondentes ao termo de busca
        List<String> partialResults = performSearch(keyword);

        // Adicione os resultados parciais ao SearchManager
        searchManager.addSearchResult(searchId, partialResults);


        // Marque a busca como concluída
        markSearchAsDone(searchId);
    }

    public List<String> getPartialResults() {
        return partialResults;
    }

    private List<String> performSearch(String keyword) {
        // Implemente a lógica de busca real aqui
        // Use Jsoup ou outras bibliotecas para fazer web scraping e encontrar resultados
        // Retorne os resultados parciais como uma lista de URLs
        return Collections.emptyList(); // Exemplo: retornar uma lista vazia por enquanto
    }

    private void markSearchAsDone(String searchId) {
        // Implemente a marcação da busca como concluída
        // Você pode usar uma estrutura de dados para rastrear o status das buscas
        // Por exemplo, um mapa onde a chave é o ID da busca e o valor é o status (ativo ou concluído)
    }
}

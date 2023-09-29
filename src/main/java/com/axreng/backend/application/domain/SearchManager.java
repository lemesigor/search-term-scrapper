package com.axreng.backend.application.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchManager {
        private Map<String, List<String>> searchResults; // Mapa para armazenar resultados parciais por ID

        public SearchManager() {
            this.searchResults = new HashMap<>();
        }

        public synchronized void addSearchResult(String searchId, List<String> partialResults) {
            // Adicione os resultados parciais ao mapa
            if (!searchResults.containsKey(searchId)) {
                searchResults.put(searchId, new ArrayList<>());
            }
            searchResults.get(searchId).addAll(partialResults);
        }

        public synchronized List<String> getSearchResults(String searchId) {
            // Obtenha os resultados parciais para um ID de busca específico
            return searchResults.getOrDefault(searchId, new ArrayList<>());
        }

        // Implemente métodos para marcar buscas como concluídas, verificar status, etc., conforme necessário
}

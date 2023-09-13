package com.example.myapplicationserch

class SearchHistoryManager {
    private val searchHistory = mutableListOf<String>()

    fun addSearchQuery(query: String) {
        searchHistory.add(query)
    }

    fun getSearchHistory(): List<String> {

        return searchHistory.toList()
    }

    fun clearSearchHistory() {
        searchHistory.clear()
    }
}
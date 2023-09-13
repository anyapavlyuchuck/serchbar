package com.example.myapplicationserch

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val searchHistoryManager = SearchHistoryManager()
    private lateinit var searchAutoCompleteTextView: AutoCompleteTextView
    private lateinit var countryListView: ListView
    private lateinit var historyListView: ListView
    private lateinit var showAllResultsButton: Button
    private lateinit var noResultsTextView: TextView
    private lateinit var backButton: ImageButton
    private lateinit var clearHistoryButton: Button


    private val countryList = arrayOf(
        "Russia", "USA", "China", "India", "Brazil", "Japan", "Canada", "Germany", "France", "Australia", "Mexico", "South Korea", "Italy", "United Kingdom", "Spain", "Argentina", "Turkey", "Saudi Arabia", "Egypt", "Nigeria", "South Africa", "Greece", "Sweden", "Norway", "Switzerland", "Netherlands", "Belgium", "Austria", "Poland", "Ukraine", "Thailand", "Vietnam", "Indonesia", "Philippines", "Malaysia", "Singapore", "New Zealand", "Chile", "Colombia", "Peru", "Venezuela", "Iran", "Iraq", "Syria", "Lebanon", "Jordan", "Israel", "Palestine", "Kenya", "Ethiopia", "Morocco", "Tunisia"
    )

    private val searchHistory = mutableListOf<String>()
    private val filteredCountries = mutableListOf<String>()
    private var showAllResults = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchAutoCompleteTextView = findViewById(R.id.searchAutoCompleteTextView)
        countryListView = findViewById(R.id.countryListView)
        historyListView = findViewById(R.id.historyListView)
        showAllResultsButton = findViewById(R.id.showAllResultsButton)
        noResultsTextView = findViewById(R.id.noResultsTextView)
        backButton = findViewById(R.id.backButton)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)

        // Адаптер для списка стран (первоначально отображаем все страны)
        val allCountriesAdapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_list_item_1,
            countryList
        )
        countryListView.adapter = allCountriesAdapter

        // Адаптер для строки поиска с автозаполнением
        val autoCompleteAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, searchHistory)
        searchAutoCompleteTextView.setAdapter(autoCompleteAdapter)


        // Адаптер для списка стран
        val countryAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, filteredCountries)
        countryListView.adapter = countryAdapter

        // Слушатель для строки поиска
        searchAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedQuery = autoCompleteAdapter.getItem(position).toString()
            if (selectedQuery.isNotEmpty()) {
                searchHistoryManager.addSearchQuery(selectedQuery)
                updateHistoryListView()
                searchAutoCompleteTextView.text.clear()
            }
        }

// Слушатель для списка стран
        countryListView.setOnItemClickListener { _, _, position, _ ->
            val selectedCountry = filteredCountries[position]
            if (selectedCountry.isNotEmpty()) {
                searchHistoryManager.addSearchQuery(selectedCountry)
                updateHistoryListView()
                searchAutoCompleteTextView.text.clear()
                // Здесь можно выполнить дополнительное действие при выборе страны
                Toast.makeText(this, "Выбрана страна: $selectedCountry", Toast.LENGTH_SHORT).show()
            }
        }
        searchAutoCompleteTextView = findViewById(R.id.searchAutoCompleteTextView)
        // Слушатель для изменения текста в строке поиска
        searchAutoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Не используется
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Фильтруем и сортируем список стран при вводе текста в строке поиска
                val filterText = s.toString().trim()
                filteredCountries.clear()

                if (filterText.isNotEmpty()) {
                    val matchingCountries = countryList.filter {
                        it.contains(filterText, ignoreCase = true)
                    }.sortedWith(compareBy { it.startsWith(filterText, ignoreCase = true) })

                    if (matchingCountries.size > 5) {
                        // Если результатов больше 5, то ограничиваем до 5
                        filteredCountries.addAll(matchingCountries.take(5))
                        showAllResultsButton.visibility = View.VISIBLE
                    } else {
                        filteredCountries.addAll(matchingCountries)
                        showAllResultsButton.visibility = View.GONE
                    }

                    backButton.visibility = View.VISIBLE // Показываем кнопку "Назад"
                    historyListView.visibility = View.GONE // Скрываем историю поиска
                    clearHistoryButton.visibility = View.GONE // Скрываем кнопку "Очистить историю поиска"
                } else {
                    // Если строка поиска пуста, показываем историю поиска, скрываем список стран
                    filteredCountries.addAll(countryList)
                    showAllResultsButton.visibility = View.GONE
                    backButton.visibility = View.GONE // Скрываем кнопку "Назад"
                    historyListView.visibility = if (searchHistory.isNotEmpty()) View.VISIBLE else View.GONE // Отображаем/скрываем историю поиска
                    clearHistoryButton.visibility = if (searchHistory.isNotEmpty()) View.VISIBLE else View.GONE // Отображаем/скрываем кнопку "Очистить историю поиска"
                }

                val filteredAdapter = ArrayAdapter(
                    applicationContext,
                    android.R.layout.simple_list_item_1,
                    filteredCountries
                )

                countryListView.adapter = filteredAdapter

                // Отображаем текст "No results" при отсутствии результатов
                noResultsTextView.visibility =
                    if (filteredCountries.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                // Не используется
            }
        })

        // Слушатель для кнопки "Все результаты"
        showAllResultsButton.setOnClickListener {
            showAllResults = !showAllResults
            val buttonText = if (showAllResults) "Скрыть результаты" else "Все результаты"

            if (showAllResults) {
                val allCountriesAdapter = ArrayAdapter(
                    applicationContext,
                    android.R.layout.simple_list_item_1,
                    countryList
                )
                countryListView.adapter = allCountriesAdapter
            } else {
                val filteredAdapter = ArrayAdapter(
                    applicationContext,
                    android.R.layout.simple_list_item_1,
                    filteredCountries
                )
                countryListView.adapter = filteredAdapter
            }

            showAllResultsButton.text = buttonText
        }


        backButton.setOnClickListener {
            searchAutoCompleteTextView.text.clear()
            backButton.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
        }


        clearHistoryButton.setOnClickListener {
            searchHistoryManager.clearSearchHistory()
            updateHistoryListView()
        }
    }

    private fun updateHistoryListView() {
        val historyAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, searchHistory)
        historyListView.adapter = historyAdapter
        historyListView.visibility = if (searchHistory.isNotEmpty()) View.VISIBLE else View.GONE
    }

}

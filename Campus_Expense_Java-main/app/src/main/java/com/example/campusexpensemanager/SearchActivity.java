package com.example.campusexpensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexpensemanager.DatabaseSQLite.ExpenseDB;
import com.example.campusexpensemanager.Expense.Expense;
import com.example.campusexpensemanager.Expense.ExpenseAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText editSearch;
    private Button buttonSearch;
    private Button btnSearchBack;
    private RecyclerView recyclerViewSearchResults;
    private ExpenseAdapter expenseAdapter;
    private ExpenseDB expenseDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        editSearch = findViewById(R.id.edit_parent);
        buttonSearch = findViewById(R.id.button_content);
        recyclerViewSearchResults = findViewById(R.id.recycler_view_search_results);
         btnSearchBack = findViewById(R.id.Search_back);
        expenseDB = new ExpenseDB(this);
        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this));
        expenseAdapter = new ExpenseAdapter(this, new ArrayList<>());
        recyclerViewSearchResults.setAdapter(expenseAdapter);

        buttonSearch.setOnClickListener(v -> performSearch());
        btnSearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, ExpenseTracking.class);
                startActivity(intent);
            }
        });
    }

    private void performSearch() {
        String query = editSearch.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter search keywords.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Gọi phương thức tìm kiếm với cả query và studentID
            List<Expense> results = expenseDB.searchExpenses(query, DataStatic.studentId);
            expenseAdapter.setExpenses(results);
        } catch (Exception e) {
            Log.e("SearchActivity", "Error during search: " + e.getMessage());
            Toast.makeText(this, "Error while searching: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void deleteExpense(int expenseId) {

    }
}
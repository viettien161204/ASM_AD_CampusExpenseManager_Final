package com.example.campusexpensemanager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexpensemanager.DatabaseSQLite.BudgetDB;
import com.example.campusexpensemanager.DatabaseSQLite.ExpenseDB;
import com.example.campusexpensemanager.Expense.Budget;
import com.example.campusexpensemanager.Expense.Expense;
import com.example.campusexpensemanager.Expense.ExpenseAdapter;

import java.util.ArrayList;
import java.util.List;

public class ExpenseTracking extends AppCompatActivity {

    private TextView textBudget;
    private TextView textBudgetStatus;  // Thêm TextView cho trạng thái ngân sách
    private EditText editBudget;
    private Button buttonSetBudget;
    private Button buttonAddExpense;
    private Button buttonAddMoney;
    private Button buttonBack;
    private Button buttonSearch ;
    private TextView textTotalExpense;
    private RecyclerView recycleViewExpense;
    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenseList;

    private ExpenseDB expenseDB;
    private BudgetDB budgetDB;
    private double currentBudget;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expensetracking_activity);

        textBudget = findViewById(R.id.text_budget);
        textBudgetStatus = findViewById(R.id.text_budget_status);  // Khởi tạo TextView trạng thái ngân sách
        editBudget = findViewById(R.id.edit_budget);
        buttonSetBudget = findViewById(R.id.button_set_budget);
        buttonAddExpense = findViewById(R.id.button_add_expense);
        buttonAddMoney = findViewById(R.id.button_add_money);
        buttonBack = findViewById(R.id.button_back);
        Button buttonSearch = findViewById(R.id.button_search);
        textTotalExpense = findViewById(R.id.text_total_expenses);
        recycleViewExpense = findViewById(R.id.recycler_view_expenses);

        expenseDB = new ExpenseDB(this);
        budgetDB = new BudgetDB(this);

        recycleViewExpense.setLayoutManager(new LinearLayoutManager(this));
        expenseList = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(this, expenseList);
        recycleViewExpense.setAdapter(expenseAdapter);

        loadBudget();
        loadExpense();

        buttonSetBudget.setOnClickListener(v -> setBudget());
        buttonAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(ExpenseTracking.this, AddExpenseActivity.class);
            startActivity(intent);
        });
        buttonAddMoney.setOnClickListener(v -> addMoneyToBudget());
        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(ExpenseTracking.this, MainActivity.class);
            startActivity(intent);
        });
        buttonSearch.setOnClickListener(v -> {
            Intent intent = new Intent(ExpenseTracking.this, SearchActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpense();
        loadBudget();
    }

    private void addMoneyToBudget() {
        String amountStr = editBudget.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tìm ngân sách hiện tại cho studentID cụ thể
        double newBudget = currentBudget + amount;
        updateBudgetInDB(newBudget); // Cập nhật ngân sách trong DB cho studentID
        currentBudget = newBudget; // Cập nhật currentBudget
        textBudget.setText("Balance: $" + currentBudget);
        updateBudgetStatus();  // Cập nhật trạng thái ngân sách
        editBudget.setText("");
        Toast.makeText(this, "Money added to budget", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadExpense() {
        List<Expense> expenses = new ArrayList<>();
        double totalAmount = 0;

        expenses = expenseDB.getAllExpenseByStudentID(DataStatic.studentId);
        expenseAdapter.setExpenses(expenses);
            recycleViewExpense.setVisibility(View.VISIBLE);

        recycleViewExpense.setAdapter(expenseAdapter);


        for(Expense expense : expenses) {

            totalAmount += expense.getAmount();

        }
        textTotalExpense.setText("Total: $" + totalAmount);
//
//        SQLiteDatabase db = expenseDB.getReadableDatabase();
//        Cursor cursor = db.query(
//                ExpenseDB.TABLE_EXPENSES,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null
//        );
//        expenseList.clear();
//
//        if (cursor.getCount() == 0) {
//            textTotalExpense.setText("Total: $0");
//            recycleViewExpense.setVisibility(View.GONE);
//        } else {
//            recycleViewExpense.setVisibility(View.VISIBLE);
//            while (cursor.moveToNext()) {
//                int id = cursor.getInt(cursor.getColumnIndexOrThrow(ExpenseDB.COLUMM_ID));
//                String description = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseDB.COLUMM_DESCRIPTION));
//                String date = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseDB.COLUMM_DATE));
//                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(ExpenseDB.COLUMM_AMOUNT));
//                String category = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseDB.COLUMN_CATEGORY));
//                String studentID = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseDB.COLUMN_STUDENTID));
//
//                Expense expense = new Expense(id, description, date, amount, category, studentID);
//                expenses.add(expense);
//                totalAmount += amount;
//            }
//
//            expenseAdapter.setExpenses(expenses);
//            textTotalExpense.setText("Total: $" + totalAmount);
//        }
//        cursor.close();
    }

    @SuppressLint("Range")
    private void loadBudget() {
        ArrayList<Budget> budgets = budgetDB.getAllBudgetsByStudentID(DataStatic.studentId);
        currentBudget = 0;

        for (Budget budget : budgets) {
            currentBudget += budget.getAmount(); // Tính tổng ngân sách chỉ cho studentID
        }

        textBudget.setText("Balance: $" + currentBudget);
        updateBudgetStatus(); // Cập nhật trạng thái ngân sách
    }

    private void setBudget() {
        String budgetStr = editBudget.getText().toString().trim();
        double budget;
        try {
            budget = Double.parseDouble(budgetStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid budget", Toast.LENGTH_SHORT).show();
            return;
        }

        // Xóa tất cả ngân sách cũ và thêm ngân sách mới cho studentID
        budgetDB.deleteAllBudgetsForStudent(DataStatic.studentId); // Xóa ngân sách cũ
        budgetDB.addBudget((float) budget, DataStatic.studentId); // Thêm ngân sách mới
        currentBudget = budget; // Cập nhật currentBudget thành giá trị mới
        textBudget.setText("Balance: $" + currentBudget);
        updateBudgetStatus(); // Cập nhật trạng thái ngân sách
        editBudget.setText("");
    }

    public void deleteExpense(int expenseId) {
        SQLiteDatabase db = expenseDB.getWritableDatabase();
        double amount = getExpenseAmountById(expenseId);

        // Xóa chi phí
        db.delete(ExpenseDB.TABLE_EXPENSES, ExpenseDB.COLUMM_ID + " = ?", new String[]{String.valueOf(expenseId)});

        // Cập nhật ngân sách
        currentBudget += amount;
        updateBudgetInDB(currentBudget);

        loadExpense();
        textBudget.setText("Balance: $" + currentBudget);
        updateBudgetStatus(); // Cập nhật trạng thái ngân sách
        Toast.makeText(this, "Expense deleted successfully", Toast.LENGTH_SHORT).show();
    }

    // Phương thức để lấy số tiền chi tiêu theo ID
    @SuppressLint("Range")
    private double getExpenseAmountById(int expenseId) {
        SQLiteDatabase db = expenseDB.getReadableDatabase();
        Cursor cursor = db.query(ExpenseDB.TABLE_EXPENSES,
                null,
                ExpenseDB.COLUMM_ID + " = ?",
                new String[]{String.valueOf(expenseId)},
                null, null, null);

        double amount = 0;
        if (cursor.moveToFirst()) {
            amount = cursor.getDouble(cursor.getColumnIndex(ExpenseDB.COLUMM_AMOUNT));
        }
        cursor.close();
        return amount;
    }

    private void updateBudgetInDB(double newBudget) {
        SQLiteDatabase db = budgetDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BudgetDB.COLUMN_AMOUNT, newBudget);
        db.update(BudgetDB.TABLE_BUDGET, values, null, null);
    }

    private void updateBudgetStatus() {
        if (currentBudget < 0) {
            textBudgetStatus.setText("Exceed balance: $" + Math.abs(currentBudget));
            textBudgetStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        } else if (currentBudget <= 500 && currentBudget > 0) {
            textBudgetStatus.setText("Balance is running low");
            textBudgetStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
        } else {
            textBudgetStatus.setText(""); // Ẩn thông báo nếu ngân sách lớn hơn 500
        }
    }
}
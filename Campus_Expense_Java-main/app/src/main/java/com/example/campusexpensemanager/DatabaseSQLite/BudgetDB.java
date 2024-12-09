package com.example.campusexpensemanager.DatabaseSQLite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.campusexpensemanager.Expense.Budget;

import java.util.ArrayList;

public class BudgetDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "budget.db";
    private static final int DATABASE_VERSION = 3; // Tăng phiên bản cơ sở dữ liệu

    public static final String TABLE_BUDGET = "budget";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_STUDENTID = "studentID";// Thêm cột email

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_BUDGET + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_AMOUNT + " REAL, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_STUDENTID + " TEXT" + // Thêm cột studentID
                    ");";

    public BudgetDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
        onCreate(db);
    }

    public float getTotalBudget(String studentID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_BUDGET + " WHERE " + COLUMN_STUDENTID + " = ?", new String[]{studentID});
        float total = 0;

        if (cursor.moveToFirst()) {
            total = cursor.getFloat(0);
        }
        cursor.close();
        return total;
    }

    public void addBudget(double amount, String studentID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_STUDENTID, studentID); // Lưu studentID
        db.insert(TABLE_BUDGET, null, values);
    }

    public ArrayList<Budget> getAllBudgetsByStudentID(String studentID) {
        ArrayList<Budget> budgets = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BUDGET + " WHERE " + COLUMN_STUDENTID + " = ?", new String[]{studentID});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range")float amount = cursor.getFloat(cursor.getColumnIndex(COLUMN_AMOUNT));
                @SuppressLint("Range")String email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));

                Budget budget = new Budget(id, amount, email, studentID);
                budgets.add(budget);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return budgets;
    }

    public float getAllBudgetByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_BUDGET + " WHERE " + COLUMN_EMAIL + " = ?", new String[]{email});
        float total = 0;

        if (cursor.moveToFirst()) {
            total = cursor.getFloat(0);
        }
        cursor.close();
        return total;
    }

    public void updateBudget(float amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_BUDGET + " SET amount = ?", new Object[]{amount});
    }

    public void deleteBudget() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_BUDGET);
    }
    public void deleteAllBudgetsForStudent(String studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BUDGET, COLUMN_STUDENTID + " = ?", new String[]{studentId});
    }

}
package com.example.campusexpensemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.database.Cursor;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import com.example.campusexpensemanager.DatabaseSQLite.AccountDB;

import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {
    private EditText studentidET;
    private EditText passwordET;
    private AccountDB dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        studentidET = findViewById(R.id.studentid);
        passwordET = findViewById(R.id.password);
        dbHelper = new AccountDB(this);

        Button loginButton = findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String studentid = studentidET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();
                if (checkLogin(studentid, password)) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                    editor.putString("studentID", studentid);
                    editor.putString("password", password);
                    DataStatic.studentId = studentid;
                    DataStatic.password = password;
                    // Store hashed/encrypted version instead
                    editor.apply();  // or
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Student ID or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button registerButton = findViewById(R.id.btnRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        Button forgotPasswordButton = findViewById(R.id.btnForgotPassword);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean checkLogin(String studentid, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                AccountDB.COLUMN_STUDENTID,
                AccountDB.COLUMN_PASSWORD
        };

        String selection = AccountDB.COLUMN_STUDENTID + " = ? ";
        String[] selectionArgs = { studentid };

        Cursor cursor = db.query(
                AccountDB.TABLE_USERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean loginSuccess = false;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String storedHashedPassword = cursor.getString(cursor.getColumnIndexOrThrow(AccountDB.COLUMN_PASSWORD));
                // So sánh mật khẩu được nhập với mật khẩu đã băm
                if (BCrypt.checkpw(password, storedHashedPassword)) {
                    loginSuccess = true;
                }
            }
            cursor.close();
        }
        return loginSuccess;
    }
}
package com.gamealike.CSE476_Project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import GameAlikeApiInterface.ApiInterface;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordTextView, signUpTextView;

    private String cookie = "";
    private ArrayList<Integer> genreIDs = new ArrayList<>();
    private ArrayList<String> genreNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        signUpTextView = findViewById(R.id.signUpTextView);

        // Set onClickListener for the login button

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve email and password entered by the user
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Validate inputs and authenticate the user
                if (isValidInput(email, password)) {
                    try {
                        attemptLogin(email, password);
                        if (!cookie.equals("")) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish(); // Close the login activity
                        }
                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, "Invalid email or password.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Display an error message
                    Toast.makeText(LoginActivity.this, "Email or password cannot be blank.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set onClickListener for the "Forgot Password" link

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the forgot password activity or functionality
                // Example: startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        // Set onClickListener for the "Sign Up" link
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve email and password entered by the user
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Validate inputs and authenticate the user
                if (isValidInput(email, password)) {
                    try {
                        attemptCreateAccount(email, password);
                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, "Unable to create account.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Display an error message
                    Toast.makeText(LoginActivity.this, "Email or password cannot be blank.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Function to validate email and password
    private boolean isValidInput(String email, String password) {
        // Implement your validation logic here
        return !email.isEmpty() && !password.isEmpty();
    }

    private void attemptCreateAccount(String email, String password) throws JSONException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            JSONObject data;
            try {
                data = ApiInterface.createUser(email, password, email);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            final JSONObject finalData = data;
            handler.post(() -> {
                try {
                    cookie = finalData.getString("cookie");
                    loginSuccess();
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "Unable to create account.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void attemptLogin(String email, String password) throws JSONException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            JSONObject data;
            try {
                data = ApiInterface.userLogin(email, password);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            final JSONObject finalData = data;
            handler.post(() -> {
                try {
                    cookie = finalData.getString("cookie");
                    loginSuccess();
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "Unable to create account.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loginSuccess() {
        if (this.cookie.equals(""))
            return;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            JSONObject data;
            try {
                data = ApiInterface.getUserPreferences(this.cookie);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            final JSONObject finalData = data;
            handler.post(() -> {
                try {
                    JSONArray genres = finalData.getJSONArray("data");
                    if (genres.length() == 0) {
                        openConfigure();
                    } else {
                        for (int i = 0; i < genres.length(); i++) {
                            JSONObject genre = genres.getJSONObject(i);
                            this.genreIDs.add(genre.getInt("id"));
                            this.genreNames.add(genre.getString("genre"));
                        }

                        openMain();
                    }
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "Unable to load account.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void openConfigure() {
        Intent config = new Intent(LoginActivity.this, ConfigureGenresActivity.class);
        config.putExtra("cookie", this.cookie);
        startActivity(config);
        finish(); // Close the login activity
    }

    private void openMain() {
        Intent main = new Intent(LoginActivity.this, HomeActivity.class);
        main.putExtra("cookie", this.cookie);
        main.putIntegerArrayListExtra("genreIDs", this.genreIDs);
        main.putStringArrayListExtra("genreNames", this.genreNames);
        startActivity(main);
        finish(); // Close the login activity
    }


    // Resets password field when resuming login activity
    @Override
    public void onResume(){
        super.onResume();
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordEditText.setText("");
    }
}

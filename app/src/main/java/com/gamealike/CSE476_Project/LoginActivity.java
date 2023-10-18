package com.gamealike.CSE476_Project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordTextView, signUpTextView;

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

                startActivity(new Intent(LoginActivity.this, ConfigureGenresActivity.class));

                // Authentication commented out for now
                /*
                // Perform login logic here
                // Retrieve email and password entered by the user
                String email = emailEditText.getText().toString().trim();s
                String password = passwordEditText.getText().toString().trim();

                // Validate inputs and authenticate the user
                if (isValidInput(email, password)) {
                    // Authenticate user (you can add your authentication logic here)
                    // If authentication is successful, navigate to the main screen
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish(); // Close the login activity
                } else {
                    // Display an error message
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
                 */
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



        /*
        // Set onClickListener for the "Sign Up" link
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the sign-up activity
                // Example: startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });*/
    }

    // Function to validate email and password
    private boolean isValidInput(String email, String password) {
        // Implement your validation logic here
        return !email.isEmpty() && !password.isEmpty();
    }


    // Resets password field when resuming login activity
    @Override
    public void onResume(){
        super.onResume();
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordEditText.setText("");
    }
}

package algonquin.cst2335.medassist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity{
    EditText userEditText;
    EditText passwordEditText;
    Button loginButton;

    Button createUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_med);

        userEditText = findViewById(R.id.userEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        createUserButton = findViewById(R.id.createUserButton);

        /**
         * When user clicks on the login button, it checks to see if the username
         * and password exist in database. If it matches, it will let user login, else displays
         * invalid username/password message
         */
        loginButton.setOnClickListener(view -> {
            // Get the email address from the EditText
            String username = userEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (checkUserCredentials(username, password)) {
                // If a matching record is found, proceed to the next activity (e.g., MainActivity)
                Intent nextPage = new Intent(LoginActivity.this, MainActivity.class);
                nextPage.putExtra("Username", username); // Pass the username to the next activity
                startActivity(nextPage);
            } else {
                // If no matching record is found, show an error message
                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

        createUserButton.setOnClickListener(view -> {
            Intent registrationIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(registrationIntent);
        });

    }

    private boolean checkUserCredentials(String username, String password) {
        MedDatabase dbHelper = new MedDatabase(this);
        if(!dbHelper.checkUser(username, password)){
            Toast.makeText(LoginActivity.this, "Your username or password is incorrect.", Toast.LENGTH_SHORT).show();
        }
        return dbHelper.checkUser(username, password);
    }
}

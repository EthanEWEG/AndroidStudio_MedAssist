package algonquin.cst2335.medassist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity{
    EditText emailEditText;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_med);

        emailEditText = findViewById(R.id.emailEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(view -> {
            // Get the email address from the EditText
            String emailAddress = emailEditText.getText().toString();

            // Create an Intent to transition to SecondActivity
            Intent nextPage = new Intent(LoginActivity.this, MainActivity.class);

            // Pass the email address to the next activity
            nextPage.putExtra("EmailAddress", emailAddress);

            // Start the new activity
            startActivity(nextPage);
        });
    }
}

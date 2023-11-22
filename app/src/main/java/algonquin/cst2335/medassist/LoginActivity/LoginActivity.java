package algonquin.cst2335.medassist.LoginActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import algonquin.cst2335.medassist.Main.MainActivity;
import algonquin.cst2335.medassist.Medicine.MedDatabase;
import algonquin.cst2335.medassist.R;
import algonquin.cst2335.medassist.databinding.LoginMedBinding;

/**
 * The LoginActivity class allows users to log in to the MedAssist application by providing
 * their username and password. Users can also navigate to the registration and forgot password
 * screens from this activity.
 *
 * @see algonquin.cst2335.medassist.Main.MainActivity
 * @see algonquin.cst2335.medassist.Medicine.MedDatabase
 * @see algonquin.cst2335.medassist.R
 * @see algonquin.cst2335.medassist.databinding.LoginMedBinding
 */
public class LoginActivity extends AppCompatActivity{

    LoginMedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginMedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /**
         * When user clicks on the login button, it checks to see if the username
         * and password exist in database. If it matches, it will let user login, else displays
         * invalid username/password message
         */
        binding.loginButton.setOnClickListener(view -> {
            // Get the email address from the EditText
            String username = binding.userEditText.getText().toString();
            String password = binding.passwordEditText.getText().toString();

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

        binding.createUserButton.setOnClickListener(view -> {
            Intent registrationIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(registrationIntent);
        });

        binding.forgotButton.setOnClickListener((view ->{
            Intent forgotIntent = new Intent(LoginActivity.this, ForgotActivity.class);
            startActivity(forgotIntent);
        }));

    }

    private boolean checkUserCredentials(String username, String password) {
        MedDatabase dbHelper = new MedDatabase(this);
        if(!dbHelper.checkUser(username, password)){
            Toast.makeText(LoginActivity.this, "Your username or password is incorrect.", Toast.LENGTH_SHORT).show();
        }
        return dbHelper.checkUser(username, password);
    }
}

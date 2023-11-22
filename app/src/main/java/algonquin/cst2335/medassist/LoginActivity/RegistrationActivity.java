// RegistrationActivity.java
package algonquin.cst2335.medassist.LoginActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import algonquin.cst2335.medassist.Medicine.MedDatabase;
import algonquin.cst2335.medassist.databinding.RegistrationBinding;

/**
 * The RegistrationActivity class allows users to register a new account by providing a username,
 * password, and passcode. The user registration information is stored in the MedDatabase.
 *
 * @see algonquin.cst2335.medassist.Medicine.MedDatabase
 * @see algonquin.cst2335.medassist.databinding.RegistrationBinding
 */
public class RegistrationActivity extends AppCompatActivity {

    RegistrationBinding binding;
    Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement your user registration logic here.
                String username = binding.usernameEditText.getText().toString();
                String password = binding.passwordEditText.getText().toString();
                String passwordConfirm = binding.confirmPasswordEditText.getText().toString();
                String passCode = binding.passcodeEditText.getText().toString();

                // Check if passcode contains only numerical values
                if (!passCode.matches("\\d+")) {
                    // If passcode contains non-numeric characters, show an error message
                    Toast.makeText(RegistrationActivity.this, "Passcode should contain only numerical values", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(passwordConfirm)) {
                    Toast.makeText(RegistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    // Now, store the user's data in the database
                    MedDatabase dbHelper = new MedDatabase(RegistrationActivity.this);
                    long newRowId = dbHelper.insertUser(username, password, passCode);

                    if (newRowId != -1) {
                        // User registration successful
                        Toast.makeText(RegistrationActivity.this, "User created successfully. Make sure to remember your passcode.", Toast.LENGTH_LONG).show();
                        // Close the registration activity
                        Intent loginIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                    } else {
                        // User registration failed
                        Toast.makeText(RegistrationActivity.this, "Failed to create user. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}



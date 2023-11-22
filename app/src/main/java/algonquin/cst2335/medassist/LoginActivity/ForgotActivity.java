package algonquin.cst2335.medassist.LoginActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import algonquin.cst2335.medassist.Medicine.MedDatabase;
import algonquin.cst2335.medassist.databinding.ForgotUserpassBinding;

public class ForgotActivity extends AppCompatActivity {

    ForgotUserpassBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ForgotUserpassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.submitButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String passcode = binding.passcodeEditText.getText().toString();
                MedDatabase dbHelper = new MedDatabase(ForgotActivity.this);
                String[] userPass = dbHelper.getUsernamePasswordByPasscode(passcode);
                if(userPass != null){
                    binding.usernamePasswordTextView.setText("Username: " + userPass[0] +
                            "\nPassword: " + userPass[1]);
                    binding.usernamePasswordTextView.setVisibility(View.VISIBLE);
                }else{
                    binding.usernamePasswordTextView.setVisibility(View.INVISIBLE);
                    Toast.makeText(ForgotActivity.this, "Passcode could not find your username and password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

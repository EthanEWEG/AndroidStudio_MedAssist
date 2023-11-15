package algonquin.cst2335.medassist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import algonquin.cst2335.medassist.databinding.MedFragmentBinding;

public class MedicineDetailActivity extends AppCompatActivity {
    MedFragmentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MedFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve the selected medicine from the intent
        Medicine medicine = getIntent().getParcelableExtra("medicine");
        Doctor doctor = getIntent().getParcelableExtra("doctor");

        if (medicine != null) {
            // Display the detailed information in your layout using the binding object
            binding.medName.setText(medicine.getName());
            binding.medDosageValue.setText(medicine.getDosage());
            binding.medFrequencyValue.setText(medicine.getFrequency());
            binding.medQuantityValue.setText(medicine.getQuantity());
            binding.medRefillValue.setText(medicine.getRefills());
            binding.medDurationValue.setText(medicine.getDuration());
            binding.medExpirationValue.setText(medicine.getExpiration());
            binding.medDocNameValue.setText(doctor.getDocName());
            binding.medDocNumberValue.setText(doctor.getDocNumber());
            binding.medInstructionsValue.setText(medicine.getInstructions());
        }
    }
}

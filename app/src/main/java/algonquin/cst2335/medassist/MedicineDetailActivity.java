package algonquin.cst2335.medassist;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import algonquin.cst2335.medassist.databinding.MedFragmentBinding;

public class MedicineDetailActivity extends AppCompatActivity {
    MedFragmentBinding binding;
    Button deleteButton;
    private Medicine medicineToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MedFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        deleteButton = binding.medDelete;

        // Retrieve the selected medicine from the intent
        Medicine medicine = getIntent().getParcelableExtra("medicine");
        Doctor doctor = getIntent().getParcelableExtra("doctor");
        int position = getIntent().getIntExtra("position", -1);

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

        deleteButton.setOnClickListener(view ->{
            medicineToDelete = medicine;

            //Allows user to undo deletion in the event of misclick
            Snackbar snackbar = Snackbar.make(view, "Medicine deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo", v -> undoDeletion(medicineToDelete));

            snackbar.show();

            //Deletes medicine
            deleteMedicine();
        });
    }

    /**
     * Deletes the medicine from Database
     */
    private void deleteMedicine() {
        // Call the method from MedDatabase to delete the record
        MedDatabase dbHelper = new MedDatabase(this);
        dbHelper.deleteMedicine(medicineToDelete.getId());
    }

    /**
     *
     */
    private void undoDeletion(Medicine medicineToDelete) {
        // Restore the medicine to the database
        MedDatabase dbHelper = new MedDatabase(this);
        dbHelper.insertMedicine(medicineToDelete);

        // Show a message indicating that the deletion has been undone
        Snackbar.make(binding.getRoot(), "Deletion undone", Snackbar.LENGTH_SHORT).show();
    }
}

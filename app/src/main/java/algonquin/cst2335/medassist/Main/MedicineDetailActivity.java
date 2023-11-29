package algonquin.cst2335.medassist.Main;

import android.os.Bundle;
import android.util.Log;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import algonquin.cst2335.medassist.Medicine.Doctor;
import algonquin.cst2335.medassist.Medicine.MedDatabase;
import algonquin.cst2335.medassist.Medicine.Medicine;
import algonquin.cst2335.medassist.databinding.MedFragmentBinding;

public class MedicineDetailActivity extends AppCompatActivity {
    MedFragmentBinding binding;
    Button deleteButton;
    Button updateButton;
    private Medicine medicineToDelete;
    private Doctor doctorToDelete;
    private RecyclerViewInterface recyclerViewInterface;
    private Medicine medicineToUpdate;
    private Medicine originalMedicine;

    private Doctor docToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MedFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        deleteButton = binding.medDelete;
        updateButton = binding.medUpdate;

        // Retrieve the selected medicine from the intent
        Medicine medicine = getIntent().getParcelableExtra("medicine");
        Doctor doctor = getIntent().getParcelableExtra("doctor");
        int position = getIntent().getIntExtra("position", -1);

        Log.d("durationAtStart",medicine.getDuration());

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
            doctorToDelete = doctor;
            //Allows user to undo deletion in the event of misclick
            Snackbar snackbar = Snackbar.make(view, "Medicine deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo", v -> undoDeletion(medicineToDelete));

            snackbar.show();

            //Deletes medicine
            deleteMedicine();

            if (recyclerViewInterface != null) {
                recyclerViewInterface.onMedicineDeleted();
            }
            new Handler().postDelayed(() -> finish(), 7000);
        });

        updateButton.setOnClickListener(view ->{

            if (binding.medUpdate.getText().toString().equals("update")){
                //makes the buttons editable
                binding.medName.setFocusable(true);
                binding.medName.setFocusableInTouchMode(true);
                binding.medDosageValue.setFocusable(true);
                binding.medDosageValue.setFocusableInTouchMode(true);
                binding.medFrequencyValue.setFocusable(true);
                binding.medFrequencyValue.setFocusableInTouchMode(true);
                binding.medQuantityValue.setFocusable(true);
                binding.medQuantityValue.setFocusableInTouchMode(true);
                binding.medRefillValue.setFocusable(true);
                binding.medRefillValue.setFocusableInTouchMode(true);
                binding.medDurationValue.setFocusable(true);
                binding.medDurationValue.setFocusableInTouchMode(true);
                binding.medExpirationValue.setFocusable(true);
                binding.medExpirationValue.setFocusableInTouchMode(true);
                binding.medDocNameValue.setFocusable(true);
                binding.medDocNameValue.setFocusableInTouchMode(true);
                binding.medDocNumberValue.setFocusable(true);
                binding.medDocNumberValue.setFocusableInTouchMode(true);
                binding.medInstructionsValue.setFocusable(true);
                binding.medInstructionsValue.setFocusableInTouchMode(true);

                //stores original values
                storeOriginalValues();
                //Allows user to undo deletion in the event of misclick
                Snackbar snackbar = Snackbar.make(view, "Updating medicine", Snackbar.LENGTH_LONG);
                snackbar.show();

                //change the button to confirm
                binding.medUpdate.setText("confirm");
            }
            else if (binding.medUpdate.getText().toString().equals("confirm")){

                AddFragment addFragment = new AddFragment();

                //values to check
                String medName = binding.medName.getText().toString();
                String dosage = binding.medDosageValue.getText().toString();
                String quantity = binding.medQuantityValue.getText().toString();
                String frequency = binding.medFrequencyValue.getText().toString();
                String refills = binding.medRefillValue.getText().toString();
                String duration = binding.medDurationValue.getText().toString();
                String expiration = binding.medExpirationValue.getText().toString();
                String docName = binding.medDocNameValue.getText().toString();
                String docNumber = binding.medDocNumberValue.getText().toString();
                String instructions = binding.medInstructionsValue.getText().toString();

                // Call the isInputValid method
                boolean isValid = addFragment.isInputValid(medName, dosage, quantity, frequency, refills, duration, expiration, instructions, docName, docNumber);

                // Check the result
                if (isValid && addFragment.isValidDate(duration, true) && addFragment.isValidDate(expiration, true)) {
                    updateMedicine(medicine.getId());

                    //makes the buttons editable
                    binding.medName.setFocusable(false);
                    binding.medName.setFocusableInTouchMode(false);
                    binding.medDosageValue.setFocusable(false);
                    binding.medDosageValue.setFocusableInTouchMode(false);
                    binding.medFrequencyValue.setFocusable(false);
                    binding.medFrequencyValue.setFocusableInTouchMode(false);
                    binding.medQuantityValue.setFocusable(false);
                    binding.medQuantityValue.setFocusableInTouchMode(false);
                    binding.medRefillValue.setFocusable(false);
                    binding.medRefillValue.setFocusableInTouchMode(false);
                    binding.medDurationValue.setFocusable(false);
                    binding.medDurationValue.setFocusableInTouchMode(false);
                    binding.medExpirationValue.setFocusable(false);
                    binding.medExpirationValue.setFocusableInTouchMode(false);
                    binding.medDocNameValue.setFocusable(false);
                    binding.medDocNameValue.setFocusableInTouchMode(false);
                    binding.medDocNumberValue.setFocusable(false);
                    binding.medDocNumberValue.setFocusableInTouchMode(false);
                    binding.medInstructionsValue.setFocusable(false);
                    binding.medInstructionsValue.setFocusableInTouchMode(false);

                    //Allows user to undo deletion in the event of misclick
                    Snackbar snackbar = Snackbar.make(view, "Medicine information updated", Snackbar.LENGTH_LONG)
                    .setAction("Undo", v -> undoUpdate(medicine.getId()));

                    snackbar.show();

                    binding.medUpdate.setText("update");

                }
                else {
                    // The input is not valid
                    // Show an error message or handle accordingly
                    Toast.makeText(this, "Invalid input. Please check the fields.", Toast.LENGTH_SHORT).show();
                }

            }


        });
    }

    /**
     * Deletes the medicine from Database
     */
    private void deleteMedicine() {
        // Call the method from MedDatabase to delete the record
        MedDatabase dbHelper = new MedDatabase(this);
        dbHelper.deleteMedicine(medicineToDelete, doctorToDelete);
    }

    /**
     * Undoes the deletion of a Medicine by restoring it to the database.
     *
     * @param medicineToDelete The Medicine object representing the deleted medicine.
     */
    private void undoDeletion(Medicine medicineToDelete) {
        // Restore the medicine to the database
        MedDatabase dbHelper = new MedDatabase(this);
        dbHelper.insertMedicine(medicineToDelete, doctorToDelete);

        // Show a message indicating that the deletion has been undone
        Snackbar.make(binding.getRoot(), "Undo Deletion?", Snackbar.LENGTH_LONG).show();
    }

    /**
     * Updates the medicine from Database
     */
    private void updateMedicine(long medicineId) {
        Log.d("UpdateMedicine", "updateMedicine is called");
        // Retrieve the updated values from the UI
        String medName = binding.medName.getText().toString();
        String dosage = binding.medDosageValue.getText().toString();
        String quantity = binding.medQuantityValue.getText().toString();
        String frequency = binding.medFrequencyValue.getText().toString();
        String refills = binding.medRefillValue.getText().toString();
        String duration = binding.medDurationValue.getText().toString();
        String expiration = binding.medExpirationValue.getText().toString();
        String docName = binding.medDocNameValue.getText().toString();
        String docNumber = binding.medDocNumberValue.getText().toString();
        String instructions = binding.medInstructionsValue.getText().toString();

        // Create a Medicine object with updated values
        medicineToUpdate = new Medicine(medName, dosage, quantity, frequency, refills, duration, expiration, instructions);
        medicineToUpdate.setId(medicineId);

        docToUpdate = new Doctor(docName, docNumber);

        //todo missing updateDocotor.

        // Call the method from MedDatabase to update the record
        MedDatabase dbHelper = new MedDatabase(this);
        dbHelper.updateMedicine(medicineToUpdate.getId(), medicineToUpdate);
    }

    /**
     * Stores the original values of the Medicine object before it is updated
     */
    private void storeOriginalValues() {
        originalMedicine = new Medicine(
                binding.medName.getText().toString(),
                binding.medDosageValue.getText().toString(),
                binding.medQuantityValue.getText().toString(),
                binding.medFrequencyValue.getText().toString(),
                binding.medRefillValue.getText().toString(),
                binding.medDurationValue.getText().toString(),
                binding.medExpirationValue.getText().toString(),
                binding.medInstructionsValue.getText().toString()
        );
    }

    /**
     * Restores the original values to the UI and updates the record in the database
     */
    private void undoUpdate(long medicineId) {
        Log.d("UndoUpdate", "undoUpdate is called");
        if (originalMedicine != null) {
            // Restore the original values to the UI
            binding.medName.setText(originalMedicine.getName());
            binding.medDosageValue.setText(originalMedicine.getDosage());
            binding.medFrequencyValue.setText(originalMedicine.getFrequency());
            binding.medQuantityValue.setText(originalMedicine.getQuantity());
            binding.medRefillValue.setText(originalMedicine.getRefills());
            binding.medDurationValue.setText(originalMedicine.getDuration());
            binding.medExpirationValue.setText(originalMedicine.getExpiration());
            binding.medInstructionsValue.setText(originalMedicine.getInstructions());
            originalMedicine.setId(medicineId);

            // Call the method from MedDatabase to update the record with original values
            MedDatabase dbHelper = new MedDatabase(this);
            dbHelper.updateMedicine(originalMedicine.getId(), originalMedicine); // Assuming you don't need to update the doctor info

            // Clear the originalMedicine variable
            originalMedicine = null;
        }
    }
}

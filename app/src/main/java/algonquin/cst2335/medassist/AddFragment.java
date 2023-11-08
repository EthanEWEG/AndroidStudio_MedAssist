package algonquin.cst2335.medassist;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.util.concurrent.ListenableFuture;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import algonquin.cst2335.medassist.databinding.AddFragmentBinding;

import android.content.pm.PackageManager;
import android.Manifest;
import android.widget.Toast;
import androidx.lifecycle.LifecycleOwner;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class AddFragment extends DialogFragment{

    AddFragmentBinding binding;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    // Define a constant for camera permission request
    private static final int REQUEST_CAMERA_PERMISSION = 1;


//    private void refreshMedicineList() {
//        MedDatabase medDb = new MedDatabase(requireContext());
//        List<Medicine> medicineList = medDb.getAllMedicines();
//        MedicineAdapter adapter = new MedicineAdapter(medicineList, this);
//        RecyclerView recyclerView = requireActivity().findViewById(R.id.recyclerView);
//        recyclerView.setAdapter(adapter);
//    }
    private void setMedicineAdapterInMainActivity() {
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setMedicineAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate your custom layout
        binding = AddFragmentBinding.inflate(inflater, container, false);
        View view = (binding.getRoot());

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        binding.cameraButton.setOnClickListener(v -> {
                //closes camera (or else itd stay open but hidden)
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    cameraProvider.unbindAll(); // Release camera resources
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                if (checkCameraPermissions()) {
                    startCamera();
                } else {
                    requestCameraPermissions();
                }

        });

        //Submits info for med
        binding.submitButton.setOnClickListener(click ->
        {
            // Retrieve user input for Medicine Name, Dosage, Quantity, Frequency, Refills, Duration, Expiration date, and special instructions
            String medName = binding.editMedName.getText().toString();
            String dosage = binding.editDosage.getText().toString();
            String quantity = binding.editQuantity.getText().toString();
            String frequency = binding.editFrequency.getText().toString();
            String refills = binding.editRefills.getText().toString();
            String duration = binding.editDuration.getText().toString();
            String expiration = binding.editExpiration.getText().toString();
            String instructions = binding.editInstructions.getText().toString();
            if (instructions.isEmpty()){
                instructions = "No special Instructions were specified";
            }
            // Create a Medicine object with user input
            Medicine newMedicine = new Medicine(medName, dosage, quantity, frequency, refills, duration, expiration, instructions);

            if(isInputValid(medName, dosage, quantity, frequency, refills, duration, expiration, instructions) && isValidDate(duration) && isValidDate(expiration)) {
                // Insert the new medicine into the database
                MedDatabase medDb = new MedDatabase(requireContext());
                long newRowId = medDb.insertMedicine(newMedicine);

                if (newRowId != -1) {
                    // Successfully added the medicine to the database, refresh the RecyclerView
                    //refreshMedicineList();
                    setMedicineAdapterInMainActivity();
                    // Close the AddFragment or update UI as needed
                    dismiss();
                } else {
                    // Handle insertion failure
                    Toast.makeText(requireContext(), "Failed to add medicine. Please try again.", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(requireContext(), "Please fill out all the fields correctly. Use the format YYYY/MM/DD for duration and expiration.", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
    private boolean isInputValid(String medName, String dosage, String quantity, String frequency, String refills, String duration, String expiration, String instructions){
        return !medName.isEmpty() && !dosage.isEmpty() && !quantity.isEmpty() && !frequency.isEmpty() && !refills.isEmpty() && !duration.isEmpty() && !expiration.isEmpty() && !instructions.isEmpty();
    }

    private boolean isValidDate(String date){
        if(date.matches("\\d{4}/\\d{2}/\\d{2}")){
            try{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                sdf.setLenient(false);
                sdf.parse(date);
                return true;
            } catch (ParseException e) {
                return false;
            }
        }
        return false;
    }
    private boolean checkCameraPermissions() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    //Starts up the camera for scanning
    private void startCamera() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setPrompt("Scan a QR Code");
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        scanActivityResultLauncher.launch(intentIntegrator.createScanIntent());
    }

    private final ActivityResultLauncher<Intent> scanActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    IntentResult scanResult = IntentIntegrator.parseActivityResult(result.getResultCode(), result.getData());
                    if (scanResult != null) {
                        // Handles the scanned QR code data here
                        String qrCodeData = scanResult.getContents();

                        //Confirm QR read variable
                        boolean readQRConfirm = false;

                        //QR code keys for each value
                        //test qr entry : name=Advil.dosage=5.quantity=150.frequency=6 times a day max.refills=0.duration=2024/01/01.expiration=2026/01/01.instructions=Keep away from children
                        //name=, dosage=, quantity=, frequency=, refills=, duration=, expiration=, instructions=
                        String[] QRdata = qrCodeData.split("\\.");
                        for (String data : QRdata) {
                            if (data.startsWith("name=")) {
                                binding.editMedName.setText(data.substring("name=".length()).trim());
                                readQRConfirm = true;
                            } else if (data.startsWith("dosage=")) {
                                binding.editDosage.setText(data.substring("dosage=".length()).trim());
                                readQRConfirm = true;
                            } else if (data.startsWith("quantity=")) {
                                binding.editQuantity.setText(data.substring("quantity=".length()).trim());
                                readQRConfirm = true;
                            } else if (data.startsWith("frequency=")) {
                                binding.editFrequency.setText(data.substring("frequency=".length()).trim());
                                readQRConfirm = true;
                            } else if (data.startsWith("refills=")) {
                                binding.editRefills.setText(data.substring("refills=".length()).trim());
                                readQRConfirm = true;
                            } else if (data.startsWith("duration=")) {
                                binding.editDuration.setText(data.substring("duration=".length()).trim());
                                readQRConfirm = true;
                            } else if (data.startsWith("expiration=")) {
                                binding.editExpiration.setText(data.substring("expiration=".length()).trim());
                                readQRConfirm = true;
                            } else if (data.startsWith("instructions=")) {
                                binding.editInstructions.setText(data.substring("instructions=".length()).trim());
                                readQRConfirm = true;
                            }
                        }

                        if(readQRConfirm){
                            Toast toast = Toast.makeText(this.getActivity(),"QR Read Successful", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else{
                            Toast toast = Toast.makeText(this.getActivity(),"QR Read Failed", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                    }
                }
            }
    );

}
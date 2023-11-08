package algonquin.cst2335.medassist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
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

public class AddFragment extends DialogFragment{

    AddFragmentBinding binding;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView cameraPreviewView;
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

        cameraPreviewView = binding.cameraPreview; // Initialize the camera preview view

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        binding.cameraButton.setOnClickListener(v -> {
            // Permissions are granted, proceed with camera setup
            closeKeyboard();

            // Change the visibility of items to make visible/invisible
            if (cameraPreviewView.getVisibility() == View.VISIBLE) {
                cameraPreviewView.setVisibility(View.GONE);
                binding.captureButton.setVisibility(View.GONE);
                binding.submitButton.setVisibility(View.VISIBLE);

                //show medical input items
                inputElements(2);

                //closes camera (or else itd stay open but hidden)
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    cameraProvider.unbindAll(); // Release camera resources
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                cameraPreviewView.setVisibility(View.VISIBLE);
                binding.captureButton.setVisibility(View.VISIBLE);
                binding.submitButton.setVisibility(View.GONE);

                //hide medical input items
                inputElements(1);

                if (cameraPreviewView.getVisibility() == View.VISIBLE) {
                    if (checkCameraPermissions()) {
                        startCamera();
                    } else {
                        requestCameraPermissions();
                    }
                }
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

    //Sets up the camera for its preview
    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Bind the camera preview to the PreviewView
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(cameraPreviewView.getSurfaceProvider());

                // Sets up the image capture use case if needed
                ImageCapture imageCapture = new ImageCapture.Builder()
                        .setTargetRotation(requireActivity().getWindowManager().getDefaultDisplay().getRotation())
                        .build();

                // Create a CameraSelector and select the desired camera (e.g., front or rear)
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Attach the use cases to the camera
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle((LifecycleOwner) requireActivity(), cameraSelector, preview, imageCapture);



            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    //closeKeyboard() method to close the keyboard when clicking cameraPreview button
    private void closeKeyboard() {
        View currentFocusView = requireActivity().getCurrentFocus();
        if (currentFocusView != null) {
            currentFocusView.clearFocus();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocusView.getWindowToken(), 0);
        }
    }

    private void inputElements(int i) {
        if (i == 1) {
            binding.editMedName.setVisibility(View.GONE);
            binding.textMedName.setVisibility(View.GONE);
            binding.editDosage.setVisibility(View.GONE);
            binding.textDosage.setVisibility(View.GONE);
            binding.dosageUnit.setVisibility(View.GONE);
            binding.editQuantity.setVisibility(View.GONE);
            binding.textQuantity.setVisibility(View.GONE);
            binding.editFrequency.setVisibility(View.GONE);
            binding.textFrequency.setVisibility(View.GONE);
            binding.editRefills.setVisibility(View.GONE);
            binding.textRefill.setVisibility(View.GONE);
            binding.editDuration.setVisibility(View.GONE);
            binding.textDuration.setVisibility(View.GONE);
            binding.editExpiration.setVisibility(View.GONE);
            binding.textExpiration.setVisibility(View.GONE);
            binding.editInstructions.setVisibility(View.GONE);
            binding.textInstructions.setVisibility(View.GONE);
        }
        else {
            binding.editMedName.setVisibility(View.VISIBLE);
            binding.textMedName.setVisibility(View.VISIBLE);
            binding.editDosage.setVisibility(View.VISIBLE);
            binding.textDosage.setVisibility(View.VISIBLE);
            binding.dosageUnit.setVisibility(View.VISIBLE);
            binding.editQuantity.setVisibility(View.VISIBLE);
            binding.textQuantity.setVisibility(View.VISIBLE);
            binding.editFrequency.setVisibility(View.VISIBLE);
            binding.textFrequency.setVisibility(View.VISIBLE);
            binding.editRefills.setVisibility(View.VISIBLE);
            binding.textRefill.setVisibility(View.VISIBLE);
            binding.editDuration.setVisibility(View.VISIBLE);
            binding.textDuration.setVisibility(View.VISIBLE);
            binding.editExpiration.setVisibility(View.VISIBLE);
            binding.textExpiration.setVisibility(View.VISIBLE);
            binding.editInstructions.setVisibility(View.VISIBLE);
            binding.textInstructions.setVisibility(View.VISIBLE);
        }
    }
}
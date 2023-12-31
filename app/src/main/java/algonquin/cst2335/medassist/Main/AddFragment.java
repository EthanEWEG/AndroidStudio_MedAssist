package algonquin.cst2335.medassist.Main;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import algonquin.cst2335.medassist.Medicine.Doctor;
import algonquin.cst2335.medassist.Medicine.MedDatabase;
import algonquin.cst2335.medassist.Medicine.Medicine;
import algonquin.cst2335.medassist.databinding.AddFragmentBinding;
import algonquin.cst2335.medassist.databinding.MedViewBinding;

import android.content.pm.PackageManager;
import android.Manifest;
import android.widget.Toast;
import androidx.lifecycle.LifecycleOwner;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class AddFragment extends DialogFragment{

    AddFragmentBinding binding;
    private RecyclerViewInterface recyclerViewInterface;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    // Define a constant for camera permission request
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private int currentTabId;
    private Context context;

    public AddFragment(){}
    public AddFragment(int currentTabId) {
        this.currentTabId = currentTabId;
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


        /**
         * Submits information for medicine
         */
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
            String docName = binding.editDocName.getText().toString();
            String docNumber = binding.editDocNumber.getText().toString();
            String instructions = binding.editInstructions.getText().toString();

            String[] values = {medName, dosage, quantity, frequency, refills, duration, expiration, docName, docNumber, instructions};
            for (int i = 1; i < values.length; i++) {
                    if(values[1].isEmpty() || values[2].isEmpty() || values[3].isEmpty() || values[4].isEmpty()){
                        values[i] = "0";
                    }
                    if(i == 7 && values[i].isEmpty()){
                        values[7] = "No Doctor was provided";
                    }
                    if(i == 8 && values[i].isEmpty()){
                        values[8] = "No phone number provided";
                    }
                    if (i == 9 && values[9].isEmpty()){
                        values[9] = "No special Instructions were specified";
                    }

            }
            if(duration.isEmpty()){
                duration = expiration;
            }
//
            if (isInitialInputValid(medName, expiration)){
            //if(isValidDate(duration, true) && isValidDate(expiration, true)) {
                // Create a Medicine object with user input
                Medicine newMedicine = new Medicine(medName, dosage, quantity, frequency, refills, duration, expiration, instructions);
                Doctor newDoc = new Doctor(docName, docNumber);

                if(isValidDate(requireContext(), expiration, true) && checkDateIsAfterToday(requireContext(), expiration)) {
                //if (isInputValid(medName, expiration)) {
                    // Insert the new medicine into the database
                    MedDatabase medDb = new MedDatabase(requireContext());

                    long newRowId = medDb.insertMedicine(newMedicine, newDoc);


                    if (newRowId != -1) {
                        // Successfully added the medicine to the database, refresh the RecyclerView
                        if(recyclerViewInterface != null){
                            recyclerViewInterface.onMedicineAdded();
                        }
                        // Close the AddFragment or update UI as needed
                        dismiss();
                    } else {
                        // Handle insertion failure
                        Toast.makeText(requireContext(), "Failed to add medicine. Please try again.", Toast.LENGTH_LONG).show();
                    }
                } else{
                    Toast.makeText(requireContext(), "Please Double check your date values.", Toast.LENGTH_LONG).show();
                }

            }
            callMainActivityMethod();
        });

        return view;
    }

    /**
     * Checks the validity of phone number that the user entered. It must be 10 digits long
     * @param docNumber - The number entered by user
     * @return - The doctors number if valid, else returns null.
     */
    public String validateAndFormatPhoneNumber(String docNumber) {
        docNumber = docNumber.replaceAll("[^0-9]", "");

        if (docNumber.length() == 10) {
            return docNumber;
        } else {
            //Toast.makeText(context, "Invalid phone number. Please enter a 10-digit phone number.", Toast.LENGTH_LONG).show();
            showToast(context, "Invalid phone number. Please enter a 10-digit phone number.");
            return null;
        }
    }

    /**
     * Checks to see if each input is valid
     * @param medName - Name of medicine
     * @param expiration - The expiration of medicine
     * @return
     */
    private boolean isInitialInputValid(String medName, String expiration){

        String[] fields = {"Medication Name", "Expiration"};
        int empFieldCount = 0;

        for(int i = 0; i < fields.length;i++){
            String value = getInitialValueByIndex(i, medName, expiration);

            if (value.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter " + fields[i] + " value.", Toast.LENGTH_LONG).show();
                empFieldCount++;
            }

            if (empFieldCount > 0) {
                Toast.makeText(requireContext(), "Please enter values for Medicine Name and Expiration Date", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    public boolean isInputValid(Context context, Boolean currentMed, String medName, String dosage, String quantity, String frequency, String refills, String duration, String expiration, String instructions, String docName, String docNumber){
//        return !medName.isEmpty() && !dosage.isEmpty() && !quantity.isEmpty() && !frequency.isEmpty() && !refills.isEmpty() && !duration.isEmpty() && !expiration.isEmpty() && !instructions.isEmpty();
        String[] fields = {"Medication Name", "Dosage", "Quantity", "Frequency", "Refills", "Duration", "Expiration","Doctor Name", "Doctor Number"};


        /**
         * adjust to check individual fields, make sure its correct data type
         * dosage, quantity, frequency, refills and docNumber already numbers,
         *
         * check strings duration, medName, doc name
         */
        for(int i = 0; i < fields.length;i++){
            String value = getValueByIndex(i, medName, dosage, quantity, frequency, refills, duration, expiration, instructions, docName, docNumber);

            if (context == null) {
                return false; // Handle the case where the context is null
            }

            switch(fields[i]){
                case "Medication Name":
                    if (value.trim().isEmpty()) {
                        //Toast.makeText(context, "Medication Name cannot be empty.", Toast.LENGTH_SHORT).show();
                        showToast(context, "Medication Name cannot be empty.");
                        return false;
                    }
                    break;
                case "Dosage":
                    if (!value.trim().isEmpty() && Double.parseDouble(value) < 0) {
                        //Toast.makeText(context, "Dosage cannot be below 0.", Toast.LENGTH_SHORT).show();
                        showToast(context, "Dosage cannot be below 0.");
                        return false;
                    }
                    break;
                case "Quantity":
                    if (!value.trim().isEmpty() && Integer.parseInt(value) < 0) {
                        Toast.makeText(context, "Quantity cannot be below 0.", Toast.LENGTH_SHORT).show();
                        showToast(context, "Quantity cannot be below 0.");
                        return false;
                    }
                    break;
                case "Frequency":
                    if (value.trim().isEmpty()) {
                        // Frequency can be empty
                        return true;
                    }
                    if (!value.matches(".*\\d+.*")) {
                        //Toast.makeText(context, "Frequency should contain a number value.", Toast.LENGTH_SHORT).show();
                        showToast(context, "Frequency should contain a number value.");
                        return false;
                    }
                    break;
                case "Refills":
                    if (!value.trim().isEmpty() && Integer.parseInt(value) < 0) {
                        //Toast.makeText(context, "Refills cannot be below 0.", Toast.LENGTH_SHORT).show();
                        showToast(context, "Refills cannot be below 0.");
                        return false;
                    }
                    break;
                case "Doctor Number":
                    if (!value.trim().isEmpty()) {
                        // Validate and format the phone number
                        String formattedNumber = validateAndFormatPhoneNumber(value);
                        if (formattedNumber == null) {
                            return false; // Invalid phone number format
                        }
                        // Update the value with the formatted phone number
                        value = formattedNumber;
                    }
                    break;
                case "Duration":
                    if(value.trim().isEmpty()){
                        value.equals(fields[6]);
                    }
                     break;
                case "Expiration":
                    if (currentMed) {
                        // Call an existing method isValidDate for current medicine
                        return isValidDate(context, value, true);
                    }else{
                        if (isValidDate(context, value, true)) {
                            return true;
                        } else {
                            showToast(context, "Invalid date entered");
                        }
                    }
                    // For past medicine, keep the previous value
                    break;
                case "Doctor Name":
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    private void showToast(Context context, String message) {
        // You can use the application context to ensure the Toast is displayed even if the calling class is not an Activity
        Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * Obtains the value of each field and checks to see if it is empty. Else, it will return value.
     * @param index - The ID
     * @param medName - The name of medicine
     * @param expiration - The expiration of medicine

     * @return - Values for each textField
     */
    private String getInitialValueByIndex(int index, String medName, String expiration) {
        switch (index) {
            case 0:
                return medName;
            case 1:
                return expiration;
            default:
                return "";
        }
    }

    private String getValueByIndex(int index, String medName, String dosage, String quantity, String frequency, String refills, String duration, String expiration, String instructions, String docName, String docNumber) {
        switch (index) {
            case 0:
                return medName;
            case 1:
                return dosage;
            case 2:
                return quantity;
            case 3:
                return frequency;
            case 4:
                return refills;
            case 5:
                return duration;
            case 6:
                return expiration;
            case 7:
                return instructions;
            case 8:
                return docName;
            case 9:
                return docNumber;
            default:
                return "";
        }
    }

    /**
     * Checks the date entered by user to follow standard protocol. E.g., date entered is after current
     * date, date entered respects the day of month. For example, 2023/02/30 will return false,
     * 2023/02/28 will return true.
     * @param date - Date entered by user.
     * @param checkValidity - True to check validity of date
     * @return
     */
    public boolean isValidDate(Context context, String date, boolean checkValidity){
        if (context == null) {
            // Handle the case where the context is null, e.g., return false or throw an exception
            return false;
        }

        if (date.matches("\\d{4}/\\d{2}/\\d{2}")) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                sdf.setLenient(false);
                Date parsedDate = sdf.parse(date);

                if (checkDateValidity(parsedDate) && (checkValidity /*&& checkDateIsAfterToday(context, parsedDate)*/)) {
                    return true;
                }
            } catch (ParseException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Checks if the month is greater than 12 or less than 1, and the day is within the valid range for the month.
     * If invalid, shows a toast and returns false.
     * @param date - The date entered by the user
     * @return - True if the date entered is valid e.g., 2023/03/29. False if date entered is not
     *          valid e.g., 2023/02/30
     */
    private boolean checkDateValidity(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int month = calendar.get(Calendar.MONTH) + 1; // Month is 0-based in Calendar
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.w("checkDateValidity: ", calendar.toString());
        if (month < 1 || month > 12) {
            Toast.makeText(requireContext(), "Invalid month. Please enter a month between 1 and 12.", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check if the day is within the valid range for the month
        int maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (day < 1 || day > maxDaysInMonth) {
            Toast.makeText(requireContext(), "Invalid day. Please enter a valid day for the selected month.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    /**
     * Checks if the date is after today's date. If not, shows a toast and returns false.
     * @param date - Date entered by user
     * @return - True, if the date entered is after current date. False, if date entered is before
     *           current date.
     */
    private boolean checkDateIsAfterToday(Context context, String date) {
        if (context == null) {
            // Handle the case where the context is null, e.g., return false or throw an exception
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            sdf.setLenient(false);
            Date parsedDate = sdf.parse(date);

            long dateMillis = parsedDate.getTime();
            long currentDateMillis = System.currentTimeMillis();

            if (dateMillis < currentDateMillis) {
                //Toast.makeText(requireContext(), "Invalid date. Please enter a date after today.", Toast.LENGTH_LONG).show();
                showToast(context, "Invalid date. Please enter a date after today.");
                return false;
            }

            return true;
        }catch(ParseException e){
            return false;
        }

    }

    /**
     * Checks whether the camera permission is granted
     * @return - {@code true} if the camera permission is granted, {@code false} otherwise.
     */
    private boolean checkCameraPermissions() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests camera permissions from the user.
     * The result of the permission request will be handled in the onRequestPermissionsResult method.
     */
    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    /**
     * Initiates the process of starting the camera for scanning QR codes.
     * Uses the IntentIntegrator to configure the camera settings and launches the scanning activity.
     * The result of the scanning activity will be handled by the scanActivityResultLauncher.
     */
    private void startCamera() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setPrompt("Scan a QR Code");
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        scanActivityResultLauncher.launch(intentIntegrator.createScanIntent());
    }

    /**
     * Activity result launcher for handling the result of the QR code scanning activity.
     * Uses IntentIntegrator to parse the scanning result and extracts information from the scanned QR code.
     * The scanned data is then populated into corresponding EditText fields in the UI.
     * Displays a Toast message indicating the success or failure of the QR code reading.
     */
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

    private void callMainActivityMethod() {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setCurrentMedicineAdapter();
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
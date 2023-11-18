package algonquin.cst2335.medassist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import algonquin.cst2335.medassist.databinding.MedViewBinding;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface{

    private boolean isAddFragmentVisible = false;
    private boolean isSearchFragmentVisible = false;

    MedViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MedViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MedDatabase medDb = new MedDatabase(this);

        //medDb.deleteMostRecentMedicine();

        binding.current.setOnClickListener(click -> {
            //Hides past recycler view -> displays current recycler view
            //Removes the current fragment (if any)
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentLocation);
            if (currentFragment != null) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.remove(currentFragment);
                fragmentTransaction.commit();
            }
            //resets add/searchFragment flag
            isAddFragmentVisible = false;
            isSearchFragmentVisible = false;

            List<Medicine> medicineList = medDb.getAllMedicines();
            List<Medicine> currentMedList = new ArrayList<>();

            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            if(!medicineList.isEmpty()) {
                for (Medicine medicine : medicineList) {
                    String expirationDateString = medicine.getExpiration();
                    LocalDate expirationDate = LocalDate.parse(expirationDateString, formatter);
                    if (currentDate.isBefore(expirationDate)) {
                        currentMedList.add(medicine);
                    }
                }
            }
            AtomicReference<MedicineAdapter> adapter = new AtomicReference<>(new MedicineAdapter(currentMedList, this));
            recyclerView.setAdapter(adapter.get());

        });
        binding.current.performClick();

        binding.past.setOnClickListener(click -> {
            //Hides current recycler view -> displays past recycler view
            //Removes the current fragment (if any)
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentLocation);
            if (currentFragment != null) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.remove(currentFragment);
                fragmentTransaction.commit();
            }
            //resets add/searchFragment flag
            isAddFragmentVisible = false;
            isSearchFragmentVisible = false;

            List<Medicine> medicineList = medDb.getAllMedicines();
            List<Medicine> currentMedList = new ArrayList<>();

            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

            for(Medicine medicine : medicineList){
                String expirationDateString = medicine.getExpiration();
                LocalDate expirationDate = LocalDate.parse(expirationDateString, formatter);

                if(currentDate.isAfter(expirationDate)){
                    currentMedList.add(medicine);
                }
            }
            AtomicReference<MedicineAdapter> adapter = new AtomicReference<>(new MedicineAdapter(currentMedList, this));
            recyclerView.setAdapter(adapter.get());
        });

        binding.add.setOnClickListener(click -> {

            //resets searchFragment flag
            isSearchFragmentVisible = false;

            if (!isAddFragmentVisible) {
                AddFragment addFragment = new AddFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                // Replace the current fragment with the AddFragment
                fragmentTransaction.replace(R.id.fragmentLocation, addFragment);
                fragmentTransaction.commit();

                isAddFragmentVisible = true;
            }

        });


        binding.search.setOnClickListener(click -> {

            //resets addFragment flag
            isAddFragmentVisible = false;

            if (!isSearchFragmentVisible) {
                SearchFragment searchFragment = new SearchFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                // Replace the current fragment with the AddFragment
                fragmentTransaction.replace(R.id.fragmentLocation, searchFragment);
                fragmentTransaction.commit();

                isSearchFragmentVisible = true;
            }

        });



        binding.settings.setOnClickListener(click -> {

            //open settings dialog box

        });

    }

    public void setMedicineAdapter() {
        MedDatabase medDb = new MedDatabase(this);
        List<Medicine> medicineList = medDb.getAllMedicines();
        MedicineAdapter adapter = new MedicineAdapter(medicineList, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        MedDatabase medDb = new MedDatabase(this);
        List<Medicine> medList = medDb.getAllMedicines();
        Medicine medicine = medList.get(position);

        Intent intent = new Intent(this, MedicineDetailActivity.class);
        intent.putExtra("medicine", medicine);
        startActivity(intent);
    }


    public void calendar(View view) {

        //dialog box for Alerts
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View alertView = getLayoutInflater().inflate(R.layout.notification_alertdialog,null);
        AlertDialog dialog = builder.create();
        dialog.setView(alertView);

        TextView alertTitle = alertView.findViewById(R.id.alertTitle);
        Button alertDate = alertView.findViewById(R.id.dateButton);
        Button alertTime = alertView.findViewById(R.id.clockButton);
        Button alertDay = alertView.findViewById(R.id.repeatDayButton);
        Button alertWeek = alertView.findViewById(R.id.repeatWeekButton);
        Button alertMonth = alertView.findViewById(R.id.repeatMonthButton);
        Button alertYear = alertView.findViewById(R.id.repeatYearButton);
        ImageButton alertCancel = alertView.findViewById(R.id.cancelButton);
        Spinner alertFrequency = alertView.findViewById(R.id.spinnerFrequency);
        Spinner alertEnd = alertView.findViewById(R.id.spinnerEnd);
        Spinner alertAlert = alertView.findViewById(R.id.spinnerAlert);
        CalendarView calendarView = (CalendarView) alertView.findViewById(R.id.calendarView);
        TimePicker timerPicker = alertView.findViewById(R.id.timePicker);

        //set the title to medicines name
        //alertTitle.setText();

        // Set the date and time to the current date and time
        Calendar currentDateTime = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        alertDate.setText(dateFormat.format(currentDateTime.getTime()));
        alertTime.setText(timeFormat.format(currentDateTime.getTime()));

        //calenderView
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            // Construct the selected date in the desired format
            alertDate.setText(String.format(Locale.getDefault(), "%04d/%02d/%02d", year, month + 1, dayOfMonth));
        });

        //timePicker
        timerPicker.setOnTimeChangedListener((view12, hourOfDay, minute) -> {
            String amPm;
            if (hourOfDay < 12) {
                amPm = "AM";
            } else {
                amPm = "PM";
                if (hourOfDay > 12) {
                    hourOfDay -= 12;
                }
            }

            // Construct the selected time in the desired format
            alertTime.setText(String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay, minute, amPm));
        });

        //List options for when the notifications end
        List<String> endOptions = new ArrayList<>();
        endOptions.add("Ends never");
        // Generate "Ends after 1, 2, 3...120 occurrences"
        for (int i = 1; i <= 120; i++) {
            endOptions.add("Ends after " + i + " occurrences");
        }

        //List options for when to receive the notification
        String[] alertAlertOptions = {"Event end time", "0 minutes", "1 minute", "5 minutes", "10 minutes", "15 minutes", "20 minutes", "30 minutes", "45 minutes", "60 minutes",
                "90 minutes", "2 hours", "3 hours", "4 hours", "6 hours", "8 hours", "12 hours", "18 hours", "24 hours", "2 days", "3 days", "5 days", "7 days", "10 days", "14 days", "21 days", "28 days"};

        //String to chose which day week month or year button should display
        final String[] DWMY = { "day" };
        //List options for when the notifications end
        List<String> freqOptions = new ArrayList<>();


        // Initialize the adapter for alertEnd spinner
        ArrayAdapter<String> endAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, endOptions);
        endAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alertEnd.setAdapter(endAdapter);

        // Initialize the adapter for alertAlert spinner
        ArrayAdapter<String> alertAlertAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, alertAlertOptions);
        alertAlertAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alertAlert.setAdapter(alertAlertAdapter);
        //sets the default value to 15 minutes (index 5)
        alertAlert.setSelection(5);

        // Initialize the adapter for alertFrequency spinner
        ArrayAdapter<String> freqAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, freqOptions);
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alertFrequency.setAdapter(freqAdapter);


        // Nested method to update the frequency options based on DWMY
        Runnable updateFreqOptions = () -> {
            List<String> newFreqOptions = new ArrayList<>();
            newFreqOptions.add("Never");

            for (int i = 1; i <= 120; i++) {
                if (i > 2) {
                    newFreqOptions.add("Every " + i + " " + DWMY[0] + "s");
                } else {
                    newFreqOptions.add("Every " + i + " " + DWMY[0]);
                }
            }

            freqAdapter.clear(); // Clear the existing data
            freqAdapter.addAll(newFreqOptions); // Add the new data
            alertFrequency.setSelection(1);
            freqAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
        };

        // Swaps between repeat day/week/month/year
        alertDay.setOnClickListener(v -> {
            DWMY[0] = "day";
            updateFreqOptions.run();
        });

        alertWeek.setOnClickListener(v -> {
            DWMY[0] = "week";
            updateFreqOptions.run();
        });

        alertMonth.setOnClickListener(v -> {
            DWMY[0] = "month";
            updateFreqOptions.run();
        });

        alertYear.setOnClickListener(v -> {
            DWMY[0] = "year";
            updateFreqOptions.run();
        });

        //initializes the frequency spinner and sets value to "never" (index 0) to start
        alertDay.performClick();
        alertFrequency.setSelection(0);

        //resets alertEnd and alertFreq spinners to "never"
        alertCancel.setOnClickListener(v ->{
            alertFrequency.setSelection(0);
            alertEnd.setSelection(0);
        });

        //todo
        // create calendarView for when alertDate is clicked
        // create timePickerDialog for when alertTime is clicked

        alertDate.setOnClickListener(v -> {
            // Show CalendarView or DatePickerDialog
            // Implement your logic here
        });

        alertTime.setOnClickListener(v -> {
            // Show TimePickerDialog
            // Implement your logic here
        });

        // Set up buttons and their alignment
        builder.setPositiveButton("Save", (dialogInterface, i) -> {
            // Handle Save button click
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            // Handle Cancel button click
        });

        // Set layout parameters for the custom view
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        alertView.setLayoutParams(layoutParams);

        dialog.show();

    }

}
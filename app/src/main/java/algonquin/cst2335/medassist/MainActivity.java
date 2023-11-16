package algonquin.cst2335.medassist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;
import android.widget.TimePicker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import algonquin.cst2335.medassist.databinding.MedViewBinding;

/**
 * Work on user registration asap...
 */
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

        /**
         * On Click Listener Current Tab
         */
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
                    String durationDateString = medicine.getDuration();
                    LocalDate expirationDate = LocalDate.parse(expirationDateString, formatter);
                    LocalDate durationDate = LocalDate.parse(durationDateString, formatter);
                    if (currentDate.isBefore(expirationDate) || currentDate.isBefore(durationDate)) {
                        currentMedList.add(medicine);
                    }
                }
            }
            AtomicReference<MedicineAdapter> adapter = new AtomicReference<>(new MedicineAdapter(currentMedList, this));
            recyclerView.setAdapter(adapter.get());

        });

        /**
         * On Click Listener Past Tab
         */
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
                String durationDateString = medicine.getDuration();
                LocalDate expirationDate = LocalDate.parse(expirationDateString, formatter);
                LocalDate durationDate = LocalDate.parse(durationDateString, formatter);
                if(currentDate.isAfter(expirationDate) || currentDate.isAfter(durationDate)){
                    currentMedList.add(medicine);
                }
            }
            AtomicReference<MedicineAdapter> adapter = new AtomicReference<>(new MedicineAdapter(currentMedList, this));
            recyclerView.setAdapter(adapter.get());
        });

        /**
         * On Click Listener Add Tab
         */
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

        /**
         * On Click Listener Search Tab
         */
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

        /**
         * On Click Listener Settings Tab
         */
        binding.settings.setOnClickListener(click -> {

            //open settings dialog box

        });

    }

    /**
     * Adds and set new medicine information to recyclerView
     */
    public void setMedicineAdapter() {
        MedDatabase medDb = new MedDatabase(this);
        List<Medicine> medicineList = medDb.getAllMedicines();
        MedicineAdapter adapter = new MedicineAdapter(medicineList, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Obtains the position of item from recyclerView and views full medicine information
     * along with doctor's name and number
     * @param position
     */
    @Override
    public void onItemClick(int position, View clickedView) {
        if(clickedView.getId() == R.id.calendarIcon){
            calendar(clickedView);
        }
        else{
            MedDatabase medDb = new MedDatabase(this);
            List<Medicine> medList = medDb.getAllMedicines();
            Medicine medicine = medList.get(position);
            List<Doctor> docList = medDb.getAllDoctor();
            Doctor doctor = docList.get(position);

            //TODO
            /**
             * Create function to filter current and past MEDICINE
             */
            Intent intent = new Intent(this, MedicineDetailActivity.class);
            intent.putExtra("medicine", medicine);
            intent.putExtra("doctor", doctor);
            startActivity(intent);
        }

    }

    public void calendar(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // Creates a custom TextView for the title
        TextView title = new TextView(MainActivity.this);
        title.setText("Set Medication Reminder");
        title.setTextSize(20); // Adjusts the text size
        title.setTypeface(null, Typeface.BOLD); // Makes the text bold
        title.setGravity(Gravity.CENTER); // Aligns the text
        title.setPadding(0, 25, 0, 25); // Adds top and bottom padding

        // Add the title TextView to the dialog
        builder.setCustomTitle(title);

        ScrollView scrollView = new ScrollView(MainActivity.this);

        TextView message = new TextView(MainActivity.this);
        message.setText("");
        message.setGravity(Gravity.CENTER);

        // Create a linear layout to hold the views
        LinearLayout linearLayout = new LinearLayout(MainActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 20);

        // Set up the TimePicker for start time
        TimePicker timePicker = new TimePicker(MainActivity.this);
        timePicker.setIs24HourView(true);

        TextView hourlyReminderText = new TextView(MainActivity.this);
        hourlyReminderText.setText("Select the hourly reminder:");
        hourlyReminderText.setGravity(Gravity.CENTER);
        hourlyReminderText.setPadding(0, 0, 0, 16);

        // Set up the NumberPicker for duration (hours)
        NumberPicker hoursNumberPicker = new NumberPicker(MainActivity.this);
        hoursNumberPicker.setMinValue(0);
        hoursNumberPicker.setMaxValue(24);
        hoursNumberPicker.setWrapSelectorWheel(true);


        // Add the TimePicker and NumberPicker to the linear layout
        linearLayout.addView(timePicker);
        linearLayout.addView(hourlyReminderText);
        linearLayout.addView(hoursNumberPicker);

        TextView dayReminderText = new TextView(MainActivity.this);
        dayReminderText.setText("Select day reminder:");
        dayReminderText.setGravity(Gravity.CENTER);
        dayReminderText.setPadding(0,0,0,16);
        linearLayout.addView(dayReminderText);

        String[] frequencyOptions = {"Every day", "Every two days", "Every three days", "Every four days", "Every week"};
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, frequencyOptions);
        Spinner frequencySpinner = new Spinner(this);
        frequencySpinner.setAdapter(frequencyAdapter);
        frequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String reminderMessage = "Your medication reminder is set to start at: " + timePicker.getHour() + ":" + timePicker.getMinute()
                        + " for every: " + hoursNumberPicker.getValue() + " hours, and to be taken: " + frequencySpinner.getSelectedItem().toString();
                message.setText(reminderMessage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
        linearLayout.addView(frequencySpinner);

        // Add the message TextView to the linear layout
        linearLayout.addView(message);

        scrollView.addView(linearLayout);
        // Set up buttons and their alignment
        builder.setPositiveButton("Set Reminder", (dialog, which) -> {
            // Handle Set Reminder button click
            int startHour = timePicker.getHour();
            int startMinute = timePicker.getMinute();
            int durationHours = hoursNumberPicker.getValue();
            MedDatabase medDb = new MedDatabase(this);
            List<Medicine> medList = medDb.getAllMedicines();
//            Medicine medicine = medList.get(position);

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Handle Cancel button click
        });

        // Add the linear layout to the dialog
        builder.setView(scrollView);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
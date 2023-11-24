package algonquin.cst2335.medassist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import algonquin.cst2335.medassist.databinding.MedViewBinding;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface{

    private boolean isAddFragmentVisible = false;
    private boolean isSearchFragmentVisible = false;

    MedViewBinding binding;
    private SortCriteria currentSortCriteria = SortCriteria.ADDED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MedViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MedDatabase medDb = new MedDatabase(this);

        //creates the notification channel for this apps notifications
        createNotificationChannel();

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
            if (currentMedList.isEmpty()){
                binding.noMedicine.setText("No medicines in the \ncurrent database\n right now");
                binding.noMedicine.setGravity(Gravity.CENTER);
                binding.noMedicine.setVisibility(View.VISIBLE);
            }
            else if (!currentMedList.isEmpty()){
                binding.noMedicine.setVisibility(View.GONE);
            }
            AtomicReference<MedicineAdapter> adapter = new AtomicReference<>(new MedicineAdapter(currentMedList, this));
            recyclerView.setAdapter(adapter.get());

        });
        //opens app in current medicine view
        binding.current.performClick();

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
            List<Medicine> pastMedList = new ArrayList<>();

            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

            if(!medicineList.isEmpty()) {
                for (Medicine medicine : medicineList) {
                    String expirationDateString = medicine.getExpiration();
                    String durationDateString = medicine.getDuration();
                    LocalDate expirationDate = LocalDate.parse(expirationDateString, formatter);
                    LocalDate durationDate = LocalDate.parse(durationDateString, formatter);
                    if (currentDate.isAfter(expirationDate) || currentDate.isAfter(durationDate)) {
                        pastMedList.add(medicine);
                    }
                }
            }
            if (pastMedList.isEmpty()){
                binding.noMedicine.setText("No medicines in the \npast database\n right now");
                binding.noMedicine.setGravity(Gravity.CENTER);
                binding.noMedicine.setVisibility(View.VISIBLE);
            }
            else if (!pastMedList.isEmpty()){
                binding.noMedicine.setVisibility(View.GONE);
            }

            AtomicReference<MedicineAdapter> adapter = new AtomicReference<>(new MedicineAdapter(pastMedList, this));
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

        /**
         * On Click Listener Sort Alphabet
         */
        binding.sortAlphabetic.setOnClickListener(click ->{
            currentSortCriteria = SortCriteria.NAME;
            sortAndSetAdapter();
        });

        /**
         * On Click Listener Sort Frequency
         */
        binding.sortFrequency.setOnClickListener(click -> {
            currentSortCriteria = SortCriteria.FREQUENCY;
            sortAndSetAdapter();
        });

        /**
         * On Click Listener Sort Added
         */
        binding.sortDateAdded.setOnClickListener(click -> {
            currentSortCriteria = SortCriteria.ADDED;
            sortAndSetAdapter();
        });

    }

    private void sortAndSetAdapter() {
        MedDatabase medDb = new MedDatabase(this);
        List<Medicine> medicineList = medDb.getAllMedicines();

        switch (currentSortCriteria) {
            case NAME:
                Collections.sort(medicineList, Comparator.comparing(Medicine::getName));
                break;
            case FREQUENCY:
                Collections.sort(medicineList, Comparator.comparingInt(medicine -> {
                    try {
                        return Integer.parseInt(medicine.getFrequency());
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }));
                break;
            case ADDED:
                // Assuming Medicine class has a timestamp field for added time
                //Collections.sort(medicineList, Comparator.comparing(Medicine::getAddedTimestamp).reversed());
                Collections.sort(medicineList, Comparator.comparingLong(Medicine::getId));
                break;
        }

        MedicineAdapter adapter = new MedicineAdapter(medicineList, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
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
        intent.putExtra("position", position);
        startActivity(intent);
//        startActivityForResult(intent, MEDICINE_DETAIL_REQUEST_CODE);

    }

    //tracks which button was clicked
    String[] DWMH = {"day"};

    public void calendar(View view) {

        //dialog box for Alerts
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View alertView = getLayoutInflater().inflate(R.layout.notification_alertdialog,null);
        AlertDialog dialog = builder.create();
        dialog.setView(alertView);

        //gathering all of the menu item links
        TextView alertTitle = alertView.findViewById(R.id.alertTitle);
        Button alertDate = alertView.findViewById(R.id.dateButton);
        Button alertTime = alertView.findViewById(R.id.clockButton);
        Button alertDay = alertView.findViewById(R.id.repeatDayButton);
        Button alertWeek = alertView.findViewById(R.id.repeatWeekButton);
        Button alertMonth = alertView.findViewById(R.id.repeatMonthButton);
        Button alertHour = alertView.findViewById(R.id.repeatYearButton);
        ImageButton alertCancel = alertView.findViewById(R.id.cancelButton);
        Spinner alertFrequency = alertView.findViewById(R.id.spinnerFrequency);
        Spinner alertEnd = alertView.findViewById(R.id.spinnerEnd);
        Spinner alertAlert = alertView.findViewById(R.id.spinnerAlert);
        CalendarView calendarView = alertView.findViewById(R.id.calendarView);
        TimePicker timerPicker = alertView.findViewById(R.id.timePicker);
        //gets the corresponding medicine DB ID
        String medicineId = view.getTag(R.id.medicineID).toString();
        //save/cancel buttons
        Button alertSave = alertView.findViewById(R.id.save);
        Button alertCancelNoti = alertView.findViewById(R.id.cancel);



        //calenderView
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            // Construct the selected date in the desired format
            alertDate.setText(String.format(Locale.getDefault(), "%04d/%02d/%02d", year, month + 1, dayOfMonth));
        });

        //timePicker
        timerPicker.setIs24HourView(false);
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

        //initialize calendarView as invisible
        calendarView.setVisibility(View.GONE);
        alertDate.setOnClickListener(v -> {
            //appears/disappears when button is clicked
            if (calendarView.getVisibility() == View.VISIBLE){
                calendarView.setVisibility(View.GONE);
            }
            else if (calendarView.getVisibility() == View.GONE){
                calendarView.setVisibility(View.VISIBLE);
            }
        });
        //initialize timePicker as invisible
        timerPicker.setVisibility(View.GONE);
        alertTime.setOnClickListener(v -> {
            //appears/disappears when button is clicked
            if (timerPicker.getVisibility() == View.VISIBLE){
                timerPicker.setVisibility(View.GONE);
            }
            else if (timerPicker.getVisibility() == View.GONE){
                timerPicker.setVisibility(View.VISIBLE);
            }
        });


        //List options for when the notifications end
        List<String> endOptions = new ArrayList<>();
        endOptions.add("Ends never");
        // Generate "Ends after 1, 2, 3...120 rences"
        for (int i = 1; i <= 120; i++) {
            if (i > 1) {
                endOptions.add("Ends after " + i + " recurrences");
            }
            else{
                endOptions.add("Ends after " + i + " recurrence");
            }
        }

        //List options for when to receive the notification
        String[] alertAlertOptions = {"0 minutes", "1 minute", "5 minutes", "10 minutes", "15 minutes", "20 minutes", "30 minutes", "45 minutes", "60 minutes",
                "90 minutes", "2 hours", "3 hours", "4 hours", "6 hours", "8 hours", "12 hours", "18 hours", "24 hours", "2 days", "3 days", "5 days", "7 days", "10 days", "14 days", "21 days", "28 days"};

        //List options for when to get repeat notifications
        List<String> freqOptions = new ArrayList<>();

        // Initialize the adapter for alertEnd spinner
        ArrayAdapter<String> endAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, endOptions);
        endAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alertEnd.setAdapter(endAdapter);

        // Initialize the adapter for alertAlert spinner
        ArrayAdapter<String> alertAlertAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, alertAlertOptions);
        alertAlertAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alertAlert.setAdapter(alertAlertAdapter);

        // Initialize the adapter for alertFrequency spinner
        ArrayAdapter<String> freqAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, freqOptions);
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alertFrequency.setAdapter(freqAdapter);

        // Nested method to update the frequency options based on DWMH
        Runnable updateFreqOptions = () -> {
            List<String> newFreqOptions = new ArrayList<>();
            newFreqOptions.add("Never");

            for (int i = 1; i <= 120; i++) {
                if (i > 1) {
                    newFreqOptions.add("Every " + i + " " + DWMH[0] + "s");
                } else {
                    newFreqOptions.add("Every " + i + " " + DWMH[0]);
                }
            }

            freqAdapter.clear(); // Clear the existing data
            freqAdapter.addAll(newFreqOptions); // Add the new data
            alertFrequency.setSelection(1);
        };

        // Swaps between repeat day/week/month/year
        alertDay.setOnClickListener(v -> {
            DWMH[0] = "day";
            updateFreqOptions.run();
        });

        alertWeek.setOnClickListener(v -> {
            DWMH[0] = "week";
            updateFreqOptions.run();
        });

        alertMonth.setOnClickListener(v -> {
            DWMH[0] = "month";
            updateFreqOptions.run();
        });

        alertHour.setOnClickListener(v -> {
            DWMH[0] = "hour";
            updateFreqOptions.run();
        });

        //sets the spinner back to whatever list set it should be.
        if (DWMH[0].equals("day")){
            alertDay.performClick();
        }
        else if (DWMH[0].equals("week")){
            alertWeek.performClick();
        }
        else if (DWMH[0].equals("month")){
            alertMonth.performClick();
        }
        else if (DWMH[0].equals("hour")){
            alertHour.performClick();
        }

        //resets alertEnd and alertFreq spinners to "never"
        alertCancel.setOnClickListener(v ->{
            alertFrequency.setSelection(0);
            alertEnd.setSelection(0);
        });

        //tracks if a notification was found in the db
        boolean notificationFound = false;
        //Gets an instance of MedDatabase
        MedDatabase medDatabase = new MedDatabase(this);
        //gets all database notification rows
        List<Notification> notificationList = medDatabase.getAllNotifications();

        //iterates over all notification rows
        //This is where the notification saving happens
        for (Notification notification : notificationList){
            if (notification.getMedicineId() == Long.parseLong(medicineId)){
                alertTitle.setText(notification.getNotiName());
                alertDate.setText(notification.getNotiDate());
                alertTime.setText(notification.getNotiTime());
                //spinner repeat date
                for (int i = 0; i < alertFrequency.getCount(); i++){
                    Object item = alertFrequency.getItemAtPosition(i);
                    if (item != null && item.equals(notification.getNotiRepeatDate())){
                        alertFrequency.setSelection(i);
                        break;
                    }
                }
                //spinner repeat occurence
                for (int i = 0; i < alertEnd.getCount(); i++){
                    Object item = alertEnd.getItemAtPosition(i);
                    if (item != null && item.equals(notification.getNotiRepeatAmount())){
                        alertEnd.setSelection(i);
                        break;
                    }
                }
                //spinner for when to receive notification
                for (int i = 0; i < alertAlert.getCount(); i++){
                    Object item = alertAlert.getItemAtPosition(i);
                    if (item != null && item.equals(notification.getNotiTimeBefore())){
                        alertAlert.setSelection(i);
                        break;
                    }
                }

                //change the button text
                alertSave.setText("Update");
                alertCancelNoti.setText("Remove");

                //updates all the notification information (this button would now have the text(update))
                /**
                 *
                 *   UPDATE
                 *
                 */
                alertSave.setOnClickListener(v ->{

                    //user information formatted how its needed
                    String notificationName = alertTitle.getText().toString();
                    String notificationDate = alertDate.getText().toString();
                    String notificationTime = alertTime.getText().toString();
                    String notificationRepeatDate = alertFrequency.getSelectedItem().toString();
                    String notificationRepeatAmount = alertEnd.getSelectedItem().toString();
                    String notificationTimeBefore = alertAlert.getSelectedItem().toString();
                    long medicineID = Long.parseLong(medicineId);

                    // Create a Notification object with the users input
                    Notification updateNotification = new Notification(notificationName, notificationDate, notificationTime,
                            notificationRepeatDate, notificationRepeatAmount, notificationTimeBefore, medicineID);

                    // Inserts the new Notification info into the database
                    MedDatabase medDb = new MedDatabase(this);

                    //gets the notification ID based on medicine ID
                    long notificationID = medDb.getNotificationByMedicineId(medicineID);

                    //updates the info
                    long newRowId = medDb.updateNotification(notificationID, updateNotification);

                    if (newRowId != -1) {
                        dialog.dismiss();
                        Toast.makeText(this, "Notification "+ notificationName +" updated", Toast.LENGTH_LONG).show();

                        //Cancels the existing PendingIntent
                        Intent cancelIntent = new Intent(this, ReminderBroadcast.class);
                        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(MainActivity.this, Integer.parseInt(medicineId), cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager cancelAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        cancelAlarmManager.cancel(cancelPendingIntent);

                        //Creates new pendingIntent for alertManager
                        Intent intent = new Intent(this, ReminderBroadcast.class);
                        intent.putExtra("medicineTitle", alertTitle.getText().toString());
                        intent.putExtra("frequency", medDb.getFrequencyById(medicineID));
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,Integer.parseInt(medicineId),intent,PendingIntent.FLAG_UPDATE_CURRENT);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        long triggerTime = calculateTriggerTime(notificationDate, notificationTime, notificationTimeBefore);
                        long intervalTime = calculateIntervalTime(notificationRepeatDate);
                        String[] repeatAmount = notificationRepeatAmount.split("\\s+");

                        if (notificationRepeatDate.equals("Never")) {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                        }
                        else if (notificationRepeatAmount.equals("Ends never")){
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, intervalTime, pendingIntent);
                        }
                        else {
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, intervalTime, pendingIntent);

                            // Cancel the existing PendingIntent after occurences
                            Intent cancellationIntent = new Intent(this, CancelReminderBroadcast.class);
                            cancellationIntent.putExtra("key", pendingIntent);
                            PendingIntent cancellationPendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(medicineId), cancellationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime + intervalTime * Long.parseLong(repeatAmount[2]), cancellationPendingIntent);
                        }

                    } else {
                        // Handle insertion failure
                        Toast.makeText(this, "Failed to updated notification. Please try again.", Toast.LENGTH_LONG).show();
                    }

                });
                //removes the notification (this button would now have the text(remove))
                /**
                 *
                 *  REMOVE
                 *
                 */
                alertCancelNoti.setOnClickListener(v ->{

                    //notification name
                    String notificationName = alertTitle.getText().toString();
                    //medicineID foreign key
                    long medicineID = Long.parseLong(medicineId);

                    // Inserts the new Notification info into the database
                    MedDatabase medDb = new MedDatabase(this);

                    //gets the notification ID based on medicine ID
                    long notificationID = medDb.getNotificationByMedicineId(medicineID);

                    //removes the notification
                    long newRowId = medDb.deleteNotification(notificationID);

                    if (newRowId != -1) {
                        dialog.dismiss();
                        Toast.makeText(this, "Notification "+ notificationName +" removed", Toast.LENGTH_LONG).show();

                        // Cancel the existing PendingIntent
                        Intent cancelIntent = new Intent(this, ReminderBroadcast.class);
                        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(MainActivity.this, Integer.parseInt(medicineId), cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager cancelAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        cancelAlarmManager.cancel(cancelPendingIntent);
                    } else {
                        // Handle insertion failure
                        Toast.makeText(this, "Failed to remove notification. Please try again.", Toast.LENGTH_LONG).show();
                    }
                });

                notificationFound = true;

            }
        }

        /**
         *
         * NEW NOTIFICATION
         *
         */
        if (!notificationFound){
            //setting of initial values

            //set the title to medicines name
            //Gets the list of all medicines from the database
            List<Medicine> medicineList = medDatabase.getAllMedicines();
            //Now iterating through the medicineList and accessing individual Medicine objects
            for (Medicine medicine : medicineList) {
                String DBmedicineId = String.valueOf(medicine.getId());
                String DBmedicineName = medicine.getName();

                if (DBmedicineId.equals(medicineId)){
                    alertTitle.setText(DBmedicineName);
                }

            }

            // Set the date and time to the current date and time
            Calendar currentDateTime = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            alertDate.setText(dateFormat.format(currentDateTime.getTime()));
            alertTime.setText(timeFormat.format(currentDateTime.getTime()));

            //sets the default value to 15 minutes (index 5)
            alertAlert.setSelection(4);

            alertDay.performClick();
            alertFrequency.setSelection(0);

            /**
             *
             *  SAVE
             *
             */
            alertSave.setOnClickListener(v ->{

                //user information formatted how its needed
                String notificationName = alertTitle.getText().toString();
                String notificationDate = alertDate.getText().toString();
                String notificationTime = alertTime.getText().toString();
                String notificationRepeatDate = alertFrequency.getSelectedItem().toString();
                String notificationRepeatAmount = alertEnd.getSelectedItem().toString();
                String notificationTimeBefore = alertAlert.getSelectedItem().toString();
                long medicineID = Long.parseLong(medicineId);

                // Create a Notification object with the users input
                Notification newNotification = new Notification(notificationName, notificationDate, notificationTime,
                        notificationRepeatDate, notificationRepeatAmount, notificationTimeBefore, medicineID);

                // Inserts the new Notification info into the database
                MedDatabase medDb = new MedDatabase(this);

                long newRowId = medDb.insertNotification(newNotification);

                if (newRowId != -1) {
                    dialog.dismiss();
                    Toast.makeText(this, "Notification "+ notificationName +" added", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(this, ReminderBroadcast.class);
                    intent.putExtra("medicineTitle", alertTitle.getText().toString());
                    intent.putExtra("frequency", medDb.getFrequencyById(medicineID));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,Integer.parseInt(medicineId),intent,PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    long triggerTime = calculateTriggerTime(notificationDate, notificationTime, notificationTimeBefore);
                    long intervalTime = calculateIntervalTime(notificationRepeatDate);
                    String[] repeatAmount = notificationRepeatAmount.split("\\s+");

                    if (notificationRepeatDate.equals("Never")) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                    }
                    else if (notificationRepeatAmount.equals("Ends never")){
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, intervalTime, pendingIntent);
                    }
                    else {
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, intervalTime, pendingIntent);

                        // Cancel the existing PendingIntent after occurences
                        Intent cancellationIntent = new Intent(this, CancelReminderBroadcast.class);
                        cancellationIntent.putExtra("key", pendingIntent);
                        PendingIntent cancellationPendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(medicineId), cancellationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime + intervalTime * Long.parseLong(repeatAmount[2]), cancellationPendingIntent);
                    }
                } else {
                    // Handle insertion failure
                    Toast.makeText(this, "Failed to add notification. Please try again.", Toast.LENGTH_LONG).show();
                }

            });

            /**
             *
             * CANCEL
             *
             */
            alertCancelNoti.setOnClickListener(v ->{

                dialog.dismiss();
                //nothing happens

            });

        }



        //todo
        // gather all the information and set up notification with it (Calendar notification).
        // setup expiration and duration automatic notifications.

        // Set layout parameters for the custom view
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        alertView.setLayoutParams(layoutParams);

        dialog.show();

    }

    private void createNotificationChannel(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "MedReminderChannel";
            String description = "Channel for MedAssist reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyMed",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private long calculateIntervalTime(String intervalTime){
        long time = 0;
        String[] splitTime = intervalTime.split("\\s+");

        if (splitTime.length != 3) {
            //Invalid format
            return -1;
        }

        if (splitTime[2].equals("hours") || splitTime[2].equals("hour")){
            time = Long.parseLong(splitTime[1])/* * 60 */* 60 * 1000;
        }
        else if (splitTime[2].equals("days") || splitTime[1].equals("day")){
            time = Long.parseLong(splitTime[1]) * 24 * 60 * 60 * 1000;
        }
        else if (splitTime[2].equals("weeks") || splitTime[1].equals("week")){
            time = Long.parseLong(splitTime[1]) * 7 * 24 * 60 * 60 * 1000;
        }
        // technically this could be made more accurate because each month isnt 30 days
        else if (splitTime[2].equals("months") || splitTime[1].equals("month")){
            time = Long.parseLong(splitTime[1]) * 30 * 7 * 24 * 60 * 60 * 1000;
        }

        return time;
    }

    private long calculateTriggerTime(String date, String time, String timeBefore){

        // Combine date and time strings to create the complete dateTime string
        String dateTimeString = date + " " + time;

        // Parse the dateTime string
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.getDefault());
        Date dateTime;
        try {
            dateTime = dateTimeFormat.parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle parsing error
            return -1; // Return an invalid value in case of error
        }

        // Calculate the trigger time by subtracting timeBefore in milliseconds
        long triggerTimeMillis = dateTime.getTime() - convertTimeBeforeToMillis(timeBefore);

        return triggerTimeMillis;

    }

    //This method is used to calculate how much time to send the notification before hand
    private long convertTimeBeforeToMillis(String timeBefore) {
        long time = 0;
        String[] splitTime = timeBefore.split("\\s+");

        if (splitTime.length != 2) {
            //Invalid format
            return -1;
        }

        if (splitTime[1].equals("minutes") || splitTime[1].equals("minute")){
            time = Long.parseLong(splitTime[0]) * 60 * 1000;
        }
        else if (splitTime[1].equals("hours")){
            time = Long.parseLong(splitTime[0]) * 60 * 60 * 1000;
        }
        else if (splitTime[1].equals("days")){
            time = Long.parseLong(splitTime[0]) * 24 * 60 * 60 * 1000;
        }

        return time;
    }

}
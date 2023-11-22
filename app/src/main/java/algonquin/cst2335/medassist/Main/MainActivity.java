package algonquin.cst2335.medassist.Main;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import algonquin.cst2335.medassist.Medicine.Doctor;
import algonquin.cst2335.medassist.Medicine.MedDatabase;
import algonquin.cst2335.medassist.Medicine.Medicine;
import algonquin.cst2335.medassist.Main.MedicineDetailActivity;
import algonquin.cst2335.medassist.R;
import algonquin.cst2335.medassist.databinding.MedViewBinding;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface {

    private boolean isAddFragmentVisible = false;
    private boolean isSearchFragmentVisible = false;
    private int currentTabId = R.id.current;
    MedViewBinding binding;
    private SortOrder currentSortOrder = SortOrder.ASCENDING;
    private SortCriteria previousSortCriteria = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MedViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MedDatabase medDb = new MedDatabase(this);
//        Medicine pastMedicine = new Medicine("Nicotine", "500", "200", "1", "0", "2019/12/31", "2019/12/31", "");
//        Doctor pastDoc = new Doctor("Dr.Go", "0987654321");
//        medDb.insertMedicine(pastMedicine, pastDoc);
//        Medicine pastMedicine1 = new Medicine("Meth", "1000", "200", "5", "0", "2018/12/31", "2018/12/31", "");
//        Doctor pastDoc1 = new Doctor("Dr.Go", "1223334444");
//        medDb.insertMedicine(pastMedicine1, pastDoc1);
//        Medicine pastMedicine2 = new Medicine("Caffeine", "5000", "100", "3", "0", "2020/12/31", "2020/12/31", "");
//        Doctor pastDoc2 = new Doctor("Dr.Go", "1223334444");
//        medDb.insertMedicine(pastMedicine2, pastDoc2);
        setCurrentMedicineAdapter();


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

            currentTabId = R.id.current;
            AtomicReference<MedicineAdapter> adapter = new AtomicReference<>(new MedicineAdapter(medDb.getCurrentMedicines(), this));
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

            currentTabId = R.id.past;
            AtomicReference<MedicineAdapter> adapter = new AtomicReference<>(new MedicineAdapter(medDb.getPastMedicines(), this));
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
            toggleSortingOrder(SortCriteria.NAME);
        });

        /**
         * On Click Listener Sort Frequency
         */
        binding.sortFrequency.setOnClickListener(click -> {
            toggleSortingOrder(SortCriteria.FREQUENCY);
        });

        /**
         * On Click Listener Sort Added
         */
        binding.sortDateAdded.setOnClickListener(click -> {
            toggleSortingOrder(SortCriteria.ADDED);
        });

    }

    /**
     * Gets the id of current tab
     * @return - The ID of current tab.
     */
    public int getCurrentTabId(){
        return currentTabId;
    }

    /**
     * Toggles the sorting order based on the specified sorting criteria.
     * If the same sorting criteria is clicked again, it reverses the order.
     * If a different sorting criteria is clicked, it resets the order to ascending.
     * The sorting order is then used to sort and set the adapter accordingly.
     * @param sortCriteria - The sorting criteria to be applied.
     * @see SortCriteria
     * @see SortOrder
     * @see #showSortingOrderToast(SortCriteria)
     * @see #sortAndSetAdapter(SortCriteria)
     */
    private void toggleSortingOrder(SortCriteria sortCriteria) {
        if (sortCriteria == previousSortCriteria) {
            // If the same sorting criteria is clicked again, reverse the order
            currentSortOrder = (currentSortOrder == SortOrder.ASCENDING) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
        } else {
            // If a different sorting criteria is clicked, reset the order to ascending
            currentSortOrder = SortOrder.ASCENDING;
        }
        showSortingOrderToast(sortCriteria);
        previousSortCriteria = sortCriteria;
        sortAndSetAdapter(sortCriteria);
    }

    /**
     * Toast message for sorting criteria
     * @param sortCriteria - the order of sort e.g., ascending or descending
     */
    private void showSortingOrderToast(SortCriteria sortCriteria) {
        String orderMessage = (currentSortOrder == SortOrder.ASCENDING) ? "Ascending" : "Descending";
        String toastMessage = "Sorting " + sortCriteria.name().toLowerCase() + " in " + orderMessage + " order";
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }
    /**
     * Sorts the rearranges records on recyclerView based on user's input
     */
    private void sortAndSetAdapter(SortCriteria sortCriteria) {
        MedDatabase medDb = new MedDatabase(this);
        List<Medicine> medicineList = new ArrayList<>();

        int currentTabId = getCurrentTabId();

        if (currentTabId == R.id.current) {
            medicineList = medDb.getCurrentMedicines();
        } else if (currentTabId == R.id.past) {
            medicineList = medDb.getPastMedicines();
        }

        switch (sortCriteria) {
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
                Collections.sort(medicineList, Comparator.comparingLong(Medicine::getId));
                break;
        }

        // Reverse the list if the current order is descending
        if (currentSortOrder == SortOrder.DESCENDING) {
            Collections.reverse(medicineList);
        }

        MedicineAdapter adapter = new MedicineAdapter(medicineList, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Sets the recyclerView to current medicine.
     */
    public void setCurrentMedicineAdapter(){
        MedDatabase medDb = new MedDatabase(this);
        List<Medicine> medicineList = medDb.getCurrentMedicines();
        MedicineAdapter adapter = new MedicineAdapter(medicineList, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Sets the recyclerView to the past medicine.
     */
    public void setPastMedicineAdapter(){
        MedDatabase medDb = new MedDatabase(this);
        List<Medicine> medicineList = medDb.getPastMedicines();
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
        int currentTabId = getCurrentTabId();

        List<Medicine> medList = new ArrayList<>();
        if (currentTabId == R.id.current) {
            medList = new ArrayList<>(medDb.getCurrentMedicines());
        } else if (currentTabId == R.id.past){
            medList = new ArrayList<>(medDb.getPastMedicines());
        }
        // Check if the position is valid
        if (position >= 0 && position < medList.size()) {
            // Retrieve the selected Medicine object
            Medicine selectedMedicine = medList.get(position);

            Doctor linkedDoctor = medDb.getDoctorForMedicine(selectedMedicine.getId());

            // Start MedicineDetailActivity with the selected Medicine
            Intent intent = new Intent(this, MedicineDetailActivity.class);
            intent.putExtra("medicine", selectedMedicine);
            intent.putExtra("doctor", linkedDoctor);
            startActivity(intent);
        }
    }

    /**
     * Callback method invoked when a medicine is deleted. Depending on the current tab,
     * it updates the corresponding medicine adapter:
     * - If the current tab is "Current," it sets the current medicine adapter.
     * - If the current tab is "Past," it sets the past medicine adapter.
     *
     * @see #setCurrentMedicineAdapter()
     * @see #setPastMedicineAdapter()
     */
    @Override
    public void onMedicineDeleted() {
        if(currentTabId == R.id.current){
            setCurrentMedicineAdapter();
        }else if(currentTabId == R.id.past){
            setPastMedicineAdapter();
        }
    }

    /**
     * Callback method invoked when a new medicine is added. It always sets the current medicine adapter.
     *
     * @see #setCurrentMedicineAdapter()
     */
    @Override
    public void onMedicineAdded() {
        setCurrentMedicineAdapter();
    }

    /**
     * Helps user select when notifications should be sent
     * @param view
     */
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
package algonquin.cst2335.medassist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import algonquin.cst2335.medassist.databinding.MedViewBinding;

public class MainActivity extends AppCompatActivity {

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
//        List<Medicine> medicineList = medDb.getAllMedicines();
//        AtomicReference<MedicineAdapter> adapter = new AtomicReference<>(new MedicineAdapter(medicineList));
//        recyclerView.setAdapter(adapter.get());

        binding.current.setOnClickListener(click -> {
            //Hides past recycler view -> displays current recycler view
            //Removes the current fragment (if any)
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentLocation);
            if (currentFragment != null) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.remove(currentFragment);
                fragmentTransaction.commit();
            }
            //resets addFragment flag
            isAddFragmentVisible = false;
            isSearchFragmentVisible = false;

            List<Medicine> medicineList = medDb.getAllMedicines();
            List<Medicine> currentMedList = new ArrayList<>();

            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

            for(Medicine medicine : medicineList){
                String expirationDateString = medicine.getExpiration();
                LocalDate expirationDate = LocalDate.parse(expirationDateString, formatter);

                if(currentDate.isBefore(expirationDate)){
                    currentMedList.add(medicine);
                }
            }
            AtomicReference<MedicineAdapter> adapter = new AtomicReference<>(new MedicineAdapter(currentMedList));
            recyclerView.setAdapter(adapter.get());

        });

        binding.past.setOnClickListener(click -> {
            //Hides current recycler view -> displays past recycler view
            //Removes the current fragment (if any)
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentLocation);
            if (currentFragment != null) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.remove(currentFragment);
                fragmentTransaction.commit();
            }
            //resets addFragment flag
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
            AtomicReference<MedicineAdapter> adapter = new AtomicReference<>(new MedicineAdapter(currentMedList));
            recyclerView.setAdapter(adapter.get());
        });

        binding.add.setOnClickListener(click -> {

            if (!isAddFragmentVisible) {
                AddFragment addFragment = new AddFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                // Replace the current fragment with the AddFragment
                fragmentTransaction.replace(R.id.fragmentLocation, addFragment);
                fragmentTransaction.commit();

                isAddFragmentVisible = true;
            }

            isSearchFragmentVisible = false;

        });


        binding.search.setOnClickListener(click -> {

            //inflate search_fragment

        });

        binding.settings.setOnClickListener(click -> {
            if (!isSearchFragmentVisible) {
                // Create a new instance of SearchFragment
                SearchFragment searchFragment = new SearchFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                // Replace the current fragment with the SearchFragment instance
                fragmentTransaction.replace(R.id.fragmentLocation, searchFragment);
                fragmentTransaction.commit();

                isSearchFragmentVisible = true;
            }

        });


//        MedDatabase medDb = new MedDatabase(this);
//        medDb.deleteMostRecentMedicine();
//        medDb.insertMedicine(new Medicine("Tylenol", "500mg", "2 times a day", "2023-10-10", "2023-10-11"));
//        List<Medicine> medicineList = medDb.getAllMedicines();
//        MedicineAdapter adapter = new MedicineAdapter(medicineList);
//        recyclerView.setAdapter(adapter);

    }
}
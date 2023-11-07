package algonquin.cst2335.medassist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

import algonquin.cst2335.medassist.databinding.MedViewBinding;

import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import com.google.common.util.concurrent.ListenableFuture;

public class MainActivity extends AppCompatActivity {

    private boolean isAddFragmentVisible = false;
    private boolean isSearchFragmentVisible = false;

    MedViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MedViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

            //TODO current medicine recycler view list to be added
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
            //resets add/searchFragment flag
            isAddFragmentVisible = false;
            isSearchFragmentVisible = false;

            //TODO past medicine recycler view list to be added
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

            //open settings dialogue box

        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        MedDatabase medDb = new MedDatabase(this);
        medDb.deleteMostRecentMedicine();
        medDb.insertMedicine(new Medicine("Tylenol", "500mg", "2 times a day", "2023-10-10", "2023-10-11"));
        List<Medicine> medicineList = medDb.getAllMedicines();
        MedicineAdapter adapter = new MedicineAdapter(medicineList);
        recyclerView.setAdapter(adapter);

    }
}
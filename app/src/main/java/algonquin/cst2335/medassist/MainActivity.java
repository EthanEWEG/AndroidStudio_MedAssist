package algonquin.cst2335.medassist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

import algonquin.cst2335.medassist.databinding.MedViewBinding;

public class MainActivity extends AppCompatActivity {

    MedViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MedViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.current.setOnClickListener(click -> {

            //Hides past recycler view -> displays current recycler view

        });

        binding.past.setOnClickListener(click -> {

            //Hides current recycler view -> displays past recycler view

        });

        binding.add.setOnClickListener(click -> {

            //inflate add_fragment

        });

        binding.search.setOnClickListener(click -> {

            //inflate search_fragment

        });

        binding.settings.setOnClickListener(click -> {

            //inflate settings_fragment

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
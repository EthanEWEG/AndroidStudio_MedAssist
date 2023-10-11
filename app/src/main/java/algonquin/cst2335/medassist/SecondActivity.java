package algonquin.cst2335.medassist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_med);

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
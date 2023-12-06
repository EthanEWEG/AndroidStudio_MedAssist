package algonquin.cst2335.medassist.Main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import algonquin.cst2335.medassist.Medicine.Doctor;
import algonquin.cst2335.medassist.Medicine.MedDatabase;
import algonquin.cst2335.medassist.Medicine.Medicine;
import algonquin.cst2335.medassist.R;

public class SearchFragment extends DialogFragment implements RecyclerViewInterface {

    private RecyclerView recyclerView;
    private MedicineAdapter adapter;
    private List<Medicine> searchResults = new ArrayList<>();
    private EditText searchEditText;

    private MedDatabase medDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        recyclerView = view.findViewById(R.id.searchRecyclerView);
        searchEditText = view.findViewById(R.id.searchMedInput);

       medDb = new MedDatabase(requireContext());
        List<Medicine> medicineList = medDb.getAllMedicines();
        searchResults.addAll(medicineList);

        // Initialize and set up your RecyclerView and adapter here
        adapter = new MedicineAdapter(searchResults, this); // 'this' refers to the SearchFragment

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Add a TextWatcher to the searchEditText to listen for changes in the text
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed in this case
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Call loadSearchResults with the updated query
                loadSearchResults(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed in this case
            }
        });

        return view;
    }

    /**
     * Loads search results based on the given query and updates the adapter with the results.
     *
     * @param query The search query to filter the medicine list.
     */
    private void loadSearchResults(String query) {

        List<Medicine> medicineList = medDb.getAllMedicines();
        List<Medicine> searchResultsList = new ArrayList<>();

        for (Medicine medicine : medicineList) {
            if (medicine.getName().toLowerCase().startsWith(query.toLowerCase())) {
                searchResultsList.add(medicine);
            }
        }

        // Notify the adapter that the data has changed
        adapter.updateData(searchResultsList);

    }

    @Override
    public void onItemClick(int position, View v) {
        Medicine selectedMedicine = searchResults.get(position);

        Doctor linkedDoctor = medDb.getDoctorForMedicine(selectedMedicine.getId());
        Intent intent = new Intent(requireContext(), MedicineDetailActivity.class);
        intent.putExtra("medicine", selectedMedicine);
        intent.putExtra("doctor", linkedDoctor);
        startActivity(intent);
    }

    @Override
    public void onMedicineAdded() {

    }

    @Override
    public void onMedicineDetail() {

    }
}
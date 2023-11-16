package algonquin.cst2335.medassist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends DialogFragment implements RecyclerViewInterface {

    private RecyclerView recyclerView;
    private MedicineAdapter adapter;
    private List<Medicine> searchResults = new ArrayList<>();
    private EditText searchEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        recyclerView = view.findViewById(R.id.searchRecyclerView);
        searchEditText = view.findViewById(R.id.searchMedInput);

        // Initialize and set up your RecyclerView and adapter here
        adapter = new MedicineAdapter(searchResults, this); // 'this' refers to the SearchFragment

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Implement a method to load search results and update the RecyclerView
        loadSearchResults("");

        return view;
    }

    // Implement a method to load search results and update the RecyclerView
    private void loadSearchResults(String query) {
        // Clear the existing search results
        // searchResults.clear();
        MedDatabase medDb = new MedDatabase(requireContext());

        List<Medicine> medicineList = medDb.getAllMedicines();
        List<Medicine> searchResultsList = new ArrayList<>();

        if (query.isEmpty()) {
            // If the query is empty, add all medicines to search results
            searchResultsList.addAll(medicineList);
        } else {
            // Perform the search based on the query
            for (Medicine medicine : medicineList) {
                // Your search criteria logic here
                // For example, if medicine.getName() contains the query, add it to search results
                if (medicine.getName().toLowerCase().contains(query.toLowerCase())) {
                    searchResultsList.add(medicine);
                }
            }
        }

        // Add the search results to the 'searchResults' list
        searchResults.addAll(searchResultsList);

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(int position, View v) {
        MedDatabase medDb = new MedDatabase(requireContext());
        List<Medicine> medList = medDb.getAllMedicines();
        Medicine medicine = medList.get(position);

        Intent intent = new Intent(requireContext(), MedicineDetailActivity.class);
        intent.putExtra("medicine", medicine);
        startActivity(intent);
    }
}
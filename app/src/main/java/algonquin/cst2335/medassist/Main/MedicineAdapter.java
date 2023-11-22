package algonquin.cst2335.medassist.Main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import algonquin.cst2335.medassist.Medicine.Medicine;
import algonquin.cst2335.medassist.R;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>{
    private List<Medicine> medicineList;
    public final RecyclerViewInterface recyclerViewInterface;

    /**
     * Constructor for creating a MedicineAdapter.
     *
     * @param medicineList - The list of Medicine objects to be displayed in the RecyclerView.
     * @param recyclerViewInterface - An interface for handling item click events in the RecyclerView.
     */
    public MedicineAdapter(List<Medicine> medicineList, RecyclerViewInterface recyclerViewInterface) {
        this.medicineList = medicineList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    /**
     * Called when the RecyclerView needs a new {@link MedicineViewHolder}.
     *
     * @param parent     The ViewGroup into which the new View will be added.
     * @param viewType   The type of the new View.
     * @return A new MedicineViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_medicine, parent, false);
        return new MedicineViewHolder(view, recyclerViewInterface);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.medicineName.setText(medicine.getName());
        holder.dosage.setText(medicine.getDosage());
        holder.frequency.setText(medicine.getFrequency());
        holder.quantity.setText(medicine.getQuantity());

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in the adapter.
     */
    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    /**
     * ViewHolder for displaying Medicine items in the RecyclerView.
     */
    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView medicineName;
        TextView dosage;
        TextView frequency;
        TextView quantity;
        View calendar;

        /**
         * Constructor for creating a MedicineViewHolder.
         * @param itemView                 The View that holds the layout for each item in the RecyclerView.
         * @param recyclerViewInterface    An interface for handling item click events in the RecyclerView.
         */
        public MedicineViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            medicineName = itemView.findViewById(R.id.medicineName);
            dosage = itemView.findViewById(R.id.dosage);
            frequency = itemView.findViewById(R.id.frequency);
            quantity = itemView.findViewById(R.id.quantity);
            calendar = itemView.findViewById(R.id.calendarIcon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            //recyclerViewInterface.onItemClick(position);
                            recyclerViewInterface.onItemClick(position, v);
                        }
                    }
                }
            });


        }
    }
}

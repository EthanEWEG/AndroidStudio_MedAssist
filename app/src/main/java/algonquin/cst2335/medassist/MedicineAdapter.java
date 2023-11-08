package algonquin.cst2335.medassist;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>{
    private List<Medicine> medicineList;
    private final RecyclerViewInterface recyclerViewInterface;

    public MedicineAdapter(List<Medicine> medicineList, RecyclerViewInterface recyclerViewInterface) {
        this.medicineList = medicineList;
        this.recyclerViewInterface = recyclerViewInterface;
    }
    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_medicine, parent, false);
        return new MedicineViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.medicineName.setText(medicine.getName());
        holder.dosage.setText(medicine.getDosage());
        holder.frequency.setText(medicine.getFrequency());
        holder.quantity.setText(medicine.getQuantity());

    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }


    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView medicineName;
        TextView dosage;
        TextView frequency;
        TextView quantity;
        public MedicineViewHolder(View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            medicineName = itemView.findViewById(R.id.medicineName);
            dosage = itemView.findViewById(R.id.dosage);
            frequency = itemView.findViewById(R.id.frequency);
            quantity = itemView.findViewById(R.id.quantity);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
    public List<Medicine> getMedicineList() {
        return medicineList;
    }
}

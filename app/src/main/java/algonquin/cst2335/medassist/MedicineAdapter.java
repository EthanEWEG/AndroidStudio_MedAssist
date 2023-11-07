package algonquin.cst2335.medassist;

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

    public MedicineAdapter(List<Medicine> medicineList) {
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.medicineName.setText(medicine.getName());
        holder.dosage.setText(medicine.getDosage());
        holder.frequency.setText(medicine.getFrequency());
        holder.quantity.setText(medicine.getQuantity());
//        holder.expiration.setText(medicine.getExpiration());
//        holder.refillDate.setText(medicine.getRefills());
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
        TextView expiration;
        TextView refillDate;
        LinearLayout additionalInfoLayout;

        public MedicineViewHolder(View itemView) {
            super(itemView);
            medicineName = itemView.findViewById(R.id.medicineName);
            dosage = itemView.findViewById(R.id.dosage);
            frequency = itemView.findViewById(R.id.frequency);
            quantity = itemView.findViewById(R.id.quantity);

        }
    }
}

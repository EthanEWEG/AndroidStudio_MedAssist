package algonquin.cst2335.medassist.Main;

import android.view.View;

interface RecyclerViewInterface {
    void onItemClick(int position, View v);
    void onMedicineDeleted();
    void onMedicineAdded();
}

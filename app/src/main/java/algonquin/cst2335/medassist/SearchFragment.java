package algonquin.cst2335.medassist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import algonquin.cst2335.medassist.databinding.AddFragmentBinding;
import algonquin.cst2335.medassist.databinding.SearchFragmentBinding;

public class SearchFragment extends DialogFragment {

    SearchFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate your custom layout
        binding = SearchFragmentBinding.inflate(inflater, container, false);
        View view = (binding.getRoot());

        return view;
    }


}

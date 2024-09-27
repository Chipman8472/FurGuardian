package ca.furguardian.it.petwellness.ui.another;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ca.furguardian.it.petwellness.databinding.FragmentAnotherBinding;

public class AnotherFragment extends Fragment {

    private FragmentAnotherBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AnotherViewModel dashboardViewModel =
                new ViewModelProvider(this).get(AnotherViewModel.class);

        binding = FragmentAnotherBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAnother;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
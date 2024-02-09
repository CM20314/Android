package com.cm20314.mapapp.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.cm20314.mapapp.R;
import com.cm20314.mapapp.databinding.FragmentSettingsBinding;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

//    private FragmentSettingsBinding binding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        android.content.Context c = getContext();
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        if (key.equals("stepFreeNav")) {
            // Handle preference change
            //String value = sharedPreferences.getString(key, "");
            //System.out.println("HI");
            // Do something with the new value
        }
    }

//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        SettingsViewModel dashboardViewModel =
//                new ViewModelProvider(this).get(SettingsViewModel.class);
//
//        binding = FragmentSettingsBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        final TextView textView = binding.textDashboard;
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        return root;
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
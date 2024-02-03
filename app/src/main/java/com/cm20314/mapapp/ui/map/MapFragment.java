package com.cm20314.mapapp.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cm20314.mapapp.R;
import com.cm20314.mapapp.databinding.FragmentMapBinding;
import com.cm20314.mapapp.interfaces.IHttpRequestCallback;
import com.cm20314.mapapp.models.MapDataResponse;
import com.cm20314.mapapp.services.HttpRequestService;
import com.cm20314.mapapp.services.MapDataService;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private AutoCompleteTextView autoCompleteTextView;
    private final MapDataService mapDataService = new MapDataService();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //MapViewModel mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        ViewGroup root = binding.getRoot();

        //final TextView textView = binding.textHome;
        //mapViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        autoCompleteTextView = root.findViewById(R.id.autoCompleteTextView);

        // Set up your data source and adapter for suggestions
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, getYourSuggestionsData());

        autoCompleteTextView.setAdapter(adapter);

        // Set click listeners for the buttons
        Button fav_button = root.findViewById(R.id.fav_button);
        Button fav_button2 = root.findViewById(R.id.fav_button2);
        Button fav_button3 = root.findViewById(R.id.fav_button3);
        Button recent_button = root.findViewById(R.id.recent_button);
        Button recent_button2 = root.findViewById(R.id.recent_button2);
        Button recent_button3 = root.findViewById(R.id.recent_button3);

        fav_button.setOnClickListener(v -> setAutoCompleteText("address of home"));
        fav_button2.setOnClickListener(v -> setAutoCompleteText("address of work"));
        fav_button3.setOnClickListener(v -> setAutoCompleteText("address of gym"));

        recent_button.setOnClickListener(v -> setAutoCompleteText(recent_button.getText().toString()));
        recent_button2.setOnClickListener(v -> setAutoCompleteText(recent_button2.getText().toString()));
        recent_button3.setOnClickListener(v -> setAutoCompleteText(recent_button3.getText().toString()));

        return root;
    }

    /*@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapDataService.getMap(0, 0, new IHttpRequestCallback<MapDataResponse>() {
            @Override
            public void onCompleted(HttpRequestService.HttpRequestResponse<MapDataResponse> response) {
                if(response.ResponseStatusCode == 200){
                    drawMapContent(response.Content);
                }
                else{
                    // unsuccessful API call
                }
            }

            @Override
            public void onException() {
                // erroneous API call
            }
        });
    }*/

    private void setAutoCompleteText(String text) {
        autoCompleteTextView.setText(text);
    }

    private void drawMapContent(MapDataResponse content){

    }

    // Replace this method with your actual data source
    private List<String> getYourSuggestionsData() {
        List<String> suggestions = new ArrayList<>();
        // Populate your suggestions list here
        suggestions.add("CB 1.10");
        suggestions.add("CB 1.11");
        suggestions.add("1W 2.101");
        suggestions.add("8W 2.1");
        suggestions.add("8W 1.1");
        suggestions.add("10W 2.47");
        suggestions.add("CB 2.10");
        suggestions.add("CB 2.11");
        suggestions.add("CB 2.12");
        suggestions.add("CB 2.13");
        // Add more suggestions as needed
        return suggestions;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
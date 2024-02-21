package com.cm20314.mapapp.ui.map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cm20314.mapapp.R;
import com.cm20314.mapapp.databinding.FragmentMapBinding;
import com.cm20314.mapapp.interfaces.IHttpRequestCallback;
import com.cm20314.mapapp.models.Container;
import com.cm20314.mapapp.models.MapDataResponse;
import com.cm20314.mapapp.services.HttpRequestService;
import com.cm20314.mapapp.services.MapDataService;
import com.cm20314.mapapp.ui.CanvasView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private FragmentMapBinding binding;
    private AutoCompleteTextView startSearchView;
    private AutoCompleteTextView endSearchView;
    private RelativeLayout startSearchLayout;
    private RelativeLayout endSearchLayout;
    private LinearLayout favouritesLayout;
    private LinearLayout recentsLayout;
    private ImageView backButton;
    private final MapDataService mapDataService = new MapDataService();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //MapViewModel mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        ViewGroup root = binding.getRoot();

        startSearchView = root.findViewById(R.id.start_search_view);
        endSearchView = root.findViewById(R.id.end_search_view);

        favouritesLayout = root.findViewById(R.id.favourites_container);
        recentsLayout = root.findViewById(R.id.recents_container);

        startSearchLayout = root.findViewById(R.id.start_search_layout);
        endSearchLayout = root.findViewById(R.id.end_search_layout);

        backButton = root.findViewById(R.id.start_placeholder_icon);

        // Set up your data source and adapter for suggestions
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, getYourSuggestionsData());

        endSearchView.setAdapter(adapter);
        endSearchView.setOnItemClickListener(this);

        backButton.setOnClickListener(this);

        ArrayList<String> dummyRecentValues = new ArrayList<>();
        dummyRecentValues.add("CB 1.12");
        dummyRecentValues.add("1W");
        dummyRecentValues.add("East Building");

        ConfigureFavourites(new ArrayList<Container>());
        ConfigureRecents(dummyRecentValues);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadMapData();

            SwitchUIToState(1);
    }

    private void loadMapData(){
        mapDataService.getMap(0, 0, new IHttpRequestCallback<MapDataResponse>() {
            @Override
            public void onCompleted(HttpRequestService.HttpRequestResponse<MapDataResponse> response) {
                if(response.ResponseStatusCode == 200){
                    System.out.println("Successful call.");
                    drawMapContent(response.Content);
                }
                else{
                    System.out.println("Unsuccessful call.");
                    // unsuccessful API call
                }
            }

            @Override
            public void onException() {
                // erroneous API call
                System.out.println("Failed call.");
            }
        });
    }

    private void ConfigureFavourites(List<Container> containers){
        for(Container c : containers){
            Button btn = new Button(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
            int horizontalMarginInPixels = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    4,
                    getContext().getResources().getDisplayMetrics()
            );
            layoutParams.leftMargin = horizontalMarginInPixels;
            layoutParams.rightMargin = horizontalMarginInPixels;
            btn.setPadding(5, 0, 5, 0);
            btn.setLayoutParams(layoutParams);
            btn.setBackground(getResources().getDrawable(R.drawable.favs_recent_buttons_style));
            btn.setEllipsize(TextUtils.TruncateAt.END);
            btn.setMaxLines(1);
            btn.setText(c.longName);
            btn.setBackgroundResource(R.drawable.favs_recent_buttons_style);
            btn.setOnClickListener(v -> setAutoCompleteText(c.longName));
            btn.setTextColor(getResources().getColor(com.google.android.material.R.color.design_default_color_on_primary));
            favouritesLayout.addView(btn);
        }
    }

    private void ConfigureRecents(List<String> recentSearches){
        for(String recentSearch : recentSearches){
            Button btn = new Button(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
            int horizontalMarginInPixels = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    4,
                    getContext().getResources().getDisplayMetrics()
            );
            layoutParams.leftMargin = horizontalMarginInPixels;
            layoutParams.rightMargin = horizontalMarginInPixels;
            btn.setPadding(5, 0, 5, 0);
            btn.setLayoutParams(layoutParams);
            btn.setBackground(getResources().getDrawable(R.drawable.favs_recent_buttons_style));
            btn.setEllipsize(TextUtils.TruncateAt.END);
            btn.setMaxLines(1);
            btn.setText(recentSearch);
            btn.setOnClickListener(v -> setAutoCompleteText(recentSearch));
            btn.setBackgroundResource(R.drawable.favs_recent_buttons_style);
            btn.setTextColor(getResources().getColor(com.google.android.material.R.color.design_default_color_on_primary));
            recentsLayout.addView(btn);
        }
    }

    private void setAutoCompleteText(String text) {
        endSearchView.setText(text);

        HideKeyboardAndDisableFocus(endSearchView);
        SwitchUIToState(2);
    }

    private void HideKeyboardAndDisableFocus(View view){
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }

    private void drawMapContent(MapDataResponse content){
        System.out.println("Map downloaded.");
        CanvasView canvasView = binding.getRoot().findViewById(R.id.map_canvas_view);
        canvasView.SetMapData(content);
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

    private void SwitchUIToState(int state){
        switch (state){
            case 1:
                SwitchToMapUI();
                break;
            case 2:
                SwitchToStartEndSelectionUI();
                break;
        }
    }

    private void SwitchToMapUI(){
        startSearchLayout.setVisibility(View.GONE);
        favouritesLayout.setVisibility(View.VISIBLE);
        recentsLayout.setVisibility(View.VISIBLE);
    }

    private void SwitchToStartEndSelectionUI(){
        startSearchLayout.setVisibility(View.VISIBLE);
        favouritesLayout.setVisibility(View.GONE);
        recentsLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HideKeyboardAndDisableFocus(endSearchView);
        SwitchUIToState(2);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.start_placeholder_icon){
            SwitchUIToState(1);
        }
    }
}
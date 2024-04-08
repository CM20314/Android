package com.cm20314.mapapp.ui.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cm20314.mapapp.MainActivity;
import com.cm20314.mapapp.R;
import com.cm20314.mapapp.databinding.FragmentMapBinding;
import com.cm20314.mapapp.interfaces.IHttpRequestCallback;
import com.cm20314.mapapp.models.Container;
import com.cm20314.mapapp.models.MapDataResponse;
import com.cm20314.mapapp.models.MapSearchResponse;
import com.cm20314.mapapp.models.NodeArcDirection;
import com.cm20314.mapapp.models.RouteRequestData;
import com.cm20314.mapapp.models.RouteResponseData;
import com.cm20314.mapapp.services.Constants;
import com.cm20314.mapapp.services.HttpRequestService;
import com.cm20314.mapapp.services.LocationService;
import com.cm20314.mapapp.services.MapDataService;
import com.cm20314.mapapp.services.RoutingService;
import com.cm20314.mapapp.ui.CanvasView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.utils.storage.SharedPreferencesManager;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MapFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, TextWatcher, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener  {

    private FragmentMapBinding binding;
    private AutoCompleteTextView startSearchView;
    private AutoCompleteTextView endSearchView;
    private RelativeLayout startSearchLayout;
    private RelativeLayout endSearchLayout;
    private LinearLayout favouritesLayout;
    private LinearLayout recentsLayout;
    private ImageView backButton;
    private ImageView favouriteButton;
    private ImageView exitDirectionsButton;

    private MapViewModel mapViewModel;

    private SharedPreferences preferences;
    private final MapDataService mapDataService = new MapDataService();
    private LocationService locationService;
    private final RoutingService routingService = new RoutingService();
    private List<String> suggestionsList = new ArrayList<>();
    private ArrayAdapter<String> endSearchViewAdapter;
    private CanvasView canvasView;
    private Button getDirectionsButton;
    private LinearLayout directionsLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView directionsStartTextView;
    private TextView directionsEndTextView;
    private TextView directionsCommandTextView;

    private ImageView favouritesIcon;
    private ImageView recentsIcon;

    private CheckBox stepFreeCheckbox;
    private LinearLayout getDirectionsLayout;

    private boolean stepFree;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        preferences = getContext().getSharedPreferences(getDefaultSharedPreferencesName(getContext()), Context.MODE_PRIVATE);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        ViewGroup root = binding.getRoot();

        locationService = new LocationService(getContext(), new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                canvasView.UpdateLocation(locationService.getLocation());
                UpdateDirections();
            }
        });

        canvasView = root.findViewById(R.id.map_canvas_view);
        canvasView.UpdateLocation(locationService.getLocation(), false);

        startSearchView = root.findViewById(R.id.start_search_view);
        endSearchView = root.findViewById(R.id.end_search_view);

        favouritesLayout = root.findViewById(R.id.favourites_container);
        recentsLayout = root.findViewById(R.id.recents_container);

        startSearchLayout = root.findViewById(R.id.start_search_layout);
        endSearchLayout = root.findViewById(R.id.end_search_layout);

        backButton = root.findViewById(R.id.start_placeholder_icon);

        favouriteButton = root.findViewById(R.id.favourite_button);

        getDirectionsButton = root.findViewById(R.id.get_directions_button);
        getDirectionsButton.setOnClickListener(this);

        exitDirectionsButton = root.findViewById(R.id.exit_directions_button);
        exitDirectionsButton.setOnClickListener(this);

        // Set up your data source and adapter for suggestions
        endSearchViewAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line);

        endSearchView.setAdapter(endSearchViewAdapter);
        endSearchView.setOnItemClickListener(this);
        endSearchView.addTextChangedListener(this);

        backButton.setOnClickListener(this);
        favouriteButton.setOnClickListener(this);

        directionsLayout = root.findViewById(R.id.directions_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(directionsLayout);

        directionsStartTextView = root.findViewById(R.id.directions_start_text_view);
        directionsCommandTextView = root.findViewById(R.id.directions_command_text_view);
        directionsEndTextView = root.findViewById(R.id.directions_end_text_view);

        favouritesIcon = root.findViewById(R.id.favourites_icon);
        recentsIcon = root.findViewById(R.id.recents_icon);

        stepFreeCheckbox = root.findViewById(R.id.step_free_checkbox);

        getDirectionsLayout = root.findViewById(R.id.get_directions_layout);

        favouritesIcon.setOnLongClickListener(this);
        recentsIcon.setOnLongClickListener(this);

        getStepFreeFromPreferences();
        stepFreeCheckbox.setOnCheckedChangeListener(this);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadMapData();

        SwitchUIToState(1);

        if(true){//preferences.getBoolean("firstLaunch", true)){
            preferences.edit().putBoolean("firstLaunch", false).apply();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setIcon(R.drawable.accessible_24dp);
            builder.setTitle("Step-free Navigation");
            builder.setMessage("Would you like to enable step-free navigation?");
            builder.setNegativeButton("NO", null);

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getContext(), "Step-free enabled", Toast.LENGTH_SHORT).show();
                    preferences.edit().putBoolean("stepFreeNav", true).apply();
                    getStepFreeFromPreferences();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void getStepFreeFromPreferences(){
        stepFree = preferences.getBoolean("stepFreeNav", false);
        stepFreeCheckbox.setChecked(stepFree);
    }

    private static String getDefaultSharedPreferencesName(Context context) {
        return context.getPackageName() + "_preferences";
    }
    private void loadMapData(){
        mapDataService.getMap(0, 0, new IHttpRequestCallback<MapDataResponse>() {
            @Override
            public void onCompleted(HttpRequestService.HttpRequestResponse<MapDataResponse> response) {
                if(response.ResponseStatusCode == 200){
                    System.out.println("Successful call.");
                    drawMapContent(response.Content);
                }
                else if(response.ResponseStatusCode != 0){
                    System.out.println("Unsuccessful call.");
                    Toast.makeText(getContext(), "Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onException() {
                // erroneous API call
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void ConfigureFavourites(Set<String> containerNames){
        int childrenCount = favouritesLayout.getChildCount();
        if(childrenCount > 1){
            for (int i = 1; i < childrenCount; i++){
                favouritesLayout.removeViewAt(1);
            }
        }
        for(String c : containerNames){
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
            btn.setPadding(10, 2, 10, 2);
            btn.setLayoutParams(layoutParams);
            btn.setBackground(getResources().getDrawable(R.drawable.favs_recent_buttons_style));
            btn.setTextColor(getColor(androidx.appcompat.R.attr.colorAccent));
            btn.setEllipsize(TextUtils.TruncateAt.END);
            btn.setMaxLines(1);
            btn.setText(c);
            btn.setBackgroundResource(R.drawable.favs_recent_buttons_style);
            btn.setOnClickListener(v -> {
                mapViewModel.destination = c;
                setAutoCompleteText(c);
            });
            favouritesLayout.addView(btn);
        }
    }

    private void ConfigureRecents(List<String> recentSearches){
        int childrenCount = recentsLayout.getChildCount();
        if(childrenCount > 1){
            for (int i = 1; i < childrenCount; i++){
                recentsLayout.removeViewAt(1);
            }
        }
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
            btn.setPadding(10, 2, 10, 2);
            btn.setLayoutParams(layoutParams);
            btn.setBackground(getResources().getDrawable(R.drawable.favs_recent_buttons_style));
            btn.setTextColor(getColor(androidx.appcompat.R.attr.colorAccent));
            btn.setEllipsize(TextUtils.TruncateAt.END);
            btn.setMaxLines(1);
            btn.setText(recentSearch);
            btn.setOnClickListener(v -> {
                mapViewModel.destination = recentSearch;
                setAutoCompleteText(recentSearch);
            });
            btn.setBackgroundResource(R.drawable.favs_recent_buttons_style);
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
        fetchColourSettings();
        canvasView.SetMapData(content, locationService.getLocation(), getColor(com.google.android.material.R.attr.colorSecondaryVariant));
    }

    private void fetchColourSettings(){
        canvasView.SetColoursEnabled(preferences.getBoolean("D4_COLS_P", false), preferences.getBoolean("D4_COLS_B", false));
    }

    private void SwitchUIToState(int state){
        switch (state){
            case 1:
                SwitchToMapUI();
                break;
            case 2:
                SwitchToStartEndSelectionUI();
                break;
            case 3:
                SwitchToDirectionsUI();
                break;
        }
    }

    private void SwitchToMapUI(){
        hideBottomSheet();
        startSearchLayout.setVisibility(View.GONE);
        endSearchLayout.setVisibility(View.VISIBLE);
        favouritesLayout.setVisibility(View.VISIBLE);
        recentsLayout.setVisibility(View.VISIBLE);
        favouriteButton.setVisibility(View.GONE);
        getDirectionsLayout.setVisibility(View.GONE);
        exitDirectionsButton.setVisibility(View.GONE);
        startSearchView.setText("");
        ConfigureFavourites(preferences.getStringSet(Constants.FAVOURITES_SET_KEY,new HashSet<>()));
        ConfigureRecents(getRecents());
    }

    private void SwitchToStartEndSelectionUI(){
        hideBottomSheet();
        startSearchLayout.setVisibility(View.VISIBLE);
        endSearchLayout.setVisibility(View.VISIBLE);
        favouritesLayout.setVisibility(View.GONE);
        recentsLayout.setVisibility(View.GONE);
        favouriteButton.setVisibility(View.VISIBLE);
        getDirectionsLayout.setVisibility(View.VISIBLE);
        exitDirectionsButton.setVisibility(View.GONE);

        startSearchView.setText(R.string.my_location);
        startSearchView.setTextColor(getColor(androidx.appcompat.R.attr.colorPrimary));

        if (IsFavourite(mapViewModel.destination)){
            favouriteButton.setImageResource(R.drawable.ic_star_red_filled_24dp);
        }

        else{
            favouriteButton.setImageResource(R.drawable.ic_star_red_24dp);
        }

       AddToRecents(mapViewModel.destination);
    }

    private void SwitchToDirectionsUI(){
        showBottomSheet();
        startSearchLayout.setVisibility(View.GONE);
        endSearchLayout.setVisibility(View.GONE);
        favouritesLayout.setVisibility(View.GONE);
        recentsLayout.setVisibility(View.GONE);
        favouriteButton.setVisibility(View.GONE);
        getDirectionsLayout.setVisibility(View.GONE);
        exitDirectionsButton.setVisibility(View.VISIBLE);
    }

    private void AddToRecents(String destination) {

        SharedPreferences.Editor editor = preferences.edit();
        String recents1 = preferences.getString(Constants.RECENTS1_KEY,"");
        String recents2 = preferences.getString(Constants.RECENTS2_KEY,"");
        String recents3 = preferences.getString(Constants.RECENTS3_KEY,"");

        if (destination.equals(recents1) || destination.equals(recents2) || destination.equals(recents3)){
            return;
        }

        else{
            editor.putString(Constants.RECENTS1_KEY,recents2);
            editor.putString(Constants.RECENTS2_KEY,recents3);
            editor.putString(Constants.RECENTS3_KEY,destination);
        }

        editor.apply();
    }

    private List<String> getRecents(){
        String recents1 = preferences.getString(Constants.RECENTS1_KEY,"");
        String recents2 = preferences.getString(Constants.RECENTS2_KEY,"");
        String recents3 = preferences.getString(Constants.RECENTS3_KEY,"");

        ArrayList<String> recents = new ArrayList<>();
        if (!recents3.equals("")) recents.add(recents3);
        if (!recents2.equals("")) recents.add(recents2);
        if (!recents1.equals("")) recents.add(recents1);

        return recents;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HideKeyboardAndDisableFocus(endSearchView);
        mapViewModel.destination = (String) parent.getItemAtPosition(position);
        SwitchUIToState(2);

        Map<String, String> properties = new HashMap<>();
        properties.put("Container Name", mapViewModel.destination);
        Analytics.trackEvent("Search item clicked", properties);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.start_placeholder_icon){
            SwitchUIToState(1);
        } else if (v.getId() == R.id.favourite_button) {
            ToggleFavourite();
        } else if (v.getId() == R.id.get_directions_button) {
            GetDirections();
        }else if (v.getId() == R.id.exit_directions_button) {
            SwitchUIToState(1);
        }
    }

    private void GetDirections(){
        RouteRequestData requestData = new RouteRequestData();
        requestData.startCoordinate = locationService.getLocation();
        requestData.endContainerName = endSearchView.getText().toString();
        if(stepFree) requestData.accessibilityLevel = 1;

        Map<String, String> properties = new HashMap<>();
        properties.put("Source X", String.valueOf(requestData.startCoordinate.x));
        properties.put("Source Y", String.valueOf(requestData.startCoordinate.y));
        properties.put("Destination", requestData.endContainerName);
        properties.put("Step Free Activated", stepFree ? "y" : "n");
        Analytics.trackEvent("Directions Requested", properties);

        routingService.requestPath(requestData, new IHttpRequestCallback<RouteResponseData>() {
            @Override
            public void onCompleted(HttpRequestService.HttpRequestResponse<RouteResponseData> response) {
                if(response.ResponseStatusCode == 200){
                    if(response.Content.success){
                        canvasView.UpdateRoute(response.Content, true);
                        directionsEndTextView.setText(response.Content.destination);
                        UpdateDirections();
                        SwitchUIToState(3);
                    }
                    else{
                        Toast.makeText(getContext(), response.Content.errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(), "Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onException() {
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ToggleFavourite() {

        if (mapViewModel.destination == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> favourites = preferences.getStringSet(Constants.FAVOURITES_SET_KEY,new HashSet<String>());
        HashSet<String> favouritesEditable = new HashSet<>(favourites);

        if (favouritesEditable.contains(mapViewModel.destination)){
            favouritesEditable.remove(mapViewModel.destination);
            favouriteButton.setImageResource(R.drawable.ic_star_red_24dp);

            Map<String, String> properties = new HashMap<>();
            properties.put("Container Name", mapViewModel.destination);
            Analytics.trackEvent("Favourite deregistered", properties);
        }

        else{
           favouritesEditable.add(mapViewModel.destination);
           favouriteButton.setImageResource((R.drawable.ic_star_red_filled_24dp));

            Map<String, String> properties = new HashMap<>();
            properties.put("Container Name", mapViewModel.destination);
            Analytics.trackEvent("Favourite registered", properties);
        }

        editor.putStringSet(Constants.FAVOURITES_SET_KEY,favouritesEditable);

        editor.apply();
    }

    private boolean IsFavourite(String containerName){

        Set<String> favourites = preferences.getStringSet(Constants.FAVOURITES_SET_KEY,new HashSet<String>());
        if (favourites.contains(containerName)){
            return true;
        }

        else{
            return false;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(endSearchView.getText().toString().equals("")) return;
        mapDataService.searchContainers(
                endSearchView.getText().toString(), new IHttpRequestCallback<MapSearchResponse>() {
                    @Override
                    public void onCompleted(HttpRequestService.HttpRequestResponse<MapSearchResponse> response) {
                        if(response.ResponseStatusCode == 200){
                            suggestionsList = response.Content.results.stream().map(c -> c.longName).collect(Collectors.toList());
                            endSearchViewAdapter.clear();
                            endSearchViewAdapter.addAll(suggestionsList);
                            endSearchViewAdapter.notifyDataSetChanged();
                        }
                        else if(response.ResponseStatusCode != 0){
                            Toast.makeText(getContext(), "Server error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onException() {
                        Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void showBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void hideBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void UpdateDirections(){
        if(canvasView.routeData != null){
            NodeArcDirection res = routingService.updateDirectionCommand(canvasView.routeData.nodeArcDirections, locationService.getLocation(), directionsCommandTextView);
            canvasView.SetCurrentNodeArcDirection(res);
            if(res == null){
                // re-request directions
                GetDirections();
            }
        }
    }

    private int getColor(int attrId){
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(attrId, typedValue, true);
        return  typedValue.data;
    }

    @Override
    public boolean onLongClick(View v) {
        boolean colourModeActivatedPaths = preferences.getBoolean("D4_COLS_P", false);
        boolean colourModeActivatedBuildings = preferences.getBoolean("D4_COLS_B", false);

        SharedPreferences.Editor editor = preferences.edit();
        String toastText;

        if(v.getId() == R.id.favourites_icon){
            boolean newColourModeActivatedPaths = !colourModeActivatedPaths;
            editor.putBoolean("D4_COLS_P", newColourModeActivatedPaths);
            toastText = newColourModeActivatedPaths ? "Paths: Colour mode activated" : "Paths: Colour mode deactivated";
        }
        else if(v.getId() == R.id.recents_icon){
            boolean newColourModeActivatedBuildings = !colourModeActivatedBuildings;
            editor.putBoolean("D4_COLS_B", newColourModeActivatedBuildings);
            toastText = newColourModeActivatedBuildings ? "Buildings: Colour mode activated" : "Buildings: Colour mode deactivated";
        } else {
            toastText = "";
        }


        editor.apply();
        Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
        fetchColourSettings();
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        stepFree = !stepFree;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("stepFreeNav", stepFree);
        editor.apply();
    }
}
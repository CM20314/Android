package com.cm20314.mapapp.ui.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.cm20314.mapapp.R;
import com.cm20314.mapapp.databinding.FragmentMapBinding;
import com.cm20314.mapapp.interfaces.IHttpRequestCallback;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles map fragment of tabbed view
 */
public class MapFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, TextWatcher, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener  {

    private FragmentMapBinding binding;
    private AutoCompleteTextView startSearchView;
    private AutoCompleteTextView endSearchView;
    private RelativeLayout startSearchLayout;
    private RelativeLayout endSearchLayout;
    private LinearLayout favouritesLayout;
    private LinearLayout recentsLayout;
    private ImageView favouriteButton;
    private ImageView exitDirectionsButton;
    private String destination;
    private SharedPreferences preferences;
    private final MapDataService mapDataService = new MapDataService();
    private LocationService locationService;
    private final RoutingService routingService = new RoutingService();
    private List<String> suggestionsList = new ArrayList<>();
    private ArrayAdapter<String> endSearchViewAdapter;
    private CanvasView canvasView;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private TextView directionsEndTextView;
    private TextView directionsCommandTextView;
    private CheckBox stepFreeCheckbox;
    private LinearLayout getDirectionsLayout;
    private boolean stepFree;

    /**
     * Initialises required services and binds views to attribute views
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        preferences = requireContext().getSharedPreferences(requireContext().getPackageName() + "_preferences", Context.MODE_PRIVATE);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        ViewGroup root = binding.getRoot();

        locationService = new LocationService(requireContext(), location -> {
            canvasView.updateLocation(locationService.getLocation());
            updateDirections();
        });

        canvasView = root.findViewById(R.id.map_canvas_view);
        canvasView.updateLocation(locationService.getLocation(), false);

        startSearchView = root.findViewById(R.id.start_search_view);
        endSearchView = root.findViewById(R.id.end_search_view);

        favouritesLayout = root.findViewById(R.id.favourites_container);
        recentsLayout = root.findViewById(R.id.recents_container);

        startSearchLayout = root.findViewById(R.id.start_search_layout);
        endSearchLayout = root.findViewById(R.id.end_search_layout);

        ImageView backButton = root.findViewById(R.id.start_placeholder_icon);

        favouriteButton = root.findViewById(R.id.favourite_button);

        Button getDirectionsButton = root.findViewById(R.id.get_directions_button);
        getDirectionsButton.setOnClickListener(this);

        exitDirectionsButton = root.findViewById(R.id.exit_directions_button);
        exitDirectionsButton.setOnClickListener(this);

        // Set up data source and adapter for suggestions
        endSearchViewAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line);

        endSearchView.setAdapter(endSearchViewAdapter);
        endSearchView.setOnItemClickListener(this);
        endSearchView.addTextChangedListener(this);

        backButton.setOnClickListener(this);
        favouriteButton.setOnClickListener(this);

        LinearLayout directionsLayout = root.findViewById(R.id.directions_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(directionsLayout);

        directionsCommandTextView = root.findViewById(R.id.directions_command_text_view);
        directionsEndTextView = root.findViewById(R.id.directions_end_text_view);

        ImageView favouritesIcon = root.findViewById(R.id.favourites_icon);
        ImageView recentsIcon = root.findViewById(R.id.recents_icon);

        stepFreeCheckbox = root.findViewById(R.id.step_free_checkbox);

        getDirectionsLayout = root.findViewById(R.id.get_directions_layout);

        favouritesIcon.setOnLongClickListener(this);
        recentsIcon.setOnLongClickListener(this);

        getStepFreeFromPreferences();
        stepFreeCheckbox.setOnCheckedChangeListener(this);

        return root;
    }

    /**
     * On first launch, asks user to enable step-free navigation.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadMapData();

        SwitchUIToState(1);

        if(preferences.getBoolean("firstLaunch", true)){
            preferences.edit().putBoolean("firstLaunch", false).apply();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setIcon(R.drawable.accessible_24dp);
            builder.setTitle("Step-free Navigation");
            builder.setMessage("Would you like to enable step-free navigation?");
            builder.setNegativeButton("NO", null);

            builder.setPositiveButton("YES", (dialog, which) -> {
                Toast.makeText(getContext(), "Step-free enabled", Toast.LENGTH_SHORT).show();
                preferences.edit().putBoolean("stepFreeNav", true).apply();
                getStepFreeFromPreferences();
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    /**
     * Retrieves step-free settings from preferences and updates UI
     */
    private void getStepFreeFromPreferences(){
        stepFree = preferences.getBoolean("stepFreeNav", false);
        stepFreeCheckbox.setChecked(stepFree);
    }

    /**
     * Loads map data from map data service
     */
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

    /**
     * Adds favourite buildings and rooms to the favourites view
     * @param containerNames Favourite buildings and rooms
     */
    private void ConfigureFavourites(Set<String> containerNames){
        int childrenCount = favouritesLayout.getChildCount();
        if(childrenCount > 1){
            // Remove existing children (apart from favourites icon at index 0)
            for (int i = 1; i < childrenCount; i++){
                favouritesLayout.removeViewAt(1);
            }
        }
        for(String c : containerNames){
            // Create object and add to favourites container
            Button btn = new Button(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
            int horizontalMarginInPixels = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    4,
                    requireContext().getResources().getDisplayMetrics()
            );
            layoutParams.leftMargin = horizontalMarginInPixels;
            layoutParams.rightMargin = horizontalMarginInPixels;
            btn.setPadding(10, 2, 10, 2);
            btn.setLayoutParams(layoutParams);
            btn.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.favs_recent_buttons_style));
            btn.setTextColor(getColor(androidx.appcompat.R.attr.colorAccent));
            btn.setEllipsize(TextUtils.TruncateAt.END);
            btn.setMaxLines(1);
            btn.setText(c);
            btn.setBackgroundResource(R.drawable.favs_recent_buttons_style);
            btn.setOnClickListener(v -> {
                destination = c;
                setAutoCompleteText(c);
            });
            favouritesLayout.addView(btn);
        }
    }

    /**
     * Adds recently searched/viewed buildings and rooms to the recents view
     * @param recentSearches Recent buildings and rooms
     */
    private void ConfigureRecents(List<String> recentSearches){
        int childrenCount = recentsLayout.getChildCount();
        if(childrenCount > 1){
            // Remove existing children (apart from favourites icon at index 0)
            for (int i = 1; i < childrenCount; i++){
                recentsLayout.removeViewAt(1);
            }
        }
        for(String recentSearch : recentSearches){
            // Create object and add to favourites container
            Button btn = new Button(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
            int horizontalMarginInPixels = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    4,
                    requireContext().getResources().getDisplayMetrics()
            );
            layoutParams.leftMargin = horizontalMarginInPixels;
            layoutParams.rightMargin = horizontalMarginInPixels;
            btn.setPadding(10, 2, 10, 2);
            btn.setLayoutParams(layoutParams);
            btn.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.favs_recent_buttons_style));
            btn.setTextColor(getColor(androidx.appcompat.R.attr.colorAccent));
            btn.setEllipsize(TextUtils.TruncateAt.END);
            btn.setMaxLines(1);
            btn.setText(recentSearch);
            btn.setOnClickListener(v -> {
                destination = recentSearch;
                setAutoCompleteText(recentSearch);
            });
            btn.setBackgroundResource(R.drawable.favs_recent_buttons_style);
            recentsLayout.addView(btn);
        }
    }

    /**
     * Processes a clicked auto-complete search suggestion by populating the search view, hiding the keyboard and switching the UI state.
     * @param text Search query
     */
    private void setAutoCompleteText(String text) {
        endSearchView.setText(text);

        HideKeyboardAndDisableFocus(endSearchView);
        SwitchUIToState(2);
    }

    /**
     * Hides the keyboard and disables its focus
     * @param view The view to disable focus for
     */
    private void HideKeyboardAndDisableFocus(View view){
        InputMethodManager mgr = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }

    /**
     * Draw map content on canvas
     * @param content Map content
     */
    private void drawMapContent(MapDataResponse content){
        fetchColourSettings();
        canvasView.setMapData(content, locationService.getLocation(), getColor(com.google.android.material.R.attr.colorSecondaryVariant));
    }

    /**
     * Retrieves the colour-enabled setting from shared preferences
     */
    private void fetchColourSettings(){
        canvasView.setColoursEnabled(preferences.getBoolean(Constants.COLOURS_PATHS, false), preferences.getBoolean(Constants.COLOURS_BUILDINGS, false));
    }

    /**
     * Switches the UI state to one of: (1) map (2) start/end selection (3) directions
     * @param state New state
     */
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

    /**
     * Shows and hides appropriate views to display map UI (state 1).
     */
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

    /**
     * Shows and hides appropriate views to display start/end selection UI (state 2).
     */
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

        if (isFavourite(destination)){
            favouriteButton.setImageResource(R.drawable.ic_star_red_filled_24dp);
        }

        else{
            favouriteButton.setImageResource(R.drawable.ic_star_red_24dp);
        }

       addToRecents(destination);
    }

    /**
     * Shows and hides appropriate views to display directions UI (state 3).
     */
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

    /**
     * Adds a destination to recent searches
     * @param destination Building/room to add
     */
    private void addToRecents(String destination) {

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

    /**
     * Retrieves recent searches from shared preferences
     * @return List of recent searches
     */
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

    /**
     * Sets end destination and switches UI state to directions
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HideKeyboardAndDisableFocus(endSearchView);
        destination = (String) parent.getItemAtPosition(position);
        SwitchUIToState(2);

        Map<String, String> properties = new HashMap<>();
        properties.put("Container Name", destination);
        Analytics.trackEvent("Search item clicked", properties);
    }

    /**
     * Click handler for start (back) placeholder button, favourites button, get directions button and exit directions button
     */
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.start_placeholder_icon){
            SwitchUIToState(1);
        } else if (v.getId() == R.id.favourite_button) {
            toggleFavourite();
        } else if (v.getId() == R.id.get_directions_button) {
            getDirections();
        }else if (v.getId() == R.id.exit_directions_button) {
            SwitchUIToState(1);
        }
    }

    /**
     * Requests directions to a given end container
     */
    private void getDirections(){
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
                        canvasView.updateRoute(response.Content, true);
                        directionsEndTextView.setText(response.Content.destination);
                        updateDirections();
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

    /**
     * Toggle favourite (register or deregister) for a building or room
     */
    private void toggleFavourite() {
        if (destination == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> favourites = preferences.getStringSet(Constants.FAVOURITES_SET_KEY,new HashSet<>());
        HashSet<String> favouritesEditable = new HashSet<>(favourites);

        if (favouritesEditable.contains(destination)){
            favouritesEditable.remove(destination);
            favouriteButton.setImageResource(R.drawable.ic_star_red_24dp);

            Map<String, String> properties = new HashMap<>();
            properties.put("Container Name", destination);
            Analytics.trackEvent("Favourite deregistered", properties);
        }

        else{
           favouritesEditable.add(destination);
           favouriteButton.setImageResource((R.drawable.ic_star_red_filled_24dp));

            Map<String, String> properties = new HashMap<>();
            properties.put("Container Name", destination);
            Analytics.trackEvent("Favourite registered", properties);
        }

        editor.putStringSet(Constants.FAVOURITES_SET_KEY,favouritesEditable);

        editor.apply();
    }

    /**
     * Checks whether a building or room is currently a favourite
     * @param containerName Building/room to check
     * @return True if it is a favourite, otherwise False
     */
    private boolean isFavourite(String containerName){

        Set<String> favourites = preferences.getStringSet(Constants.FAVOURITES_SET_KEY,new HashSet<>());
        return favourites.contains(containerName);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * When text is changed, request a search from the map data service and display the results in the suggestions list
     */
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

    /**
     * Shows the bottom sheet (that displays directions)
     */
    private void showBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    /**
     * Hides the bottom sheet (that displays directions)
     */
    private void hideBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    /**
     * Update directions (or re-request them if the user has diverged away from the path by too large of a distance)
     */
    private void updateDirections(){
        if(canvasView.routeData != null){
            NodeArcDirection res = routingService.updateDirectionCommand(canvasView.routeData.nodeArcDirections, locationService.getLocation(), directionsCommandTextView);
            canvasView.setCurrentNodeArcDirection(res);
            if(res == null){
                // re-request directions
                getDirections();
            }
        }
    }

    /**
     * Helper method to retrieve colour from attribute ID
     */
    private int getColor(int attrId){
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(attrId, typedValue, true);
        return typedValue.data;
    }

    /**
     * [Used for empirical evaluation] Long click toggles colour mode
     */
    @Override
    public boolean onLongClick(View v) {
        boolean colourModeActivatedPaths = preferences.getBoolean(Constants.COLOURS_PATHS, false);
        boolean colourModeActivatedBuildings = preferences.getBoolean(Constants.COLOURS_BUILDINGS, false);

        SharedPreferences.Editor editor = preferences.edit();
        String toastText;

        if(v.getId() == R.id.favourites_icon){
            boolean newColourModeActivatedPaths = !colourModeActivatedPaths;
            editor.putBoolean(Constants.COLOURS_PATHS, newColourModeActivatedPaths);
            toastText = newColourModeActivatedPaths ? "Paths: Colour mode activated" : "Paths: Colour mode deactivated";
        }
        else if(v.getId() == R.id.recents_icon){
            boolean newColourModeActivatedBuildings = !colourModeActivatedBuildings;
            editor.putBoolean(Constants.COLOURS_BUILDINGS, newColourModeActivatedBuildings);
            toastText = newColourModeActivatedBuildings ? "Buildings: Colour mode activated" : "Buildings: Colour mode deactivated";
        } else {
            toastText = "";
        }

        editor.apply();
        Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
        fetchColourSettings();
        return false;
    }

    /**
     * When the checkbox for step-free access changes, update the shared preferences
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        stepFree = !stepFree;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("stepFreeNav", stepFree);
        editor.apply();
    }
}
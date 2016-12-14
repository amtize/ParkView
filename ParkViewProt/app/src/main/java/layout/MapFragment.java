package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;

import graesdal.tarjei.parkviewprot.R;
import graesdal.tarjei.parkviewprot.Resources.Playground;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String PLAYGROUND_ARRAY = "playground_array";

    // -- GOOGLEMAP OBJEKTET --
    private GoogleMap map;
    // -- PLAYGROUND ARRAY --
    //  ~ Brukes kun i starten, behandling av Playground-objektene skjer i MainActivity.
    private ArrayList<Playground> playgrounds;
    // -- MARKERS --
    Marker currentMarkedMarker;

    private OnMapFragmentInteractionListener mListener;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(ArrayList<Playground> playgrounds) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(PLAYGROUND_ARRAY, playgrounds);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playgrounds = getArguments().getParcelableArrayList(PLAYGROUND_ARRAY);
        }
    }
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setOnMarkerClickListener(this);
        map.moveCamera(CameraUpdateFactory.newLatLng(playgrounds.get(0).getPosition()));
        map.moveCamera(CameraUpdateFactory.zoomTo(17));
        createMarkers();
    }
    /**
     * Metode som behandler trykk på markøren. Den oppdaterer currentMarkedMarker-referansen. Samtidig
     * så sjekker den om currentMarkedMarker == marker, isåfall kjører metoden onInfoWindowClick().
     *
     * @param marker markøren som blir trykket på
     * @return metoden returnerer alltid true, da ingen feil kan oppstå
     */
    public boolean onMarkerClick(Marker marker) {
        //Denne if-løkken har jeg slik at om man trykker på en markør to ganger på rad så vil det være som om du klikker på infovinduet.
        //Føler selv at det er litt mer intuitivt.
        if (marker.equals(currentMarkedMarker)) {
            mListener.onPlaygroundInspectRequest(marker);
            //map.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(marker.getPosition()).zoom(18).build()));
        }
        //Oppdaterer foreløpig trykt markør, samt åpner infovinduet.
        currentMarkedMarker = marker;
        marker.showInfoWindow();
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMapFragmentInteractionListener) {
            mListener = (OnMapFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void createMarkers() {
        if (mListener != null) {
            ArrayList<Marker> markers = new ArrayList<>();
            for (Playground playground: playgrounds) {
                Marker currentMarker = map.addMarker(new MarkerOptions()
                        .position(playground.getPosition())
                        .title(playground.getFlavorText()));
                markers.add(currentMarker);
            }
            mListener.onMarkersCreated(markers, playgrounds);
        }
    }

    /**
     * Denne interfacen gjør det mulig å kommunisere mellom forelderaktiviteten til denne
     * fragmenten ved at de deler "samme instans" av lytteren.
     */
    public interface OnMapFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
        void onPlaygroundInspectRequest(Marker marker);
        void onMarkersCreated(ArrayList<Marker> markers, ArrayList<Playground> playgrounds);
    }
}

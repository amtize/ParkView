package graesdal.tarjei.parkviewprot;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    public static final String STATE_PLAYGROUND = "state_playground";
    private GoogleMap mMap;
    /*Et hashmap som binder markørene på kartet til lekeplassen den tilhører.
      Dette bruker jeg slik at Playground klassen kun inneholder primitive datastrukturer.
    */
    private Map<Marker, Playground> playgroundBinder = new HashMap<Marker, Playground>();
    public Playground currentMarkedPlayground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        constructParks();
    }

    void constructParks() {
        /*
        Testmetode. Lager flere lekeplasser i tananger-området og legger disse til i hashmappet playgroundBinder. Flytter også kameraet til en av disse parkene.
         */
        ArrayList<Playground> tempPlaygroundArray = new ArrayList<>();
        tempPlaygroundArray.add(new Playground(new LatLng(58.935526, 5.583630), "Torkelstipark", "flavor", 1));
        tempPlaygroundArray.add(new Playground(new LatLng(58.939257, 5.579617), "Storevardskogen", "flavor", 2));
        tempPlaygroundArray.add(new Playground(new LatLng(58.936689, 5.572944), "Risnes", "flavor", 3));
        tempPlaygroundArray.add(new Playground(new LatLng(58.944062, 5.580475), "Myklebust", "flavor", 4));

        //Legger lekeplassene inn på kartet, og legger det inn i hashmappet
        for (int i = 0; i < 4; i++) {
            Marker currentMarker = mMap.addMarker(new MarkerOptions().position(tempPlaygroundArray.get(i).getLocation()).title(tempPlaygroundArray.get(i).getFlavorText()));
            playgroundBinder.put(currentMarker, tempPlaygroundArray.get(i));
        }
        //Flytter kameraet til en av lekeplassene
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tempPlaygroundArray.get(0).getLocation()));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));

        //NB! Siden klassen implementerer lyttegrensesnittene kan jeg bruke metoder rett fra denne klassen.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
    }

    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(getBaseContext(), ParkInspectActivity.class);
        intent.putExtra(STATE_PLAYGROUND, currentMarkedPlayground);
        startActivity(intent);
        return;
    }

    public boolean onMarkerClick(Marker marker) {
        currentMarkedPlayground = playgroundBinder.get(marker);
        marker.showInfoWindow();
        return true;
    }
}

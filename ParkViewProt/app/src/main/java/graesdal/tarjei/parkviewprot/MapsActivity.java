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

    public static final String CURRENT_PLAYGROUND = "current_playground";
    private GoogleMap mMap;
    /*Et hashmap som binder markørene på kartet til lekeplassen den tilhører.
      Dette bruker jeg slik at Playground klassen kun inneholder primitive datatyper.
    */
    private Map<Marker, Playground> playgroundBinder = new HashMap<Marker, Playground>();
    //Lager en "dummy" lekeplass slik at jeg slipper å ta hensyn til spesialtilfelle i onMarkerClick(). Dette bør forandres.
    // FIXME: Finn en annen løsning istedenfor bruk av dummyelement.
    public Playground currentMarkedPlayground = new Playground(new LatLng(0,0),"","",0);

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
            Marker currentMarker = mMap.addMarker(new MarkerOptions().position(tempPlaygroundArray.get(i).getPosition()).title(tempPlaygroundArray.get(i).getFlavorText()));
            playgroundBinder.put(currentMarker, tempPlaygroundArray.get(i));
        }
        //Flytter kameraet til en av lekeplassene
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tempPlaygroundArray.get(0).getPosition()));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));

        //NB! Siden klassen implementerer lyttegrensesnittene kan jeg bruke metoder rett fra denne klassen.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        // TODO: Lag custom infovindu
    }

    /*
    Skifter til ParkInspectActivity når man trykker på infovinduet, samt sender markøren klikket hører til.
    Derfra kan man hente ut informasjonen om lekeplassen inne i ParkInspectActivity sin onCreate().
    Nøkkelen er CURRENT_PLAYGROUND.
     */
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(getBaseContext(), ParkInspectActivity.class);
        intent.putExtra(CURRENT_PLAYGROUND, currentMarkedPlayground);
        startActivity(intent);
        return;
    }

    public boolean onMarkerClick(Marker marker) {
        //Denne if-løkken har jeg slik at om man trykker på en markør to ganger på rad så vil det være som om du klikker på infovinduet.
        //Føler selv at det er litt mer intuitivt.
        if (marker.getPosition().equals(currentMarkedPlayground.getPosition())) {
            onInfoWindowClick(marker);
        }
        //Oppdaterer foreløpig trykt markør, samt åpner infovinduet.
        currentMarkedPlayground = playgroundBinder.get(marker);
        marker.showInfoWindow();
        return true;
    }
}

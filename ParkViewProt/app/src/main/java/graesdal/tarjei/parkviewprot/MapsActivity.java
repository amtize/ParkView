package graesdal.tarjei.parkviewprot;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, View.OnClickListener {

    /*
    Forbedringer:
        - Les mer om singleton og Application-subklassen. Kan jeg da slippe å sende lekeplassene rundt, og heller
        ha et globalt hashmap med all dataen, som gjør det lett å modifisere fra alle deler av appen?
     */

    // -- ID TELLER --
    public static int id = 0;
    // -- NAVN FOR Å DELE INFO MELLOM AKTIVITETER --
    public static final String CURRENT_PLAYGROUND = "current_playground";
    public static final String PLAYGROUND_NAME = "playground_name";
    // -- FLAGG --
    public static final int PLAYGROUND_MODIFIED = 1;
    public static final int PLAYGROUND_NOT_MODIFIED = 2;
    public static final int PLAYGROUND_SUBMIT_REQUEST = 3;
    // -- KARTOBJEKTET
    private GoogleMap mMap;
    /*
      Et hashmap som binder markørene på kartet til lekeplassen den tilhører.
      Dette bruker jeg slik at Playground klassen kun inneholder primitive datatyper, og ikke
      GoogleMap sitt Markerobjekt.
    */
    private Map<Marker, Playground> playgroundBinder = new HashMap<Marker, Playground>();
    public Marker currentMarkedMarker = null;
    public Marker draggableMarker = null;
    private Playground temporaryPlayground;
    private Button lockPositionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initiateButtons();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        constructParks();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.submitButton) {
            Intent intent = new Intent(getBaseContext(), PlaygroundSubmitActivity.class);
            startActivityForResult(intent, PLAYGROUND_SUBMIT_REQUEST);
        }
        if (view.getId() == R.id.lockPositionButton) {
            draggableMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            draggableMarker.setDraggable(false);
            playgroundBinder.get(draggableMarker).setPosition(draggableMarker.getPosition());
            lockPositionButton.setVisibility(View.INVISIBLE);
        }
    }

    private void initiateButtons() {
        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
        lockPositionButton = (Button) findViewById(R.id.lockPositionButton);
        lockPositionButton.setOnClickListener(this);
    }

    void constructParks() {
        /*
        Testmetode. Lager flere lekeplasser i tananger-området og legger disse til i hashmappet playgroundBinder. Flytter også kameraet til en av disse parkene.
         */
        ArrayList<Playground> tempPlaygroundArray = new ArrayList<>();
        tempPlaygroundArray.add(new Playground(new LatLng(58.935526, 5.583630), "Torkelstipark", "flavor", id++));
        tempPlaygroundArray.add(new Playground(new LatLng(58.939257, 5.579617), "Storevardskogen", "flavor", id++));
        tempPlaygroundArray.add(new Playground(new LatLng(58.936689, 5.572944), "Risnes", "flavor", id++));
        tempPlaygroundArray.add(new Playground(new LatLng(58.944062, 5.580475), "Myklebust", "flavor", id++));

        //Legger lekeplassene inn på kartet, og legger det inn i hashmappet
        for (int i = 0; i < 4; i++) {
            Marker currentMarker = mMap.addMarker(new MarkerOptions()
                    .position(tempPlaygroundArray.get(i).getPosition())
                    .title(tempPlaygroundArray.get(i).getFlavorText()));
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
        intent.putExtra(CURRENT_PLAYGROUND, playgroundBinder.get(currentMarkedMarker));
        startActivityForResult(intent, PLAYGROUND_MODIFIED);
        return;
    }

    public boolean onMarkerClick(Marker marker) {
        //Denne if-løkken har jeg slik at om man trykker på en markør to ganger på rad så vil det være som om du klikker på infovinduet.
        //Føler selv at det er litt mer intuitivt.
        if (marker.equals(currentMarkedMarker)) {
            onInfoWindowClick(marker);
        }
        //Oppdaterer foreløpig trykt markør, samt åpner infovinduet.
        currentMarkedMarker = marker;
        marker.showInfoWindow();
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLAYGROUND_MODIFIED) {
            if (resultCode == RESULT_OK) {
                // Om vi får tilbake PLAYGROUND_MODIFIED fra ParkInspectActivitien skal vi erstatte den gamle
                // lekeplassen med den modifiserte vi får tilsendt.
                Playground tempPlayground = (Playground)data.getParcelableExtra(MapsActivity.CURRENT_PLAYGROUND);
                playgroundBinder.remove(currentMarkedMarker);
                playgroundBinder.put(currentMarkedMarker, tempPlayground);
            }
        } else if (requestCode == PLAYGROUND_SUBMIT_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Om vi får en ny PLAYGROUND_SUBMIT_REQUEST fra PlaygroundSubmitActivity skal vi
                // lage en ny markør for denne.
                Toast.makeText(this, "Dra og slipp den blå markøren til der lekeplassen befinner seg. Når du er ferdig trykker du lås inn posisjonen knappen.",
                        Toast.LENGTH_LONG).show();
                initializeMarkerPlacement();
                Playground playground = new Playground(draggableMarker.getPosition(),data.getParcelableExtra(MapsActivity.PLAYGROUND_NAME).toString(), "", id++);
                playgroundBinder.put(draggableMarker, playground);
                lockPositionButton.setVisibility(View.VISIBLE);
            }
        }
    }

    //Kode som initialiserer plasseringen av marker for en lekeplass man submitter
    private void initializeMarkerPlacement() {
        draggableMarker = mMap.addMarker(new MarkerOptions()
                .position(mMap.getCameraPosition().target)
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
    }
}

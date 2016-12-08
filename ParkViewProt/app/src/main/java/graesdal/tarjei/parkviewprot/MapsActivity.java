package graesdal.tarjei.parkviewprot;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.Manifest;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
                                                GoogleMap.OnMarkerClickListener,
                                                GoogleMap.OnInfoWindowClickListener,
                                                View.OnClickListener,
                                                GoogleApiClient.ConnectionCallbacks,
                                                GoogleApiClient.OnConnectionFailedListener,
                                                com.google.android.gms.location.LocationListener {

    /*
    TODO: FIKS ALT SOM HAR MED ID'er Å GJØRE. NÅ STARTER DET PÅ 0 MEN DET BURDE IKKE DET!
    Kommentarer:
        - Les mer om singleton og Application-subklassene. Kan jeg da slippe å sende lekeplassene rundt, og heller
        ha et globalt hashmap med all dataen, som gjør det lett å modifisere fra alle deler av appen?
        - Jeg må bygge et system for å laste opp og hente info fra nettet. Derfor kan det være at det finnes
        bedre løsninger på det jeg har gjort så langt. Uansett så er første prioriteten å bygge grunnlaget
        for selve appen, så får alt som har med nett-synkronisering å gjøre komme senere.
        - Jeg burde kanskje samle alle lyttere i en egen hjelpeklasse for å få bedre oversikt.

     Plan ovenfor integrering til nettet:
        - Foreløpig så lever alle lekeplassene som sine egne objekter med sitt eget navn, sin posisjon og rating.
        Tanken er at jeg skal kunne overføre dette enten til en .json eller .xml, og sende det til en database som finnes på nettet.
        Da vil du første gang du åpner appen, laste ned alle disse .json/xml filene og gjøre de om til Playground objekter. Deretter
        vil appen lage Marker-objekter av disse, og vise de. Arrayen av Playground objekter ønsker jeg å lagre
        lokalt på telefonen, og heller synkronisere dette mot databasen når du åpner appen for å spare databruk.
     */

    // -- ID TELLER --
    public static int id = 0;
    // -- NAVN FOR Å DELE INFO MELLOM AKTIVITETER --
    public static final String CURRENT_PLAYGROUND = "current_playground";
    public static final String PLAYGROUND_NAME = "playground_name";
    public static final String USER_LOGIN_DATA = "user_login_data";
    public static final String USER_LOGIN = "user_login";
    // -- FLAGG --
    public static final int PLAYGROUND_MODIFIED = 1;
    public static final int PLAYGROUND_NOT_MODIFIED = 2;
    public static final int PLAYGROUND_SUBMIT_REQUEST = 3;
    public static final int UPDATE_INTERVAL_IN_MILLISECONDS = 500;
    public static final int FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 200;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 4;
    public static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 5;
    // -- KARTOBJEKTET --
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient = null;
    Location mCurrentLocation;
    boolean mLocationPermissionGranted;
    LocationRequest mLocationRequest;
    // -- BRUKEREN --
    private boolean isLoggedIn;
    private User user = null;
    // -- DEBUG --
    boolean debug = true;
    /*
      Et hashmap som binder markørene på kartet til lekeplassen den tilhører.
      Dette bruker jeg slik at Playground klassen kun inneholder primitive datatyper, og ikke
      GoogleMap sitt Markerobjekt.
    */
    private Map<Marker, Playground> playgroundBinder = new HashMap<Marker, Playground>();
    private Map<Integer, Marker> markerBinder = new HashMap<>();
    public Marker currentMarkedMarker = null;
    public Marker draggableMarker = null;
    private Button lockPositionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        //        .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        if (mGoogleApiClient == null){
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }
        initiateButtons();
        isLoggedIn = getIntent().getBooleanExtra(USER_LOGIN, false);
        if (isLoggedIn) initiateUserData();
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (playgroundBinder.isEmpty()) constructParks();
        if (isLoggedIn && user == null) initiateUserData();
        updateLocationUI();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mCurrentLocation.getLatitude(),
            mCurrentLocation.getLongitude())));
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */,
                    this /* OnConnectionFailedListener */)
            .addConnectionCallbacks(this)
            .addApi(LocationServices.API)
            .addApi(Places.GEO_DATA_API)
            .addApi(Places.PLACE_DETECTION_API)
            .build();
        createLocationRequest();
    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (mLocationPermissionGranted) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
         switch (requestCode) {
                case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
                }
         }
        updateLocationUI();
    }


    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mCurrentLocation = null;
        }
    }


    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        /*
         * Sets the desired interval for active location updates. This interval is
         * inexact. You may not receive updates at all if no location sources are available, or
         * you may receive them slower than requested. You may also receive updates faster than
         * requested if other applications are requesting location at a faster interval.
         */
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        /*
        * Sets the fastest rate for active location updates. This interval is exact, and your
        * application will never receive updates faster than this value.
        */
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.submitButton) {
            Intent intent = new Intent(getBaseContext(), PlaygroundSubmitActivity.class);
            startActivityForResult(intent, PLAYGROUND_SUBMIT_REQUEST);
        }
        if (view.getId() == R.id.lockPositionButton) {
            draggableMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            draggableMarker.setDraggable(false);
            draggableMarker.setTitle(playgroundBinder.get(draggableMarker).getFlavorText());
            draggableMarker.hideInfoWindow();
            playgroundBinder.get(draggableMarker).setPosition(draggableMarker.getPosition());
            lockPositionButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        //updateMarkers();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getDeviceLocation();
        if (mCurrentLocation != null) {
            // -OPPDATERER KAMERAPOSISJONEN TIL DIN POSISJON-
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mCurrentLocation.getLatitude(),
                    //mCurrentLocation.getLongitude())));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        
    }

    private void initiateButtons() {
        // -INITIALISERER ALLE KNAPPENE, OG SETTER LYTTEREN TIL Å VÆRE onClick() I DENNE KLASSEN
        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
        lockPositionButton = (Button) findViewById(R.id.lockPositionButton);
        lockPositionButton.setOnClickListener(this);
    }

    /**
     * Metode som skifter til ParkInspectActivity når infovinduet blir trykket på, samt
     * sender en referanse til den aktuelle markøren, slik at man kan vise rett info i ParkInspectActivity
     *
     * @param marker markøren som får infovinduet sitt trykket på.
     */
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(getBaseContext(), ParkInspectActivity.class);
        //Nøkkelen er CURRENT_PLAYGROUND.
        intent.putExtra(CURRENT_PLAYGROUND, playgroundBinder.get(currentMarkedMarker));
        //Starter aktiviteten for et resultat, slik at om Playground objektet blir modifisert kan jeg oppdatere det her.
        startActivityForResult(intent, PLAYGROUND_MODIFIED);
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
            onInfoWindowClick(marker);
        }
        //Oppdaterer foreløpig trykt markør, samt åpner infovinduet.
        currentMarkedMarker = marker;
        marker.showInfoWindow();
        return true;
    }

    /**
     * Denne metoden behandler alle aktiviteter som blir startet for et resultat. Først sjekker
     * den hva som aktiviteten ble spurt om slik at den kan gjøre de rette tingene. Mer info er
     * spesifisert inne i metoden.
     *
     * @param requestCode et flagg som sier hvilket resultat aktiviteten ble startet for.
     * @param resultCode et flagg fra android APIen som sier hvordan operasjonen gikk.
     *                   @see com.google.android.gms.identity.intents.AddressConstants.ResultCodes
     * @param data dataen som kommer fra aktiviteten.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PLAYGROUND_MODIFIED) {
                // Om vi får tilbake PLAYGROUND_MODIFIED fra ParkInspectActivitien skal vi erstatte den gamle
                // lekeplassen med den modifiserte vi får tilsendt.
                Playground tempPlayground = (Playground)data.getParcelableExtra(MapsActivity.CURRENT_PLAYGROUND);
                playgroundBinder.remove(currentMarkedMarker);
                playgroundBinder.put(currentMarkedMarker, tempPlayground);
            }
            else if (requestCode == PLAYGROUND_SUBMIT_REQUEST) {
                // Om vi får en ny PLAYGROUND_SUBMIT_REQUEST fra PlaygroundSubmitActivity skal vi
                // lage en ny markør for denne.
                CharSequence charSequence = "Dra og slipp den blå markøren til der lekeplassen befinner seg.";
                Toast toast = Toast.makeText(getApplicationContext(), charSequence, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                initializeMarkerPlacement();
                String name = data.getStringExtra(MapsActivity.PLAYGROUND_NAME);
                Playground playground = new Playground(draggableMarker.getPosition(), name, "", id++);
                playgroundBinder.put(draggableMarker, playground);
                markerBinder.put(playground.getId(), draggableMarker);
                lockPositionButton.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Feil " + resultCode, Toast.LENGTH_SHORT).show();
        }
    }

    //Kode som initialiserer plasseringen av marker for en lekeplass man submitter
    private void initializeMarkerPlacement() {
        draggableMarker = mMap.addMarker(new MarkerOptions()
                .position(mMap.getCameraPosition().target)
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        draggableMarker.setTitle("Trykk og hold for å flytte meg!");
        draggableMarker.showInfoWindow();
    }

    void initiateUserData() {
        String[] stringFromMain = getIntent().getStringExtra(USER_LOGIN_DATA).split(";");
        String username = stringFromMain[0];
        String password = stringFromMain[1];
        //Toast.makeText(getApplicationContext(), username + ";" + password, Toast.LENGTH_LONG).show();
        user = WriteToInternalStorage.readUser(getFilesDir(), username, password, getApplicationContext());
        if (user == null) {
            isLoggedIn = false;
            return;
        }
        ArrayList<Boolean> visitedList = user.getVisitedPlaygrounds();
        for(int _id = 1; _id < id; _id++) {
            // FIXME: TRAINWRECK
            playgroundBinder.get(markerBinder.get(_id)).setVisited(visitedList.get(_id));
        }
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
        for (Playground playground: tempPlaygroundArray) {
            Marker currentMarker = mMap.addMarker(new MarkerOptions()
                    .position(playground.getPosition())
                    .title(playground.getFlavorText()));
            playgroundBinder.put(currentMarker, playground);
            markerBinder.put(playground.getId(), currentMarker);
        }
        //Flytter kameraet til en av lekeplassene
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tempPlaygroundArray.get(0).getPosition()));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));

        //NB! Siden klassen implementerer lyttegrensesnittene kan jeg bruke metoder rett fra denne klassen.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        // TODO: Lag custom infovindu
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

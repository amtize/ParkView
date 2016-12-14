package graesdal.tarjei.parkviewprot;

import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import graesdal.tarjei.parkviewprot.Resources.Playground;
import graesdal.tarjei.parkviewprot.Resources.TwoWayHashMap;
import layout.InspectFragment;
import layout.MapFragment;

public class MainActivity extends FragmentActivity implements MapFragment.OnMapFragmentInteractionListener, InspectFragment.OnFragmentInteractionListener {


    // -- ID --
    //  ~ Id til parkene. Dette skal etter hvert ligge på nettet og ikke lokalt på telefonen
    public static int id = 1;
    // -- HASHMAP BINDER --
    //  ~ Custom hashmap-objekt som binder sammen lekeplasser og markers
    private TwoWayHashMap binder = null;
    // -- FRAGMENTMANAGER --
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MapFragment mapFragment = MapFragment.newInstance(constructParks());
        fragmentTransaction.add(R.id.container, mapFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO: fix
    }

    public void onMarkersCreated(ArrayList<Marker> markers, ArrayList<Playground> playgrounds) {
        binder = new TwoWayHashMap(markers, playgrounds);
    }

    public void onPlaygroundInspectRequest(Marker marker) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(R.id.container, InspectFragment.newInstance(binder.getPlayground(marker)));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private ArrayList<Playground> constructParks() {
        ArrayList<Playground> tempPlaygroundArray = new ArrayList<>();
        tempPlaygroundArray.add(new Playground(new LatLng(58.935526, 5.583630), "Torkelstipark", "flavor", id++));
        tempPlaygroundArray.add(new Playground(new LatLng(58.939257, 5.579617), "Storevardskogen", "flavor", id++));
        tempPlaygroundArray.add(new Playground(new LatLng(58.936689, 5.572944), "Risnes", "flavor", id++));
        tempPlaygroundArray.add(new Playground(new LatLng(58.944062, 5.580475), "Myklebust", "flavor", id++));
        return tempPlaygroundArray;
    }
}

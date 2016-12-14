package graesdal.tarjei.parkviewprot.Resources;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tarjei on 14.12.2016.
 * Klasse som inneholder to hashmaps for å kunne hente ut tilhørende lekeplass/markør med den
 * motsatte referansen.
 */
public class TwoWayHashMap {

    private Map<Marker, Playground> markerPlaygroundMap = new HashMap<>();
    private Map<Playground, Marker> playgroundMarkerMap = new HashMap<>();

    public TwoWayHashMap(ArrayList<Marker> markerList, ArrayList<Playground> playgroundList) {
        if (markerList.size() != playgroundList.size())
            throw new IndexOutOfBoundsException("Length of markerList does not match length of palygroundList");
        for (int i = 0; i < markerList.size(); i++) {
            markerPlaygroundMap.put(markerList.get(i), playgroundList.get(i));
            playgroundMarkerMap.put(playgroundList.get(i), markerList.get(i));
        }
    }

    public Marker getMarker(Playground playground) {
        return playgroundMarkerMap.get(playground);
    }

    public Playground getPlayground(Marker marker) {
        return markerPlaygroundMap.get(marker);
    }

    public boolean updatePlayground(Playground playground) {
        //Henter ut markøren som hører til lekeplassen
        Marker marker = playgroundMarkerMap.get(playground);
        //Oppdaterer lekeplassen i det ene hashmappet
        markerPlaygroundMap.remove(marker);
        markerPlaygroundMap.put(marker, playground);
        //Oppdaterer lekeplassen i det andre hashmappet
        playgroundMarkerMap.remove(playground);
        playgroundMarkerMap.put(playground, marker);
        return true;
    }

}

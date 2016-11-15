package graesdal.tarjei.parkviewprot;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.internal.ParcelableSparseArray;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tarjei on 09.11.2016.
 */

enum VisitorType {INFANT, CHILD, YOUNGSTER };

public class Playground implements Parcelable{

    private int id; //Alle lekeplasser har en unik id
    private double latitude;
    private double longitude;
    private String name; //Foreløpig vil dette være adressen
    private String flavorStr;
    //private List<Visitor> visitors;

    private class Visitor {
        VisitorType type;
        int TimeOfDayCheckIn = -1;
        // TODO: Legg til funksjonalitet for å ha en check-in tid.
    }

    public Playground(LatLng pos, String name, String flavorStr, int id) {
        this.id = id;
        this.latitude = pos.latitude;
        this.longitude = pos.longitude;
        this.name = name;
        this.flavorStr = flavorStr;
        //visitors = new ArrayList<Visitor>();
    }

    public Playground(Parcel input) {
        id = input.readInt();
        name = input.readString();
        flavorStr = input.readString();
        latitude = input.readDouble();
        longitude = input.readDouble();
    }

    //Returnerer LatLng / posisjonen til denne parken.
    public LatLng getPosition() { return new LatLng(latitude, longitude); }

    public int getNumOfVisitors() {
        //return visitors.size();
        return -1;
    }

    public String getFlavorText() { return flavorStr; }

    public String getName() { return name; }

    public void checkIn() {
        // TODO: Legg til funksjonalitet
    }

    //Skriver lekeplassen til en oppgitt Parcel.
    //Denne metoden blir kalt automatisk av operativsystemet når jeg i andre metoder kaller intent.putExtra()
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(flavorStr);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    //Har ingen spesielle datatyper her, derfor kan jeg returnere 0.
    @Override
    public int describeContents() {
        return 0;
    }

    //Lager lekeplass fra oppgitt Parcel via egen konstruktør
    //Denne metoden blir kalt automasik av operativsystemet når jeg i andre metoder kaller intent.getParcelableExtra()
    public static final Parcelable.Creator<Playground> CREATOR =
            new Parcelable.Creator<Playground>(){
        public Playground createFromParcel(Parcel in) {
            return new Playground(in);
        }

        public Playground[] newArray(int size){
            return new Playground[size];
        }
    };

}

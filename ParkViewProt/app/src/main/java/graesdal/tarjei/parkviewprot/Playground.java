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

    private int id;
    private double latitude;
    private double longitude;
    private String name;
    private String flavorStr;
    //private List<Visitor> visitors;

    private class Visitor {
        VisitorType type;
        int TimeOfDayCheckIn = -1;
        // TODO: Legg til funksjonalitet for å ha en check-in tid.
    }

    public Playground(LatLng loc, String name, String flavorStr, int id) {
        this.id = id;
        this.latitude = loc.latitude;
        this.longitude = loc.longitude;
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

    public LatLng getLocation() {
        //Returnerer LatLng / posisjonen til denne parken.
        return new LatLng(latitude, longitude);
    }

    public int getNumOfVisitors() {
        //return visitors.size();
        return -1;
    }

    public String getFlavorText() {
        return flavorStr;
    }

    public void checkIn() {
        // TODO: Legg til funksjonalitet
    }

    //Skriver lekeplassen til en oppgitt Parcel.
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(flavorStr);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //Lager lekeplass fra oppgitt Parcel via egen konstruktør
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

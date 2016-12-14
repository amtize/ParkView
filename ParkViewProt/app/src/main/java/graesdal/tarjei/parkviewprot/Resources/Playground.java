package graesdal.tarjei.parkviewprot.Resources;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Tarjei on 09.11.2016.
 */

public class Playground implements Parcelable{

    private int id; //Alle lekeplasser har en unik id
    private double latitude;
    private double longitude;
    private String name; //Foreløpig vil dette være adressen
    private String flavorStr;
    private float rating = 0;
    private int numOfRatings = 0;
    private boolean visited; //boolean som sier om lekeplassen er besøkt


    public Playground(LatLng pos, String name, String flavorStr, int id) {
        this.id = id;
        this.latitude = pos.latitude;
        this.longitude = pos.longitude;
        this.name = name;
        this.flavorStr = flavorStr;
        visited = false;
    }

    public Playground(Parcel input) {
        id = input.readInt();
        name = input.readString();
        flavorStr = input.readString();
        latitude = input.readDouble();
        longitude = input.readDouble();
        rating = input.readFloat();
        numOfRatings = input.readInt();
        visited = input.readByte() != 0; //konverterer til boolean
    }

    //Returnerer LatLng / posisjonen til denne parken.
    public LatLng getPosition() { return new LatLng(latitude, longitude); }

    public int getId() { return id; }

    public void setPosition(LatLng position) {
        latitude = position.latitude;
        longitude = position.longitude;
    }

    public void setVisited(boolean val) {
        this.visited = val;
    }

    public boolean isVisited() { return visited; }

    public void registerRating(float rating) {
        //Oppdaterer ratingen ved å bruke gjennomsnittet av den nye og de foregående.
        this.rating = ((this.rating * numOfRatings) + rating) / (numOfRatings + 1);
        numOfRatings++;
    }

    public float getRating() {
        return rating;
    }

    public String getFlavorText() { return flavorStr; }

    public String getName() { return name; }

    //Skriver lekeplassen til en oppgitt Parcel.
    //Denne metoden blir kalt automatisk av operativsystemet når jeg i andre metoder kaller intent.putExtra()
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(flavorStr);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeFloat(rating);
        dest.writeInt(numOfRatings);
        dest.writeByte((byte) (visited ? 1 : 0)); //konverterer til boolean
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

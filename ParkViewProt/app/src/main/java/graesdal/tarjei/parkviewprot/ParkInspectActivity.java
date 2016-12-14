package graesdal.tarjei.parkviewprot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;

import graesdal.tarjei.parkviewprot.Resources.Playground;

public class ParkInspectActivity extends AppCompatActivity implements RatingBar.OnRatingBarChangeListener, View.OnClickListener {

    Playground playground;
    RatingBar ratingBar;
    CheckBox checkBox;
    float currentRating = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_inspect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Henter ut lekeplassen som ble sendt fra MapsActivity.
        playground = getIntent().getParcelableExtra(MapsActivity.CURRENT_PLAYGROUND);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(this);
        //ratingBar.setMax(5);
        ratingBar.setRating(playground.getRating());
        TextView tv = (TextView) findViewById(R.id.park_inspect_text);
        TextView rtv = (TextView) findViewById(R.id.ratingText);
        rtv.setText(String.valueOf(ratingBar.getRating()));
        tv.setText(playground.getName());

        checkBox = (CheckBox) findViewById(R.id.visitedCheckBox);
        checkBox.setChecked(playground.isVisited());
        Button checkInButton = (Button) findViewById(R.id.checkInButton);
        checkInButton.setOnClickListener(this);
    }

    /*
    Her overskriver jeg finish()-metoden til systemet som blir kalt når man for eksempel trykker på "back"knappen.
    Her sender jeg den modifiserte lekeplassen tilbake til MapsActivity klassen. På et eller annent tidspunkt
    så vil nok dette bli erstattet til å gå rett til internettet.
     */
    public void finish() {
        Intent intent = new Intent(getBaseContext(), MapsActivity.class);
        if (currentRating != -1) {
            playground.registerRating(currentRating);
        }
        intent.putExtra(MapsActivity.CURRENT_PLAYGROUND, playground);
        setResult(MapsActivity.RESULT_OK, intent);
        super.finish();
    }

    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        currentRating = rating;
    }

    @Override
    public void onClick(View v) {
        // TODO: If myPosition more than 50 meters from playground.position, display NotCloseEnough
        checkBox.setChecked(true);
        playground.setVisited(true);
    }
}

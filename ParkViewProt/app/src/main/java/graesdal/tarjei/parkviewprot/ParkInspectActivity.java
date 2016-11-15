package graesdal.tarjei.parkviewprot;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class ParkInspectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_inspect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Henter ut lekeplassen som ble sendt fra MapsActivity.
        Playground pg = getIntent().getParcelableExtra(MapsActivity.STATE_PLAYGROUND);
        TextView tv = (TextView) findViewById(R.id.park_inspect_text);
        tv.setText(pg.getFlavorText());
    }

}

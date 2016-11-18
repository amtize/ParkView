package graesdal.tarjei.parkviewprot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class ParkInspectActivity extends AppCompatActivity {

    Playground playground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_inspect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Henter ut lekeplassen som ble sendt fra MapsActivity.
        playground = getIntent().getParcelableExtra(MapsActivity.CURRENT_PLAYGROUND);
        TextView tv = (TextView) findViewById(R.id.park_inspect_text);
        tv.setText(playground.getName());
    }

}

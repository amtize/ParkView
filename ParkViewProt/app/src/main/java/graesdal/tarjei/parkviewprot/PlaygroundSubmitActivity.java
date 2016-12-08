package graesdal.tarjei.parkviewprot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PlaygroundSubmitActivity extends AppCompatActivity implements View.OnClickListener{

    EditText nameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playground_submit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        Button submitButton = (Button) findViewById(R.id.sendSubmissionRequest);
        submitButton.setOnClickListener(this);
    }

    public void onClick(View view) {
        Intent intent = new Intent(getBaseContext(), MapsActivity.class);
        intent.putExtra(MapsActivity.PLAYGROUND_NAME, nameEditText.getText().toString());
        Log.d("EDIT TEXT VERDI: ",nameEditText.getText().toString());
        setResult(RESULT_OK, intent);
        super.finish();
    }



}

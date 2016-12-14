package graesdal.tarjei.parkviewprot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import graesdal.tarjei.parkviewprot.Resources.InternalStorage;
import graesdal.tarjei.parkviewprot.Resources.User;

public class StartupActivity extends AppCompatActivity {

    /*
    StartupActivity er foreløpig en "placeholder" som kun velger mellom vanlig modus og debug modus, samt andre ting.
    Denne eksisterer bare slik at jeg raskt kan bytte mellom de to.
     */

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Lager logikk som bytter til den respektive activitien fra StartupActivity
        Button launchDebug = (Button) findViewById(R.id.launch_debug);
        Button loginButton = (Button) findViewById(R.id.login);
        final Button makeUserTest = (Button) findViewById(R.id.makeUserButton);
        // TODO: Lag debug funksjon
        launchDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra(MapsActivity.USER_LOGIN, false);
                startActivity(intent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                EditText usernameT = (EditText) findViewById(R.id.usernameEditText);
                EditText passwordT = (EditText) findViewById(R.id.passwordEditText);
                intent.putExtra(MapsActivity.USER_LOGIN_DATA, usernameT.getText()+";"+passwordT.getText());
                intent.putExtra(MapsActivity.USER_LOGIN, true);
                startActivity(intent);
            }
        });
        makeUserTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testUserCreation();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void testUserCreation() {
        User user = new User("Test", "123", 1);
        InternalStorage.writeUser(getFilesDir(), user, getApplicationContext());
    }

}

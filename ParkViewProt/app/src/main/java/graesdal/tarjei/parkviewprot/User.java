package graesdal.tarjei.parkviewprot;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tarjei on 06.12.2016.
 * Testklasse for bruker. Dette vil etter hvert bli flyttet til nettet.
 */
public class User implements Serializable {

    int id;
    int score;
    String username;
    String password; // FIXME: Ikke lagre passordet i plaintext!
    //Array av booleans. Id'en til lekeplassen MÅ stemme overens med indeksen til boolean verdien, som sier om brukeren har besøkt eller ei.
    ArrayList<Boolean> visited;

    public User(String username, String password, int id) {
        score = 0;
        this.id = id;
        this.username = username;
        this.password = password;
        visited = new ArrayList<>();
        visited.add(false);
        for (int i = 1; i <= 4; i++) {
            visited.add(false);
        }
    }

    public ArrayList<Boolean> getVisitedPlaygrounds() {
        return visited;
    }

    public boolean visitPlayground(int playgroundID) {
        if (visited.get(playgroundID) == true) return false;
        visited.set(playgroundID, true);
        score++;
        return true;
    }
}

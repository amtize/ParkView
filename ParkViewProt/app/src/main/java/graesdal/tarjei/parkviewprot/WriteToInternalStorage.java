package graesdal.tarjei.parkviewprot;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * Created by Tarjei on 08.12.2016.
 */
public class WriteToInternalStorage {

    public static boolean writeUser(File dir, User user, Context context) {
        String fileName = "userData.txt";
        File file = new File(dir, fileName);
        try (FileOutputStream fStream = new FileOutputStream(file);
             ObjectOutputStream oStream = new ObjectOutputStream(fStream);) {

            oStream.writeObject(user);
            Toast.makeText(context, "LAGET BRUKER", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        };
        return true;
    }

    public static User readUser(File dir, String username, String password, Context context) {
        User user;
        File file = new File(dir.getPath() + "/userData.txt");
        try (FileInputStream fStream = new FileInputStream(file);
             ObjectInputStream oStream = new ObjectInputStream(fStream);) {

            user = (User) oStream.readObject();
            Toast.makeText(context, "Comparing username;password:\nGiven:"+username+";"+password
                    +"\nStored:"+user.username+";"+user.password, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (user.password.equals(password) && user.username.equals(username)) {
            Toast.makeText(context, "TEST OK, LOGGING IN", Toast.LENGTH_LONG).show();
            return user;
        }
        return null;
    }



}

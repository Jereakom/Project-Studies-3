package group1.oamk.ringo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PHONE_STATE = 1;



    public static ContactsDataSource datasource;

    public static Vibrator vib;

    private static String[] READ_PHONE_STATE_PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };





    public static void verifyPhoneStatePermission(Activity activity) {

        int permissionState = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);

        if (permissionState != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    READ_PHONE_STATE_PERMISSIONS,
                    REQUEST_PHONE_STATE
            );
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        datasource = new ContactsDataSource(MainActivity.this);

        vib = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);

        final Button button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, makeRingtone.class));
            }
        });

        verifyPhoneStatePermission(this);

        Button btn_exit = (Button) findViewById(R.id.exit_button);
        btn_exit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);

            }
        });

        String rootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/Music/Ringo/";
        File root = new File(rootPath);
        if (!root.exists()) {
            root.mkdirs();
        }
    }

    public void goToGallery(View view) {
        startActivity(new Intent(MainActivity.this, getRingtones.class));

    }

    public void goToPatterns(View view) {
        startActivity(new Intent(MainActivity.this, browsePatterns.class));

    }

    public void go_to_recording(View view) {
        startActivity(new Intent(MainActivity.this, makeSound.class));

    }
    public void go_to_vibration(View view) {
        startActivity(new Intent(MainActivity.this, VibrationActivity.class));

    }

    public void goToPackages(View view) {
        startActivity(new Intent(MainActivity.this, browsePackages.class));

    }

}

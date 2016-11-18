package group1.oamk.ringo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, makeRingtone.class));
            }
        });



        Button btn_exit = (Button) findViewById(R.id.exit_button);
        btn_exit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
                System.exit(0);
                TextView testy = (TextView)findViewById(R.id.test);
                testy.setText("Exit");
            }
        });
    }

    public void testThis(View view) {
        CharSequence test = "THIS IS ANOTHER TEST";
        TextView testy = (TextView)findViewById(R.id.test);
        testy.setText(test);
    }

    public void goToGallery(View view) {
        startActivity(new Intent(MainActivity.this, getRingtones.class));

    }
    public void go_to_vibration(View view) {
        startActivity(new Intent(MainActivity.this, VibrationActivity.class));

    }



    public void goToPackages(View view) {
        startActivity(new Intent(MainActivity.this, browsePackages.class));

    }

}


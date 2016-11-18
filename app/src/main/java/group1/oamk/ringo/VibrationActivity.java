package group1.oamk.ringo;


import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public  class VibrationActivity extends AppCompatActivity {
    Button btn;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_vibration_layout);

        btn = (Button) findViewById(R.id.vibrate_button);

        final Vibrator vibrator;

        vibrator = (Vibrator) getSystemService(MainActivity.VIBRATOR_SERVICE);

        btn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    vibrator.vibrate(60000);

                } else if (action == MotionEvent.ACTION_UP) {
                    vibrator.cancel();

                }

                return true;
            }


        });

    }
}




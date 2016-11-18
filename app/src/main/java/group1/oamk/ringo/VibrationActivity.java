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
import android.widget.Toast;

public  class VibrationActivity extends AppCompatActivity {
    Button btn_vibrate, btn_preview;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_vibration_layout);
        create_vibration_pattern();
        preview_vibtation();
    }

    private void preview_vibtation() {

        btn_preview =(Button) findViewById(R.id.preview_button);
        txt = (TextView) findViewById(R.id.txt);
        btn_preview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                long[] pattern = {0, 1000, 1000, 300, 200, 100, 500, 200, 100};

                // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
                v.vibrate(pattern, -1);
                txt.setText(""+pattern[3]+"");
            }
        });
    }

    private void create_vibration_pattern() {
        btn_vibrate = (Button) findViewById(R.id.vibrate_button);

        final Vibrator vibrator;

        vibrator = (Vibrator) getSystemService(MainActivity.VIBRATOR_SERVICE);

        btn_vibrate.setOnTouchListener(new View.OnTouchListener() {

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




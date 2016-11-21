package group1.oamk.ringo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by sst_h on 11/21/2016.
 */

public class Sound_Recording extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_sound_recording);

    }
    public void goToMain(View view) {
        startActivity(new Intent(Sound_Recording.this, MainActivity.class));
    }

}

package group1.oamk.ringo;

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
                CharSequence test = "asdasdasd";
                TextView testy = (TextView)findViewById(R.id.test);
                testy.setText(test);
            }
        });

    }

    public void testThis(View view) {
        CharSequence test = "THIS IS ANOTHER TEST";
        TextView testy = (TextView)findViewById(R.id.test);
        testy.setText(test);
    }

}


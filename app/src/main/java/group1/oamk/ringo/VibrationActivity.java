package group1.oamk.ringo;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class VibrationActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0 ;

    private static final int PICK_CONTACT = 1000;

    public ContactsDataSource datasource;

    private long[] pattern;

    public long[] getPattern(String phone_nr){


        long[] getPattern = null;
        try{
            datasource.open();
            Contact contact = datasource.getContact(phone_nr);
            datasource.close();
            getPattern = contact.getPattern();

        }catch(SQLiteException e){
            e.printStackTrace();
        }
        return getPattern;

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_vibration_layout);

        datasource = new ContactsDataSource(this);

    /*
         datasource.open();

        // datasource.createContact("25443343433", pattern);
        List<Contact> values = datasource.getAllContacts();
        Log.v("Values:", ""+values);
        datasource.close();

    */

        create_vibration_pattern();
    }



    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {

                    String number = "";

                    ContentResolver cr = getContentResolver();
                    Cursor cursor = cr.query(data.getData(), null, null, null, null);

                    while (cursor.moveToNext()) {
                        String contactId = cursor.getString(cursor
                                .getColumnIndex(ContactsContract.Contacts._ID));
                        if (Integer
                                .parseInt(cursor.getString(cursor
                                        .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                            + " = " + contactId, null, null);
                            while (phones.moveToNext()) {
                                number = phones
                                        .getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            }
                            phones.close();
                        }

                    }
                    cursor.close();
                    try
                    {
                        datasource.open();
                        datasource.createContact(number, longToString(pattern));
                        datasource.close();
                    }
                    catch (Exception e){
                        // IGNORANCE IS THE BEST POLICY
                    }

                }

        }
    }



    private String longToString(long[] longArray)
    {
        String resultString = "";

        for (int i = 0; i < longArray.length ; i++) {
            if (i == 0)
            {
                resultString += ""+longArray[i];
            }
            else
            {
                resultString += ","+longArray[i];
            }

        }
        return resultString;
    }


    private void create_vibration_pattern() {

        final Vibrator vibrator;

        final ArrayList length = new ArrayList();

        length.add((long)0);

        final long[] durationArray = {0,0,0} ;

        vibrator = (Vibrator) getSystemService(MainActivity.VIBRATOR_SERVICE);

        Button preview = (Button)findViewById(R.id.preview_button);

        Button btn = (Button)findViewById(R.id.vibrate_button);

        Button end = (Button)findViewById(R.id.end_button);

        Button start = (Button)findViewById(R.id.start_button);

        Button assign_button = (Button)findViewById(R.id.assign_button);

        final TextView time = (TextView)findViewById(R.id.time);

        assign_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);

                Log.v("CONTACT:", "");
            }
        });

        btn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                long duration ;

                if (action == MotionEvent.ACTION_DOWN) {

                    if (durationArray[2] != 0)
                    {
                        long testval = durationArray[1] - System.currentTimeMillis();
                        length.add(-1*testval);
                    }
                    durationArray[0] = System.currentTimeMillis();
                    Log.v("PressTime", ""+durationArray[0]);
                    vibrator.vibrate(60000);

                } else if (action == MotionEvent.ACTION_UP) {
                    durationArray[1] = System.currentTimeMillis();
                    duration = durationArray[1] - durationArray[0];

                    time.setText(""+duration);

                    durationArray[2] = duration;

                    length.add(duration);
                    vibrator.cancel();

                }

                Log.v("Length:", ""+length.toString());
                return true;
            }


        });

        end.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (length.size() > 0)
                {
                    long[] longth = new long[length.size()];
                    for(int i = 0; i < length.size(); i++)
                    {
                        longth[i] = (long) length.get(i);
                    }

                    pattern = longth;
                }
                return true;
            }


        });

        preview.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                long[] longth = new long[length.size()];
                for(int i = 0; i < length.size(); i++)
                {
                    longth[i] = (long) length.get(i);
                }

                vibrator.vibrate(longth, -1);
                return true;
            }

        });

        start.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                length.clear();
                length.add((long)0);
                durationArray[2] = 0;
                return true;
            }

        });

    }
}




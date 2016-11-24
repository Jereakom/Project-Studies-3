package group1.oamk.ringo;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static group1.oamk.ringo.MainActivity.datasource;
import static group1.oamk.ringo.MainActivity.vib;


public class VibrationActivity extends AppCompatActivity {

    private static final int PICK_CONTACT = 1000;

    private long[] pattern;

    private static final int REQUEST_CONTACT_PERMISSIONS = 1;

    private static String[] READ_CONTACT_PERMISSIONS = {
            Manifest.permission.READ_CONTACTS
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_vibration_layout);

        int permissionContact = ActivityCompat.checkSelfPermission(VibrationActivity.this, Manifest.permission.READ_CONTACTS);

        if (permissionContact != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    VibrationActivity.this,
                    READ_CONTACT_PERMISSIONS,
                    REQUEST_CONTACT_PERMISSIONS
            );
        }

        create_vibration_pattern();
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {

                    String number = "";
                    String contactName = "";

                    ContentResolver cr = getContentResolver();
                    Cursor cursor = cr.query(data.getData(), null, null, null, null);

                    while (cursor.moveToNext()) {
                        String contactId = cursor.getString(cursor
                                .getColumnIndex(ContactsContract.Contacts._ID));
                        contactName = cursor.getString(cursor
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
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
                        datasource.createContact(contactName, number, longToString(pattern));
                        datasource.close();
                        Toast.makeText(VibrationActivity.this, "Pattern assigned to contact!", Toast.LENGTH_SHORT).show();

                    }
                    catch (Exception e){
                        // IGNORANCE IS THE BEST POLICY
                    }

                }
        }
    }



    public static String longToString(long[] longArray)
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

        final ArrayList length = new ArrayList();

        length.add((long)0);

        final long[] durationArray = {0,0,0} ;

        Button preview = (Button)findViewById(R.id.preview_button);

        Button btn = (Button)findViewById(R.id.vibrate_button);

        Button save = (Button)findViewById(R.id.save_button);

        Button end = (Button)findViewById(R.id.end_button);

        Button start = (Button)findViewById(R.id.start_button);

        Button assign_button = (Button)findViewById(R.id.assign_button);

        final TextView time = (TextView)findViewById(R.id.time);

        assign_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
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
                    vib.vibrate(60000);

                } else if (action == MotionEvent.ACTION_UP) {
                    durationArray[1] = System.currentTimeMillis();
                    duration = durationArray[1] - durationArray[0];

                    time.setText(""+duration);

                    durationArray[2] = duration;

                    length.add(duration);
                    vib.cancel();

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

        save.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != 0)
                {
                    return true;
                }
                EditText name = (EditText)findViewById(R.id.pattern_name);
                String patternName = name.getText().toString();

                String number = "";

                if (patternName.equals(""))
                {
                    Toast.makeText(VibrationActivity.this, "Name cannot be blank", Toast.LENGTH_SHORT).show();
                    return true;
                }



                if (pattern == null)
                {
                    Toast.makeText(VibrationActivity.this, "Press \"Store\" to store the pattern before saving!", Toast.LENGTH_LONG).show();
                    return true;
                }

                try
                {
                    datasource.open();
                    datasource.createContact(patternName, number, longToString(pattern));
                    datasource.close();
                    Toast.makeText(VibrationActivity.this, "Pattern saved!", Toast.LENGTH_SHORT).show();

                }
                catch (Exception e){
                    // IGNORANCE IS THE BEST POLICY
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

                vib.vibrate(longth, -1);
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




package group1.oamk.ringo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class VibrationActivity extends AppCompatActivity {

    private static final int PICK_CONTACT = 1000;

    //public ContactsDataSource datasource = new ContactsDataSource(this);

    private long[] pattern;

    IntentFilter filterForCallEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_vibration_layout);

        filterForCallEvents = new IntentFilter("android.intent.action.PHONE_STATE");
    //    registerReceiver(myReceiver, filterForCallEvents);
        create_vibration_pattern();
    }

    /*

    public final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            telephony.listen(new PhoneStateListener(){
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    super.onCallStateChanged(state, incomingNumber);

                    long[] pattern = null;

                    //Make a database call, to get the vibrate pattern
                    if (state == 1)
                    {
                        try {

                            Contact contact = getPattern(incomingNumber);
                            pattern = contact.getPattern();
                        }

                        catch (Exception e)
                        {
                            e.getStackTrace();
                        }

                    }

                    //Set the phone to vibrate using that pattern, if there was a mapping
                    if(pattern != null){
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(pattern, -1);
                    }
                    System.out.println("incomingNumber : "+incomingNumber);
                }
            },PhoneStateListener.LISTEN_CALL_STATE);
        }
    };

    @Override
    public void onDestroy() {
        unregisterReceiver(myReceiver);
        super.onDestroy();
    }

    */

     //   ContactsDataSource datasource = new ContactsDataSource(this);


    public Contact getPattern(String phone_nr){

        Contact returncontact = null;

        long[] getPattern = null;
        try{
            MainActivity.datasource.open();
            returncontact = MainActivity.datasource.getContact(phone_nr);
            MainActivity.datasource.close();
            getPattern = returncontact.getPattern();

        }catch(SQLiteException e){
            e.printStackTrace();
        }
        return returncontact;

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
                        MainActivity.datasource.open();
                        MainActivity.datasource.createContact(number, longToString(pattern));
                        MainActivity.datasource.close();
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

        Button contact = (Button)findViewById(R.id.contact_test);

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

        contact.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Contact contact = getPattern("+358504633346");
                Log.v("patternArray:", ""+contact);
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




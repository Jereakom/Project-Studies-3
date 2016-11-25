package group1.oamk.ringo;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import static group1.oamk.ringo.MainActivity.datasource;

public final class CallReceiver extends BroadcastReceiver {

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
                        datasource.open();
                        Contact contact = datasource.getContact(incomingNumber);
                        pattern = contact.getPattern();
                        datasource.close();
                    }

                    catch (Exception e)
                    {
                        e.getStackTrace();
                    }

                }

                //Set the phone to vibrate using that pattern, if there was a mapping
                if(pattern != null){
                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(pattern, -1);
                }
                System.out.println("incomingNumber : "+incomingNumber);
            }
        },PhoneStateListener.LISTEN_CALL_STATE);
    }
}

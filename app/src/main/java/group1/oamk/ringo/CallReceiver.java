package group1.oamk.ringo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);


              /*  long[] pattern = getPattern(incomingNumber);

                if(pattern != null){
                    Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(pattern, -1);
                }*/
                Log.v("incomingNumber : ",""+incomingNumber);
            }
        },PhoneStateListener.LISTEN_CALL_STATE);
    }
}


package group1.oamk.ringo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.provider.ContactsContract.Contacts;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class getRingtones extends AppCompatActivity {
    private static final int PICK_CONTACT = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_ringtones);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ListView lv = (ListView) findViewById(R.id.ringtone_list);
        setSupportActionBar(toolbar);

        MyCustomAdapter adapter = new MyCustomAdapter(getRingtones(), this);
              lv.setAdapter(adapter);

    }

    public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<String> list = new ArrayList<String>();
        private Context context;



        public MyCustomAdapter(ArrayList<String> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return 0;
            //just return 0 if your list items do not have an Id variable.
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.row_layout, null);
            }

            //Handle TextView and display string from your list
            TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
            listItemText.setText(list.get(position));

            //Handle buttons and add onClickListeners
            Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
            Button setBtn = (Button)view.findViewById(R.id.set_btn);

            deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //do something
                    list.remove(position); //or some other task
                    notifyDataSetChanged();
                }
            });
            setBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    ringtone_name = list.get(position);
                    Log.d("ringtone : ", ringtone_name);
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);
                    notifyDataSetChanged();
                }
            });

            return view;
        }
    }

    public ArrayList<String> getRingtones() {
        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();

        ArrayList<String> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX);
            String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);

            list.add(title);
        }

        return list;
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor phone = getContentResolver().query(contactData, null, null, null, null);
                    if (phone.moveToFirst()) {
                        String contactNumberName = phone.getString(phone.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String contactLookup = phone.getString(phone.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        // Todo something when contact number selected
                        saveRingtoneToContact(contactLookup, ringtone_name);
                        successMessage();
                    }
                }
                break;
        }
    }

    public void successMessage(){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Custom ringtone successfully applied to contact!");
        dlgAlert.setTitle("Success");
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss the dialog
                    }
                });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
public String ringtone_name;
    public void saveRingtoneToContact(String contactLookup, String ringtone_name){
        // The Uri used to look up a contact by phone number
        final Uri lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, contactLookup);
// The columns used for `Contacts.getLookupUri`
        final String[] projection = new String[] {
                Contacts._ID, Contacts.LOOKUP_KEY
        };
// Build your Cursor
        final Cursor data = getContentResolver().query(lookupUri, projection, null, null, null);
        data.moveToFirst();
        try {
            // Get the contact lookup Uri
            final long contactId = data.getLong(0);
            final String lookupKey = data.getString(1);
            final Uri contactUri = Contacts.getLookupUri(contactId, lookupKey);
            if (contactUri == null) {
                // Invalid arguments
                return;
            }

            // Get the path of ringtone you'd like to use
            final String storage = Environment.getExternalStorageDirectory().getPath();
            Log.d("path", storage + "/Ringtones/" + ringtone_name.toLowerCase() + ".ogg");
            final File file = new File("system/media/audio/ringtones", ringtone_name + ".ogg"); // change this later to match our ringtones, now works only if ringtone title=file name
            final String value = Uri.fromFile(file).toString();
            Log.d("file uri", value);

            // Apply the custom ringtone
            final ContentValues values = new ContentValues(1);
            values.put(Contacts.CUSTOM_RINGTONE, value);
            getContentResolver().update(contactUri, values, null, null);
        } finally {
            // Don't forget to close your Cursor
            data.close();
        }
    }

}


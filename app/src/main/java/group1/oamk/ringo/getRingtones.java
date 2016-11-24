package group1.oamk.ringo;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.provider.ContactsContract.Contacts;

import java.io.File;
import java.util.ArrayList;

public class getRingtones extends AppCompatActivity {
    private static final int PICK_CONTACT = 1000;
    private static final int REQUEST_CONTACTS = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    MediaPlayer mediaplayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_ringtones);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ListView lv = (ListView) findViewById(R.id.ringtone_list);
        setSupportActionBar(toolbar);

        MyCustomAdapter adapter = new MyCustomAdapter(getRingtones(), this);
              lv.setAdapter(adapter);
        verifyStoragePermissions(this);
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CONTACTS);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_CONTACTS
            );
        }
    }

    public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<String> list = new ArrayList<String>();
        private Context context;



        MyCustomAdapter(ArrayList<String> list, Context context) {
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
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.row_layout, null);
            }

            TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
            listItemText.setText(list.get(position));

            Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
            Button setBtn = (Button)view.findViewById(R.id.set_btn);

            deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String deletion = list.get(position);
                    File delFile = new File(Environment.getExternalStorageDirectory() + "/Music/Ringo/Ringtones/" + deletion + ".wav");
                    delFile.delete();
                    list.remove(position);
                    notifyDataSetChanged();
                }
            });
            setBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    ringtone_name = list.get(position);
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);
                    notifyDataSetChanged();
                }
            });
            listItemText.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String soundName = list.get(position);
                    File ringtone = new File(Environment.getExternalStorageDirectory() + "/Music/Ringo/Ringtones/" + soundName + ".wav");
                    managerOfSound(ringtone);
                }
            });

            return view;
        }
    }

    public ArrayList<String> getRingtones() {
        String rootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/Music/Ringo/Ringtones/";
        File root = new File(rootPath);
        if (!root.exists()) {
            root.mkdirs();
        }
        ArrayList<String> ringtoneList = new ArrayList<>();
        String parentDirectory = Environment.getExternalStorageDirectory() + "/Music/Ringo/Ringtones";
        File dirFileObj = new File(parentDirectory);
        File[] fileList = dirFileObj.listFiles();
        for (File inFile : fileList) {
                ringtoneList.add(inFile.getAbsolutePath().substring(inFile.getAbsolutePath().lastIndexOf("/")).replace(".wav", "").replace("/", ""));
        }

        return ringtoneList;
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
                        String contactLookup = phone.getString(phone.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        saveRingtoneToContact(contactLookup, ringtone_name);
                        successMessage();
                    }
                }
                break;
        }
    }

    protected void managerOfSound(File sound) {
        verifyStoragePermissions(this);
        if (mediaplayer != null) {
            mediaplayer.reset();
            mediaplayer.release();
        }
        Uri song = Uri.fromFile(sound);
        mediaplayer = MediaPlayer.create(this, song);
        mediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mp == mediaplayer) {
                    mediaplayer.start();
                }
            }
        });
    }

    public void successMessage(){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Custom ringtone successfully applied to contact!");
        dlgAlert.setTitle("Success");
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

public String ringtone_name;

    public void saveRingtoneToContact(String contactLookup, String ringtone_name){

        final Uri lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, contactLookup);
        final String[] projection = new String[] {
                Contacts._ID, Contacts.LOOKUP_KEY
        };
        final Cursor data = getContentResolver().query(lookupUri, projection, null, null, null);
        data.moveToFirst();
        try {
            final long contactId = data.getLong(0);
            final String lookupKey = data.getString(1);
            final Uri contactUri = Contacts.getLookupUri(contactId, lookupKey);
            if (contactUri == null) {
                return;
            }

            final File file = new File("storage/emulated/0/Music/Ringo/Ringtones", ringtone_name + ".wav");
            final String value = Uri.fromFile(file).toString();

            final ContentValues values = new ContentValues(1);
            values.put(Contacts.CUSTOM_RINGTONE, value);
            getContentResolver().update(contactUri, values, null, null);
        } finally {
            data.close();
        }
    }

}


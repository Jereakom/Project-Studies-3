package group1.oamk.ringo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import static group1.oamk.ringo.MainActivity.datasource;
import static group1.oamk.ringo.MainActivity.vib;

public class browsePatterns extends AppCompatActivity {

    private static final int PICK_CONTACT = 1;

    private long[] pattern = null;

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
                        datasource.createContact(contactName, number, VibrationActivity.longToString(pattern));
                        datasource.close();
                    }
                    catch (Exception e){
                        // IGNORANCE IS THE BEST POLICY
                    }

                }

        }
    }



    @Override
    protected void onResume() {
        String action = getIntent().getAction();
        // Prevent endless loop by adding a unique action, don't restart if action is present
        if(action == null || !action.equals("Already created")) {
            Log.v("Example", "Force restart");
            Intent intent = new Intent(this, browsePatterns.class);
            startActivity(intent);
            finish();
        }
        // Remove the unique action so the next time onResume is called it will restart
        else
            getIntent().setAction(null);

        super.onResume();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntent().setAction("Already created");
        setContentView(R.layout.activity_browse_patterns);;

        ListView lv = (ListView) findViewById(R.id.patterns_list);

        ContactsAdapter adapter = new ContactsAdapter(getPatterns(), this);
        lv.setAdapter(adapter);

    }

    public class ContactsAdapter extends BaseAdapter implements ListAdapter {
        private List<Contact> list;
        private Context context;



        public ContactsAdapter(List<Contact> list, Context context) {
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
                view = inflater.inflate(R.layout.row_layout_for_patterns, null);
            }

            //Handle TextView and display string from your list
            TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
            Contact contact = list.get(position);
            if (contact.getName() == null)
            {
                listItemText.setText(contact.getPhone_number());
            }
            else
            {
                listItemText.setText(contact.getName());
            }

            //Handle buttons and add onClickListeners


            Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
            Button setBtn = (Button)view.findViewById(R.id.assign_button);

            deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Contact deletion = list.get(position);
                    try
                    {
                        datasource.open();
                        datasource.deleteContact(deletion);
                        datasource.close();

                    }
                    catch (Exception e)
                    {
                        e.getStackTrace();
                    }
                    list.remove(position);
                    notifyDataSetChanged();

                }
            });



            setBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Contact pickPattern = list.get(position);
                    pattern = pickPattern.getPattern();
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);
                }
            });

            listItemText.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    long[] pattern = list.get(position).getPattern();
                    vib.vibrate(pattern, -1);

                }
            });

            return view;
        }
    }

    public List<Contact> getPatterns() {
        datasource.open();
        List<Contact> allPatterns = datasource.getAllContacts();
        datasource.close();
        return allPatterns;

    }



}
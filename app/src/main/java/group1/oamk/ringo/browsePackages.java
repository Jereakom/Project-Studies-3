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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class browsePackages extends AppCompatActivity {
    private static final int PICK_CONTACT = 1000;

    @Override
    protected void onResume() {
        String action = getIntent().getAction();
        // Prevent endless loop by adding a unique action, don't restart if action is present
        if(action == null || !action.equals("Already created")) {
            Log.v("Example", "Force restart");
            Intent intent = new Intent(this, browsePackages.class);
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
        setContentView(R.layout.activity_browse_packages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ListView lv = (ListView) findViewById(R.id.packages_list);

        MyPackagesAdapter adapter = new MyPackagesAdapter(getPackages(), this);
        lv.setAdapter(adapter);

    }

    public class MyPackagesAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<String> list = new ArrayList<String>();
        private Context context;



        public MyPackagesAdapter(ArrayList<String> list, Context context) {
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
                    String deletion = list.get(position);
                    File delFile = new File(Environment.getExternalStorageDirectory() + "/Music/Ringo/Soundpacks/" + deletion);
                    if (delFile.isDirectory())
                    {
                        String[] children = delFile.list();
                        for (int i = 0; i < children.length; i++)
                        {
                            new File(delFile, children[i]).delete();
                        }
                        delFile.delete();
                    }
                    list.remove(position);
                    notifyDataSetChanged();
                }
            });
            setBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    notifyDataSetChanged();
                }
            });

            return view;
        }
    }
    public void goToDownload(View view) {
        startActivity(new Intent(browsePackages.this, downloadPackages.class));
    }

    public ArrayList<String> getPackages() {
        String rootPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/Music/Ringo/Soundpacks/";
        File root = new File(rootPath);
        if (!root.exists()) {
            root.mkdirs();
        }
        ArrayList<String> packageList = new ArrayList<>();
        String parentDirectory = Environment.getExternalStorageDirectory() + "/Music/Ringo/Soundpacks";
        File dirFileObj = new File(parentDirectory);
        File[] fileList = dirFileObj.listFiles();
        for (File inFile : fileList) {
            packageList.add(inFile.getAbsolutePath().toString().substring(inFile.getAbsolutePath().toString().lastIndexOf("/")).replace("/", ""));
        }

        return packageList;
    }



}
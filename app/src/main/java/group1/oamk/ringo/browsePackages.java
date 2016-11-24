package group1.oamk.ringo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
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

import java.io.File;
import java.util.ArrayList;

public class browsePackages extends AppCompatActivity {
    private static final int REQUEST_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    MediaPlayer mediaplayer = null;

    @Override
    protected void onResume() {
        String action = getIntent().getAction();
        if(action == null || !action.equals("Already created")) {
            Log.v("Example", "Force restart");
            Intent intent = new Intent(this, browsePackages.class);
            startActivity(intent);
            finish();
        }
        else
            getIntent().setAction(null);

        super.onResume();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntent().setAction("Already created");
        setContentView(R.layout.activity_browse_packages);
        ListView lv = (ListView) findViewById(R.id.packages_list);

        MyPackagesAdapter adapter = new MyPackagesAdapter(getPackages(), this);
        lv.setAdapter(adapter);

    }

    public class MyPackagesAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<String> list = new ArrayList<>();
        private Context context;



        MyPackagesAdapter(ArrayList<String> list, Context context) {
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
                    Intent i = new Intent(getApplicationContext(), makeRingtone.class);
                    i.putExtra("key",list.get(position));
                    startActivity(i);
                }
            });
            listItemText.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String soundpackName = list.get(position);
                    File soundpack = new File(Environment.getExternalStorageDirectory() + "/Music/Ringo/Soundpacks/" + soundpackName);
                    if (soundpack.isDirectory())
                    {
                        String[] children = soundpack.list();
                        managerOfSound(soundpack, children);
                    }
                }
            });

            return view;
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_STORAGE
            );
        }
    }

    protected void managerOfSound(File soundpack, String[] sound) {
        verifyStoragePermissions(this);
        for (int i = 0; i < sound.length;i++) {
            Uri song = Uri.fromFile(new File(soundpack, sound[i]));
            if (mediaplayer != null) {
                mediaplayer.reset();
                mediaplayer.release();
            }
            mediaplayer = MediaPlayer.create(this, song);
            mediaplayer.start();
            int duration = mediaplayer.getDuration();
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
            packageList.add(inFile.getAbsolutePath().substring(inFile.getAbsolutePath().lastIndexOf("/")).replace("/", ""));
        }

        return packageList;
    }



}

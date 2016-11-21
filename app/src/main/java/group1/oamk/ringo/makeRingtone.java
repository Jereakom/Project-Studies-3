package group1.oamk.ringo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.media.MediaPlayer;
import android.widget.Button;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static java.security.AccessController.getContext;


public class makeRingtone extends AppCompatActivity {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    MediaPlayer mediaplayer = null;
    String[] sounds = new String[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_ringtone);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button button = (Button) findViewById(R.id.button);
        final Button button2 = (Button) findViewById(R.id.button2);
        final Button button3 = (Button) findViewById(R.id.button3);
        final Button button4 = (Button) findViewById(R.id.button4);
        final Button button5 = (Button) findViewById(R.id.button5);
        final Button button6 = (Button) findViewById(R.id.button6);
        final Button button7 = (Button) findViewById(R.id.button7);
        final Button button8 = (Button) findViewById(R.id.button8);
        final Button play = (Button) findViewById(R.id.play);
        final Button clear = (Button) findViewById(R.id.clear);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSoundToQuery("button1");
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSoundToQuery("button2");
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSoundToQuery("button3");
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSoundToQuery("button4");
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSoundToQuery("button5");
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSoundToQuery("button6");
            }
        });
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSoundToQuery("button7");
            }
        });
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSoundToQuery("button8");
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String song = Environment.getExternalStorageDirectory() + "/Music/ME.wav";
                managerOfSound(song);
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sounds = new String[0];
            }
        });
    }

    protected void addSoundToQuery(String button) {
        String sound;
        if (button == "button1") {
            sound = Environment.getExternalStorageDirectory() + "/Music/piano/1.wav";
            sounds = push(sounds, sound);
            managerOfSound(sound);
        }
        else if (button == "button2") {
            sound = Environment.getExternalStorageDirectory() + "/Music/piano/2.wav";
            sounds = push(sounds, sound);
            managerOfSound(sound);
        }
        else if (button == "button3") {
            sound = Environment.getExternalStorageDirectory() + "/Music/piano/3.wav";
            sounds = push(sounds, sound);
            managerOfSound(sound);
        }
        else if (button == "button4") {
            sound = Environment.getExternalStorageDirectory() + "/Music/piano/4.wav";
            sounds = push(sounds, sound);
            managerOfSound(sound);
        }
        else if (button == "button5") {
            sound = Environment.getExternalStorageDirectory() + "/Music/piano/5.wav";
            sounds = push(sounds, sound);
            managerOfSound(sound);
        }
        else if (button == "button6") {
            sound = Environment.getExternalStorageDirectory() + "/Music/piano/6.wav";
            sounds = push(sounds, sound);
            managerOfSound(sound);
        }
        else if (button == "button7") {
            sound = Environment.getExternalStorageDirectory() + "/Music/piano/7.wav";
            sounds = push(sounds, sound);
            managerOfSound(sound);
        }
        else if (button == "button8") {
            sound = Environment.getExternalStorageDirectory() + "/Music/piano/8.wav";
            sounds = push(sounds, sound);
            managerOfSound(sound);
        }
    }

    private static String[] push(String[] array, String push) {
        String[] longer = new String[array.length + 1];
        for (int i = 0; i < array.length; i++)
            longer[i] = array[i];
        longer[array.length] = push;
        return longer;
    }

    protected void managerOfSound(String sound) {
        verifyStoragePermissions(this);

        AudioCombine();

        if (mediaplayer != null) {
            mediaplayer.reset();
            mediaplayer.release();
        }
        Uri song = Uri.fromFile(new File(sound));
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

    public void AudioCombine() {
        try {
            String[] args = sounds;
            DataOutputStream amplifyOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(Environment.getExternalStorageDirectory() + "/Music/ME.wav")));
            DataInputStream[] mergeFilesStream = new DataInputStream[args.length];
            long[] sizes = new long[args.length];
            for (int i = 0; i < args.length; i++) {
                File file = new File(args[i]);
                sizes[i] = (file.length() - 44) / 2;
            }
            for (int i = 0; i < args.length; i++) {
                mergeFilesStream[i] = new DataInputStream(new BufferedInputStream(new FileInputStream(args[i])));

                if (i == args.length - 1) {
                    mergeFilesStream[i].skip(24);
                    byte[] sampleRt = new byte[4];
                    mergeFilesStream[i].read(sampleRt);
                    ByteBuffer bbInt = ByteBuffer.wrap(sampleRt).order(ByteOrder.LITTLE_ENDIAN);
                    final int RECORDER_SAMPLERATE = bbInt.getInt();
                    mergeFilesStream[i].skip(16);
                } else {
                    mergeFilesStream[i].skip(44);
                }

            }

            for (int b = 0; b < args.length; b++) {
                for (int i = 0; i < (int) sizes[b]; i++) {
                    byte[] dataBytes = new byte[2];
                    try {
                        dataBytes[0] = mergeFilesStream[b].readByte();
                        dataBytes[1] = mergeFilesStream[b].readByte();
                    } catch (EOFException e) {
                        amplifyOutputStream.close();
                    }
                    short dataInShort = ByteBuffer.wrap(dataBytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
                    float dataInFloat = (float) dataInShort / 37268.0f;


                    short outputSample = (short) (dataInFloat * 37268.0f);
                    byte[] dataFin = new byte[2];
                    dataFin[0] = (byte) (outputSample & 0xff);
                    dataFin[1] = (byte) ((outputSample >> 8) & 0xff);
                    amplifyOutputStream.write(dataFin, 0, 2);

                }
            }
            amplifyOutputStream.close();
            for (int i = 0; i < args.length; i++) {
                mergeFilesStream[i].close();
            }

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        long size = 0;
        try {
            FileInputStream fileSize = new FileInputStream(Environment.getExternalStorageDirectory() + "/Music/ME.wav");
            size = fileSize.getChannel().size();
            fileSize.close();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        final int RECORDER_BPP = 16;
        final int RECORDER_SAMPLERATE = 44100;

        long datasize = size + 36;
        long byteRate = (RECORDER_BPP * RECORDER_SAMPLERATE) / 8;
        long longSampleRate = RECORDER_SAMPLERATE;
        byte[] header = new byte[44];


        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (datasize & 0xff);
        header[5] = (byte) ((datasize >> 8) & 0xff);
        header[6] = (byte) ((datasize >> 16) & 0xff);
        header[7] = (byte) ((datasize >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // format = 1
        header[21] = 0;
        header[22] = (byte) 1;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) ((RECORDER_BPP) / 8);  // block align
        header[33] = 0;
        header[34] = RECORDER_BPP;  // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (size & 0xff);
        header[41] = (byte) ((size >> 8) & 0xff);
        header[42] = (byte) ((size >> 16) & 0xff);
        header[43] = (byte) ((size >> 24) & 0xff);
        // out.write(header, 0, 44);

        try {
            RandomAccessFile rFile = new RandomAccessFile(Environment.getExternalStorageDirectory() + "/Music/ME.wav", "rw");
            rFile.seek(0);
            rFile.write(header);
            rFile.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
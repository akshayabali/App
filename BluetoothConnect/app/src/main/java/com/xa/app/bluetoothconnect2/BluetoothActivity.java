package com.xa.app.bluetoothconnect2;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class BluetoothActivity extends Activity implements OnSeekBarChangeListener  {
    EditText et1;
    EditText et2;
    EditText et3;
    SeekBar seekBar1;
    SeekBar seekBar2;
    SeekBar seekBar3;
    private static final String TAG = "BluetoothActivity";

    // The thread that does all the work
    BluetoothThread btt;

    // Handler for writing messages to the Bluetooth connection
    Handler writeHandler;

    /**
     * Launch the Bluetooth thread.
     */
    public void connectButtonPressed(View v) {
        Log.v(TAG, "Connect button pressed.");

        // Only one thread at a time
        if (btt != null) {
            Log.w(TAG, "Already connected!");
            return;
        }

        // Initialize the Bluetooth thread, passing in a MAC address
        // and a Handler that will receive incoming messages
        String address = "00:21:13:01:EB:45";
        btt = new BluetoothThread(address, new Handler() {

            @Override
            public void handleMessage(Message message) {

                String s = (String) message.obj;

                // Do something with the message
                switch (s) {
                    case "CONNECTED": {
                        TextView tv = findViewById(R.id.statusText);
                        tv.setText(R.string.Connected);
                        Button b = findViewById(R.id.writeButton);
                        b.setEnabled(true);
                        break;
                    }
                    case "DISCONNECTED": {
                        TextView tv = findViewById(R.id.statusText);
                        Button b = findViewById(R.id.writeButton);
                        b.setEnabled(false);
                        tv.setText(R.string.Disconnected);
                        break;
                    }
                    case "CONNECTION FAILED": {
                        TextView tv = findViewById(R.id.statusText);
                        tv.setText(R.string.ConnectionF);
                        btt = null;
                        break;
                    }
                    default: {
                        TextView tv = findViewById(R.id.readField);
                        tv.setText(s);
                        break;
                    }
                }
            }
        });

        // Get the handler that is used to send messages
        writeHandler = btt.getWriteHandler();

        // Run the thread
        btt.start();

        TextView tv = findViewById(R.id.statusText);
        tv.setText(R.string.Connecting);
    }

    /**
     * Kill the Bluetooth thread.
     */
    public void disconnectButtonPressed(View v) {
        Log.v(TAG, "Disconnect button pressed.");

        if(btt != null) {
            btt.interrupt();
            btt = null;
        }
    }

    /**
     * Send a message using the Bluetooth thread's write handler.
     */
    public void writeButtonPressed(View v) {
        Log.v(TAG, "Write button pressed.");
        if(et1.getText().toString().length()==0)
            et1.setText("0");
        if(et2.getText().toString().length()==0)
            et2.setText("0");
        if(et3.getText().toString().length()==0)
            et3.setText("0");
        String data = et2.getText().toString()+"\n"+et1.getText().toString()+"\n"+et3.getText().toString()+"\n\n\n";
        Message msg = Message.obtain();
        msg.obj = data;
        try {
            writeHandler.sendMessage(msg);
        }
        catch (NullPointerException e)
        {
            Log.v(TAG,"Null Pointer Exception");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        et1= findViewById(R.id.editText1);
        et2= findViewById(R.id.editText2);
        et3= findViewById(R.id.editText3);
        seekBar1=findViewById(R.id.seekBar1);
        seekBar2=findViewById(R.id.seekBar2);
        seekBar3=findViewById(R.id.seekBar3);
        seekBar1.setOnSeekBarChangeListener(this);
        seekBar2.setOnSeekBarChangeListener(this);
        seekBar3.setOnSeekBarChangeListener(this);
        /*sb1.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser)
            {
                //---change the font size of the EditText---

                et1.setText(String.valueOf(progress));
                if(et1.getText().toString().length()==0)
                    et1.setText("0");
                if(et2.getText().toString().length()==0)
                    et2.setText("0");
                if(et3.getText().toString().length()==0)
                    et3.setText("0");
                String data = et2.getText().toString()+"\n"+et1.getText().toString()+"\n"+et3.getText().toString()+"\n";
                Message msg = Message.obtain();
                msg.obj = data;
                try {
                    writeHandler.sendMessage(msg);
                }
                catch (NullPointerException e)
                {
                    Log.v(TAG,"Null Pointer Exception");
                }
            }
        });
        final float[] mProgressText1 = new float[1];
        final int[] mProgressValue1 = new int[1];

        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                try {
                    if (s != null) {
                        mProgressText1[0] = Float.parseFloat(s.toString());
                        mProgressValue1[0] = (int) mProgressText1[0];
                        sb1.setProgress(mProgressValue1[0]);
                    }
                } catch (Exception exception) {
                    sb1.setProgress(0);
                    exception.printStackTrace();
                }
            }


            public void afterTextChanged(Editable s) {

                }

        });
        */
        Button b = findViewById(R.id.writeButton);
        b.setEnabled(false);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        Log.v(TAG," "+seekBar);
        int value=(int)progress;
        switch (seekBar.getId()) {
            case R.id.seekBar1:
                Log.v(TAG,"Progress = "+progress);
                if(progress>0&&progress<255)
                    et1.setText(progress);
                break;
            case R.id.seekBar2:
                if(progress>0&&progress<255)
                    et2.setText(progress);
                break;
            case R.id.seekBar3:
                if(progress>0&&progress<255)
                    et3.setText(progress);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    /**
     * Kill the thread when we leave the activity.
     */
    protected void onPause() {
        super.onPause();

        if(btt != null) {
            btt.interrupt();
            btt = null;
        }
    }



}

package com.aaronps.bluetoothtankdemo;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aaronps.bluetoothtank.BluetoothTank;

public class MainActivity extends AppCompatActivity implements BluetoothTank.Listener, View.OnTouchListener {

    static final int MSG_CONNECT_UPDATE = 0;

    static final int STATE_DISCONNECTED = 0;
    static final int STATE_CONNECTED = 1;

    static final int SPEED_1 = 1;
    static final int SPEED_2 = 2;
    static final int SPEED_3 = 3;

    static final int MOVESTATE_STOP  = 0;
//    static final int MOVESTATE_UP    = 1;
//    static final int MOVESTATE_DOWN  = 2;
//    static final int MOVESTATE_LEFT  = 3;
//    static final int MOVESTATE_RIGHT = 4;

    BluetoothTank mBTTank;
    int mConnectState = STATE_DISCONNECTED;
    int mSelectedSpeed = SPEED_1;
    int mMoveState = MOVESTATE_STOP;

    Handler mHandler;
    TextView mConnectStatusTextView;
    Button mSpeedButton1;
    Button mSpeedButton2;
    Button mSpeedButton3;
    Button mMoveButtonStop;
    Button mMoveButtonUp;
    Button mMoveButtonDown;
    Button mMoveButtonLeft;
    Button mMoveButtonRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mConnectStatusTextView = (TextView) findViewById(R.id.textview_connect_status);
        mSpeedButton1 = (Button)findViewById(R.id.button_speed_1);
        mSpeedButton2 = (Button)findViewById(R.id.button_speed_2);
        mSpeedButton3 = (Button)findViewById(R.id.button_speed_3);
        mMoveButtonStop  = (Button)findViewById(R.id.button_move_stop);
        mMoveButtonUp    = (Button)findViewById(R.id.button_move_up);
        mMoveButtonDown  = (Button)findViewById(R.id.button_move_down);
        mMoveButtonLeft  = (Button)findViewById(R.id.button_move_left);
        mMoveButtonRight = (Button)findViewById(R.id.button_move_right);

        mBTTank = new BluetoothTank("HC-06", this);

        // because the BluetoothTank.Listener may call from another thread
        // and we want to modify some ui elements, we use a handler and send
        // messages to it.
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case MSG_CONNECT_UPDATE:
                        switch (msg.arg1)
                        {
                            case STATE_CONNECTED:
                                mConnectStatusTextView.setText("Connected!!!");
                                break;

                            case STATE_DISCONNECTED:
                                mConnectStatusTextView.setText("Disconnected. Connecting...");
                                break;
                        }
                        break;

                    default:
                        super.handleMessage(msg);
                }

            }
        };

        mSpeedButton1.setOnTouchListener(this);
        mSpeedButton2.setOnTouchListener(this);
        mSpeedButton3.setOnTouchListener(this);
        mMoveButtonStop.setOnTouchListener(this);
        mMoveButtonUp.setOnTouchListener(this);
        mMoveButtonDown.setOnTouchListener(this);
        mMoveButtonLeft.setOnTouchListener(this);
        mMoveButtonRight.setOnTouchListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mBTTank.start();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        try
        {
            mBTTank.stop();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    void setSelectedSpeed(int speed)
    {
        if ( mSelectedSpeed != speed )
        {
            mSelectedSpeed = speed;
            if ( mMoveState != MOVESTATE_STOP )
            {
                mBTTank.commandSetSpeedNow(speed);
            }
            else
            {
                mBTTank.commandSetSpeed(speed);
            }
        }
    }

    @Override
    public void onBluetoothTankConnected(BluetoothTank tank)
    {
        mHandler.obtainMessage(MSG_CONNECT_UPDATE, STATE_CONNECTED, 0).sendToTarget();
    }

    @Override
    public void onBluetoothTankDisconnected(BluetoothTank tank)
    {
        mHandler.obtainMessage(MSG_CONNECT_UPDATE, STATE_DISCONNECTED, 0).sendToTarget();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        final int am = event.getActionMasked();
        final boolean isPressed = (am == MotionEvent.ACTION_DOWN) || (am == MotionEvent.ACTION_POINTER_DOWN);
        switch ( v.getId() )
        {
            case R.id.button_speed_1: if ( isPressed ) setSelectedSpeed(SPEED_1); break;
            case R.id.button_speed_2: if ( isPressed ) setSelectedSpeed(SPEED_2); break;
            case R.id.button_speed_3: if ( isPressed ) setSelectedSpeed(SPEED_3); break;
            case R.id.button_move_stop: if ( isPressed ) mBTTank.commandStop(); break;
            case R.id.button_move_up:   if ( isPressed ) mBTTank.commandUp(); break;
            case R.id.button_move_down: if ( isPressed ) mBTTank.commandDown(); break;
            case R.id.button_move_left: if ( isPressed ) mBTTank.commandLeft(); break;
            case R.id.button_move_right:if ( isPressed ) mBTTank.commandRight(); break;
        }
        return false;
    }

}

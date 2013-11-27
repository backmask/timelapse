package com.backmask.timelapse;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.backmask.timelapse.arduino.ArduinoCommander;
import com.backmask.timelapse.arduino.ArduinoCommanderListener;
import com.backmask.timelapse.view.ServoControlView;

public class DirectControlFragment extends Fragment implements ArduinoCommanderListener {

    private ArduinoCommander m_commander;

    private boolean m_isConnected;
    private boolean m_isConnecting;

    private ServoControlView m_leftWheel;
    private ServoControlView m_rightWheel;
    private ServoControlView m_armRotation;
    private ServoControlView m_armHeight;
    private MenuItem m_connectToggle;

    public DirectControlFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_commander = new ArduinoCommander(getActivity(), this);
        m_isConnected = false;
        m_isConnecting = false;
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_direct_control, container, false);

        m_leftWheel = (ServoControlView) rootView.findViewById(R.id.left_wheel);
        m_rightWheel = (ServoControlView) rootView.findViewById(R.id.right_wheel);
        m_armRotation = (ServoControlView) rootView.findViewById(R.id.arm_rotation);
        m_armHeight = (ServoControlView) rootView.findViewById(R.id.arm_height);

        m_leftWheel.setListener(new ServoControlView.ServoControlListener() {
            @Override
            public void onValueChanged(ServoControlView triggeredBy, int newValue, int oldValue) {
                m_commander.setServoRotation(ArduinoCommander.SERVO_LEFT_WHEEL, newValue);
            }
        });

        m_rightWheel.setListener(new ServoControlView.ServoControlListener() {
            @Override
            public void onValueChanged(ServoControlView triggeredBy, int newValue, int oldValue) {
                m_commander.setServoRotation(ArduinoCommander.SERVO_RIGHT_WHEEL, newValue);
            }
        });

        m_armRotation.setListener(new ServoControlView.ServoControlListener() {
            @Override
            public void onValueChanged(ServoControlView triggeredBy, int newValue, int oldValue) {
                m_commander.setServoRotation(ArduinoCommander.SERVO_ARM_ROTATION, newValue);
            }
        });

        m_armHeight.setListener(new ServoControlView.ServoControlListener() {
            @Override
            public void onValueChanged(ServoControlView triggeredBy, int newValue, int oldValue) {
                m_commander.setServoRotation(ArduinoCommander.SERVO_ARM_HEIGHT, newValue);
            }
        });

        updateConnectivityDisplay();

        return rootView;
    }

    @Override
    public void onConnectivityUpdate(final boolean connected) {
        m_isConnecting = false;
        m_isConnected = connected;
        final Activity activity = getActivity();
        if (activity == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, connected ? "Connected" : "Disconnected", 5).show();
                updateConnectivityDisplay();
            }
        });
    }

    @Override
    public void onException(final Exception e) {
        final String exception = e.toString();
        final Activity activity = getActivity();
        if (activity == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, exception, 5).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.direct_control, menu);
        m_connectToggle = menu.findItem(R.id.connect_toggle);
        updateConnectivityDisplay();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connect_toggle:
                if (!m_isConnected && !m_isConnecting) {
                    m_isConnecting = true;
                    m_commander.connect();
                    updateConnectivityDisplay();
                    Toast.makeText(getActivity(), "Connecting", 5).show();
                } else if (m_isConnected) {
                    m_commander.disconnect();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateConnectivityDisplay() {
        if (m_connectToggle == null) return;
        if (m_isConnecting) {
            m_connectToggle.setTitle(R.string.connecting);
            m_connectToggle.setEnabled(false);
        } else if (m_isConnected) {
            m_connectToggle.setTitle(R.string.disconnect);
            m_connectToggle.setEnabled(true);
        } else {
            m_connectToggle.setTitle(R.string.connect);
            m_connectToggle.setEnabled(true);
        }
    }
}

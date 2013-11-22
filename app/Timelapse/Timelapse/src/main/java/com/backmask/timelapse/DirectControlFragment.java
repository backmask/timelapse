package com.backmask.timelapse;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.backmask.timelapse.arduino.ArduinoCommander;
import com.backmask.timelapse.arduino.ArduinoCommanderListener;
import com.backmask.timelapse.view.ServoControlView;

public class DirectControlFragment extends Fragment implements ArduinoCommanderListener {

    private ArduinoCommander m_commander;

    private ServoControlView m_leftWheel;
    private ServoControlView m_rightWheel;
    private ServoControlView m_armRotation;

    public DirectControlFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_direct_control, container, false);
        m_commander = new ArduinoCommander(getActivity(), this);
        Toast.makeText(getActivity(), "Connecting", 10).show();
        m_commander.connect();

        m_leftWheel = (ServoControlView) rootView.findViewById(R.id.left_wheel);
        m_rightWheel = (ServoControlView) rootView.findViewById(R.id.right_wheel);
        m_armRotation = (ServoControlView) rootView.findViewById(R.id.arm_rotation);

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

        return rootView;
    }

    @Override
    public void onConnectivityUpdate(final boolean connected) {
        final Activity activity = getActivity();
        if (activity == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, connected ? "Connected" : "Disconnected", 10).show();
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
                Toast.makeText(activity, exception, 10).show();
            }
        });
    }
}

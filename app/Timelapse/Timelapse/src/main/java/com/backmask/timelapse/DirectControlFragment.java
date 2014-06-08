package com.backmask.timelapse;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.backmask.timelapse.arduino.ArduinoCommander;
import com.backmask.timelapse.arduino.ArduinoCommanderListener;
import com.backmask.timelapse.view.ServoControlView;

import java.util.HashMap;
import java.util.Map;

public class DirectControlFragment extends Fragment implements ArduinoCommanderListener {

    private ArduinoCommander m_commander;

    private boolean m_isConnected;
    private boolean m_isConnecting;

    private Map<Integer, ServoControlView> m_servoViews;
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
        setRetainInstance(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_direct_control, container, false);
        m_servoViews = new HashMap<Integer, ServoControlView>();
        m_servoViews.put(R.id.left_wheel, (ServoControlView) rootView.findViewById(R.id.left_wheel));
        m_servoViews.put(R.id.right_wheel, (ServoControlView) rootView.findViewById(R.id.right_wheel));
        m_servoViews.put(R.id.arm_rotation, (ServoControlView) rootView.findViewById(R.id.arm_rotation));
        m_servoViews.put(R.id.arm_height, (ServoControlView) rootView.findViewById(R.id.arm_height));
        m_servoViews.put(R.id.device_tilt, (ServoControlView) rootView.findViewById(R.id.device_tilt));
        Button centerArm = (Button) rootView.findViewById(R.id.center_arm);
        Button shutdownArm = (Button) rootView.findViewById(R.id.shutdown_arm);
        Button tareArm = (Button) rootView.findViewById(R.id.tare_arm);

        ServoControlView.ServoControlListener servoListener = new ServoControlView.ServoControlListener() {
            @Override
            public void onValueChanged(ServoControlView triggeredBy, int newValue, int oldValue) {
                m_commander.setServoRotation(triggeredBy.getPin(), newValue);
            }
        };

        for (ServoControlView view : m_servoViews.values()) {
            view.setListener(servoListener);
        }

        centerArm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ServoControlView view : m_servoViews.values()) {
                    view.setNeutralValue();
                }
            }
        });

        shutdownArm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ServoControlView view : m_servoViews.values()) {
                    m_commander.shutdownServo(view.getPin());
                }
            }
        });

        tareArm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ServoControlView view : m_servoViews.values()) {
                    view.setCurrentAsNeutralValue(getActivity());
                }
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

package com.backmask.timelapse;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.backmask.timelapse.infuse.InfuseSocket;
import com.backmask.timelapse.infuse.InfuseSocketListener;
import com.backmask.timelapse.infuse.sensor.CameraSensor;
import com.backmask.timelapse.infuse.sensor.ISensor;

import java.util.ArrayList;
import java.util.List;

public class InfuseFragment extends Fragment implements InfuseSocketListener {
    private InfuseSocket m_socket;
    private InfuseSocketListener m_socketListener;

    private Button m_connectToggle;
    private List<SensorDefinition> m_sensors;

    private boolean m_isConnected;
    private boolean m_isConnecting;

    private class SensorDefinition {
        public ISensor sensor;
        public ToggleButton button;

        public SensorDefinition(ISensor sensor, ToggleButton button) {
            this.sensor = sensor;
            this.button = button;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
        m_isConnecting = false;
        m_isConnected = false;
        m_socketListener = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_infuse, container, false);

        final Spinner hostSelector = (Spinner) rootView.findViewById(R.id.host);
        final String[] hosts = new String[] { "192.168.0.45:2945", "192.168.0.46:2945" };
        hostSelector.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, hosts));

        m_connectToggle = (Button) rootView.findViewById(R.id.connect);
        m_connectToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!m_isConnected) {
                    m_isConnecting = true;
                    updateConnectivityDisplay();

                    m_socket = new InfuseSocket(getActivity(), "192.168.0.45", 2946, m_socketListener);
                    m_socket.connect();

                    Toast.makeText(getActivity(), "Connecting", Toast.LENGTH_SHORT).show();
                } else if (m_socket != null) {
                    m_socket.disconnect();
                }
            }
        });

        m_sensors = new ArrayList<SensorDefinition>();
        m_sensors.add(new SensorDefinition(new CameraSensor((LinearLayout) rootView.findViewById(R.id.root_layout), getActivity()), (ToggleButton) rootView.findViewById(R.id.toggleFrontCamera)));

        for (SensorDefinition sensor : m_sensors) {
            sensor.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateSensors();
                }
            });
        }

        return rootView;
    }

    private void updateSensors() {
        for (SensorDefinition sensor : m_sensors) {
            if (m_isConnected && sensor.button.isChecked())
                sensor.sensor.attach(m_socket);
            else
                sensor.sensor.detach();
        }
    }

    private void updateConnectivityDisplay() {
        if (m_connectToggle == null) return;
        if (m_isConnecting) {
            m_connectToggle.setText(R.string.connecting);
            m_connectToggle.setEnabled(false);
        } else if (m_isConnected) {
            m_connectToggle.setText(R.string.disconnect);
            m_connectToggle.setEnabled(true);
        } else {
            m_connectToggle.setText(R.string.connect);
            m_connectToggle.setEnabled(true);
        }
    }

    @Override
    public void onConnectivityUpdate(boolean connected) {
        m_isConnected = connected;
        m_isConnecting = false;

        final Activity activity = getActivity();
        if (activity == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateConnectivityDisplay();
                updateSensors();
            }
        });
    }

    @Override
    public void onException(Exception e) {
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
}

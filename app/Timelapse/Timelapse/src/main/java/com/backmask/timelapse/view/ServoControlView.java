package com.backmask.timelapse.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.backmask.timelapse.R;

public class ServoControlView extends TableRow {

    private int m_value;
    private EditText m_valueInput;
    private SeekBar m_valueBar;
    private ServoControlListener m_listener;

    public interface ServoControlListener {
        public void onValueChanged(ServoControlView triggeredBy, int newValue, int oldValue);
    }

    public ServoControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ServoControl,
                0, 0);

        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_servo_control, this, true);
        ((TextView) findViewById(R.id.servo_label)).setText(a.getText(R.styleable.ServoControl_label));

        m_listener = null;
        m_value = -1;
        m_valueInput = (EditText) findViewById(R.id.servo_value);
        m_valueBar = (SeekBar) findViewById(R.id.servo_seekbar);

        m_valueInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        try {
                            setValue(Integer.parseInt(v.getText().toString(), 10));
                        } catch (NumberFormatException e) {
                            setValue(0);
                        }
                        v.clearFocus();
                }
                return false;
            }
        });

        m_valueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        setValue(90);
    }

    public void setValue(int value) {
        if (m_value == value) return;
        if (m_listener != null) {
            m_listener.onValueChanged(this, value, m_value);
        }
        m_value = value;
        m_valueInput.setText(Integer.toString(value));
        m_valueBar.setProgress(value);
    }

    public void setListener(ServoControlListener listener) {
        m_listener = listener;
    }

    public int getValue() {
        return m_value;
    }
}

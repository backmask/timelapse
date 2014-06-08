package com.backmask.timelapse.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
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
    private int m_neutralValue;
    private int m_pin;
    private EditText m_valueInput;
    private SeekBar m_valueBar;
    private ServoControlListener m_listener;
    private String m_label;

    public interface ServoControlListener {
        public void onValueChanged(ServoControlView triggeredBy, int newValue, int oldValue);
    }

    public ServoControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ServoControl,
                0, 0);

        m_label = a.getText(R.styleable.ServoControl_label).toString();

        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_servo_control, this, true);
        ((TextView) findViewWithTag("servo_label")).setText(m_label);

        m_listener = null;
        m_neutralValue = isInEditMode() ? 90 : context.getSharedPreferences("servo_preferences", 0).getInt("neutral_value." + m_label, 90);
        m_value = -1;
        m_pin = a.getInteger(R.styleable.ServoControl_pin, -1);
        m_valueInput = (EditText) findViewWithTag("servo_value");
        m_valueBar = (SeekBar) findViewWithTag("servo_seekbar");

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

        setNeutralValue();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            setValue(bundle.getInt("value"));
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("value", m_value);
        return bundle;
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

    public void setNeutralValue() {
        setValue(m_neutralValue);
    }

    public void setCurrentAsNeutralValue(Context ctx) {
        m_neutralValue = m_value;
        ctx.getSharedPreferences("servo_preferences", 0).edit().putInt("neutral_value." + m_label, m_neutralValue).commit();
    }

    public void setListener(ServoControlListener listener) {
        m_listener = listener;
    }
    public int getValue() {
        return m_value;
    }
    public int getPin() { return m_pin; }
}

package com.backmask.timelapse.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TableRow;
import android.widget.TextView;

import com.backmask.timelapse.R;

public class ServoControlView extends TableRow {

    private String m_label;

    public ServoControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ServoControl,
                0, 0);

        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_servo_control, this, true);
        ((TextView) findViewById(R.id.servo_label)).setText(a.getText(R.styleable.ServoControl_label));
    }
}

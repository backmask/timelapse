package com.backmask.timelapse.nav;

import android.app.Fragment;
import android.content.res.Resources;
import android.util.Log;

import com.backmask.timelapse.DirectControlFragment;
import com.backmask.timelapse.InfuseFragment;
import com.backmask.timelapse.R;

public class NavConfig {
    public static class NavElement {
        public String label;
        public Class<? extends Fragment> type;
    }

    public NavElement[] elements;

    public NavConfig(final Resources ctx) {
        elements = new NavElement[]{
                new NavElement() {{
                    label = ctx.getString(R.string.label_direct_control);
                    type = DirectControlFragment.class;
                }},
                new NavElement() {{
                    label = ctx.getString(R.string.label_infuse);
                    type = InfuseFragment.class;
                }}
        };
    }

    public NavElement getAt(int idx) {
        if (idx < 0 || idx >= elements.length) {
            return null;
        }
        return elements[idx];
    }

    public Fragment getFragment(NavElement elt) {
        try {
            return elt.type.newInstance();
        } catch (Exception e) {
            Log.e("Timelapse", "NavConfig", e);
            return null;
        }
    }
}

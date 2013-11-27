package com.backmask.timelapse;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;

import com.backmask.timelapse.nav.NavConfig;

;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavConfig m_navConfig;
    private NavigationDrawerFragment m_navigationDrawerFragment;
    private CharSequence m_title;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_navConfig = new NavConfig(getResources());

        setContentView(R.layout.activity_main);

        m_navigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        m_title = getTitle();

        // Set up the drawer.
        m_navigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int idx) {
        NavConfig.NavElement elt = m_navConfig.getAt(idx);
        FragmentManager fragmentManager = getFragmentManager();

        if (elt != null && fragmentManager.findFragmentByTag(elt.label) == null) {
            m_title = elt.label;
            Fragment contentFragment = m_navConfig.getFragment(elt);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, contentFragment, elt.label)
                    .commit();
        }
    }

    public void onSectionAttached(int idx) {
        NavConfig.NavElement elt = m_navConfig.getAt(idx);
        if (elt != null) {
            m_title = elt.label;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(m_title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!m_navigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.global, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }
}

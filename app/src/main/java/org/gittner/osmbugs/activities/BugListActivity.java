package org.gittner.osmbugs.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.fragments.BugPlatformListFragment;
import org.gittner.osmbugs.statics.BugDatabase;
import org.gittner.osmbugs.statics.Globals;
import org.gittner.osmbugs.statics.Settings;

import java.util.ArrayList;

public class BugListActivity
        extends ActionBarActivity
        implements ActionBar.TabListener,
        BugPlatformListFragment.OnFragmentInteractionListener
{
    public static final int RESULT_BUG_MINI_MAP_CLICKED = 1;
    public static final String RESULT_EXTRA_BUG = "RESULT_EXTRA_BUG";
    private static final int REQUEST_CODE_BUG_EDITOR_ACTIVITY = 1;
    private ViewPager mPager = null;


    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_list);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        PlatformPagerAdapter pagerAdapter = new PlatformPagerAdapter(getFragmentManager());
        if (Settings.Keepright.isEnabled())
        {
            pagerAdapter.add(Globals.KEEPRIGHT);
        }
        if (Settings.Osmose.isEnabled())
        {
            pagerAdapter.add(Globals.OSMOSE);
        }
        if (Settings.Mapdust.isEnabled())
        {
            pagerAdapter.add(Globals.MAPDUST);
        }
        if (Settings.OsmNotes.isEnabled())
        {
            pagerAdapter.add(Globals.OSM_NOTES);
        }
        pagerAdapter.notifyDataSetChanged();
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(pagerAdapter);
        mPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener()
                {
                    @Override
                    public void onPageSelected(final int position)
                    {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });
        for (int i = 0;
             i < mPager.getAdapter().getCount();
             i++)
        {
            actionBar.addTab(
                    actionBar.newTab().setText(mPager.getAdapter().getPageTitle(i)).setTabListener(this));
        }
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (requestCode == REQUEST_CODE_BUG_EDITOR_ACTIVITY)
        {
            switch (resultCode)
            {
                case BugEditActivity.RESULT_SAVED_KEEPRIGHT:
                    BugDatabase.getInstance().reload(Globals.KEEPRIGHT);
                    break;
                case BugEditActivity.RESULT_SAVED_OSMOSE:
                    BugDatabase.getInstance().reload(Globals.OSMOSE);
                    break;
                case BugEditActivity.RESULT_SAVED_MAPDUST:
                    BugDatabase.getInstance().reload(Globals.MAPDUST);
                    break;
                case BugEditActivity.RESULT_SAVED_OSM_NOTES:
                    BugDatabase.getInstance().reload(Globals.OSM_NOTES);
                    break;
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onTabSelected(final ActionBar.Tab tab, final FragmentTransaction fragmentTransaction)
    {
        mPager.setCurrentItem(tab.getPosition());
    }


    @Override
    public void onTabUnselected(
            final ActionBar.Tab tab,
            final android.support.v4.app.FragmentTransaction fragmentTransaction)
    {
    }


    @Override
    public void onTabReselected(final ActionBar.Tab tab, final FragmentTransaction fragmentTransaction)
    {
    }


    @Override
    public void onBugClicked(final Bug bug)
    {
        /* Open the selected Bug in the Bug Editor */
        Intent i = new Intent(BugListActivity.this, bug.getEditorClass());
        i.putExtra(BugEditActivity.EXTRA_BUG, bug);
        startActivityForResult(i, REQUEST_CODE_BUG_EDITOR_ACTIVITY);
    }


    @Override
    public void onBugMiniMapClicked(final Bug bug)
    {
        Intent data = new Intent();
        data.putExtra(RESULT_EXTRA_BUG, bug);
        setResult(RESULT_BUG_MINI_MAP_CLICKED, data);
        finish();
    }


    private class PlatformPagerAdapter extends FragmentPagerAdapter
    {
        private final ArrayList<Integer> mPlatforms = new ArrayList<>();


        public PlatformPagerAdapter(final FragmentManager fm)
        {
            super(fm);
        }


        @Override
        public int getCount()
        {
            return mPlatforms.size();
        }


        @Override
        public CharSequence getPageTitle(final int position)
        {
            switch (mPlatforms.get(position))
            {
                case Globals.KEEPRIGHT:
                    return getString(R.string.keepright);
                case Globals.OSMOSE:
                    return getString(R.string.osmose);
                case Globals.MAPDUST:
                    return getString(R.string.mapdust);
                case Globals.OSM_NOTES:
                    return getString(R.string.openstreetmap_notes);
            }
            return null;
        }


        @Override
        public Fragment getItem(final int position)
        {
            return BugPlatformListFragment.newInstance(mPlatforms.get(position));
        }


        public void add(int platform)
        {
            mPlatforms.add(platform);
        }
    }
}

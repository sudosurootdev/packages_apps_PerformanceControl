package com.brewcrewfoo.performance.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.brewcrewfoo.performance.R;
import com.brewcrewfoo.performance.fragments.*;
import com.brewcrewfoo.performance.util.ActivityThemeChangeInterface;
import com.brewcrewfoo.performance.util.BootClass;
import com.brewcrewfoo.performance.util.Constants;
import com.brewcrewfoo.performance.util.Helpers;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements Constants,ActivityThemeChangeInterface {

    public static Context c;
    private SharedPreferences mPreferences;
    private ViewPager mViewPager;
    private boolean mIsLightTheme;
    public static Boolean thide=false;
    public static int nCpus=16;
    public static ArrayList<String> mCurGovernor = new ArrayList<String>();
    public static ArrayList<String> mCurIO = new ArrayList<String>();
    public static ArrayList<String> mMaxFreqSetting = new ArrayList<String>();
    public static ArrayList<String> mMinFreqSetting = new ArrayList<String>();
    public static ArrayList<String> mCPUon = new ArrayList<String>();
    public static String[] mAvailableFrequencies = new String[0];
    public static int curcpu=0;
    private boolean pref_changed=false;
    private PreferenceChangeListener mPreferenceListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c=this;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme();
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        PagerTabStrip mPagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
        mPagerTabStrip.setBackgroundColor(getResources().getColor(R.color.pc_light_gray));
        mPagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.pc_blue));
        mPagerTabStrip.setDrawFullUnderline(true);

        if(savedInstanceState==null) {
            checkForSu();
        }
        else {
            TitleAdapter titleAdapter = new TitleAdapter(getFragmentManager());
            mViewPager.setAdapter(titleAdapter);
            mViewPager.setCurrentItem(0);
        }
        mPreferenceListener = new PreferenceChangeListener();
        mPreferences.registerOnSharedPreferenceChangeListener(mPreferenceListener);
    }
    @Override
    public void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);
    }


    class TitleAdapter extends FragmentPagerAdapter {
        String titles[] = getTitles();
        private Fragment frags[] = new Fragment[titles.length];

        public TitleAdapter(FragmentManager fm) {
            super(fm);

            int i=0;
            int j=0;
            while (i<getResources().getStringArray(R.array.tabs).length) {
                boolean isvisible=mPreferences.getBoolean(getResources().getStringArray(R.array.tabs)[i],true);
                if(Helpers.is_Tab_available(i) && isvisible){
                    switch(i){
                        case 0:
                            frags[j] = new CPUSettings();
                            break;
                        case 1:
                            frags[j] = new CPUAdvanced();
                            break;
                        case 2:
                            frags[j] = new BatteryInfo();
                            break;
                        case 3:
                            frags[j] = new MemSettings();
                            break;
                        case 4:
                            frags[j] = new VoltageControlSettings();
                            break;
                        case 5:
                            frags[j] = new Advanced();
                            break;
                        case 6:
                            frags[j] = new TimeInState();
                            break;
                        case 7:
                            frags[j] = new DiskInfo();
                            break;
                        case 8:
                            frags[j] = new Tools();
                            break;
                    }
                    j++;
                }
                i++;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return frags[position];
        }

        @Override
        public int getCount() {
            return frags.length;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onDestroy() {
        mPreferences.unregisterOnSharedPreferenceChangeListener(mPreferenceListener);
        super.onDestroy();
    }
    @Override
    public void onStop() {
        if(mPreferences.getBoolean("boot_mode",false) && pref_changed){
            new Thread(new Runnable() {
                public void run(){
                    new BootClass(c,mPreferences).writeScript();
                }
            }).start();
            Toast.makeText(c, "init.d script updated", Toast.LENGTH_SHORT).show();
        }
        super.onStop();
    }
    @Override
    public void onResume() {
        super.onResume();

        if (isThemeChanged() || thide) {
            thide=false;
            Helpers.restartPC(this);
        }

    }

    /**
     * Get a list of titles for the tabstrip to display depending on if the
     * @return String[] containing titles
     */
    private String[] getTitles() {
        List<String> titleslist = new ArrayList<String>();
        int i=0;
        while (i<getResources().getStringArray(R.array.tabs).length) {
            boolean isvisible=mPreferences.getBoolean(getResources().getStringArray(R.array.tabs)[i],true);
            if(Helpers.is_Tab_available(i) && isvisible)
                titleslist.add(getResources().getStringArray(R.array.tabs)[i]);
            i++;
        }
        return titleslist.toArray(new String[titleslist.size()]);
    }

    @Override
    public boolean isThemeChanged() {
        final boolean is_light_theme = mPreferences.getBoolean(PREF_USE_LIGHT_THEME, false);
        return is_light_theme != mIsLightTheme;
    }

    @Override
    public void setTheme() {
        final boolean is_light_theme = mPreferences.getBoolean(PREF_USE_LIGHT_THEME, false);
        mIsLightTheme = mPreferences.getBoolean(PREF_USE_LIGHT_THEME, false);
        setTheme(is_light_theme ? R.style.Theme_Light : R.style.Theme_Dark);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                String r= data.getStringExtra("r");
                if(r!=null && r.equals("ok")){
                    getCPUval();
                    TitleAdapter titleAdapter = new TitleAdapter(getFragmentManager());
                    mViewPager.setAdapter(titleAdapter);
                    mViewPager.setCurrentItem(0);
                    return;
                }
            }
        }
        finish();
    }
    private void checkForSu() {
        if(mPreferences.getBoolean("theme_changed",false)){
            mPreferences.edit().putBoolean("theme_changed",false).commit();
            getCPUval();
            TitleAdapter titleAdapter = new TitleAdapter(getFragmentManager());
            mViewPager.setAdapter(titleAdapter);
            mViewPager.setCurrentItem(0);
        }
        else {
            Log.d(TAG, "check for su & busybox");
            Intent intent = new Intent(MainActivity.this, checkSU.class);
            startActivityForResult(intent, 1);
        }
        /*
        if (mPreferences.getBoolean("firstrun", true)) {
                Intent intent = new Intent(MainActivity.this, checkSU.class);
                startActivityForResult(intent, 1);
        }
        else{
            if(!Helpers.checkSu()) {
                Intent intent = new Intent(MainActivity.this, checkSU.class);
                startActivityForResult(intent, 1);
            }
            else{
                getCPUval();
                TitleAdapter titleAdapter = new TitleAdapter(getFragmentManager());
                mViewPager.setAdapter(titleAdapter);
                mViewPager.setCurrentItem(0);
           }
        }*/
    }
    private void getCPUval(){
        nCpus=Helpers.getNumOfCpus();
        final String r=Helpers.readCPU(this,nCpus);
        Log.d(TAG, "utils read: " + r);
        if(r.contains(":")){
            mAvailableFrequencies = r.split(":")[nCpus * 5].split(" ");
            mMaxFreqSetting.clear();
            mMinFreqSetting.clear();
            mCurGovernor.clear();
            mCurIO.clear();
            mCPUon.clear();
            for (int i = 0; i < nCpus; i++){
                if(Integer.parseInt(r.split(":")[i*5])<0)
                    mMinFreqSetting.add(i,mAvailableFrequencies[0]);
                else
                    mMinFreqSetting.add(i,r.split(":")[i*5]);

                if(Integer.parseInt(r.split(":")[i*5+1])<0)
                    mMaxFreqSetting.add(i,mAvailableFrequencies[mAvailableFrequencies.length-1]);
                else
                    mMaxFreqSetting.add(i,r.split(":")[i*5+1]);

                /*if(new File(HARD_LIMIT_PATH).exists()){
                    mMaxFreqSetting[i]=Helpers.readOneLine(HARD_LIMIT_PATH);
                }*/

                mCurGovernor.add(i,r.split(":")[i*5+2]);
                mCurIO.add(i,r.split(":")[i*5+3]);
                mCPUon.add(i,r.split(":")[i*5+4]);
            }
        }
    }
    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            //Toast.makeText(c, "Changed: " + key, Toast.LENGTH_LONG).show();
            pref_changed=true;
        }
    }
}


/*
 * Performance Control - An Android CPU Control application Copyright (C) 2012
 * James Roberts
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.brewcrewfoo.performance.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.Preference.OnPreferenceChangeListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.view.*;
import com.brewcrewfoo.performance.R;
import com.brewcrewfoo.performance.activities.PCSettings;
import com.brewcrewfoo.performance.util.CMDProcessor;
import com.brewcrewfoo.performance.util.Constants;
import com.brewcrewfoo.performance.util.Helpers;

import java.io.File;


public class Tools extends PreferenceFragment implements
        OnSharedPreferenceChangeListener, OnPreferenceChangeListener, Constants {

    private SharedPreferences mPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  	    mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tools_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.app_settings) {
            Intent intent = new Intent(getActivity(), PCSettings.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tools, root, false);

        return view;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}

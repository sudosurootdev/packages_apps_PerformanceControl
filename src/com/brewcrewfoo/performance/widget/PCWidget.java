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

package com.brewcrewfoo.performance.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import com.brewcrewfoo.performance.R;
import com.brewcrewfoo.performance.activities.MainActivity;
import com.brewcrewfoo.performance.util.Constants;
import com.brewcrewfoo.performance.util.Helpers;

import java.io.File;

public class PCWidget extends AppWidgetProvider implements Constants {

    SharedPreferences mPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;
        final AppWidgetManager awm = AppWidgetManager.getInstance(context);
        final ComponentName nm = new ComponentName(context, PCWidget.class);
        final String action = intent.getAction();
        if (action!=null && action.equals("com.brewcrewfoo.performance.ACTION_FREQS_CHANGED")) {
            onUpdate(context, awm, awm.getAppWidgetIds(nm));
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
        for (int awi : appWidgetIds) {
            /*String max;
            String min;
            if(new File(DYN_MAX_FREQ_PATH).exists()){
                max = Helpers.toMHz(Helpers.readOneLine(DYN_MAX_FREQ_PATH));
            }
            else{
                max = Helpers.toMHz(Helpers.readOneLine(MAX_FREQ_PATH));
            }
            if(new File(DYN_MIN_FREQ_PATH).exists()){
                min = Helpers.toMHz(Helpers.readOneLine(DYN_MIN_FREQ_PATH));
            }
            else{
                min = Helpers.toMHz(Helpers.readOneLine(MIN_FREQ_PATH));
            }
            */
            if(MainActivity.mMaxFreqSetting==null) MainActivity.mMaxFreqSetting=Helpers.getMaxSpeed(0);
            if(MainActivity.mMinFreqSetting==null) MainActivity.mMinFreqSetting=Helpers.getMinSpeed(0);
            if(MainActivity.mCurGovernor==null) MainActivity.mCurGovernor = Helpers.readOneLine(GOVERNOR_PATH);
            if(MainActivity.mCurIO==null) MainActivity.mCurIO = Helpers.getIOScheduler();
            //String io = Helpers.getIOScheduler();
            //if((MainActivity.mMaxFreqSetting!=null)&&(MainActivity.mMinFreqSetting!=null))
            onUpdateWidget(context, appWidgetManager, awi, Helpers.toMHz(MainActivity.mMaxFreqSetting), Helpers.toMHz(MainActivity.mMinFreqSetting), MainActivity.mCurGovernor, MainActivity.mCurIO);
        }
    }

    public void onUpdateWidget(Context context,AppWidgetManager appWidgetManager, int appWidgetId, String max,String min, String gov, String io) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget);
        int bgColor = mPreferences.getInt(PREF_WIDGET_BG_COLOR, 0x00000000);
        int textColor = mPreferences.getInt(PREF_WIDGET_TEXT_COLOR, 0xff808080);
        views.setImageViewBitmap(R.id.widget_bg, Helpers.getBackground(bgColor));

        views.setTextViewText(R.id.max, max);
        views.setTextViewText(R.id.min, min);
        views.setTextViewText(R.id.gov, gov);
        views.setTextViewText(R.id.io, io);
        views.setTextColor(R.id.max, textColor);
        views.setTextColor(R.id.min, textColor);
        views.setTextColor(R.id.io, textColor);
        views.setTextColor(R.id.gov, textColor);
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_bg, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

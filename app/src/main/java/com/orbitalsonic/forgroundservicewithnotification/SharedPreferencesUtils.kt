package com.orbitalsonic.forgroundservicewithnotification

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesUtils {

    fun getRunningTimer(mContext: Context):Boolean{
        val sharedPreferences: SharedPreferences=mContext.getSharedPreferences("RunningTimerPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("RunningTimerValue",false)
    }

    fun setRunningTimer(mContext: Context,isRunning:Boolean){
        val sharedPreferences: SharedPreferences=mContext.getSharedPreferences("RunningTimerPrefs", Context.MODE_PRIVATE)
        val sharedPreferencesEditor: SharedPreferences.Editor  = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean("RunningTimerValue",isRunning)
        sharedPreferencesEditor.apply()
    }

}
package com.orbitalsonic.forgroundservicewithnotification

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.orbitalsonic.forgroundservicewithnotification.SharedPreferencesUtils.getRunningTimer
import com.orbitalsonic.forgroundservicewithnotification.SharedPreferencesUtils.setRunningTimer
import com.orbitalsonic.forgroundservicewithnotification.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var timerForegroundService: TimerForegroundService? = null
    private lateinit var foregroundBroadcastReceiver: ForegroundBroadcastReceiver
    private lateinit var  serviceIntent:Intent

    private var foregroundServiceBound = false

    private val foregroundServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as TimerForegroundService.LocalBinder
            timerForegroundService = binder.service
            foregroundServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            timerForegroundService = null
            foregroundServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        foregroundBroadcastReceiver = ForegroundBroadcastReceiver()
        serviceIntent = Intent(this, TimerForegroundService::class.java)
        onClickMethod()

        Log.i("ServiceTesting","onCreate")


    }

    private fun onClickMethod() {

        binding.btnStartTimer.setOnClickListener {
            if (getRunningTimer(this)) {
                setRunningTimer(this,false)
                timerForegroundService?.onSWatchStop()
                binding.btnStartTimer.text = "Resume Timer"
            } else {
                setRunningTimer(this,true)
                timerForegroundService?.onSWatchStart()
                binding.btnStartTimer.text = "Pause Timer"
//                if (foregroundPermissionApproved()) {
//                    timerForegroundService?.onSWatchStart()
//                } else {
//                    requestForegroundPermissions()
//                }
            }
        }

        binding.btnResetTimer.setOnClickListener {

        }
    }


    private fun resetTimer(){
        binding.btnStartTimer.text = "Start Timer"
        binding.stopWatchClockHr.text = "00"
        binding.stopWatchClockMin.text = ":00"
        binding.stopWatchClockSec.text = ":00"
    }

    private fun  settingViews(mTimerString:String){
        val splitTimeSW = mTimerString.split(":").toTypedArray()
        binding.stopWatchClockHr.text = splitTimeSW[0]
        binding.stopWatchClockMin.text = ":"+splitTimeSW[1]
        binding.stopWatchClockSec.text = ":"+splitTimeSW[2]
    }

    override fun onStart() {
        super.onStart()
        Log.i("ServiceTesting","onStart")
        bindService(serviceIntent, foregroundServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        Log.i("ServiceTesting","onStop")
        if (foregroundServiceBound) {
            unbindService(foregroundServiceConnection)
            foregroundServiceBound = false
        }

        super.onStop()
    }


    override fun onPause() {
        Log.i("ServiceTesting","onPause")
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            foregroundBroadcastReceiver
        )
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.i("ServiceTesting","onResume")
        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundBroadcastReceiver,
            IntentFilter(
                Constants.ACTION_FOREGROUND_TIMER_BROADCAST
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        setRunningTimer(this,false)
        timerForegroundService?.destroyService()
    }


    private inner class ForegroundBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val mTimer = intent.getStringExtra(Constants.EXTRA_TIMER)

            if (mTimer != null) {
                settingViews(mTimer)
            }
        }
    }
}
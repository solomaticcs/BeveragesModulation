package tony.beveragesmodulation.technicalsubject.preexercise;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;


/**
 * http://androidbiancheng.blogspot.tw/2011/02/activityservicebroadcastreceiver.html
 */
public class PreExerciseService extends Service {
    private static final String TAG = "PreExerciseService";
    public static final String MY_ACTION = "PreExerciseService.MY_ACTION";
    private PreExerciseReceiver mreceiver;
    private boolean flag;

    private CountDownTimer cdt = null;

    private long secondsUntilFinished;

    public long getSecondsUntilFinished() {
        return secondsUntilFinished;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        mreceiver = new PreExerciseReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MY_ACTION);
        registerReceiver(mreceiver, intentFilter);

        flag = intent.getExtras().getBoolean("flag");
        if(flag) {
            int millis = 1000;
            int seconds = 60;
            int mins = intent.getExtras().getInt("mins");
            cdt = new CountDownTimer(millis * seconds * mins, millis) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if(!flag) {
                        this.cancel();
                    }
                    secondsUntilFinished = millisUntilFinished / 1000;
                    Log.e(TAG, "seconds remaining:" + secondsUntilFinished);
                    Intent intent = new Intent();
                    intent.setAction(PreExerciseDetailActivity.MY_ACTION);
                    intent.putExtra("seconds_remaining", secondsUntilFinished);
                    sendBroadcast(intent);
                }

                @Override
                public void onFinish() {
                    Log.e(TAG, "Done!");
                    Intent intent = new Intent();
                    intent.setAction(PreExerciseDetailActivity.MY_ACTION);
                    secondsUntilFinished = 0;
                    intent.putExtra("seconds_remaining", secondsUntilFinished);
                    sendBroadcast(intent);
                }
            };

            cdt.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        flag = false;
        cdt.cancel();
        unregisterReceiver(mreceiver);
    }

    public class LocalBinder extends Binder {
        public PreExerciseService getService() {
            return  PreExerciseService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

//    public class TimerServiceReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            int rqs = intent.getIntExtra("RQS", 0);
//            if (rqs == PreExerciseDetailActivity.RQS_STOP_SERVICE){
//                flag = false;
//                stopSelf();
//            }
//        }
//    }
}

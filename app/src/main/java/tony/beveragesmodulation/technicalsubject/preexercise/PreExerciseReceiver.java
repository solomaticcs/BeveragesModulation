package tony.beveragesmodulation.technicalsubject.preexercise;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PreExerciseReceiver extends BroadcastReceiver {
    private static final String TAG = "PreExerciseReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        long secondsRemaining = intent.getLongExtra("seconds_remaining", 0);
        Log.e(TAG, "seconds remaining:" + secondsRemaining);
        //計算目前已過分鐘數
        Long minius = secondsRemaining / 60;
        //計算目前已過秒數
        Long seconds = secondsRemaining % 60;
//        timeTV.setText(String.format(getString(R.string.tm_limittime_text), String.valueOf(minius), String.valueOf(seconds)));
        if(mListener != null) {
            mListener.updateTime(minius,seconds);
        }
    }

    private PreExerciseServiceListener mListener = null;

    public void setListener(PreExerciseServiceListener mListener){
        this.mListener = mListener;
    }
}

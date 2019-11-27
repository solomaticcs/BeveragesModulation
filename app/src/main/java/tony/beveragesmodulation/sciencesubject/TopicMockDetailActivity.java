package tony.beveragesmodulation.sciencesubject;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;
import tony.beveragesmodulation.utils.Util;

/**
 *  模擬考試作答頁面
 */
public class TopicMockDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "TopicMockDetailActivity";
    private Toolbar toolbar;
    private Button prevBtn, nextBtn, resultBtn, resultCancelBtn, backTopicPracticeBtn, remindCancelBtn;
    private TextView questionTV, timeTV, useransTV, correctansTV, resultMessageTV, remindMessageTV, resultUserNameTV;
    private RadioButton[] rb = new RadioButton[4];
    private RadioGroup rg;
    private Button submitBtn;
    private RelativeLayout resultLayout,remindLayout;
    private String groupMockName;
    private int mockID, index;

    public final static String MY_ACTION = "TopicMockDetailActivity.MY_ACTION";
    public static final int RQS_STOP_SERVICE = 1;
    private MyReceiver myReceiver;

    private String SQL_GID = "SELECT `id`,`correctans`,`question`,`ans1`,`ans2`,`ans3`,`ans4` FROM `sciencesubject` " +
        "WHERE `gid` = %1$s ORDER BY RANDOM() LIMIT %2$s";
    private String SQL_ALL = "SELECT `id`,`correctans`,`question`,`ans1`,`ans2`,`ans3`,`ans4` FROM `sciencesubject` " +
            "ORDER BY RANDOM() LIMIT %1$s";

    private ArrayList<TopicItem> topicItemArrayList = new ArrayList<>();

    private static final double maxScore = 100.0; //滿分
    private static final double passScore = 80.0; //及格分數
    private static final double finalLevelPassScore = 60.0; //最後一關及格分數

//    private long secondsRemaining;
//    private boolean timerStatus = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_mock_detail);

        Bundle bundle = getIntent().getExtras();
        groupMockName = bundle.getString("groupMockName");
        mockID = bundle.getInt("mockID");
        Log.i(TAG, "群組名稱：" + groupMockName + " 群組代號：" + mockID);

        String title = getResources().getStringArray(R.array.tm_list_name)[mockID];
        setTitle(title);

        initActionBar();
        initView();

        // 取得題目ArrayList
        topicItemArrayList = getTopicItemArrayList();
        Log.i(TAG, "topicItemArrayList size: " + topicItemArrayList.size());
        for(int i = 0; i < topicItemArrayList.size(); i ++) {
            Log.i(TAG, topicItemArrayList.get(i).toString());
        }

        // 設定介面
        setLayoutText(topicItemArrayList, index);

        // 檢查是否過關
        int level = MainApp.getTgMockStatusField();
        double levelScore = getLevelScore(maxScore, topicItemArrayList);
        if (level >= mockID + 1) {
            Log.i(TAG, "已經過關。");
            passOrNopassResult(levelScore);
        } else if (level < mockID + 1) {
            Log.i(TAG, "尚未過關。");
            // 如果已經作答完成
            if(checkAreAllTopicDone(topicItemArrayList)) {
                passOrNopassResult(levelScore);
            } else { //如果尚未作答完成
                startTimer();
            }
        }
    }

    private void startTimer() {
        // 註冊推播
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MY_ACTION);
        registerReceiver(myReceiver, intentFilter);

        // StartService
        Intent intent = new Intent();
        intent.setClass(TopicMockDetailActivity.this, TopicTimerService.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("flag", true);
        if(mockID == TopicMockGroupFragment.MOCK_GROUP_7) {
            bundle.putInt("mins", 100);
        } else {
            bundle.putInt("mins", 30);
//            bundle.putInt("mins", 1);
        }
        intent.putExtras(bundle);
        startService(intent);
        bindService(intent, connc, Context.BIND_AUTO_CREATE);
    }

    private void stopTimer() {
        if(mService != null) {
            // 註銷推播
            unregisterReceiver(myReceiver);

            // StopService
            Intent intent = new Intent();
            intent.setAction(TopicTimerService.MY_ACTION);
            intent.putExtra("RQS", RQS_STOP_SERVICE);
            sendBroadcast(intent);

            unbindService(connc);

            stopService(new Intent(this, TopicTimerService.class));

            mService = null;
        }
    }

    private TopicTimerService mService = null;

    private ServiceConnection connc = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((TopicTimerService.LocalBinder)service).getService();
        }
    };

    private ArrayList<TopicItem> getNewTopicItemArrayList() {
        // 索引直設定成第一筆
        index = 0;
        // 建立ArrayList物件用來儲存TopicItem(題目)
        ArrayList<TopicItem> arrayList = new ArrayList<>();

        String SQL_Format = "";
        int limitNum = 0;
        // 如果是一～六關
        if (mockID >= TopicMockGroupFragment.MOCK_GROUP_1 &&
                mockID <= TopicMockGroupFragment.MOCK_GROUP_6) {
            int gID = mockID + 1;
            limitNum = 25;
            SQL_Format = String.format(SQL_GID, gID, limitNum);
        }
        // 如果是最後一關
        if (mockID == TopicMockGroupFragment.MOCK_GROUP_7) {
            limitNum = 80;
            SQL_Format = String.format(SQL_ALL, limitNum);
        }
        Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(SQL_Format, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                arrayList.add(new TopicItem(c.getInt(0), c.getInt(1), c.getString(2),
                        c.getString(3), c.getString(4), c.getString(5), c.getString(6)));
                c.moveToNext();
            }
        }
        c.close();
        return arrayList;
    }

    private ArrayList<TopicItem> getTopicItemArrayList() {
        // 索引直設定成第一筆
        index = 0;
        // 建立ArrayList物件用來儲存TopicItem(題目)
        ArrayList<TopicItem> arrayList = new ArrayList<>();
        // 取得所有題目
        ArrayList<TopicItem> arrayListFromSP = MainApp.getTgMockGroupArrayList(mockID);

        // 如果SharedPreference裡面有題目紀錄
        if(arrayListFromSP.size() > 0) {
            arrayList = arrayListFromSP;
        } else { //如果沒有紀錄，就取得題目
            // 取得目前過關level
            int level = MainApp.getTgMockStatusField();
            // 確認是否已經過關，如果已經過關
            if(level >= mockID + 1) {
                Log.i(TAG, "已經過關，但是沒有紀錄。");
            } else if (level < mockID + 1) {
                arrayList = getNewTopicItemArrayList();
            }
        }
        return arrayList;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        try {
            stopTimer();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
        try {
            stopTimer();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        try {
            stopTimer();
        } catch (Exception e) {

        }
    }

    private void initActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the menu item

                return true;
            }
        });
        // Inflate a menu to be displayed in the toolbar
        toolbar.inflateMenu(R.menu.activity_itemdetail);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initView() {
        questionTV = (TextView) findViewById(R.id.question);
        questionTV.setOnClickListener(this); //TEST
        prevBtn = (Button) findViewById(R.id.prev_btn);
        nextBtn = (Button) findViewById(R.id.result_btn);
        submitBtn = (Button) findViewById(R.id.submit_btn);
        prevBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        timeTV = (TextView) findViewById(R.id.time_tv);
        timeTV.setText("");
        timeTV.setVisibility(View.VISIBLE);
        useransTV = (TextView) findViewById(R.id.userans_tv);
        correctansTV = (TextView) findViewById(R.id.correctans_tv);
        rg = (RadioGroup)findViewById(R.id.rgroup);
        rg.setOnCheckedChangeListener(rg_listener);
        rb[0] = (RadioButton)findViewById(R.id.ans1_rb);
        rb[1] = (RadioButton)findViewById(R.id.ans2_rb);
        rb[2] = (RadioButton)findViewById(R.id.ans3_rb);
        rb[3] = (RadioButton)findViewById(R.id.ans4_rb);

        // 提醒頁面
        remindLayout = (RelativeLayout)findViewById(R.id.level_remind_layout);
        remindLayout.setVisibility(View.GONE);
        remindMessageTV = (TextView) findViewById(R.id.level_remind_text);
        remindCancelBtn = (Button) findViewById(R.id.level_remind_cancel_btn);
        remindCancelBtn.setOnClickListener(closeRemindLayoutListener);

        // 結果頁面
        resultLayout = (RelativeLayout)findViewById(R.id.level_result_layout);
        resultLayout.setVisibility(View.GONE);
        resultMessageTV = (TextView) findViewById(R.id.level_result_message);
        resultBtn = (Button) findViewById(R.id.level_result_btn);
        resultCancelBtn = (Button) findViewById(R.id.level_result_cancel_btn);
        resultUserNameTV = (TextView) findViewById(R.id.level_result_username);
        backTopicPracticeBtn = (Button) findViewById(R.id.level_result_back_btn);
        resultCancelBtn.setOnClickListener(closeResultLayoutListener);
        backTopicPracticeBtn.setOnClickListener(backToMenuListener);
    }

    /**
     * 設定問題與4個答案，並且更新button與radiobutton為enable or disable
     */
    private void setLayoutText(ArrayList<TopicItem> arrayList, int index) {
        Log.i(TAG, "setLayoutText");
        Log.i(TAG, "arrayList size:" + arrayList.size());
        if(arrayList.size() > 0) {
            Log.i(TAG, arrayList.get(index).toString());
            // 問題
            questionTV.setText((index + 1) + "." + arrayList.get(index).getQuestion());
            // 答案1~4
            if(arrayList.get(index).getId() == 62) {
                for (int i = 0; i < arrayList.get(index).getAns().length; i++) {
                    String uri = "drawable/" + arrayList.get(index).getAns()[i].split("\\.")[0].toLowerCase();
                    int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                    rb[i].setText("(" + (i + 1) + ")");
                    rb[i].setCompoundDrawablesWithIntrinsicBounds(0, 0, imageResource, 0);
                }
            } else {
                for (int i = 0; i < arrayList.get(index).getAns().length; i++) {
                    rb[i].setText(arrayList.get(index).getAns()[i]);
                    rb[i].setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }

            updateViews(arrayList, index);

            Log.i(TAG, "第" + (index + 1) + "題");
            if (index == arrayList.size() - 1) {
                Log.i(TAG, "這一關的最後一筆");
                // 取得沒做完的題目，除了最後一題
                ArrayList<Integer> notDoneList = getNotDoneTopicWithoutLast();
                if(notDoneList.size() > 0) {
                    // 修改remind文字
                    String str = "";
                    for (int i = 0; i < notDoneList.size(); i++) {
                        str += String.valueOf(notDoneList.get(i));
                        if (i != notDoneList.size() - 1) {
                            str += ", ";
                        }
                    }
                    remindMessageTV.setText(String.format(getString(R.string.tm_remind_message), str));
                    // 顯示提醒介面，提醒還有哪幾題沒做完
                    remindLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private RadioGroup.OnCheckedChangeListener rg_listener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int p = group.indexOfChild(findViewById(checkedId));
            Log.i(TAG, "選擇第" + (p + 1) + "個答案");
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prev_btn:
                Log.i(TAG, "prev_btn");
                if((index - 1) >= 0) {
                    index--;
                    setLayoutText(topicItemArrayList, index);
                } else {
                    Toast.makeText(getApplicationContext(), "沒有上一個！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.result_btn:
                Log.i(TAG, "next_btn");
                if((index + 1) <= (topicItemArrayList.size() - 1)) {
                    index++;
                    setLayoutText(topicItemArrayList, index);
                } else {
                    Toast.makeText(getApplicationContext(), "沒有下一個！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.submit_btn:
                Log.i(TAG, "submit_btn");

                // 取得使用者答案
                int radioButtonID  = rg.getCheckedRadioButtonId();
                View radioButton = rg.findViewById(radioButtonID);
                int idx = rg.indexOfChild(radioButton);
                // 如果沒有選擇答案
                if(idx == -1) {
                    Toast.makeText(getApplicationContext(), "請選擇答案！", Toast.LENGTH_SHORT).show();
                    return;
                }

                int ansNum = idx + 1;

                // 設定使用者答案
                topicItemArrayList.get(index).setUserAns(ansNum);

                // 設定此已經作答完成
                topicItemArrayList.get(index).setIsAlreadyAnswer(true);

                // 確認答案是否正確
                if(ansNum == topicItemArrayList.get(index).getCorrectAns()) {
                    Toast.makeText(getApplicationContext(), "恭喜答對了！",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "答錯囉！",Toast.LENGTH_SHORT).show();
                }

                // 更新view
                updateViews(topicItemArrayList, index);

                // 如果這關題目都已經完成了
                if(checkAreAllTopicDone(topicItemArrayList)) {
                    // 計算使用者作答此關卡的分數
                    double levelScore = getLevelScore(maxScore, topicItemArrayList);
                    Log.i(TAG, "levelScore:" + levelScore);
                    Log.i(TAG, "secondsUntilFinished:" + mService.getSecondsUntilFinished());
                    // 如果時間還沒到，就判斷分數
                    if(mService.getSecondsUntilFinished() > 0) {
                        passOrNopassResult(levelScore);
                        stopTimer();
                    } else { // 如果時間已經到了
                        String messageStr = null, firstBtnStr = null;
                        Button.OnClickListener btnCLK = null;
                        boolean isShowBackBtn = false;
                        // 判斷是否已經過關
                        int level = MainApp.getTgMockStatusField();
                        if(level >= mockID + 1) {
                            // text
                            messageStr = String.format(getString(R.string.tm_timeout_message_alpass), "" + levelScore);
                            firstBtnStr = "回選單";
                            // listener
                            btnCLK = backToMenuListener;
                            //
                            isShowBackBtn = false;
                        } else if (level < mockID + 1) {
                            // text
                            messageStr = String.format(getString(R.string.tm_timeout_message_nopass, "" + levelScore));
                            firstBtnStr = "重新測驗";
                            // listener
                            btnCLK = restartTopicListener;
                            //
                            isShowBackBtn = true;
                        }
                        resultMessage(messageStr,firstBtnStr,btnCLK, isShowBackBtn);
                    }
                }
                break;
            case R.id.question:
                Log.i(TAG, "點選題目文字。");
                if(Util.TESTDEBUG) {
                    if(index == topicItemArrayList.size() -1) {
                        /**
                         * 快速過關測試用
                         */
                        //
                        if (MainApp.getTgMockStatusField() < mockID + 1) {
                            MainApp.editTgMockStatusField(mockID + 1);
                        }
                        MainApp.editTgMockGroupArrayList(mockID, "");
                        // 提示訊息
                        Toast.makeText(TopicMockDetailActivity.this, "測試模擬使用者完整通過" + (mockID + 1) + "關測驗", Toast.LENGTH_SHORT).show();
                        //
                        stopTimer();
                    }
                }
                break;
        }
    }

    /**
     * 檢查是否有通過紀錄相關訊息
     */
    private void passOrNopassResult(double levelScore) {
        String messageStr, firstBtnStr;
        Button.OnClickListener btnCLK;
        boolean isShowBackBtn;

        // 將此關的作答題目紀錄
        MainApp.editTgMockGroupArrayList(mockID, topicItemArrayList);

        // 如果小於最後一關，分數必須為80分以上才能通過
        // 如果是最後一關，分數必須為60分以上才能通過
        if((mockID < TopicMockGroupFragment.MOCK_GROUP_7 && levelScore >= passScore)
                || (mockID == TopicMockGroupFragment.MOCK_GROUP_7 && levelScore >= finalLevelPassScore)) {
            int level = MainApp.getTgMockStatusField();
            // 檢查是否設置過關level後是否大於原本的level
            // 如果大於就設置，小於或等於就不設置以免影響主選單ListView的狀態
            if(mockID + 1 > level) {
                // 更改過關的level
                MainApp.editTgMockStatusField(mockID + 1);
            }
            // 設定文字
            messageStr = String.format(getString(R.string.tm_result_message_pass),
                    "" + levelScore, "" + (mockID + 1));
            firstBtnStr = "回選單";
            // listener
            btnCLK = backToMenuListener;
            // 是否顯示回題庫練習按鈕
            isShowBackBtn = false;
        } else { //如果低於80分
            // 設定文字
            messageStr = String.format(getString(R.string.tm_result_message_nopass),
                    "" + levelScore, "" + (mockID + 1));
            firstBtnStr = "重新測驗";
            // listener
            btnCLK = restartTopicListener;
            // 是否顯示回題庫練習按鈕
            isShowBackBtn = true;
        }
        resultMessage(messageStr, firstBtnStr, btnCLK, isShowBackBtn);
    }

    /**
     *
     *  訊息文字 , 結果按鈕文字, 結果按鈕聆聽者
     */
    private void resultMessage(String messageStr, String firstBtnStr, Button.OnClickListener btnCLK, boolean isVisible) {
        // 顯示整個結果介面
        resultLayout.setVisibility(View.VISIBLE);
        // 使用者名字
        resultUserNameTV.setText(MainApp.getUserNameField());
        // 設定訊息文字
        resultMessageTV.setText(messageStr);
        // 設定第一個按鈕的文字
        resultBtn.setText(firstBtnStr);
        // 設定按鈕的聆聽者
        resultBtn.setOnClickListener(btnCLK);
        // 設定是否顯示「回題庫練習」按鈕
        backTopicPracticeBtn.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private Button.OnClickListener restartTopicListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e(TAG, "restartTopicListener");
            // 結果頁面去除
            resultLayout.setVisibility(View.GONE);
            // 該關卡重新測驗
            // 取得新題目
            topicItemArrayList = getNewTopicItemArrayList();
            // 設定介面與文字
            setLayoutText(topicItemArrayList, index);
            // 設定timer
            startTimer();
        }
    };

    private Button.OnClickListener backToMenuListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e(TAG, "backToMenuListener");
            finish();
        }
    };

    private Button.OnClickListener closeResultLayoutListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e(TAG, "closeResultLayoutListener");
            resultLayout.setVisibility(View.GONE);
        }
    };

    private Button.OnClickListener closeRemindLayoutListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e(TAG, "closeRemindLayoutListener");
            remindLayout.setVisibility(View.GONE);
        }
    };

    /**
     * 更新submit狀態, radioButton狀態, 設定使用者答案TV的文字
     */
    private void updateViews(ArrayList<TopicItem> arrayList, int index) {
        // 清除radioButton點選狀態
        rg.clearCheck();
        // 如果已經答過
        if(arrayList.get(index).isAlreadyAnswer()) {
            // RadioButton 不能點選
            for (RadioButton aRb : rb) {
                aRb.setEnabled(false);
            }
            // Button不能點選
            submitBtn.setEnabled(false);
            // 顯示使用者選擇的答案
            int userAns = arrayList.get(index).getUserAns();
            rb[userAns - 1].setChecked(true);
            useransTV.setVisibility(View.VISIBLE);
            useransTV.setText(
                    String.format(getString(R.string.tm_userans_text),
                            arrayList.get(index).getAns()[arrayList.get(index).getUserAns() - 1]));
            if(mockID + 1 >= MainApp.getTgMockStatusField()) {
                correctansTV.setVisibility(View.VISIBLE);
                correctansTV.setText(
                        String.format(getString(R.string.tm_correctans_text),
                                arrayList.get(index).getAns()[arrayList.get(index).getCorrectAns() - 1]));
            } else {
                correctansTV.setVisibility(View.GONE);
            }
        } else { //尚未答過
            // RadioButton 可以點選
            for (RadioButton aRb : rb) {
                aRb.setEnabled(true);
            }
            // 按鈕可以點選
            submitBtn.setEnabled(true);
            // 使用者選擇的答案為空
            useransTV.setVisibility(View.GONE);
            // 正確答案為空
            correctansTV.setVisibility(View.GONE);
        }
    }

    /**
     *  取得尚未作答完成的題目，除了最後一題
     */
    private ArrayList<Integer> getNotDoneTopicWithoutLast() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for(int i = 0; i < topicItemArrayList.size(); i++) {
            if(!topicItemArrayList.get(i).isAlreadyAnswer()) {
                if(i != topicItemArrayList.size() -1) {
                    arrayList.add(i + 1);
                }
            }
        }
        return arrayList;
    }

    /**
     * 確認這關題目是否已經全部作答完畢
     */
    private boolean checkAreAllTopicDone(ArrayList<TopicItem> topicItems) {
        int count = 0;
        for(int i = 0; i < topicItems.size(); i++) {
            //如果使用者答案不是第0個答案，而且已經被標示為作答完成
            if(topicItems.get(i).getUserAns() != 0 && topicItems.get(i).isAlreadyAnswer()) {
                count++;
            } else {
                return false;
            }
        }
        return count == topicItems.size();
    }

    /**
     * 取得此關卡的分數
     */
    private double getLevelScore(double maxScore, ArrayList<TopicItem> topicItems) {
        // 計算每題分數
        double eachScore = maxScore /
                (double)topicItems.size();
        // 計算正確題數
        int correctCount = 0;
        for(int i = 0; i < topicItems.size(); i++) {
            int userAns = topicItems.get(i).getUserAns();
            int correctAns = topicItems.get(i).getCorrectAns();
            if(userAns == correctAns) {
                correctCount++;
            }
        }
        // 回傳每題分數 * 正確題數 = 此關分數
        return eachScore * correctCount;
    }


    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long secondsRemaining = intent.getLongExtra("seconds_remaining", 0);
            Log.e(TAG, "seconds remaining:" + secondsRemaining);
            //計算目前已過分鐘數
            Long minius = secondsRemaining / 60;
            //計算目前已過秒數
            Long seconds = secondsRemaining % 60;
            timeTV.setText(String.format(getString(R.string.tm_limittime_text), String.valueOf(minius), String.valueOf(seconds)));
        }
    }
}

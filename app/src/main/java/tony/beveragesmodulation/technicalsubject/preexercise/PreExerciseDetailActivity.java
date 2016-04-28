package tony.beveragesmodulation.technicalsubject.preexercise;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;
import tony.beveragesmodulation.utils.Util;

/**
 * 前置操作練習-詳細作答頁面
 */
public class PreExerciseDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PreExerciseDetailActivity";
    private Toolbar toolbar;
    private String tgGroupID;
    private int orderID, dID;
    private String status;

    // 飲調項目的材料擺放
    private PreExerciseItem preExerciseItem = null;

    // 基本SQL
    private static final String basicSQL = " SELECT A.`id`, A.`id_order`, A.`tg_groupid`, A.`name`," +
            " A.`ingredient`, A.`modulation`,  A.`decorations`, A.`cup_utensils`," +
            " B.`material_area_id`, B.`material_area`, B.`decoration_area_id`, B.`decoration_area`, B.`coffee_area_id`, B.`coffee_area`," +
            " B.`work_area_id`, B.`work_area`, B.`mezzanine_id`, B.`mezzanine`, B.`finished_area_id`, B.`finished_area`, " +
            " B.`cup_utensils_id`, B.`cup_utensils` " +
            " FROM `drink_recipe` AS A, `preoperation` as B " +
            " WHERE A.`id` = B.`id` ";
    // 隨機找出一筆
    private static final String randomOneItem_SQL = basicSQL + " ORDER BY RANDOM() LIMIT 1 ";
    // 列出該類別所有item
    private static final String groupItem_SQL = basicSQL + " AND A.`tg_groupid` = '%1$s' ORDER BY A.`id` ASC ";
    // 找出指定id
    private static final String oneItem_SQL = basicSQL + " AND A.`id` = '%1$d' ";

    private TextView itemTitle, ingredientDes, modulationFunctionDes, decorationDes, cupDes, timeTV;
    private Button choiceBtn, checkBtn;
    private GridView userAnsGridView;

    private PreExerciseAdapter preExerciseAdapter;

    public static final int RQS_STOP_SERVICE = 1;
    public final static String MY_ACTION = "PreExerciseDetailActivity.MY_ACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_preexercise_detail);

        tgGroupID = getIntent().getExtras().getString("tgGroupID", "");

        // 隨機出題
        if(tgGroupID.equals("random")) {
            // status
            status = getIntent().getExtras().getString("status", "none");
            Log.e(TAG, "status:" + status);
            if(status.equals("update")) {
                // 向資料庫取得資料
                Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(randomOneItem_SQL, null);
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    preExerciseItem = new PreExerciseItem(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9), c.getString(10), c.getString(11), c.getString(12), c.getString(13), c.getString(14), c.getString(15), c.getString(16), c.getString(17), c.getString(18), c.getString(19), c.getString(20), c.getString(21));
                    MainApp.editPreExerciseItem(preExerciseItem);
                }
                c.close();
                // 刷新隨機的SharedPreference紀錄
                PreExerciseUtil.refreshRandomRecord();
            } else {
                preExerciseItem = MainApp.getPreExerciseItem();
            }
        } else if(tgGroupID.equals("mf_topic")) {
            // ID
            dID = getIntent().getExtras().getInt("dID");
            // 根據ID取得題目
            String SQL_Format = String.format(oneItem_SQL, dID);
            // 向資料庫取得資料
            Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(SQL_Format, null);
            if(c.getCount() > 0) {
                c.moveToFirst();
                preExerciseItem = new PreExerciseItem(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9), c.getString(10), c.getString(11), c.getString(12), c.getString(13), c.getString(14), c.getString(15), c.getString(16), c.getString(17), c.getString(18), c.getString(19), c.getString(20), c.getString(21));
                MainApp.editPreExerciseItem(tgGroupID, dID, preExerciseItem);
            }
        } else { // 根據群組第幾個出題
            // 第幾筆
            orderID = getIntent().getExtras().getInt("orderID", 1);
            // 根據群組取得項目
            String SQL_Format = String.format(groupItem_SQL, tgGroupID);
            // 計算迴圈次數
            int ii = 1;
            // 向資料庫取得資料
            Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(SQL_Format, null);
            if(c.getCount() > 0) {
                c.moveToFirst();
                for(int i=0;i<c.getCount();i++) {
                    if(ii == orderID) {
                        preExerciseItem = new PreExerciseItem(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9), c.getString(10), c.getString(11), c.getString(12), c.getString(13), c.getString(14), c.getString(15), c.getString(16), c.getString(17), c.getString(18), c.getString(19), c.getString(20), c.getString(21));
                        MainApp.editPreExerciseItem(tgGroupID, orderID, preExerciseItem);
                        break;
                    } else {
                        ii++;
                        c.moveToNext();
                    }
                }
            }
            c.close();
        }

        // print
        Log.i(TAG, "preExerciseItem:" + preExerciseItem.toString());

        setTitle("前置操作練習");

        initActionBar();
        initView();
        setTextLayout();

        /*
        if(PreExerciseUtil.getTimerService() == null && PreExerciseUtil.getMyReceiver() == null) {
            Log.e(TAG, "do service");
            startTimer();
        } else {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MY_ACTION);
            registerReceiver(PreExerciseUtil.getMyReceiver(), intentFilter);
        }
        PreExerciseUtil.getMyReceiver().setListener(new PreExerciseServiceListener() {
            @Override
            public void updateTime(long min, long sec) {
                timeTV.setText("剩餘時間：" + min + ":" + sec);
            }
        });
        */
    }

    private void setTextLayout() {
        Log.e(TAG, "setTextLayout");
        itemTitle.setText(preExerciseItem.getName());
        ingredientDes.setText(Util.getCutDotText(preExerciseItem.getIngredientDes()));
        modulationFunctionDes.setText(Util.getCutDotText(preExerciseItem.getModulationFunctionDes()));
        decorationDes.setText(Util.getCutDotText(preExerciseItem.getDecorationsDes()));
        cupDes.setText(preExerciseItem.getCupUtensilsDes());
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
        itemTitle = (TextView) findViewById(R.id.item_title);
        // TEST
        if(Util.TESTDEBUG) {
            itemTitle.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // 如果不是隨機項目
                    if (!tgGroupID.equals("random") && !tgGroupID.equals("mf_topic")) {
                        // 取得通過杯數
                        int level = MainApp.getPeGroupStatusField(tgGroupID);
                        // 如果通過杯數小於orderID才更新狀態
                        if(level < orderID) {
                            // 儲存該群組通過杯數
                            MainApp.editPeGroupStatusField(tgGroupID, orderID);
                        }
                        Toast.makeText(getApplicationContext(), "快速通過" + preExerciseItem.getName(), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }
        ingredientDes = (TextView) findViewById(R.id.ingredient_des);
        modulationFunctionDes = (TextView) findViewById(R.id.modulationfunction_des);
        decorationDes = (TextView) findViewById(R.id.decoration_des);
        cupDes = (TextView) findViewById(R.id.cup_des);
        choiceBtn = (Button) findViewById(R.id.choice_btn);
        checkBtn = (Button) findViewById(R.id.result_btn);
        choiceBtn.setOnClickListener(this);
        checkBtn.setOnClickListener(this);
        userAnsGridView = (GridView) findViewById(R.id.userans_gridview);
        userAnsGridView.setEmptyView(findViewById(R.id.empty_tv));
        preExerciseAdapter = new PreExerciseAdapter(getApplicationContext());
        userAnsGridView.setAdapter(preExerciseAdapter);
        timeTV = (TextView)findViewById(R.id.time_tv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");

        // 全部使用者從公共材料區所選擇的項目
        ArrayList<PreExercisePAItem> allAMSaveItems, beChoiceItems;
        // 取得公共材料區(PEPA)所有項目
        if(tgGroupID.equals("random")) {
            allAMSaveItems = PreExerciseUtil.getAllAMGroupItems();
        } else if(tgGroupID.equals("mf_topic")) {
            allAMSaveItems = PreExerciseUtil.getAllAMGroupItems(tgGroupID, dID);
        } else {
            allAMSaveItems = PreExerciseUtil.getAllAMGroupItems(tgGroupID, orderID);
        }
        beChoiceItems = PreExerciseUtil.getBeChoicePEPAItems(allAMSaveItems);
        // 取得公共材料區(PEPA)被選取的項目並更新adapter內容
        preExerciseAdapter.updatePreExercisePAItems(beChoiceItems, false, false);
    }

    @Override
    public void onBackPressed() {
        if(tgGroupID.equals("random")) {
            confirmExitRandom();
        } else {
            if (!tgGroupID.equals("random") && !tgGroupID.equals("mf_topic")) {
                Intent intent = new Intent(PreExerciseDetailActivity.this, PreExerciseLevelActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("tgGroupID", tgGroupID);
                intent.putExtras(bundle);
                startActivity(intent);
            }
            finish();
        }
//        stopTimer();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否触发按键为back键
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed();
            return true;
        } else { // 如果不是back键正常响应
            return super.onKeyDown(keyCode, event);
        }
    }

    // 確認是否退出隨機出題
    private void confirmExitRandom() {
        AlertDialog.Builder ad = new AlertDialog.Builder(PreExerciseDetailActivity.this);
        ad.setTitle("提示訊息");
        ad.setMessage("一旦離開隨機出題後此筆紀錄將會消失，確定要離開？");
        ad.setPositiveButton("離開", new DialogInterface.OnClickListener() {// 退出按鈕
            public void onClick(DialogInterface dialog, int i) {
                // TODO Auto-generated method stub
                PreExerciseDetailActivity.this.finish();// 關閉activity
            }
        });
        ad.setNegativeButton("繼續作答", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                // 不退出不用執行任何操作
            }
        });
        ad.show();// 示對話框
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Bundle bundle;
        switch (v.getId()) {
            case R.id.choice_btn:
                Log.e(TAG, "進行選擇");

                intent = new Intent(PreExerciseDetailActivity.this, PreExercisePAGroupActivity.class);
                bundle = new Bundle();
                bundle.putString("tgGroupID", tgGroupID);
                if (!tgGroupID.equals("random") && !tgGroupID.equals("mf_topic")) {
                    bundle.putInt("orderID", orderID);
                } else if (tgGroupID.equals("mf_topic")) {
                    bundle.putInt("dID", dID);
                }
                intent.putExtras(bundle);
                startActivity(intent);

                //
                finish();
                break;
            case R.id.result_btn:
                Log.e(TAG, "檢查");

                intent = new Intent(PreExerciseDetailActivity.this, PreExerciseDetailCheckActivity.class);
                bundle = new Bundle();
                bundle.putString("tgGroupID", tgGroupID);
                if (!tgGroupID.equals("random") && !tgGroupID.equals("mf_topic")) {
                    bundle.putInt("orderID", orderID);
                } else if (tgGroupID.equals("mf_topic")) {
                    bundle.putInt("dID", dID);
                }
                intent.putExtras(bundle);
                startActivity(intent);

                //
                finish();
                break;
        }
    }

    private void startTimer() {
        // 註冊推播
        PreExerciseUtil.setMyReceiver(new PreExerciseReceiver());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MY_ACTION);
        registerReceiver(PreExerciseUtil.getMyReceiver(), intentFilter);

        // StartService
        Intent intent = new Intent();
        intent.setClass(PreExerciseDetailActivity.this, PreExerciseService.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("flag", true);
        bundle.putInt("mins", 6);
        intent.putExtras(bundle);
        startService(intent);
        bindService(intent, connc, Context.BIND_AUTO_CREATE);
    }

    private void stopTimer() {
        if(PreExerciseUtil.getTimerService() != null) {
            // 註銷推播
            unregisterReceiver(PreExerciseUtil.getMyReceiver());

//            // StopService
//            Intent intent = new Intent();
//            intent.setAction(PreExerciseService.MY_ACTION);
//            intent.putExtra("RQS", RQS_STOP_SERVICE);
//            sendBroadcast(intent);

            unbindService(connc);

            stopService(new Intent(this, PreExerciseService.class));

            PreExerciseUtil.setTimerService(null);
        }
    }

    private ServiceConnection connc = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PreExerciseUtil.setTimerService(((PreExerciseService.LocalBinder) service).getService());
        }
    };


    /*
    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(PreExerciseUtil.getMyReceiver());
            unbindService(connc);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(PreExerciseUtil.getMyReceiver());
            unbindService(connc);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(PreExerciseUtil.getMyReceiver());
            unbindService(connc);
        } catch (Exception e) {

        }
    }
    */
}

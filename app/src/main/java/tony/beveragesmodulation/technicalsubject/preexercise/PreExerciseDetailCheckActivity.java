package tony.beveragesmodulation.technicalsubject.preexercise;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;

/**
 * 前置操作練習-詳細作答確認材料頁面
 */
public class PreExerciseDetailCheckActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "PreExerciseDetailCheckActivity";
    private Toolbar toolbar;

    private TextView itemTitle, checkMessage;
    private Button backDetailBtn, nextBtn;
    private GridView checkResultGridview;
    private PreExerciseAdapter adapter;

    private String tgGroupID;
    private int orderID, dID;

    private static final String SQL = "SELECT A.id,A.am_groupid,B.am_groupname,A.item_name,A.use_function,A.applicable_topic_description,A.img_name " +
            "FROM `appliance_material` AS A,`appliance_material_group` AS B " +
            "WHERE A.am_groupid = B.am_groupid " +
            "AND A.am_groupid != 10 ";

    // 飲調項目的材料擺放
    private PreExerciseItem preExerciseItem = null;

    // 全部使用者從公共材料區所選擇的項目
    private ArrayList<PreExercisePAItem> allAMSaveItems = new ArrayList<>();
    // 被選取的所有項目
    private ArrayList<PreExercisePAItem> beChoiceItems = new ArrayList<>();
    // 正確答案項目
    private ArrayList<PreExercisePAItem> correctItems = new ArrayList<>();
    // 混再一起的項目
    private ArrayList<PreExercisePAItem> newItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preexercise_detail_check);

        setTitle("前置操作練習 - 檢查選取材料");

        Bundle bundle = getIntent().getExtras();
        tgGroupID = bundle.getString("tgGroupID");
        if(tgGroupID.equals("random")) {
            preExerciseItem = MainApp.getPreExerciseItem();
            allAMSaveItems = PreExerciseUtil.getAllAMGroupItems();
        } else if(tgGroupID.equals("mf_topic")) {
            dID = bundle.getInt("dID");
            preExerciseItem = MainApp.getPreExerciseItem(tgGroupID, dID);
            allAMSaveItems = PreExerciseUtil.getAllAMGroupItems(tgGroupID, dID);
        } else {
            orderID = bundle.getInt("orderID");
            preExerciseItem = MainApp.getPreExerciseItem(tgGroupID, orderID);
            allAMSaveItems = PreExerciseUtil.getAllAMGroupItems(tgGroupID, orderID);
        }

        initActionBar();
        initView();

        setListView();
        setTextLayout();
    }

    /**
     *  確認答案是否正確
     */
    private void setTextLayout() {
        itemTitle.setText(preExerciseItem.getName());

        // 正確數量
        int correctCount = 0;
        // 使用者拿對數量
        int userCorrectCount = 0;
        // 使用者拿錯數量
        int userWrongCount = 0;
        // 使用者缺少數量
        int userLossCount = 0;
        for(int i = 0; i < newItems.size(); i ++) {
            // 正確答案
            if(newItems.get(i).isCorrectAns()) {
                // 正確答案數量+1
                correctCount++;

                // 使用者有拿取，正確數量+1
                if(newItems.get(i).isChecked()) {
                    userCorrectCount++;
                } else { // 使用者沒拿取，缺少數量+1
                    userLossCount++;
                }
            } else { // 錯誤答案

                // 使用者有拿取，錯誤數量+1
                if(newItems.get(i).isChecked()) {
                    userWrongCount++;
                }
            }
        }

        // 設定檢查結果訊息
        checkMessage.setText(String.format(getString(R.string.pe_detail_check_message), userCorrectCount, userWrongCount, userLossCount));

        // 設定button
        if(correctCount == userCorrectCount) {
            nextBtn.setEnabled(true);
            backDetailBtn.setEnabled(false);
        } else {
            nextBtn.setEnabled(false);
            backDetailBtn.setEnabled(true);
        }
    }

    private void setListView() {
        // 被選取的所有項目
        beChoiceItems = PreExerciseUtil.getBeChoicePEPAItems(allAMSaveItems);
        // 正確答案項目
        correctItems = getArrayListFromIds();
        // 取得混合項目(使用者選擇的答案與正確答案)
        newItems = getNewItems(beChoiceItems, correctItems);
        // 更新adapter
        adapter.updatePreExercisePAItems(newItems, true, false);
    }

    /**
     *  檢查是否有選取到全部正確答案，並產生新的陣列
     */
    private ArrayList<PreExercisePAItem> getNewItems(ArrayList<PreExercisePAItem> beChoiceItems,
                              ArrayList<PreExercisePAItem> correctItems) {
        // 宣告新陣列
        ArrayList<PreExercisePAItem> paItemArrayList = new ArrayList<>();
        // 加入所有正確材料項目
        paItemArrayList.addAll(correctItems);
        //
        for(int i = 0; i < beChoiceItems.size(); i ++) {
            boolean isExist = false;
            for(int j = 0; j < paItemArrayList.size(); j ++) {
                if(beChoiceItems.get(i).getId() == paItemArrayList.get(j).getId()) {
                    isExist = true;
                    // 如果使用者選取的答案有在陣列裡面，就將陣列裡面的check狀態改為true
                    paItemArrayList.get(j).setIsChecked(true);
                }
            }
            if(!isExist) {
                paItemArrayList.add(beChoiceItems.get(i));
            }
        }
        Collections.sort(paItemArrayList, new Comparator<PreExercisePAItem>() {
            @Override
            public int compare(PreExercisePAItem lhs, PreExercisePAItem rhs) {
                return lhs.getId() - rhs.getId();
            }
        });
        Log.i(TAG, "paItemArrayList size:" + paItemArrayList.size());
        return paItemArrayList;
    }

    /**
     * 依編號取得item
     */
    private ArrayList<PreExercisePAItem> getArrayListFromIds() {
        // 儲存該項目需要的材料ID
        ArrayList<Integer> integers = new ArrayList<>();
        int[] materialAreaIds = PreExerciseUtil.getAreaIds(preExerciseItem.getMaterialAreaId());
        int[] decorationAreaIds = PreExerciseUtil.getAreaIds(preExerciseItem.getDecorationAreaId());
        int[] coffeeAreaIds = PreExerciseUtil.getAreaIds(preExerciseItem.getCoffeeAreaId());
        int[] workAreaIds = PreExerciseUtil.getAreaIds(preExerciseItem.getWorkAreaId());
        int[] mezzanineIds = PreExerciseUtil.getAreaIds(preExerciseItem.getMezzanineId());
        int[] finishedAreaIds = PreExerciseUtil.getAreaIds(preExerciseItem.getFinishedAreaId());
        int[] cupUtensilsIds = PreExerciseUtil.getAreaIds(preExerciseItem.getCupUtensilsId());
        PreExerciseUtil.addAreaIdsToArrayList(integers, materialAreaIds, decorationAreaIds, coffeeAreaIds, workAreaIds, mezzanineIds, finishedAreaIds, cupUtensilsIds);
        Collections.sort(integers);
        Log.i(TAG, "===================");
        String listString = "";
        for (int i : integers)
        {
            listString += i + " ,";
        }
        Log.i(TAG, "" + listString);

        ArrayList<PreExercisePAItem> paItems = new ArrayList<>();
        String SQL_Format = SQL;
        for(int i = 0; i < integers.size(); i ++) {
            if(i == 0) {
                SQL_Format += " AND (A.id=" + integers.get(i);
            } else if (i == integers.size() - 1) {
                SQL_Format += " OR A.id=" + integers.get(i) + ") ORDER BY A.id ASC";
            } else {
                SQL_Format += " OR A.id=" + integers.get(i);
            }
        }
        Log.i(TAG, "SQL_Format:" + SQL_Format);
        Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(SQL_Format,null);
        if(c.getCount() > 0) {
            c.moveToFirst();
            for(int i = 0 ; i < c.getCount(); i ++) {
                paItems.add(new PreExercisePAItem(c.getInt(0), c.getInt(1), c.getString(2),
                        c.getString(3), c.getString(4), c.getString(5), c.getString(6)));
                c.moveToNext();
            }
        }

        // 全部設定為正確答案
        for(PreExercisePAItem p : paItems) {
            p.setIsCorrectAns(true);
        }

        return paItems;
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
        checkMessage = (TextView) findViewById(R.id.check_message_tv);
        backDetailBtn = (Button) findViewById(R.id.back_detail_btn);
        nextBtn = (Button) findViewById(R.id.result_btn);
        backDetailBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        checkResultGridview = (GridView) findViewById(R.id.checkresult_gridview);
        adapter = new PreExerciseAdapter(getApplicationContext());
        checkResultGridview.setAdapter(adapter);
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
            case R.id.back_detail_btn:
                Log.e(TAG, "回公共材料區重選");

                onBackPressed();
                break;
            case R.id.result_btn:
                Log.e(TAG, "下一步");
                if(tgGroupID.equals("random")) {
                    MainApp.editNeedPEPAList(newItems);
                } else if(tgGroupID.equals("mf_topic")) {
                    MainApp.editNeedPEPAList(tgGroupID, dID, newItems);
                } else {
                    MainApp.editNeedPEPAList(tgGroupID, orderID, newItems);
                }
                intent = new Intent(PreExerciseDetailCheckActivity.this, PreExercisePlaceActivity.class);
                bundle = new Bundle();
                bundle.putString("tgGroupID", tgGroupID);
                if (!tgGroupID.equals("random") && !tgGroupID.equals("mf_topic")) {
                    bundle.putInt("orderID", orderID);
                } else if(tgGroupID.equals("mf_topic")) {
                    bundle.putInt("dID", dID);
                }
                intent.putExtras(bundle);
                startActivity(intent);

                //
                finish();
                break;
        }
    }

    private boolean checkPEPAItemSame(ArrayList<PreExercisePAItem> arrayList1,ArrayList<PreExercisePAItem> arrayList2) {
        // 如果大小不一樣直接return false
        if(arrayList1.size() != arrayList2.size()) {
            return false;
        }
        // 計算數量
        int count = 0;
        for(int i =0;i<arrayList1.size();i++) {
            // 如果id相同 +1
            if(arrayList1.get(i).getId() == arrayList2.get(i).getId()) {
                count ++;
            }
        }
        // 如果數量=陣列數量 return true
        if(count == arrayList1.size()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PreExerciseDetailCheckActivity.this, PreExerciseDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("tgGroupID", tgGroupID);
        if (!tgGroupID.equals("random") && !tgGroupID.equals("mf_topic")) {
            bundle.putInt("orderID", orderID);
        } else if(tgGroupID.equals("mf_topic")) {
            bundle.putInt("dID", dID);
        }
        intent.putExtras(bundle);
        startActivity(intent);

        //
        finish();
    }
}

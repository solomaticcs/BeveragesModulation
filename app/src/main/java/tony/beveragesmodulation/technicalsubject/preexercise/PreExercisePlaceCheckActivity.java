package tony.beveragesmodulation.technicalsubject.preexercise;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;

public class PreExercisePlaceCheckActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "PreExercisePlaceCheckActivity";
    private Toolbar toolbar;

    private TextView itemTitle, checkMessage;
    private Button backPlaceBtn;
    private GridView checkResultGridview;
    private PreExerciseAdapter adapter;

    private String tgGroupID;
    private int orderID, dID;

    private PreExerciseItem preExerciseItem;

    private ArrayList<PreExercisePAItem> placeItems;
    private RelativeLayout resultLayout;
    private TextView resultTitleTv;
    private Button resultKnowBtn;
    private TextView resultUserNameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preexercise_place_check);

        setTitle("前置操作練習 - 檢查材料擺放");

        Bundle bundle = getIntent().getExtras();
        tgGroupID = bundle.getString("tgGroupID");
        if(tgGroupID.equals("random")) {
            preExerciseItem = MainApp.getPreExerciseItem();
            placeItems = MainApp.getPlaceList();
        } else if(tgGroupID.equals("mf_topic")) {
            dID = bundle.getInt("dID");
            preExerciseItem = MainApp.getPreExerciseItem(tgGroupID, dID);
            placeItems = MainApp.getPlaceList(tgGroupID, dID);
        } else {
            orderID = bundle.getInt("orderID");
            preExerciseItem = MainApp.getPreExerciseItem(tgGroupID, orderID);
            placeItems = MainApp.getPlaceList(tgGroupID,orderID);
        }

        initActionBar();
        initView();
        setListView();
        setLayout();
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
        backPlaceBtn = (Button) findViewById(R.id.back_place_btn);
        backPlaceBtn.setOnClickListener(this);
        checkResultGridview = (GridView) findViewById(R.id.checkresult_gridview);
        adapter = new PreExerciseAdapter(getApplicationContext());
        checkResultGridview.setAdapter(adapter);

        // Result
        resultLayout = (RelativeLayout) findViewById(R.id.result_layout);
        resultTitleTv = (TextView) findViewById(R.id.result_title);
        resultKnowBtn = (Button) findViewById(R.id.result_know_btn);
        resultUserNameTv = (TextView) findViewById(R.id.result_username);
    }

    private void setListView() {
        // 取得每個位置包含的材料ID
        int[] materialAreaIds = PreExerciseUtil.getAreaIds(preExerciseItem.getMaterialAreaId());
        int[] decorationAreaIds = PreExerciseUtil.getAreaIds(preExerciseItem.getDecorationAreaId());
        int[] coffeeAreaIds = PreExerciseUtil.getAreaIds(preExerciseItem.getCoffeeAreaId());
        int[] workAreaIds = PreExerciseUtil.getAreaIds(preExerciseItem.getWorkAreaId());
        int[] mezzanineIds = PreExerciseUtil.getAreaIds(preExerciseItem.getMezzanineId());
        int[] finishedAreaIds = PreExerciseUtil.getAreaIds(preExerciseItem.getFinishedAreaId());
        int[] cupUtensilsIds = PreExerciseUtil.getAreaIds(preExerciseItem.getCupUtensilsId());

        // 檢查每個材料是否放置於正確位置
        for(int i = 0; i < placeItems.size();i ++) {
            PreExercisePAItem item = placeItems.get(i);
            boolean b = false; // 用來檢查ID是否存在於陣列中
            switch(item.getPlaceAreaId()) {
                case 0:
                    b = checkArrayContainId(materialAreaIds, item.getId());
                    break;
                case 1:
                    b = checkArrayContainId(decorationAreaIds, item.getId());
                    break;
                case 2:
                    b = checkArrayContainId(coffeeAreaIds, item.getId());
                    break;
                case 3:
                    b = checkArrayContainId(workAreaIds, item.getId());
                    break;
                case 4:
                    b = checkArrayContainId(mezzanineIds, item.getId());
                    break;
                case 5:
                    b = checkArrayContainId(finishedAreaIds, item.getId());
                    break;
                case 6:
                    b = checkArrayContainId(cupUtensilsIds, item.getId());
                    break;
            }

            StringBuilder sb = new StringBuilder();
            ArrayList<String> correctPlaceAreaName = getCorrectPlaceAreaName(materialAreaIds, decorationAreaIds, coffeeAreaIds, workAreaIds, mezzanineIds, finishedAreaIds, cupUtensilsIds, item.getId());
            for(int j = 0;j < correctPlaceAreaName.size();j++) {
                sb.append(correctPlaceAreaName.get(j)).append("\t");
            }
            Log.i(TAG, "" + item.getId() + "." + item.getItemName() + " : " + sb.toString());

            item.setIsCorrectPlaceArea(b);
        }

        adapter.updatePreExercisePAItems(placeItems, false, true);
    }

    private ArrayList<String> getCorrectPlaceAreaName(int[] materialAreaIds, int[] decorationAreaIds, int[] coffeeAreaIds, int[] workAreaIds, int[] mezzanineIds, int[] finishedAreaIds, int[] cupUtensilsIds, int itemId) {
        ArrayList<String> arrayList = new ArrayList<>();
        if(checkArrayContainId(materialAreaIds, itemId)) {
            arrayList.add("材料區");
        }
        if(checkArrayContainId(decorationAreaIds, itemId)) {
            arrayList.add("裝飾物區");
        }
        if(checkArrayContainId(coffeeAreaIds, itemId)) {
            arrayList.add("義式咖啡機");
        }
        if(checkArrayContainId(workAreaIds, itemId)) {
            arrayList.add("工作區");
        }
        if(checkArrayContainId(mezzanineIds, itemId)) {
            arrayList.add("夾層");
        }
        if(checkArrayContainId(finishedAreaIds, itemId)) {
            arrayList.add("成品區");
        }
        if(checkArrayContainId(cupUtensilsIds, itemId)) {
            arrayList.add("杯皿區");
        }
        return arrayList;
    }

    private void setLayout() {
        itemTitle.setText(preExerciseItem.getName());

        // 放置正確數量
        int userCorrectCount = 0;
        // 放置錯誤數量
        int userWrongCount = 0;
        for(int i = 0; i < placeItems.size(); i ++) {
            if(placeItems.get(i).isCorrectPlaceArea()) {
                userCorrectCount++;
            } else {
                userWrongCount++;
            }
        }

        // 設定檢查結果訊息
        checkMessage.setText(String.format(getString(R.string.pe_place_check_message), userCorrectCount, userWrongCount));

        // 設定button
        if(userCorrectCount == placeItems.size()) {
            backPlaceBtn.setEnabled(false);

            Toast.makeText(getApplicationContext(), "恭喜全部答對！", Toast.LENGTH_SHORT).show();
            // 取得通過杯數
            int level = MainApp.getPeGroupStatusField(tgGroupID);
            // 如果通過杯數小於orderID才更新過關狀態
            if(level < orderID) {
                // 儲存該群組通過杯數
                MainApp.editPeGroupStatusField(tgGroupID, orderID);
            }

            if(tgGroupID.equals("random")) {
                PreExerciseUtil.clearItemList();
            } else if(tgGroupID.equals("mf_topic")) {
                PreExerciseUtil.clearItemList(tgGroupID, dID);
            } else {
                PreExerciseUtil.clearItemList(tgGroupID, orderID);
            }

            // result layout and views
            resultLayout.setVisibility(View.VISIBLE);
            resultUserNameTv.setText(MainApp.getUserNameField());
            resultTitleTv.setText(Html.fromHtml(String.format(getString(R.string.pe_result_congratulation), preExerciseItem.getName())));
            resultKnowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // close result layout
                    resultLayout.setVisibility(View.GONE);
                    // 跳回群組頁面
                    finish();
                }
            });

        } else {
            backPlaceBtn.setEnabled(true);
        }
    }

    private boolean checkArrayContainId(int[] ids,int id) {
        for(int j =0;j<ids.length;j++) {
            if(id == ids[j]) {
                return true;
            }
        }
        return false;
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
        Intent intent = new Intent(PreExercisePlaceCheckActivity.this, PreExercisePlaceActivity.class);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_place_btn:
                Log.e(TAG, "回材料放置區重選");

                onBackPressed();
                break;
        }
    }
}

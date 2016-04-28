package tony.beveragesmodulation.technicalsubject.preexercise;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;
import tony.beveragesmodulation.view.HorizontalListView;

/**
 * 前置操作練習 - 放置位置
 */
public class PreExercisePlaceActivity extends AppCompatActivity {
    private static final String TAG = "PreExercisePlaceActivity";
    private Toolbar toolbar;

    private String tgGroupID;
    private int orderID, dID;

    private TextView itemName, emptyTv, emptyTv2, resultTitleTv, resultUserNameTv;
    private TextView coffeeAreaTv, materialAreaTv, finalAreaTv, decorationAreaTv, workAreaTv, mezzanineTv, cupareaTv;

    private HorizontalListView hListView,hListView2;
    private PreExerciseAdapter adapter, adapter2;

    private Button buttonSpinner, submitBtn, resultKnowBtn;

    private RelativeLayout resultLayout;

    private ArrayList<PreExercisePAItem> needItems;

    // 飲調項目的材料擺放
    private PreExerciseItem preExerciseItem = null;

    private static final int ALL_ITEM = -2;
    private static final int NO_CHOICE_PLACE = -1;

    private int buttonSpinnerChoiceGroupId = ALL_ITEM; // -2: 所有 0: 材料區 1: 裝飾物區 2:義式咖啡機 3:工作區 4:夾層 5:成品區 6:器皿區
    private String[] buttonSpinnerItemName = {"全部","材料區","裝飾物區","義式咖啡機","工作區","夾層","成品區","器皿區"};
    private int[] buttonSpinnerIds = {ALL_ITEM, 0, 1, 2, 3, 4, 5, 6};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preexercise_place);

        setTitle("前置操作練習 - 材料擺放");

        Bundle bundle = getIntent().getExtras();
        tgGroupID = bundle.getString("tgGroupID");
        if(tgGroupID.equals("random")) {
            preExerciseItem = MainApp.getPreExerciseItem();
            needItems = MainApp.getPlaceList();
            if(needItems.size() == 0) {
                needItems = MainApp.getNeedPEPAList();
            }
        } else if(tgGroupID.equals("mf_topic")) {
            dID = bundle.getInt("dID");
            preExerciseItem = MainApp.getPreExerciseItem(tgGroupID, dID);
            needItems = MainApp.getPlaceList(tgGroupID, dID);
            if(needItems.size() == 0) {
                needItems = MainApp.getNeedPEPAList(tgGroupID, dID);
            }
        } else {
            orderID = bundle.getInt("orderID");
            preExerciseItem = MainApp.getPreExerciseItem(tgGroupID, orderID);
            needItems = MainApp.getPlaceList(tgGroupID,orderID);
            if(needItems.size() == 0) {
                needItems = MainApp.getNeedPEPAList(tgGroupID, orderID);
            }
        }

        Log.i(TAG, "needItems size: " + needItems.size());
        Log.i(TAG, preExerciseItem.toString());

        initActionBar();
        initView();
        updateView();
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
        itemName = (TextView) findViewById(R.id.item_title);

        // 中間各區域
        coffeeAreaTv = (TextView) findViewById(R.id.coffeearea_tv);
        materialAreaTv = (TextView) findViewById(R.id.materialarea_tv);
        finalAreaTv = (TextView) findViewById(R.id.finalarea_tv);
        decorationAreaTv = (TextView) findViewById(R.id.decorationarea_tv);
        workAreaTv = (TextView) findViewById(R.id.workarea_tv);
        mezzanineTv = (TextView) findViewById(R.id.mezzanine_tv);
        cupareaTv = (TextView) findViewById(R.id.cuparea_tv);

        submitBtn = (Button) findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(submitBtnCLK);

//        // 取得通過杯數
//        int passCupNum = MainApp.getPeGroupStatusField(tgGroupID);
//        if(passCupNum >= orderID) {
//            submitBtn.setEnabled(false);
//        }

        emptyTv = (TextView)findViewById(R.id.empty_tv);
        hListView = (HorizontalListView)findViewById(R.id.horizon_listview);
        adapter = new PreExerciseAdapter(getApplicationContext());
        hListView.setAdapter(adapter);
        hListView.setOnItemClickListener(new ListViewItemClick(adapter));
        if(adapter.getCount() == 0) {
            emptyTv.setVisibility(View.VISIBLE);
        } else {
            emptyTv.setVisibility(View.GONE);
        }

        emptyTv2 = (TextView)findViewById(R.id.empty_tv2);
        hListView2 = (HorizontalListView)findViewById(R.id.horizon_listview2);
        adapter2 = new PreExerciseAdapter(getApplicationContext());
        hListView2.setAdapter(adapter2);
        hListView2.setOnItemClickListener(new ListViewItemClick(adapter2));
        if(adapter2.getCount() == 0) {
            emptyTv2.setVisibility(View.VISIBLE);
        } else {
            emptyTv2.setVisibility(View.GONE);
        }

        // 按鈕下拉式選單
        buttonSpinner = (Button) findViewById(R.id.button_spinner);
        buttonSpinner.setText(buttonSpinnerItemName[0]);
        buttonSpinner.setOnClickListener(btnSpCLK);

        // 設定項目名稱
        itemName.setText(preExerciseItem.getName());

        // Result
        resultLayout = (RelativeLayout) findViewById(R.id.result_layout);
        resultTitleTv = (TextView) findViewById(R.id.result_title);
        resultKnowBtn = (Button) findViewById(R.id.result_know_btn);
        resultUserNameTv = (TextView) findViewById(R.id.result_username);
    }

    private ArrayList<PreExercisePAItem> getChoicePlaceItem(ArrayList<PreExercisePAItem> paItems, boolean b, int pos) {
        final int ALL_ITEM = -2;
        final int NO_CHOICE_PLACE = -1;

        ArrayList<PreExercisePAItem> arrayList = new ArrayList<>();

        for(int i=0;i<paItems.size();i++) {
            PreExercisePAItem paItem = paItems.get(i);
            // 如果是true，而且放置區域ID不等於-1，表示有選擇放置區域
            if(b && paItem.getPlaceAreaId() != NO_CHOICE_PLACE) {
                // 如果是-2，就是選取所有的項目
                // 或 選取區域代號
                if(pos == ALL_ITEM || paItem.getPlaceAreaId() == pos) {
                    arrayList.add(paItem);
                }
            } else if(!b && paItem.getPlaceAreaId() == NO_CHOICE_PLACE) { // 如果是false，而且沒有選擇放置區域
                arrayList.add(paItem);
            }
        }

        return arrayList;
    }

    private HashMap<Integer, Integer> getEachAreaItemLength(ArrayList<PreExercisePAItem> paItems) {
        // 0: 材料區 1: 裝飾物區 2:義式咖啡機 3:工作區 4:夾層 5:成品區 6:器皿區
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        hashMap.put(0, 0);
        hashMap.put(1, 0);
        hashMap.put(2, 0);
        hashMap.put(3, 0);
        hashMap.put(4, 0);
        hashMap.put(5, 0);
        hashMap.put(6, 0);
        for(int i=0;i<paItems.size();i++) {
            int key = paItems.get(i).getPlaceAreaId();
            if(key >=0 && key <=6) {
                hashMap.put(key, hashMap.get(key) + 1);
            }
        }
        return hashMap;
    }

    private Button.OnClickListener submitBtnCLK = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e(TAG, "Submit");

            editPlaceList();

            Intent intent = new Intent(PreExercisePlaceActivity.this, PreExercisePlaceCheckActivity.class);
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

            /**
            // 取得每個位置包含的材料ID
            int[] materialAreaIds = PreExerciseUtil.getAreaIds(preExerciseItem.getMaterialAreaId());
            int[] decorationAreaIds = PreExerciseUtil.getAreaIds(preExerciseItem.getDecorationAreaId());
            int[] coffeeAreaIds = PreExerciseUtil.getAreaIds(preExerciseItem.getCoffeeAreaId());
            int[] workAreaIds = PreExerciseUtil.getAreaIds(preExerciseItem.getWorkAreaId());
            int[] mezzanineIds = PreExerciseUtil.getAreaIds(preExerciseItem.getMezzanineId());
            int[] finishedAreaIds = PreExerciseUtil.getAreaIds(preExerciseItem.getFinishedAreaId());
            int[] cupUtensilsIds = PreExerciseUtil.getAreaIds(preExerciseItem.getCupUtensilsId());

            // 檢查每個材料是否放置於正確位置
            int count = 0;
            for(int i = 0; i < needItems.size();i ++) {
                PreExercisePAItem item = needItems.get(i);
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
                if(b) {
                    count ++;
                }
                item.setIsCorrectPlaceArea(b);
            }

            // 如果全部答對
            if(count == needItems.size()) {
                Toast.makeText(getApplicationContext(), "恭喜全部答對！",Toast.LENGTH_SHORT).show();
                // 取得通過杯數
                int level = MainApp.getPeGroupStatusField(tgGroupID);
                // 如果通過杯數小於orderID才更新過關狀態
                if(level < orderID) {
                    // 儲存該群組通過杯數
                    MainApp.editPeGroupStatusField(tgGroupID, orderID);
                }

                editPlaceList();

                // 按鈕不能按
                submitBtn.setEnabled(false);
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
                Toast.makeText(getApplicationContext(), "沒有全對唷！", Toast.LENGTH_SHORT).show();
            }
             **/

        } //--onClick---END----
    };

    private boolean checkArrayContainId(int[] ids,int id) {
        for(int j =0;j<ids.length;j++) {
            if(id == ids[j]) {
                return true;
            }
        }
        return false;
    }

    private Button.OnClickListener btnSpCLK = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 開啟AlertDialog 選擇Spinner
            LayoutInflater li = LayoutInflater.from(getApplicationContext());
            View promptsView = li.inflate(R.layout.dialog_layout, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PreExercisePlaceActivity.this);
            alertDialogBuilder.setView(promptsView);

            // set dialog message
            alertDialogBuilder.setTitle("選擇類別");

            // create alert dialog
            final AlertDialog alertDialog = alertDialogBuilder.create();
            final Spinner dialogSpinner = (Spinner) promptsView
                    .findViewById(R.id.mySpinner);

            final ArrayAdapter<String> spinnerAdapter = getSpinnerAdapter(buttonSpinnerItemName);
            dialogSpinner.setAdapter(spinnerAdapter);

            int defaultPos = 0;
            for(int i =0;i<buttonSpinnerIds.length;i++) {
                if(buttonSpinnerChoiceGroupId == buttonSpinnerIds[i]) {
                    Log.i(TAG, "buttonSpinnerChoiceGroupId:" + buttonSpinnerChoiceGroupId + " buttonSpinnerIds[" + i + "]: " + buttonSpinnerIds[i]);
                    defaultPos = i;
                    break;
                }
            }
            dialogSpinner.setSelection(defaultPos);

            final Button mButton = (Button) promptsView.findViewById(R.id.submitBtn);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    int pos = dialogSpinner.getSelectedItemPosition();
                    buttonSpinnerChoiceGroupId = buttonSpinnerIds[pos];
                    buttonSpinner.setText(buttonSpinnerItemName[pos]);
                    updateView();
                }
            });

            // show it
            alertDialog.show();
            alertDialog.setCanceledOnTouchOutside(true);
        }
    };

    /**
     *  更新 TextView 與 ListView
     */
    private void updateView() {
        /**
         * TextView
         */
        // 0: 材料區 1: 裝飾物區 2:義式咖啡機 3:工作區 4:夾層 5:成品區 6:器皿區
        HashMap<Integer,Integer> hashMap = getEachAreaItemLength(needItems);
        for(int i = 0 ; i < hashMap.size();i++) {
            Log.i(TAG, "" + hashMap.get(i).toString());
        }
        materialAreaTv.setText("材料區 (" + hashMap.get(0) + ")");
        decorationAreaTv.setText("裝飾物區 (" + hashMap.get(1) + ")");
        coffeeAreaTv.setText("義\n式\n咖\n啡\n機\n(" + hashMap.get(2) + ")");
        workAreaTv.setText("工作區 (" + hashMap.get(3) + ")");
        mezzanineTv.setText("夾層 (" + hashMap.get(4) + ")");
        finalAreaTv.setText("成品區 (" + hashMap.get(5) + ")");
        cupareaTv.setText("器皿區 (" + hashMap.get(6) + ")");

        /**
         * ListView
         */
        adapter.updatePreExercisePAItems(needItems, false, false);
//        adapter.updatePreExercisePAItems(getChoicePlaceItem(needItems, false, buttonSpinnerChoiceGroupId), false);
        if(adapter.getCount() == 0) {
            emptyTv.setVisibility(View.VISIBLE);
        } else {
            emptyTv.setVisibility(View.GONE);
        }
        adapter2.updatePreExercisePAItems(getChoicePlaceItem(needItems, true, buttonSpinnerChoiceGroupId), false, false);
        if(adapter2.getCount() == 0) {
            emptyTv2.setVisibility(View.VISIBLE);
        } else {
            emptyTv2.setVisibility(View.GONE);
        }
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

    class ListViewItemClick implements AdapterView.OnItemClickListener{

        private PreExerciseAdapter listViewAdapter;

        public ListViewItemClick(PreExerciseAdapter listViewAdapter) {
            this.listViewAdapter = listViewAdapter;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LayoutInflater li = LayoutInflater.from(getApplicationContext());
            View promptsView = li.inflate(R.layout.dialog_layout, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PreExercisePlaceActivity.this);
            alertDialogBuilder.setView(promptsView);

            // set dialog message
            alertDialogBuilder.setTitle("選擇放置位置");

            // create alert dialog
            final AlertDialog alertDialog = alertDialogBuilder.create();
            final Spinner dialogSpinner = (Spinner) promptsView
                    .findViewById(R.id.mySpinner);
            String[] mItems = getResources().getStringArray(R.array.pe_sp_listname) ;
            ArrayAdapter<String> spinnerAdapter = getSpinnerAdapter(mItems);
            dialogSpinner.setAdapter(spinnerAdapter);

            // 取得ITEM
            final PreExercisePAItem preExercisePAItem = (PreExercisePAItem) listViewAdapter.getItem(position);
            // 選擇預設位置
            if(preExercisePAItem.getPlaceAreaId() == NO_CHOICE_PLACE) {
                dialogSpinner.setSelection(0);
            } else {
                dialogSpinner.setSelection(preExercisePAItem.getPlaceAreaId());
            }

            final Button mButton = (Button) promptsView.findViewById(R.id.submitBtn);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "submit");
                    // 關閉alertDialog
                    alertDialog.dismiss();
                    // 下拉式選擇的取得position
                    int placeAreaPosition = dialogSpinner.getSelectedItemPosition();
                    // 紀錄選擇的項目
                    preExercisePAItem.setPlaceAreaId(placeAreaPosition);
                    // 更新兩個ListView
                    updateView();
                }
            });

            // show it
            alertDialog.show();
            alertDialog.setCanceledOnTouchOutside(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        editPlaceList();
    }

    private void editPlaceList() {
        if(tgGroupID.equals("random")) {
            MainApp.editPlaceList(needItems);
        } else if(tgGroupID.equals("mf_topic")) {
            MainApp.editPlaceList(tgGroupID, dID, needItems);
        } else {
            MainApp.editPlaceList(tgGroupID, orderID, needItems);
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
        Intent intent = new Intent(PreExercisePlaceActivity.this, PreExerciseDetailCheckActivity.class);
        Bundle bundle = new Bundle();
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
    }

    private ArrayAdapter<String> getSpinnerAdapter(String[] items) {
        return new ArrayAdapter<String>(PreExercisePlaceActivity.this,
                android.R.layout.simple_spinner_dropdown_item, items){

            @Override
            public View getDropDownView(int position, View convertView,ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView)view.findViewById(android.R.id.text1);
                text.setTextColor(getResources().getColor(R.color.spinner_textcolor));
                text.setTypeface(null, Typeface.BOLD);
                return view;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView)view.findViewById(android.R.id.text1);
                text.setTextColor(getResources().getColor(R.color.spinner_textcolor));
                text.setTypeface(null, Typeface.BOLD);
                return view;
            }
        };
    }
}

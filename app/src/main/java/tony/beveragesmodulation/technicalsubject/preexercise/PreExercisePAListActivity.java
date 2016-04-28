package tony.beveragesmodulation.technicalsubject.preexercise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;
import tony.beveragesmodulation.utils.ImgUtil;

/**
 * 前置操作練習-材料選擇頁面
 */
public class PreExercisePAListActivity extends AppCompatActivity {
    private static final String TAG = "PreExercisePAGroupActivity";
    private Toolbar toolbar;
    private int orderID,amGroupID, dID;
    private String tgGroupID, name;
    private GridView paListGridview;
    private GridViewAdapter gridViewAdapter;
    private ArrayList<PreExercisePAItem> preExercisePAItemArrayList = new ArrayList<>();

    private static final String SQL = "SELECT A.id,A.am_groupid,B.am_groupname,A.item_name,A.use_function,A.applicable_topic_description,A.img_name " +
            "FROM `appliance_material` AS A,`appliance_material_group` AS B " +
            "WHERE A.am_groupid = B.am_groupid AND A.am_groupid = %1$d ";

    private TextView timeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preexercise_pa_list);

        // 取得類別編號與名稱
        Bundle bundle = getIntent().getExtras();
        tgGroupID = bundle.getString("tgGroupID");
        amGroupID = bundle.getInt("amGroupID");
        name = bundle.getString("name");

        // 設定標題
        setTitle("公共材料區 - " + name);

        // 取得所有項目(是否被點選)
        if(tgGroupID.equals("random")) {
            preExercisePAItemArrayList = MainApp.getPEPAArrayList(amGroupID);
        } else if(tgGroupID.equals("mf_topic")) {
            dID = bundle.getInt("dID");
            preExercisePAItemArrayList = MainApp.getPEPAArrayList(tgGroupID,dID,amGroupID);
        } else {
            orderID = bundle.getInt("orderID");
            preExercisePAItemArrayList = MainApp.getPEPAArrayList(tgGroupID,orderID,amGroupID);
        }

        Log.i(TAG, "preExercisePAItemArrayList size:" + preExercisePAItemArrayList.size());

        // 如果沒有紀錄，就從資料庫取得
        if(preExercisePAItemArrayList.size() == 0) {
            // 取得該類別的所有item
            String SQL_Format = String.format(SQL, amGroupID);
            Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(SQL_Format, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                for (int i = 0; i < c.getCount(); i++) {
                    preExercisePAItemArrayList.add(new PreExercisePAItem(c.getInt(0), c.getInt(1), c.getString(2),
                            c.getString(3), c.getString(4), c.getString(5), c.getString(6)));
                    Log.i(TAG, preExercisePAItemArrayList.get(i).toString());
                    c.moveToNext();
                }
            }
        }

        initActionBar();
        initView();

        /*
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PreExerciseDetailActivity.MY_ACTION);
        registerReceiver(PreExerciseUtil.getMyReceiver(), intentFilter);

        PreExerciseUtil.getMyReceiver().setListener(new PreExerciseServiceListener() {
            @Override
            public void updateTime(long min, long sec) {
                timeTV.setText("剩餘時間：" + min + ":" + sec);
            }
        });
        */
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
        paListGridview = (GridView) findViewById(R.id.pa_list_gridview);
        gridViewAdapter = new GridViewAdapter(this, R.layout.pe_grid_item, preExercisePAItemArrayList);
        paListGridview.setAdapter(gridViewAdapter);
        paListGridview.setOnItemClickListener(gridViewAdapter);
        timeTV = (TextView) findViewById(R.id.time_tv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pe_pa_list_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.submit_btn:
                if(tgGroupID.equals("random")) {
                    MainApp.editPEPAArrayList(amGroupID, preExercisePAItemArrayList);
                } else if(tgGroupID.equals("mf_topic")) {
                    MainApp.editPEPAArrayList(tgGroupID, dID, amGroupID, preExercisePAItemArrayList);
                } else {
                    MainApp.editPEPAArrayList(tgGroupID, orderID, amGroupID, preExercisePAItemArrayList);
                }

                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 確認是否有存在於random list
     */
    private boolean checkPEPARandomItemExist(PreExercisePAItem preExercisePAItem) {
        // 先取得原有的資料
        ArrayList<PreExercisePAItem> preExercisePAItems = MainApp.getPEPAArrayList(amGroupID);
        for(int i = 0; i < preExercisePAItems.size(); i++) {
            // 如果有相同的ID表示有相同物件
            if(preExercisePAItem.getId() == preExercisePAItems.get(i).getId()) {
                return true;
            }
        }
        return false;
    }

    /**
    *  參考：http://javatechig.com/android/android-gridview-example-building-image-gallery-in-android
    */
    class GridViewAdapter extends ArrayAdapter implements AdapterView.OnItemClickListener{

        private Context context;
        private int layoutResourceId;
        private ArrayList data;

        public GridViewAdapter(Context context, int layoutResourceId, ArrayList data) {
            super(context, layoutResourceId, data);
            this.context = context;
            this.layoutResourceId = layoutResourceId;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
                holder = new ViewHolder();
                holder.imageTitle = (TextView) convertView.findViewById(R.id.text);
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            PreExercisePAItem item = (PreExercisePAItem) data.get(position);

            holder.imageTitle.setText(item.getId() + "." + item.getItemName());
            String uri = "drawable/" + item.getImgName();
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Bitmap bitmap = ImgUtil.decodeSampledBitmapFromResource(getResources(), imageResource, 100, 100);
            holder.image.setImageBitmap(bitmap);

            if(item.isChecked()) { //如果有被點選，顯示黑色背景白色文字
                convertView.setBackgroundColor(Color.BLACK);
                holder.imageTitle.setTextColor(Color.WHITE);
            } else { //如果沒被點選，顯示透明背景黑色文字
//                convertView.setBackgroundResource(R.color.alpha_color);
                convertView.setBackgroundColor(Color.TRANSPARENT);
                holder.imageTitle.setTextColor(Color.BLACK);
            }

            return convertView;
        }

        class ViewHolder {
            TextView imageTitle;
            ImageView image;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PreExercisePAItem item = (PreExercisePAItem) data.get(position);
            item.setIsChecked(!item.isChecked());
            Log.e(TAG, "" + item.toString());
            Log.e(TAG, item.getItemName() + "的點選狀態被設定為" + item.isChecked());

            if(item.isChecked()) { //如果有被點選，顯示黑色背景白色文字
                view.setBackgroundColor(Color.BLACK);
                ViewHolder holder = (ViewHolder) view.getTag();
                holder.imageTitle.setTextColor(Color.WHITE);
            } else { //如果沒被點選，顯示透明背景黑色文字
                view.setBackgroundResource(R.color.alpha_color);
                ViewHolder holder = (ViewHolder) view.getTag();
                holder.imageTitle.setTextColor(Color.BLACK);
            }
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
        Intent intent = new Intent(PreExercisePAListActivity.this, PreExercisePAGroupActivity.class);
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

    /*
    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(PreExerciseUtil.getMyReceiver());
        } catch (Exception e) {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(PreExerciseUtil.getMyReceiver());
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(PreExerciseUtil.getMyReceiver());
        } catch (Exception e) {

        }
    }
    */
}

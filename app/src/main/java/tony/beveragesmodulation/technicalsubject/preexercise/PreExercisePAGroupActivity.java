package tony.beveragesmodulation.technicalsubject.preexercise;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;

public class PreExercisePAGroupActivity extends AppCompatActivity {
    private static final String TAG = "PreExercisePAGroupActivity";
    private Toolbar toolbar;

    private ListView paGroupListview;
    private CustomAdapter customAdapter;

    private static final String SQL = "SELECT `am_groupid`,`am_groupname` FROM `appliance_material_group` WHERE `am_groupid` != 10 ORDER BY `am_groupid` ASC ";

    private ArrayList<HashMap<String, Object>> hashMapArrayList = new ArrayList<>();

    private String tgGroupID;
    private int orderID;
    private int dID;
    private TextView timeTV;

    private ArrayList<PreExercisePAItem> preExercisePAItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preexercise_pa_group);

        Bundle bundle = getIntent().getExtras();
        tgGroupID = bundle.getString("tgGroupID");
        if (!tgGroupID.equals("random") && !tgGroupID.equals("mf_topic")) {
            orderID = bundle.getInt("orderID");
        } else if(tgGroupID.equals("mf_topic")) {
            dID = bundle.getInt("dID");
        }

        // 設定標題
        setTitle("公共材料區");

        // 器具類別
        Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(SQL, null);
        if(c.getCount() > 0) {
            c.moveToFirst();
            for(int i=0;i<c.getCount();i++) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", c.getInt(0));
                hashMap.put("name", c.getString(1));
                hashMap.put("count", 0);
                hashMapArrayList.add(hashMap);
                c.moveToNext();
            }
        }
        c.close();

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
        paGroupListview = (ListView) findViewById(R.id.pa_group_listview);
        customAdapter = new CustomAdapter(getApplicationContext(), hashMapArrayList);
        paGroupListview.setAdapter(customAdapter);
        paGroupListview.setOnItemClickListener(customAdapter);
        timeTV = (TextView) findViewById(R.id.time_tv);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 取得器具類別所有item
        for(int i = 1; i <= 9; i++) {
            if(tgGroupID.equals("random")) {
                preExercisePAItems = MainApp.getPEPAArrayList(i);
            } else if(tgGroupID.equals("mf_topic")) {
                preExercisePAItems = MainApp.getPEPAArrayList(tgGroupID, dID, i);
            } else {
                preExercisePAItems = MainApp.getPEPAArrayList(tgGroupID, orderID, i);
            }
            int count = 0;
            for(int j = 0; j < preExercisePAItems.size(); j++) {
                if(preExercisePAItems.get(j).isChecked()) {
                    count++;
                }
            }
            hashMapArrayList.get(i - 1).put("count", count);
        }

        // 印出hashMapArrayList
        for(int i = 0;i<hashMapArrayList.size();i++) {
            Log.i(TAG, hashMapArrayList.get(i).toString());
        }

        customAdapter.updateHashMap(hashMapArrayList);
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
        Intent intent = new Intent(PreExercisePAGroupActivity.this, PreExerciseDetailActivity.class);
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

    class CustomAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
        private LayoutInflater mInflater;
        private ArrayList<HashMap<String, Object>> data;

        public CustomAdapter(Context context, ArrayList<HashMap<String, Object>> hashMapArrayList) {
            this.mInflater = LayoutInflater.from(context);
            this.data = hashMapArrayList;
        }

        public void updateHashMap(ArrayList<HashMap<String, Object>> hashMapArrayList) {
            this.data = hashMapArrayList;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView tv;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, null);
                holder = new ViewHolder();
                holder.tv = (TextView)convertView;
                holder.tv.setTextColor(Color.BLACK);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // name
            String name = (String)data.get(position).get("name");
            // choice item count
            String countStr = "";
            int count = (int) data.get(position).get("count");
            if(count != 0) {
                countStr = "(" + count + ")";
            }
            String str = name + " " + countStr;
            // set text
            holder.tv.setText(str);
            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.e(TAG, "id:" + data.get(position).get("id") + " name:" + data.get(position).get("name"));
            int theId = (int) data.get(position).get("id");
            String theName = (String) data.get(position).get("name");
            Intent intent = new Intent(PreExercisePAGroupActivity.this, PreExercisePAListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("tgGroupID", tgGroupID);
            if (!tgGroupID.equals("random") && !tgGroupID.equals("mf_topic")) {
                bundle.putInt("orderID", orderID);
            } else if(tgGroupID.equals("mf_topic")) {
                bundle.putInt("dID",dID);
            }
            bundle.putInt("amGroupID", theId);
            bundle.putString("name", theName);
            intent.putExtras(bundle);
            startActivity(intent);

            //
            finish();
        }
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

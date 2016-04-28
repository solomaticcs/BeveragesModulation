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

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;

public class PreExerciseMFTopicActivity extends AppCompatActivity {
    private static final String TAG = "PreExerciseMFTopicActivity";
    private Toolbar toolbar;
    private ListView listView;

    private String tgGroupID;
    private int mid;
    private String mfunction;

    private int[] ids;
    private String[] listName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mf_topic);

        Bundle bundle = getIntent().getExtras();
        tgGroupID = bundle.getString("tgGroupID");
        mid = bundle.getInt("mid");
        mfunction = bundle.getString("mfunction");

        setTitle("調製法選擇練習 - " + mfunction);

        Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery("SELECT `applicabletopic_id` FROM modulation WHERE `mid` = " + mid, null);
        if(c.getCount() > 0) {
            c.moveToFirst();
            String[] idsStr = c.getString(0).split(",");
            //SELECT `id`,`name` FROM drink_recipe WHERE `id` = 1 OR `id` = 2
            String SQLStr = "SELECT `id`,`name` FROM drink_recipe WHERE ";
            for(int i = 0;i < idsStr.length; i++) {
                SQLStr += " `id` = " + idsStr[i];
                if(i != idsStr.length -1) { // 如果不是最後一個
                    SQLStr += " OR ";
                }
            }
            Log.i(TAG, "SQLStr:" + SQLStr);

            Cursor c2 = MainApp.getDatabaseDAO().getDB().rawQuery(SQLStr, null);
            if(c2.getCount() > 0) {
                ids = new int[c2.getCount()];
                listName = new String[c2.getCount()];
                c2.moveToFirst();
                for(int i = 0; i< c2.getCount(); i++) {
                    ids[i] = c2.getInt(0);
                    listName[i] = c2.getString(1);
                    c2.moveToNext();
                }
            }
            c2.close();
        }
        c.close();

        initActionBar();
        initView();
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
        listView = (ListView)findViewById(R.id.listView);
        CustomAdapter customAdapter = new CustomAdapter(this, listName);
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PreExerciseMFTopicActivity.this, PreExerciseDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("tgGroupID", tgGroupID);
                bundle.putInt("dID", ids[position]);
                intent.putExtras(bundle);
                startActivity(intent);

                //
                finish();
            }
        });
    }

    class CustomAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private String[] listData;

        public CustomAdapter(Context context, String[] data) {
            this.mInflater = LayoutInflater.from(context);
            this.listData = data;
        }

        @Override
        public int getCount() {
            return listData.length;
        }

        @Override
        public Object getItem(int position) {
            return listData[position];
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
                holder = new ViewHolder();
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1,null);
                holder.tv = (TextView)convertView;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv.setTextColor(Color.BLACK);
            holder.tv.setText(listData[position]);
            return convertView;
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

    @Override
    public void onBackPressed() {
        finish();
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

}
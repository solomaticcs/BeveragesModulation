package tony.beveragesmodulation.technicalsubject.modulationprocess;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;
import tony.beveragesmodulation.view.dragndroplist.DragNDropListView;
import tony.beveragesmodulation.view.dragndroplist.DragNDropSimpleAdapter;

/**
 * 調製流程測驗頁面
 */
public class MPTestActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MPTestActivity";
    private Toolbar toolbar;
    private String SQL = "SELECT `id`,`function_name`,`sample_name`,`process_description` FROM `modulation_process` ORDER BY RANDOM()";

    private TextView titleTv;

    private DragNDropListView dragNDropListView;
    private DragNDropSimpleAdapter adapter;

    private Button submitBtn;

    private ArrayList<MPItem> mpItems = new ArrayList<>();

    private int index = 0;

    private RelativeLayout resultLayout;
    private TextView resultMessageTv;
    private TextView resultBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp_test);

        setTitle("調製流程測驗");

        // 取得調製流程全部資料
        Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(SQL, null);
        if(c.getCount() > 0) {
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                mpItems.add(new MPItem(c.getInt(0), c.getString(1), c.getString(2), c.getString(3)));
                c.moveToNext();
            }
        }
        c.close();

        for(int i = 0; i<mpItems.size();i++) {
            Log.i(TAG, "" + mpItems.get(i).toString());
        }

        initActionBar();
        initView();
        setView();
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
        titleTv = (TextView) findViewById(R.id.title_tv);
        submitBtn = (Button) findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(this);
        dragNDropListView = (DragNDropListView) findViewById(R.id.process_listview);

        resultLayout = (RelativeLayout) findViewById(R.id.result_layout);
        resultMessageTv = (TextView) findViewById(R.id.message_tv);
        resultBtn = (Button) findViewById(R.id.result_btn);
    }

    private void setView() {
        // title
        titleTv.setText(mpItems.get(index).getSampleName());

        // listView
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        ArrayList<HashMap<String, Object>> allProcess = mpItems.get(index).getProcess();

        for(int i = 0;i < allProcess.size(); i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", allProcess.get(i).get("id"));
            map.put("text",allProcess.get(i).get("text"));
            listItem.add(map);
            Log.i(TAG, "" + allProcess.get(i).get("id") + " | " + allProcess.get(i).get("text"));
        }

        long seed = System.nanoTime();
        Collections.shuffle(listItem, new Random(seed));

        adapter = new DragNDropSimpleAdapter(this,
                listItem , R.layout.row, new String[]{"text"},
                new int[]{R.id.text},R.id.handler);

        dragNDropListView.setDragNDropAdapter(adapter);
    }

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
        if(v.equals(submitBtn)) {
            Log.e(TAG, "確定");

            ArrayList<HashMap<String, Object>> allProcess = mpItems.get(index).getProcess();
            int sameCount = 0;
            for (int i = 0; i < dragNDropListView.getCount(); i++) {
                HashMap<String, Object> process = allProcess.get(i);
                HashMap<String, Object> listViewItemHashMap = (HashMap<String, Object>) adapter.getItem(i);
                Log.i(TAG, "" + listViewItemHashMap.get("id") + " | " + listViewItemHashMap.get("text"));
                if ((int) (process.get("id")) == (int) (listViewItemHashMap.get("id"))) {
                    sameCount++;
                }
            }

            // 全對
            if(sameCount == dragNDropListView.getCount()) {
                resultLayout.setVisibility(View.VISIBLE);
                if (index < (mpItems.size() - 1)) {
                    resultMessageTv.setText("恭喜答對了！");
                    resultBtn.setText("下一題");
                    resultBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e(TAG, "下一題");
                            resultLayout.setVisibility(View.GONE);

                            index++;

                            setView();
                        }
                    });
                } else {
                    resultMessageTv.setText("恭喜答對了！\n題目全部作答完成！");
                    resultBtn.setText("回上一頁");
                    resultBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e(TAG, "回上一頁");

                            finish();
                        }
                    });
                }
            } else {
                resultLayout.setVisibility(View.VISIBLE);
                resultMessageTv.setText("答錯囉！");
                resultBtn.setText("重新選擇答案");
                resultBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "重新選擇答案");
                        resultLayout.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

//    class CustomAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{
//        private Context cxt;
//        private LayoutInflater li;
//        private ArrayList<MPItem> data;
//        public CustomAdapter(Context context, ArrayList<MPItem> data) {
//            this.cxt = context;
//            this.li = LayoutInflater.from(context);
//            this.data = data;
//        }
//
//        @Override
//        public int getCount() {
//            return data.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return data.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        private class ViewHolder {
//            TextView title;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder;
//            if(convertView == null) {
//                convertView = li.inflate(android.R.layout.simple_list_item_1, parent, false);
//                holder = new ViewHolder();
//                holder.title = (TextView) convertView;
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//            return convertView;
//        }
//
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//        }
//    }
}

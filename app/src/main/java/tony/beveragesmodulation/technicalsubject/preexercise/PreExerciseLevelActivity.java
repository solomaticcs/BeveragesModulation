package tony.beveragesmodulation.technicalsubject.preexercise;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;

/**
 * 前置操作練習-題組關卡頁面
 */
public class PreExerciseLevelActivity extends AppCompatActivity {
    private static final String TAG = "PreExerciseLevelActivity";
    private Toolbar toolbar;
    private ListView listView;
    private String[] listName = {"第一杯","第二杯","第三杯","第四杯","第五杯","第六杯"};
    private String tgGroupID;
    private CustomAdapter customAdapter;
    private TextView messageTV;
    private Button examBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preexercise_level);

        tgGroupID = getIntent().getExtras().getString("tgGroupID");

        setTitle("前置操作練習 題組：" + tgGroupID);

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
        messageTV = (TextView) findViewById(R.id.message_tv);
        examBtn = (Button) findViewById(R.id.exam_btn);
        examBtn.setVisibility(View.VISIBLE);
        int level = MainApp.getPeGroupStatusField(tgGroupID);
        if(level == -1) { // 如果還沒測驗按鈕顯示：開始測驗
            examBtn.setText(getString(R.string.pe_exam_start));
        } else if(level >= 0) { // 如果已經開始測驗按鈕顯示：重新測驗
            examBtn.setText(getString(R.string.pe_exam_re));
        }
        examBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int level = MainApp.getPeGroupStatusField(tgGroupID);
                String messageStr = "";
                String positiveBtnStr = "";
                if (level == -1) {
                    messageStr = "要開始測驗了嗎？";
                    positiveBtnStr = "開始測驗";
                } else if (level >= 0) {
                    messageStr = "確定要重新開始測驗嗎？";
                    positiveBtnStr = "重新測驗";
                }

                //詢問視窗
                new AlertDialog.Builder(PreExerciseLevelActivity.this)
                        .setTitle("提示訊息")
                        .setMessage(messageStr)
                        .setPositiveButton(positiveBtnStr, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 設定模擬考試為開始測驗 0->通過關數
                                MainApp.editPeGroupStatusField(tgGroupID, 0);
                                // 清空這第一～六杯的材料擺放物件(給予空字串)
                                for (int i = 1; i <= 6; i++) {
                                    MainApp.editPreExerciseItem(tgGroupID, i, "");
                                }
                                // 清空這第一～六杯的從公共財料區選來的材料(給予空字串)
                                for( int i = 1; i <= 6; i++) {
                                    MainApp.editNeedPEPAList(tgGroupID, i, "");
                                    MainApp.editPlaceList(tgGroupID, i, "");
                                    for(int j = 1; j <= 9; j++) { // am_groupid 群組編號 1 ~ 9
                                        MainApp.editPEPAArrayList(tgGroupID, i, j, "");
                                    }
                                }

                                updateListViewStatus();
                                updateOtherView();

                                // 按鈕顯示
                                examBtn.setVisibility(View.VISIBLE);
                                int level = MainApp.getPeGroupStatusField(tgGroupID);
                                if (level == -1) { // 如果還沒測驗按鈕顯示：開始測驗
                                    examBtn.setText(getString(R.string.pe_exam_start));
                                } else if (level >= 0) { // 如果已經開始測驗按鈕顯示：重新測驗
                                    examBtn.setText(getString(R.string.pe_exam_re));
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });
        listView = (ListView) findViewById(R.id.level_listview);

        int passNumberOfCups = MainApp.getPeGroupStatusField(tgGroupID);
        Log.i(TAG, tgGroupID + "的通過杯數：" + passNumberOfCups);
        final HashMap<Integer, Boolean> hashMap = getItemPositionEnable(passNumberOfCups);
        customAdapter = new CustomAdapter(this, listName, hashMap);
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(customAdapter);
    }

    /**
     * get each position enable status
     */
    private HashMap<Integer, Boolean> getItemPositionEnable(int level) {
        Log.i(TAG, "----通過" + level + "杯----");
        HashMap<Integer, Boolean> hashMap = new HashMap<>();
        for(int j = 0;j <= level; j++) {
            Log.i(TAG,"O");
            hashMap.put(j, true);
        }
        for(int j = (level + 1); j < 7; j++) {
            Log.i(TAG,"X");
            hashMap.put(j, false);
        }
        Log.i(TAG, "---------");
        return hashMap;
    }

    /**
     * 更新ListView的狀態，為每個item設置boolean，是否可以點選。
     */
    private void updateListViewStatus() {
        // 重新取得每個位置是否可以點選的狀態並更新
        int passNumberOfCups = MainApp.getPeGroupStatusField(tgGroupID);
        final HashMap<Integer, Boolean> newHashMap = getItemPositionEnable(passNumberOfCups);
        customAdapter.updateHashMap(newHashMap);
    }

    /**
     * 更新其他介面
     */

    private void updateOtherView() {
        int level = MainApp.getPeGroupStatusField(tgGroupID);
        // 如果完成六杯
        if(level == 6) {
            String str = "已經全部過關，可點選重新測驗。";
            SpannableString spanString = new SpannableString(str);
            spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
            spanString.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spanString.length(),0);
            messageTV.setVisibility(View.VISIBLE);
            messageTV.setText(spanString);
        } else {
            messageTV.setVisibility(View.GONE);
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
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListViewStatus();
        updateOtherView();
    }

    class CustomAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{

        private LayoutInflater mInflater;
        private String[] listData;
        private HashMap<Integer, Boolean> hashMap;

        public CustomAdapter(Context context, String[] data, HashMap<Integer, Boolean> hashMap) {
            this.mInflater = LayoutInflater.from(context);
            this.listData = data;
            this.hashMap = hashMap;
        }

        public void updateHashMap(HashMap<Integer, Boolean> hashMap) {
            this.hashMap.clear();
            this.hashMap.putAll(hashMap);
            this.notifyDataSetChanged();
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return hashMap.get(position);
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
            ImageView image;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.mock_listview, null);
                holder = new ViewHolder();
                holder.tv = (TextView) convertView.findViewById(R.id.title);
                holder.image = (ImageView)convertView.findViewById(R.id.image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv.setTextColor(Color.BLACK);
            holder.tv.setText(listData[position]);

            if(!hashMap.get(position)) {
                convertView.setBackgroundColor(Color.GRAY);
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }

            if(position + 1 <= MainApp.getPeGroupStatusField(tgGroupID)) {
                holder.image.setVisibility(View.VISIBLE);
            } else {
                holder.image.setVisibility(View.GONE);
            }

            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.e(TAG, "tgGroupID:" + tgGroupID + "第幾個" + (position + 1));
            Intent intent = new Intent(PreExerciseLevelActivity.this, PreExerciseDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("tgGroupID", tgGroupID);
            bundle.putInt("orderID", (position + 1));
            intent.putExtras(bundle);
            startActivity(intent);

            //
            finish();
        }
    }
}

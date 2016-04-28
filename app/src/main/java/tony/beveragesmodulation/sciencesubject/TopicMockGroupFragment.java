package tony.beveragesmodulation.sciencesubject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
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

public class TopicMockGroupFragment extends Fragment {
    private static final String TAG = "TopicMockGroupFragment";
    private Context cxt;

    public static final int MOCK_GROUP_1 = 0;
    public static final int MOCK_GROUP_2 = 1;
    public static final int MOCK_GROUP_3 = 2;
    public static final int MOCK_GROUP_4 = 3;
    public static final int MOCK_GROUP_5 = 4;
    public static final int MOCK_GROUP_6 = 5;
    public static final int MOCK_GROUP_7 = 6;

    private CustomAdapter customAdapter;
    private TextView messageTV;

    public TopicMockGroupFragment(Context cxt) {
        this.cxt = cxt;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        return initView(inflater, container);
    }

    private View initView(LayoutInflater inflater, ViewGroup container) {
        Log.e(TAG, "initView");
        View view = inflater.inflate(R.layout.fragment_topic_mock_group, container, false);
        messageTV = (TextView) view.findViewById(R.id.tm_message);
        /**
         * 根據通過的關數來enable or disable listView的item
         * 共七關
         * -1: 尚未開始 (all disable)
         * 0: OXXXXXX
         * 1: OOXXXXX
         * 2: OOOXXXX
         * 3: OOOOXXX
         * 4: OOOOOXX
         * 5: OOOOOOX
         * 6: OOOOOOO
         * 7: OOOOOOO
         */
        int level = MainApp.getTgMockStatusField();
        final HashMap<Integer, Boolean> hashMap = getItemPositionEnable(level);

        final String[] listName = getResources().getStringArray(R.array.tm_list_name);
        final ListView listView = (ListView)view.findViewById(R.id.topic_mock_group_listview);
        customAdapter = new CustomAdapter(cxt,listName,hashMap);
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(customAdapter);

        final Button examBtn = (Button) view.findViewById(R.id.exam_btn);
        examBtn.setVisibility(View.VISIBLE);
        if(level == -1) { // 如果還沒測驗按鈕顯示：開始測驗
            examBtn.setText(getString(R.string.tm_exam_start));
        } else if(level >= 0) { // 如果已經開始測驗按鈕顯示：重新測驗
            examBtn.setText(getString(R.string.tm_exam_re));
        }
        examBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int level = MainApp.getTgMockStatusField();
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
                new AlertDialog.Builder(getActivity())
                        .setTitle("提示訊息")
                        .setMessage(messageStr)
                        .setPositiveButton(positiveBtnStr, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 設定模擬考試為開始測驗 0->通過關數
                                MainApp.editTgMockStatusField(0);
                                // 清空這第一～七關的ArrayList物件(給予空字串)
                                for (int i = 0; i < 7; i++) {
                                    MainApp.editTgMockGroupArrayList(i, "");
                                }

                                updateListViewStatus();
                                updateOtherView();

                                // 按鈕顯示
                                examBtn.setVisibility(View.VISIBLE);
                                int level = MainApp.getTgMockStatusField();
                                if (level == -1) { // 如果還沒測驗按鈕顯示：開始測驗
                                    examBtn.setText(getString(R.string.tm_exam_start));
                                } else if (level >= 0) { // 如果已經開始測驗按鈕顯示：重新測驗
                                    examBtn.setText(getString(R.string.tm_exam_re));
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
        return view;
    }

    /**
     * 更新ListView的狀態，為每個item設置boolean，是否可以點選。
     */
    private void updateListViewStatus() {
        // 重新取得每個位置是否可以點選的狀態並更新
        int level = MainApp.getTgMockStatusField();
        final HashMap<Integer, Boolean> newHashMap = getItemPositionEnable(level);
        customAdapter.updateHashMap(newHashMap);
    }

    /**
     * 更新其他View
     * 這裡是更新畫面最下方的TextView
     * 顯示已經全數通過的message
     */
    private void updateOtherView() {
        int level = MainApp.getTgMockStatusField();
        // 如果全數過關
        if(level == 7) {
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
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        updateListViewStatus();
        updateOtherView();
    }

    /**
     * get each position enable status
     */
    private HashMap<Integer, Boolean> getItemPositionEnable(int level) {
        Log.i(TAG,"----通過" + level + "關----");
        HashMap<Integer, Boolean> hashMap = new HashMap<>();
        for(int j = 0;j <= level; j++) {
            Log.i(TAG,"O");
            hashMap.put(j, true);
        }
        for(int j = (level + 1); j < 7; j++) {
            Log.i(TAG,"X");
            hashMap.put(j, false);
        }
        Log.i(TAG,"---------");
        return hashMap;
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

            if(position + 1 <= MainApp.getTgMockStatusField()) {
                holder.image.setVisibility(View.VISIBLE);
            } else {
                holder.image.setVisibility(View.GONE);
            }

            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i(TAG, "name:" + listData[position]);
            Intent intent = new Intent(getActivity(), TopicMockDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("groupMockName", listData[position]);
            bundle.putInt("mockID", position);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}

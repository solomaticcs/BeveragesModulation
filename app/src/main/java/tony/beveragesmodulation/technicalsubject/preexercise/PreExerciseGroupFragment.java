package tony.beveragesmodulation.technicalsubject.preexercise;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;

/**
 * 前置操作練習
 */
public class PreExerciseGroupFragment extends Fragment {
    private static final String TAG = "PreExerciseGroupFragment";
    private Context cxt;

    public PreExerciseGroupFragment(Context context) {
        this.cxt = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        showPreProcessExplanationDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        return initView(inflater, container);
    }

    private View initView(LayoutInflater inflater, ViewGroup container) {
        Log.e(TAG, "initView");
        View view = inflater.inflate(R.layout.fragment_preexercise_group, container, false);
        Button randomTopicBtn = (Button) view.findViewById(R.id.random_topic_btn);
        randomTopicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "隨機出題");
                Intent intent = new Intent(getActivity(), PreExerciseDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("tgGroupID", "random");
                bundle.putString("status", "update");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        Button mfTopicBtn = (Button)view.findViewById(R.id.mf_topic_btn);
        mfTopicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "調製法選擇練習");

                showMFTopicDialog();
            }
        });
        final String[] listName = getResources().getStringArray(R.array.pe_list_name);
        ListView listView = (ListView) view.findViewById(R.id.preexercise_group_listview);
        CustomAdapter customAdapter = new CustomAdapter(cxt,listName);
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, listName[position]);
                Intent intent = new Intent(getActivity(), PreExerciseLevelActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("tgGroupID", listName[position]);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }

    private void showMFTopicDialog() {
        // 開啟AlertDialog 選擇Spinner
        LayoutInflater li = LayoutInflater.from(cxt);
        View promptsView = li.inflate(R.layout.dialog_mf_topic, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        final int[] listId;
        final String[] listName;

        Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery("SELECT `mid`,`mfunction` FROM `modulation`", null);
        if(c.getCount() > 0) {
            listId = new int[c.getCount()];
            listName = new String[c.getCount()];
            c.moveToFirst();
            for(int i = 0;i < c.getCount(); i++) {
                listId[i] = c.getInt(0);
                listName[i] = c.getString(1);
                c.moveToNext();
            }
            ListView listView = (ListView) promptsView.findViewById(R.id.listView);
            CustomAdapter customAdapter = new CustomAdapter(getActivity(), listName);
            listView.setAdapter(customAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), PreExerciseMFTopicActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("tgGroupID", "mf_topic");
                    bundle.putInt("mid", listId[position]);
                    bundle.putString("mfunction", listName[position]);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        // show it
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    private void showPreProcessExplanationDialog() {
        // 開啟AlertDialog 選擇Spinner
        LayoutInflater li = LayoutInflater.from(cxt);
        View promptsView = li.inflate(R.layout.dialog_preprocess_explanation, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);

//        alertDialogBuilder.setTitle("提示訊息");

        final AlertDialog alertDialog = alertDialogBuilder.create();

        TextView content = (TextView) promptsView.findViewById(R.id.content);
        StringBuilder sb = new StringBuilder();
        Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery("SELECT `id`,`process` FROM preprocess", null);
        if(c.getCount() > 0) {
            c.moveToFirst();
            for(int i = 0;i < c.getCount(); i++) {
                sb.append(c.getInt(0) + "." + c.getString(1) + "\n");
                c.moveToNext();
            }
        }
        content.setText(sb.toString());

        Button button = (Button) promptsView.findViewById(R.id.know_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        // show it
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
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
}

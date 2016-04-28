package tony.beveragesmodulation.technicalsubject.publicarea;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import tony.beveragesmodulation.R;

/**
 * 公共材料區
 */
public class PublicAreaFragment extends Fragment implements Button.OnClickListener{
    private static final String TAG = "PublicArea_Fragment";
    private Context cxt;

    private Button leftBtn1,leftBtn2,rightBtn1,rightBtn2,rightBtn3,rightBtn4,rightBtn5,rightBtn6,rightBtn7,rightBtn8;

    public static final String EXTRA_KEY = "mode"; //模式
    public static final int EXAM_APPLIANCE = 1;
    public static final int EXAM_CUP = 2;
    public static final int EXAM_MATERIAL = 3;
    public static final int EXAM_FRESH = 4;
    public static final int EXAM_BURDEN = 5;
    public static final int EXAM_BREW = 6;
    public static final int EXAM_SMOOTHIES = 7;
    public static final int EXAM_EXPRESSO = 8;
    public static final int EXAM_OTHER = 9;
    public static final int EXAM_OPERATOR_STATION = 10;
    public static final int EXAM_COMPLEX = 11;

    public PublicAreaFragment(Context cxt) {
        this.cxt = cxt;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater, container);
    }

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_publicarea, container, false);
        leftBtn1 = (Button)view.findViewById(R.id.left_button1);
        leftBtn2 = (Button)view.findViewById(R.id.left_button2);
        rightBtn1 = (Button)view.findViewById(R.id.right_button1);
        rightBtn2 = (Button)view.findViewById(R.id.right_button2);
        rightBtn3 = (Button)view.findViewById(R.id.right_button3);
        rightBtn4 = (Button)view.findViewById(R.id.right_button4);
        rightBtn5 = (Button)view.findViewById(R.id.right_button5);
        rightBtn6 = (Button)view.findViewById(R.id.right_button6);
        rightBtn7 = (Button)view.findViewById(R.id.right_button7);
        rightBtn8 = (Button)view.findViewById(R.id.right_button8);
        leftBtn1.setOnClickListener(this);
        leftBtn2.setOnClickListener(this);
        rightBtn1.setOnClickListener(this);
        rightBtn2.setOnClickListener(this);
        rightBtn3.setOnClickListener(this);
        rightBtn4.setOnClickListener(this);
        rightBtn5.setOnClickListener(this);
        rightBtn6.setOnClickListener(this);
        rightBtn7.setOnClickListener(this);
        rightBtn8.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(),PublicAreaDetailActivity.class);
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.left_button1: // 其他
                bundle.putInt("groupID",9);
                break;
            case R.id.left_button2: // 操作桌
                bundle.putInt("groupID",10);
                break;
            case R.id.right_button1: // 義式咖啡機
                bundle.putInt("groupID", 8);
                break;
            case R.id.right_button2: // 機具、器具
                bundle.putInt("groupID", 1);
                break;
            case R.id.right_button3: // 杯皿類
                bundle.putInt("groupID", 2);
                break;
            case R.id.right_button4: // 器材
                bundle.putInt("groupID", 3);
                break;
            case R.id.right_button5: // 生鮮類
                bundle.putInt("groupID", 4);
                break;
            case R.id.right_button6: // 配料類
                bundle.putInt("groupID", 5);
                break;
            case R.id.right_button7: // 沖泡類
                bundle.putInt("groupID", 6);
                break;
            case R.id.right_button8: // 材料-糖漿(果露)
                bundle.putInt("groupID", 7);
                break;
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pa_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.test_item:
                // 開啟AlertDialog 選擇Spinner
                LayoutInflater li = LayoutInflater.from(cxt);
                View promptsView = li.inflate(R.layout.dialog_publicarea_exam, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setView(promptsView);

                // set dialog message
                alertDialogBuilder.setTitle("選擇考試類別")
                    .setIcon(R.drawable.exam);

                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();

                ListView listView = (ListView) promptsView.findViewById(R.id.listView);
                String[] items = {"機具、器具測驗","杯皿測驗","器料測驗","生鮮測驗","配料測驗","沖泡測驗","糖漿、果露測驗","義式咖啡機","其他","操作台","綜合測驗"};
                CustomAdapter customAdapter = new CustomAdapter(cxt);
                customAdapter.updateData(items);
                listView.setAdapter(customAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity(), PublicAreaTestActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt(EXTRA_KEY, (position + 1));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                Button closeBtn = (Button)promptsView.findViewById(R.id.close_btn);
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                // show it
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
                break;
        }
        return true;
    }

    class CustomAdapter extends BaseAdapter {

        private Context cxt;
        private LayoutInflater li;
        private String[] data;

        public CustomAdapter(Context context) {
            cxt = context;
            li = LayoutInflater.from(context);
        }

        public void updateData(String[] data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Object getItem(int position) {
            return data[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            TextView text;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if(convertView == null) {
                convertView = li.inflate(android.R.layout.simple_list_item_1,null);
                holder.text = (TextView)convertView;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setTextColor(Color.BLACK);
            holder.text.setText(data[position]);

            return convertView;
        }
    }
}

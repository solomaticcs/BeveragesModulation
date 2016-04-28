package tony.beveragesmodulation.technicalsubject.drinkrecipe;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;

/**
 * 配方測驗
 */
public class DrinkRecipeTestActivity extends AppCompatActivity
        implements Button.OnClickListener{
    private static final String TAG = "DRTestActivity";

    private Toolbar toolbar;
    private GridView gridView;
    private TextView mf_des01, userAnsTv, ansTv;
    private Button submit;
    private GridAdapter adapter;
    private ArrayList<IngredientItem> titleArrayList = new ArrayList<>(); //九個成分選項

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drinkrecipe_test);
        setTitle("配方測驗");
        initActionBar();
        initView();
        setLayoutText();
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
        gridView = (GridView) findViewById(R.id.gridview);
//        gridView.setExpanded(true);
        mf_des01 = (TextView) findViewById(R.id.mf_des01);
        submit = (Button)findViewById(R.id.submit_btn);
        submit.setOnClickListener(this);
        userAnsTv = (TextView) findViewById(R.id.mf_userans);
        ansTv = (TextView) findViewById(R.id.mf_ans);
    }

    private void setLayoutText() {
        // 1.取得該item所有成分，並且加入陣列
        String[] ingredientIds = MainApp.drinkRecipeItem.getIngredientIds().split(",");
        String SQL = "SELECT `iid`,`iname` FROM `ingredient` WHERE ";
        for(int i=0;i<ingredientIds.length;i++) {
            SQL += "iid=" + ingredientIds[i];
            if(i<ingredientIds.length-1) {
                SQL += " OR ";
            } else if(i==ingredientIds.length-1) {
                SQL += " ORDER BY `iid` ASC ";
            }
        }
        Log.i(TAG,"SQL: " + SQL);
        Cursor c = null;
        c = MainApp.getDatabaseDAO().getDB().rawQuery(SQL, null);
        if(c.getCount() > 0) {
            c.moveToFirst();
            for(int i=0;i<c.getCount();i++) {
                titleArrayList.add(new IngredientItem(c.getInt(0), c.getString(1), true));
                c.moveToNext();
            }
        }
        c.close();

        // 2.補齊其他成分 (例如只有6個成分補齊另外3個)
        String SQL2 = "SELECT `iid`,`iname` FROM `ingredient` WHERE ";
        for(int i=0;i<ingredientIds.length;i++) {
            SQL2 += "iid!=" + ingredientIds[i];
            if(i < ingredientIds.length - 1) {
                SQL2 += " AND ";
            } else if (i == ingredientIds.length - 1) {
                SQL2 += " ORDER BY RANDOM() LIMIT " + (9 - ingredientIds.length);
            }
        }
        Log.i(TAG,"SQL2: " + SQL2);
        c = MainApp.getDatabaseDAO().getDB().rawQuery(SQL2, null);
        if(c.getCount() > 0) {
            c.moveToFirst();
            for(int i=0;i<c.getCount();i++) {
                titleArrayList.add(new IngredientItem(c.getInt(0), c.getString(1), false));
                c.moveToNext();
            }
        }

        // 3.洗亂順序
        long seed = System.nanoTime();
        Collections.shuffle(titleArrayList, new Random(seed));

        /*
        gridview adapter
         */
//        //測試資料
//        titleArrayList.add(new IngredientItem(1, "Test1",false));
//        titleArrayList.add(new IngredientItem(2, "Test2",false));
//        titleArrayList.add(new IngredientItem(3, "Test3",false));
//        titleArrayList.add(new IngredientItem(4, "Test4",false));
//        titleArrayList.add(new IngredientItem(5, "Test5",false));
//        titleArrayList.add(new IngredientItem(6, "Test6",true));
//        titleArrayList.add(new IngredientItem(7, "Test7",false));
//        titleArrayList.add(new IngredientItem(8, "Test8",true));
//        titleArrayList.add(new IngredientItem(9, "Test9",true));
        adapter = new GridAdapter(this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(adapter);
        /*
        文字設定
         */
        mf_des01.setText(Html.fromHtml(String.format(getString(R.string.dr_des01_title),
                MainApp.drinkRecipeItem.getName())));
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        switch (v.getId()) {
            case R.id.submit_btn:
                //印出正確答案
                ansTv.setText(getCorrectName(titleArrayList));
                //Toast
                String str = "";
                if(checkCorrect(titleArrayList)) {
                    str = "恭喜您全部答對！";
                } else {
                    str = "答錯了！";
                }
                Toast.makeText(DrinkRecipeTestActivity.this, str, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 確認是否全對
     */
    private boolean checkCorrect(ArrayList<IngredientItem> drTestItems) {
        //取得正確次數
        int correctCount = 0;
        for(int i=0;i<drTestItems.size();i++) {
            if(drTestItems.get(i).getCorrect()) {
                correctCount++;
            }
        }
        //使用者正確次數
        int userCorrectCount = 0;
        for(int i=0;i<drTestItems.size();i++) {
            if(drTestItems.get(i).getCheck()) {
                if(drTestItems.get(i).getCorrect()) {
                    userCorrectCount++;
                }
            }
        }
        if(userCorrectCount==correctCount) {
            return true;
        } else {
            return false;
        }
    }
//    private boolean checkCorrect(ArrayList<IngredientItem> drTestItems) {
//        boolean correct = true; //先假設是正確的
//        for(int i=0;i<drTestItems.size();i++) {
//            if(drTestItems.get(i).getCorrect()) { //找正確答案
//                if(!drTestItems.get(i).getCheck()) { //如果沒有選
//                    correct = false;
//                    break;
//                }
//            }
//        }
//        return correct;
//    }

    /**
     * 取得被選取項目的名稱
     */
    private String getCheckedName(ArrayList<IngredientItem> drTestItems) {
        String str = "";
        for(int i=0;i<drTestItems.size();i++) {
            if(drTestItems.get(i).getCheck()) {
                str += drTestItems.get(i).getName() + " ";
            }
        }
        return str;
    }

    /**
     * 取得正確答案的名稱
     */
    private String getCorrectName(ArrayList<IngredientItem> drTestItems) {
        String str = "";
        for(int i=0;i<drTestItems.size();i++) {
            if(drTestItems.get(i).getCorrect()) {
                str += drTestItems.get(i).getName() + " ";
            }
        }
        return str;
    }

    /**
     * 飲調測驗用的成分Item
     * 放置於Gridview
     * 拿來紀錄id與成分名稱
     * 是否點選、是否為正確答案
     */
    class IngredientItem {
        private int id; //編號
        private String name; //成分名稱
        private boolean isCheck = false; //是否點選
        private boolean isCorrect = false; //是否為正確答案

        public IngredientItem(int id, String name, boolean isCorrect) {
            this.id = id;
            this.name = name;
            this.isCorrect = isCorrect;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setCheck() {
            if(isCheck) {
                isCheck = false;
            } else {
                isCheck = true;
            }
        }

        public boolean getCheck() {
            return isCheck;
        }

        public void setCorrect(boolean isCorrect) {
            this.isCorrect = isCorrect;
        }

        public boolean getCorrect() {
            return isCorrect;
        }
    }

    /**
     * GridAdapter 配置器
     */
    class GridAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{

        private Context context;
        private LayoutInflater li;
        private Color color;

        public GridAdapter(Context context) {
            this.context = context;
            this.li = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return titleArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return titleArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = li.inflate(R.layout.gridview_item, parent, false);

            // 成分名稱
            String name = titleArrayList.get(position).getName();
            TextView textView = (TextView) convertView.findViewById(R.id.tm_text);
            textView.setText(name);

////             設定背景顏色
//            setBackgroundColor(parent.getChildAt(position), position);

            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 設定確認
            titleArrayList.get(position).setCheck();

//            // 設定背景顏色
//            setBackgroundColor(view, position);

            String checkedNames = getCheckedName(titleArrayList);

            if(checkedNames.equals("")) {
                userAnsTv.setText(getResources().getString(R.string.dr_userans_des));
            } else {
                userAnsTv.setText(checkedNames);
            }
        }

        private void setBackgroundColor(View view, int position) {
            // 是否點選
            boolean isCheck = titleArrayList.get(position).getCheck();
            if(isCheck) {
                view.setBackgroundColor(Color.YELLOW);
            } else {
                view.setBackgroundColor(Color.WHITE);
            }
        }
    }
}

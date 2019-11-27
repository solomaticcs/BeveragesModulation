package tony.beveragesmodulation.technicalsubject.modulation;

import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;

/**
 * 調製法詳細頁面
 */
public class ModulationTestActivity extends AppCompatActivity {
    private static final String TAG = "ModulationTestActivity";
    private Toolbar toolbar;
    private Button button1,button2,button3,button4,button5,button6, nextBtn;
    private TextView question, userans, correct;
    private String SQL = "SELECT `id`,`name`,`modulation_id` FROM `drink_recipe` ORDER BY RANDOM() LIMIT 1;";
    private String[] allModulationFunctions = {"直接注入法","電動攪拌法","搖盪法","攪拌法","水果切盤","義式咖啡機"};

    private int id, modulationId;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulation_test);
        // 設定標題
        setTitle("調製測驗");

        initActionBar();
        initView();
        setLayoutText();
    }

    private void setLayoutText() {
        // 隨機抽出一個飲品配方項目的id, name, modulation_id
        Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(SQL, null);
        if(c.getCount() > 0) {
            c.moveToFirst();
            id=c.getInt(0);
            name=c.getString(1);
            modulationId=Integer.valueOf(c.getString(2));
            Log.i(TAG, "id:" + id + " name:" + name + "mid:" + modulationId);
        }
        c.close();
        // 設定問題文字
        question.setText(Html.fromHtml(
                String.format(getString(R.string.mu_question), name)));
        userans.setText("");
        correct.setText("");
        nextBtn.setVisibility(View.GONE);
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
        question = (TextView)findViewById(R.id.question);
        userans = (TextView)findViewById(R.id.userans_tv);
        correct = (TextView)findViewById(R.id.correct);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        button4 = (Button)findViewById(R.id.button4);
        button5 = (Button)findViewById(R.id.button5);
        button6 = (Button)findViewById(R.id.button6);
        nextBtn = (Button)findViewById(R.id.next_btn);
        button1.setOnClickListener(submitCLK);
        button2.setOnClickListener(submitCLK);
        button3.setOnClickListener(submitCLK);
        button4.setOnClickListener(submitCLK);
        button5.setOnClickListener(submitCLK);
        button6.setOnClickListener(submitCLK);
        nextBtn.setOnClickListener(nextBtnCLK);
    }

    private Button.OnClickListener submitCLK = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            int userAnsId = Integer.valueOf(v.getTag().toString());
            if(userAnsId == modulationId) {
                Toast.makeText(getApplicationContext(), "正確！",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "答錯了！",Toast.LENGTH_SHORT).show();
            }

            // 顯示使用者選擇的答案
            userans.setText(allModulationFunctions[userAnsId - 1]);
            // 顯示正確答案
            correct.setText(allModulationFunctions[modulationId - 1]);
            // 顯示下一題的按鈕
            nextBtn.setVisibility(View.VISIBLE);
        }
    };

    private Button.OnClickListener nextBtnCLK = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            setLayoutText();
        }
    };

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
}

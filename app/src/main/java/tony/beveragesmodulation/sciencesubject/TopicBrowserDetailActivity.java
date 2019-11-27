package tony.beveragesmodulation.sciencesubject;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;

/**
 *  題組瀏覽詳細頁面
 */
public class TopicBrowserDetailActivity extends AppCompatActivity {
    private static final String TAG = "TopicBrowserDetailActivity";
    private Toolbar toolbar;
    private String groupName;
    private int groupID;
//    private String SQL = "SELECT `id`,`correctans`,`question`,`ans1`,`ans2`,`ans3`,`ans4` FROM `sciencesubject` WHERE id = %1$d";
    private String SQL = "SELECT `id`,`correctans`,`question`,`ans1`,`ans2`,`ans3`,`ans4` FROM `sciencesubject` WHERE `gid` = %1$d";
    private ArrayList<TopicItem> topicItems = new ArrayList<>();
    private TextView questionTV;
    private TextView[] ansTV = new TextView[4];
    private Button showOrHiddenAnsBtn,prevBtn,nextBtn;
    private int gID, index = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_browser_detail);
        // 取得群組代號
        Bundle bundle = getIntent().getExtras();
        groupName = bundle.getString("groupName");
        groupID = bundle.getInt("groupID");
        // 設定標題
        setTitle(String.format(getString(R.string.tb_title), groupName));
        // 根據群組代號
        gID = groupID + 1;
        String SQL_Fomat = String.format(SQL, gID);
        Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(SQL_Fomat,null);
        if(c.getCount() > 0) {
            c.moveToFirst();
            for(int i = 0 ; i <c.getCount();i++) {
                topicItems.add(new TopicItem(c.getInt(0), c.getInt(1), c.getString(2),
                        c.getString(3), c.getString(4), c.getString(5), c.getString(6)));
                c.moveToNext();
            }
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
        questionTV = (TextView) findViewById(R.id.question);
        ansTV[0] = (TextView)findViewById(R.id.ans1TV);
        ansTV[1] = (TextView)findViewById(R.id.ans2TV);
        ansTV[2] = (TextView)findViewById(R.id.ans3TV);
        ansTV[3] = (TextView)findViewById(R.id.ans4TV);
        prevBtn = (Button) findViewById(R.id.prev_btn);
        nextBtn = (Button) findViewById(R.id.result_btn);
        prevBtn.setOnClickListener(nextprevCLK);
        nextBtn.setOnClickListener(nextprevCLK);
        showOrHiddenAnsBtn = (Button) findViewById(R.id.showhiddenbtn);
        showOrHiddenAnsBtn.setOnClickListener(showOrHiddenAnsCLK);

        setLayoutText();
    }

    private void setLayoutText() {
        questionTV.setText(topicItems.get(index).getId() + "." + topicItems.get(index).getQuestion());
        for(int i=0;i<topicItems.get(index).getAns().length;i++) {
            if(topicItems.get(index).getId() == 62) {
                ansTV[i].setText("(" + (i + 1) + ")");
                String uri = "drawable/" + topicItems.get(index).getAns()[i].split("\\.")[0].toLowerCase();
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                ansTV[i].setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, imageResource, 0);
            } else {
                ansTV[i].setText(topicItems.get(index).getAns()[i]);
                if (oldColors != null) {
                    ansTV[i].setTextColor(oldColors);
                }
                ansTV[i].setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        }
    }

    private Button.OnClickListener nextprevCLK = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            int ID = 0;
            switch (v.getId()) {
                case R.id.result_btn:
                    ID = index + 1;
                    break;
                case R.id.prev_btn:
                    ID = index - 1;
                    break;
            }
            if(ID>=0 && ID<topicItems.size()) {
                index = ID;
                setLayoutText();

            } else {
                errorToastMessage(v);
            }

        }
    };

    private void errorToastMessage(View v) {
        switch (v.getId()) {
            case R.id.result_btn:
                Toast.makeText(getApplicationContext(), "沒有下一個", Toast.LENGTH_SHORT).show();
                break;
            case R.id.prev_btn:
                Toast.makeText(getApplicationContext(), "沒有上一個", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private boolean showAnsStatus = false; // 顯示答案狀態
    ColorStateList oldColors = null;    // 儲存原本的字體顏色
    private Button.OnClickListener showOrHiddenAnsCLK = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            for(int i=0;i<ansTV.length;i++) {
                if(topicItems.get(index).getCorrectAns() == (i+1)) {
                    if (!showAnsStatus) {
                        oldColors = ansTV[i].getTextColors();
                        ansTV[i].setTextColor(Color.RED);
                        showAnsStatus = true;
                    } else {
                        ansTV[i].setTextColor(oldColors);
                        showAnsStatus = false;
                    }
                }
            }
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

package tony.beveragesmodulation.technicalsubject.publicarea;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;
import tony.beveragesmodulation.utils.ImgUtil;

/**
 * 公共材料區測驗
 */
public class PublicAreaTestActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PublicAreaTestActivity";
    private Toolbar toolbar;
    private int groupID; // 哪一個考試模式

    private ImageView img;
    private RadioGroup rg;
    private RadioButton[] ans = new RadioButton[5];
    private Button submitBtn,nextBtn,backBtn;
    private RelativeLayout resultLayout;
    private TextView title,resultTitle, resultMessage, resultUserName;
    private View separateLine;

    private String SQL_GROUP_ALL = "SELECT A.id,A.am_groupid,B.am_groupname,A.item_name,A.use_function,A.applicable_topic_description,A.img_name " +
            " FROM `appliance_material` AS A,`appliance_material_group` AS B " +
            " WHERE A.am_groupid = B.am_groupid AND A.am_groupid = %1$d " +
            " %2$s" +
            " ORDER BY RANDOM() "; // 取出該群組內的所有項目

    private String SQL_ALL = "SELECT A.id,A.am_groupid,B.am_groupname,A.item_name,A.use_function,A.applicable_topic_description,A.img_name " +
            " FROM `appliance_material` AS A,`appliance_material_group` AS B " +
            " WHERE A.am_groupid = B.am_groupid " +
            " ORDER BY A.id "; // 取出全部

    private String SQL_RANDOM = "SELECT A.id,A.am_groupid,B.am_groupname,A.item_name,A.use_function,A.applicable_topic_description,A.img_name " +
            " FROM `appliance_material` AS A,`appliance_material_group` AS B " +
            " WHERE A.am_groupid = B.am_groupid " +
            " ORDER BY RANDOM() LIMIT %1$d "; // 隨機取出N個項目

    private ArrayList<Integer> otherIndexArrayList = new ArrayList<>();

    // 該群組全部項目
    private ArrayList<PublicAreaTestItem> publicAreaItemArrayList = new ArrayList<>();
    // 每次出題尋找用的未重複項目
    private ArrayList<PublicAreaTestItem> noRepeatItemArrayList = new ArrayList<>();
    // 紀錄作答過項目
    private ArrayList<PublicAreaTestItem> recordAnsItemArrayList = new ArrayList<>();
    // 題目
    private PublicAreaTestItem topicItem = null;
    // 其他答案
    private ArrayList<PublicAreaTestItem> otherItemArrayList = new ArrayList<>();
    // 紀錄答題次數
    private int theCount = 0;
    // 累積分數 (綜合測驗)
    private double accScore = 0.0;
    // 每題分數
    private double eachScore = 4.0;
    // 通過分數
    private double passScore = 80.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicarea_test);

        // 取得考試類別
        Bundle bundle = getIntent().getExtras();
        groupID = bundle.getInt(PublicAreaFragment.EXTRA_KEY);

        // 設定標題
        setTitle(getGroupTitle(groupID));

        String SQL;
        if(groupID == PublicAreaFragment.EXAM_COMPLEX) {
            SQL = SQL_ALL;
        } else {
            // 取得該群組內所有項目
            SQL = String.format(SQL_GROUP_ALL, groupID, "");
        }

        Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(SQL, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                publicAreaItemArrayList.add(i, new PublicAreaTestItem(c.getInt(0), c.getInt(1), c.getString(2),
                        c.getString(3), c.getString(4), c.getString(5), c.getString(6)));
                Log.i(TAG, publicAreaItemArrayList.get(i).toString());
                c.moveToNext();
            }
        }
        c.close();
        Log.i(TAG, "publicAreaItemArrayList size:" + publicAreaItemArrayList.size());

        initActionBar();
        initView();

        if(publicAreaItemArrayList.size() >= 5) {
            settingAndSetLayoutImgText();
        } else {
            // 顯示結果介面及文字
            resultLayout.setVisibility(View.VISIBLE);
            resultTitle.setText(Html.fromHtml(getString(R.string.pa_result_insufficient)));
            backBtn.setVisibility(View.VISIBLE);

            // 更改顯示圖片
            String uri = "drawable/" + publicAreaItemArrayList.get(0).getImgName();
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Bitmap bitmap = ImgUtil.decodeSampledBitmapFromResource(getResources(), imageResource, 100, 100);
            img.setImageBitmap(bitmap);
        }
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
        title = (TextView)findViewById(R.id.title_tv);
        img = (ImageView)findViewById(R.id.img);
        rg = (RadioGroup)findViewById(R.id.rgroup);
        ans[0] = (RadioButton)findViewById(R.id.ans1_rb);
        ans[1] = (RadioButton)findViewById(R.id.ans2_rb);
        ans[2] = (RadioButton)findViewById(R.id.ans3_rb);
        ans[3] = (RadioButton)findViewById(R.id.ans4_rb);
        ans[4] = (RadioButton)findViewById(R.id.ans5_rb);
        submitBtn = (Button)findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(this);
        resultLayout = (RelativeLayout) findViewById(R.id.result_layout);
        nextBtn = (Button) findViewById(R.id.result_btn);
        nextBtn.setOnClickListener(this);
        resultTitle = (TextView) findViewById(R.id.result_title);
        resultMessage = (TextView) findViewById(R.id.result_message);
        separateLine = (View) findViewById(R.id.separateline);
        resultUserName = (TextView) findViewById(R.id.result_username);
        backBtn = (Button) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);
    }

    private String getGroupTitle(int ID) {
        if(ID == PublicAreaFragment.EXAM_APPLIANCE) {
            return getString(R.string.pa_menu_exam_appliance);
        } else if(ID == PublicAreaFragment.EXAM_CUP) {
            return getString(R.string.pa_menu_exam_cup);
        } else if(ID == PublicAreaFragment.EXAM_MATERIAL) {
            return getString(R.string.pa_menu_exam_material);
        } else if(ID == PublicAreaFragment.EXAM_FRESH) {
            return getString(R.string.pa_menu_exam_fresh);
        } else if(ID == PublicAreaFragment.EXAM_BURDEN) {
            return getString(R.string.pa_menu_exam_burden);
        } else if(ID == PublicAreaFragment.EXAM_BREW) {
            return getString(R.string.pa_menu_exam_brew);
        } else if(ID == PublicAreaFragment.EXAM_SMOOTHIES) {
            return getString(R.string.pa_menu_exam_smoothies);
        } else if(ID == PublicAreaFragment.EXAM_EXPRESSO) {
            return getString(R.string.pa_menu_exam_expresso);
        } else if(ID == PublicAreaFragment.EXAM_OTHER) {
            return getString(R.string.pa_menu_exam_other);
        } else if(ID == PublicAreaFragment.EXAM_OPERATOR_STATION) {
            return getString(R.string.pa_menu_exam_operator_station);
        } else if(ID == PublicAreaFragment.EXAM_COMPLEX) {
            return getString(R.string.pa_menu_exam_complex);
        }
        return "";
    }

    private void settingAndSetLayoutImgText() {
        // 將黑屏gone
        resultLayout.setVisibility(View.GONE);
        // 將RadioButton選取紅點取消
        rg.clearCheck();
        // submit與radiobutton可以點擊
        buttonAndRadioButtonEnable(true);

        // 設定題數
        title.setText("第" + (theCount + 1) + "題");

        /**
         * 測試用，將前三個加入紀錄
         */
//        recordAnsItemArrayList.add(publicAreaItemArrayList.get(0));
//        recordAnsItemArrayList.add(publicAreaItemArrayList.get(1));
//        recordAnsItemArrayList.add(publicAreaItemArrayList.get(2));
        Log.i(TAG, "recordAnsItemArrayList size:" + recordAnsItemArrayList.size());

        /**
         * 篩選沒作答過的項目
         */
        noRepeatItemArrayList.clear();
        for(int i = 0; i < publicAreaItemArrayList.size(); i++) {
            // 如果此項目沒被作答過
            if(!checkItemRepeat(recordAnsItemArrayList, publicAreaItemArrayList.get(i))) {
                noRepeatItemArrayList.add(publicAreaItemArrayList.get(i));
            }
        }
        Log.i(TAG, "noRepeatItemArrayList size:" + noRepeatItemArrayList.size());

        /**
         * 還沒作答過的題目次數必須大於0
         */
        if(noRepeatItemArrayList.size() > 0) {

            /**
             * 由沒作答過的題目中，隨機選擇一個為此次題目
             */
            int topicIndex = (int) (Math.random() * noRepeatItemArrayList.size());
            topicItem = noRepeatItemArrayList.get(topicIndex);

            /**
             * 取得另外四個答案
             */
            otherIndexArrayList.clear();
            otherItemArrayList.clear();
            while (otherItemArrayList.size() < 4) {
                int theIndex;
                do {
                    theIndex = (int) (Math.random() * publicAreaItemArrayList.size()); // 0 ~ 未重複size
                } while (checkOtherIndexRepeat(theIndex)); //如果重複就繼續取得

                // 如果不存在於其他項目中
                // 而且沒有作答過
                // 而且不等於題目ID
                // 才加入另外其他答案列表中
                PublicAreaTestItem publicAreaTestItem = publicAreaItemArrayList.get(theIndex);
                if (!checkItemRepeat(otherItemArrayList, publicAreaTestItem) && !publicAreaTestItem.isAlreadyAnswer()
                        && publicAreaTestItem.getId() != topicItem.getId()) {
                    otherIndexArrayList.add(theIndex);
                    otherItemArrayList.add(publicAreaItemArrayList.get(theIndex));
                }
            }

            /**
             * 設定答案順序
             */
            int randomOrderIndex;
            ArrayList<Integer> ansOrder = new ArrayList<>(Arrays.asList(1,2,3,4,5));

            // 設定題目答案位置
            randomOrderIndex = (int) (Math.random() * ansOrder.size());
            topicItem.setAnswerPosition(ansOrder.get(randomOrderIndex));
            ansOrder.remove(randomOrderIndex);
            // 其餘四個其他答案位置
            for(int i=0;i<otherItemArrayList.size();i++) {
                randomOrderIndex = (int) (Math.random() * ansOrder.size());
                otherItemArrayList.get(i).setAnswerPosition(ansOrder.get(randomOrderIndex));
                ansOrder.remove(randomOrderIndex);
            }

            /**
             * 列出題目與四個答案
             */
            Log.i(TAG, "=======================");
            Log.i(TAG, topicItem.toString());
            for (int i = 0; i < otherItemArrayList.size(); i++) {
                Log.i(TAG, "" + otherItemArrayList.get(i).toString());
            }
            Log.i(TAG, "======================");

            /**
             * 設定正確答案的圖片
             */
            String uri = "drawable/" + topicItem.getImgName();
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            Bitmap bitmap = ImgUtil.decodeSampledBitmapFromResource(getResources(), imageResource, 100, 100);
            img.setImageBitmap(bitmap);

            /**
             * 設定答案文字
             */
            for(int i=0;i<ans.length;i++) {
                // 先確認題目再確認另外四個答案
                if(i == topicItem.getAnswerPosition()-1) {
                    ans[i].setText(topicItem.getItemName());
                } else {
                    for(int j=0;j<otherItemArrayList.size();j++) {
                        PublicAreaTestItem publicAreaTestItem = otherItemArrayList.get(j);
                        if(i == publicAreaTestItem.getAnswerPosition()-1) {
                            ans[i].setText(otherItemArrayList.get(j).getItemName());
                        }
                    }
                }
            }
        } //-----noRepeatItemArrayList.size >0 ------END----------
    }

    /**
     * 確認此索引是否有重複
     */
    private boolean checkOtherIndexRepeat(int theIndex) {
        boolean b = false;
        for(int j = 0; j < otherIndexArrayList.size(); j++) {
            if(theIndex == otherIndexArrayList.get(j)) {
                b = true;
                break;
            }
        }
        return b;
    }

    /**
     * 確認此項目是否存在於ArrayList之中
     */
    private boolean checkItemRepeat(ArrayList<PublicAreaTestItem> arrayList, PublicAreaTestItem publicAreaTestItem) {
        boolean b = false;
        for(int i=0;i< arrayList.size();i++) {
            if(arrayList.get(i).getId() == publicAreaTestItem.getId()) {
                b = true;
                break;
            }
        }
        return b;
    }

    private void buttonAndRadioButtonEnable(boolean b) {
        submitBtn.setEnabled(b);
        for(int i=0;i<rg.getChildCount();i++) {
            ((RadioButton) rg.getChildAt(i)).setEnabled(b);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_btn: //確定
                // 限制radioButton與Button不能再被點擊
                buttonAndRadioButtonEnable(false);
                // 增加答題次數
                theCount++;
                // 確認此項目是否有重複，沒有重複才紀錄此項目
                if(!checkItemRepeat(recordAnsItemArrayList,topicItem)) {
                    recordAnsItemArrayList.add(topicItem);
                }

                // 取得RadioButton的index
                int radioButtonID  = rg.getCheckedRadioButtonId();
                View radioButton = rg.findViewById(radioButtonID);
                int idx = rg.indexOfChild(radioButton);
                Log.i(TAG," index:" + idx);
                // 查看Item的位置是否與題目相同
                int ansPos = topicItem.getAnswerPosition();
                String str, color;
                if(idx == ansPos-1) { // 答對
                    str = "正確！";
                    color = "#1e90ff";
                    // 綜合測驗答對要累積分數
                    if(groupID == PublicAreaFragment.EXAM_COMPLEX) {
                        accScore += eachScore;
                    }
                } else { // 答錯
                    str = "答錯了！";
                    color = "red";
                }

                // 顯示結果介面及文字
                resultLayout.setVisibility(View.VISIBLE);
                resultTitle.setText(Html.fromHtml(
                        String.format(getString(R.string.pa_result_text), str, color, topicItem.getItemName())
                ));

                /**
                 *  顯示最後通過介面
                 */
                // 如果不是綜合頁面而且已作答數量=全部數量
                // 或 如果是綜合頁面而且題數=25
                if( (groupID != PublicAreaFragment.EXAM_COMPLEX
                        && recordAnsItemArrayList.size() == publicAreaItemArrayList.size())
                        || (groupID == PublicAreaFragment.EXAM_COMPLEX && theCount == 25) ) {
                    nextBtn.setVisibility(View.GONE);
                    separateLine.setVisibility(View.VISIBLE);
                    resultUserName.setVisibility(View.VISIBLE);
                    resultUserName.setText(MainApp.getUserNameField());
                    resultMessage.setVisibility(View.VISIBLE);
                    // 如果是綜合測驗顯示的訊息要包含分數
                    if(groupID == PublicAreaFragment.EXAM_COMPLEX) {
                        DecimalFormat df = new DecimalFormat("#");
                        String s = df.format(accScore);
                        if(accScore >= passScore) {
                            resultMessage.setText(Html.fromHtml(
                                    String.format(getString(R.string.pa_result_congratulation_score), s, getGroupTitle(groupID))
                            ));
                        } else {
                            resultMessage.setText(Html.fromHtml(
                                    String.format(getString(R.string.pa_result_fail_score), s, getGroupTitle(groupID))
                            ));
                        }

                    } else { // 如果不是綜合測驗只顯示通過訊息
                        resultMessage.setText(Html.fromHtml(
                                String.format(getString(R.string.pa_result_finishtest), getGroupTitle(groupID))
                        ));
                    }
                    backBtn.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.result_btn:
                // 更新介面
                settingAndSetLayoutImgText();
                break;
            case R.id.back_btn:
                finish();
                break;
        }
    }
}

package tony.beveragesmodulation.technicalsubject.publicarea;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;
import tony.beveragesmodulation.utils.ImgUtil;
import tony.beveragesmodulation.utils.Util;

/**
 *  公共材料詳細頁面
 */
public class PublicAreaDetailActivity extends AppCompatActivity{
    private static final String TAG = "PublicAreaDetailActivity";
    private Toolbar toolbar;
    private String SQL = "SELECT A.id,A.am_groupid,B.am_groupname,A.item_name,A.use_function,A.applicable_topic_description,A.img_name " +
            "FROM `appliance_material` AS A,`appliance_material_group` AS B " +
            "WHERE A.am_groupid = B.am_groupid AND A.am_groupid = %1$d ORDER BY A.id ASC";
    private ArrayList<PublicAreaItem> publicAreaItems = new ArrayList<>();
    private int groupID;
    private ImageView img;
    private TextView nameTV,useFunctionTV,applicableTopicDescriptionTV;
    private Button nextBtn,prevBtn;
    private int index = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicarea_detail);

        Bundle bundle = getIntent().getExtras();
        groupID = bundle.getInt("groupID");

        //根據器具材料的群組代號取得該類Item
        String SQL_Format = String.format(SQL, groupID);

        Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(SQL_Format,null);
        Log.i(TAG, "c.getCount():" + c.getCount());
        if(c.getCount() > 0) {
            c.moveToFirst();
            for(int i = 0; i < c.getCount(); i++) {
                publicAreaItems.add(new PublicAreaItem(c.getInt(0), c.getInt(1), c.getString(2),
                        c.getString(3), c.getString(4), c.getString(5), c.getString(6)));
                c.moveToNext();
            }
        }
        c.close();

        initActionBar();
        initView();
        setLayoutImageAndText();
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
        nextBtn = (Button)findViewById(R.id.result_btn);
        prevBtn = (Button)findViewById(R.id.prev_btn);
        nextBtn.setOnClickListener(nextOrPrevBtnCLK);
        prevBtn.setOnClickListener(nextOrPrevBtnCLK);
        img = (ImageView)findViewById(R.id.img);
        nameTV = (TextView)findViewById(R.id.title_tv);
        useFunctionTV = (TextView)findViewById(R.id.use_function);
        applicableTopicDescriptionTV = (TextView)findViewById(R.id.applicable_topic_description);
    }

    private void setLayoutImageAndText() {
        Log.e(TAG, "setLayoutImageAndText");

        // 設定標題
        setTitle("公共材料區 - " + publicAreaItems.get(index).getGroupName());
        // 取得drawable
        String uri = "drawable/" + publicAreaItems.get(index).getImgName();
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Bitmap bitmap = ImgUtil.decodeSampledBitmapFromResource(getResources(), imageResource, 150, 100);
        img.setImageBitmap(bitmap);

        nameTV.setText(publicAreaItems.get(index).getItemName());
        useFunctionTV.setText(publicAreaItems.get(index).getUseFunction());
        applicableTopicDescriptionTV.setText(Util.getIdCutText(publicAreaItems.get(index).getApplicableTopicDescription()));
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

    private Button.OnClickListener nextOrPrevBtnCLK = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            int ID = 0;
            switch(v.getId()) {
                case R.id.prev_btn:
                    ID = index - 1;
                    if(ID >= 0) {
                        index = ID;
                        setLayoutImageAndText();
                    } else {
                        Toast.makeText(getApplicationContext(), "沒有上一個", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.result_btn:
                    ID = index + 1;
                    if(ID < publicAreaItems.size()) {
                        index = ID;
                        setLayoutImageAndText();
                    } else {
                        Toast.makeText(getApplicationContext(), "沒有下一個", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
}

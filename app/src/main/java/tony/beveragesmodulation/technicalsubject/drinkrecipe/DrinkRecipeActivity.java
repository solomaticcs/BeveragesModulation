package tony.beveragesmodulation.technicalsubject.drinkrecipe;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;

/**
 * 調製配方Item
 */
public class DrinkRecipeActivity extends AppCompatActivity {
    private static final String TAG = "DRFragment";
    private Toolbar toolbar;
    private ImageView img;
    private TextView gidTV,ingredient, modulation_method, decorations, cup_utensils;
    private Button prevBtn, nextBtn;

    private DrinkRecipeItem drinkRecipeItem = null;

    private String groupID;
    private int id = 1;

    //編號，群組代號，群組順序，名稱，成分對應編號，成分敘述、調製法對應編號，調製法，裝飾品，杯器皿，圖片名稱
    private String SQL = "SELECT `id`,`tg_groupid`,`id_order`,`name`," +
            "`ingredient_id`,`ingredient`,`modulation_id`,`modulation`,`decorations`," +
            "`cup_utensils`,`img_name` from `drink_recipe` " +
            "WHERE `tg_groupid` = '%1$s' AND `id_order` = %2$d ORDER BY `id_order` ASC;";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drinkrecipe);

        // 取得群組代號
        Bundle bundle = getIntent().getExtras();
        groupID = bundle.getString("groupID");

        // 取得飲品配方資料根據群組代號
        String SQL_Fomat = String.format(SQL, groupID, id);

        Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(SQL_Fomat,null);
        if(c.getCount() > 0) {
            c.moveToFirst();
            drinkRecipeItem = new DrinkRecipeItem(getApplicationContext(),
                    c.getInt(0),c.getString(1),c.getInt(2),c.getString(3),
                    c.getString(4),c.getString(5),c.getString(6),c.getString(7),
                    c.getString(8),c.getString(9),c.getString(10));
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
        // 組別
        gidTV = (TextView) findViewById(R.id.gidTV);
        // 按鈕設定聆聽者
        prevBtn = (Button) findViewById(R.id.btn_prev);
        nextBtn = (Button) findViewById(R.id.btn_next);
        prevBtn.setOnClickListener(btnCLK);
        nextBtn.setOnClickListener(btnCLK);
        // 圖片
        img = (ImageView) findViewById(R.id.img);
        // 成分
        ingredient = (TextView) findViewById(R.id.input_ingredient);
        // 調製法
        modulation_method = (TextView) findViewById(R.id.input_modulation_method);
        // 裝飾物
        decorations = (TextView) findViewById(R.id.input_decorations);
        // 杯器皿
        cup_utensils = (TextView) findViewById(R.id.input_cup_utensils);

        setLayoutImageAndText();
    }

    private void setLayoutImageAndText() {
        // 設定圖片
        Drawable image = getResources().getDrawable(drinkRecipeItem.getImageResource());
        img.setImageDrawable(image);

        // 設定文字
        setTitle(drinkRecipeItem.getName());
        gidTV.setText(String.format(getString(R.string.dr_group), drinkRecipeItem.getGroupId()));
        ingredient.setText(drinkRecipeItem.getIngredient());
        modulation_method.setText(drinkRecipeItem.getModulation());
        decorations.setText(drinkRecipeItem.getDecorations());
        cup_utensils.setText(drinkRecipeItem.getCupUtensils());
    }

    //上一個與下一個按鈕的事件
    private Button.OnClickListener btnCLK = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == prevBtn.getId()) {
                Log.i(TAG,"-----上一個-----");
                //取得上一個飲品配方的資料
//                int id = MainApp.getMFIDField();
                int prevId = id - 1;
                Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(String.format(SQL, groupID, prevId),null);
                if(c.getCount() > 0) {
//                    MainApp.saveMFIDField(prevId);
                    id = prevId;
                    c.moveToFirst();
                    drinkRecipeItem = new DrinkRecipeItem(getApplicationContext(),
                            c.getInt(0),c.getString(1),c.getInt(2),c.getString(3),
                            c.getString(4),c.getString(5),c.getString(6),c.getString(7),
                            c.getString(8),c.getString(9),c.getString(10));

                    setLayoutImageAndText();
                } else {
                    Toast.makeText(getApplicationContext(), "沒有上一個。", Toast.LENGTH_SHORT).show();
                }
                c.close();
            } else if(v.getId() == nextBtn.getId()) {
                Log.i(TAG,"-----下一個-----");
                //取得下一個飲品配方的資料
//                int id = MainApp.getMFIDField();
                int nextId = id + 1;
                Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(String.format(SQL, groupID, nextId),null);
                if(c.getCount() > 0) {
//                    MainApp.saveMFIDField(nextId);
                    id = nextId;
                    c.moveToFirst();
                    drinkRecipeItem = new DrinkRecipeItem(getApplicationContext(),
                            c.getInt(0),c.getString(1),c.getInt(2),c.getString(3),
                            c.getString(4),c.getString(5),c.getString(6),c.getString(7),
                            c.getString(8),c.getString(9),c.getString(10));

                    setLayoutImageAndText();
                } else {
                    Toast.makeText(getApplicationContext(), "沒有下一個。", Toast.LENGTH_SHORT).show();
                }
                c.close();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dr_itemdetail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.next_item:
                MainApp.drinkRecipeItem = drinkRecipeItem;
                Intent intent = new Intent(getApplicationContext(), DrinkRecipeTestActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

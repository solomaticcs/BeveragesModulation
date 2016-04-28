package tony.beveragesmodulation;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import tony.beveragesmodulation.information.NoticeFragment;
import tony.beveragesmodulation.information.PamphletsBuyFragment;
import tony.beveragesmodulation.sciencesubject.TopicGroupFragment;
import tony.beveragesmodulation.sciencesubject.TopicMockGroupFragment;
import tony.beveragesmodulation.technicalsubject.afterwork.AfterWorkFragment;
import tony.beveragesmodulation.technicalsubject.drinkrecipe.DrinkRecipeGroupFragment;
import tony.beveragesmodulation.technicalsubject.modulation.ModulationFragment;
import tony.beveragesmodulation.technicalsubject.modulationprocess.MPDetailFragment;
import tony.beveragesmodulation.technicalsubject.preexercise.PreExerciseGroupFragment;
import tony.beveragesmodulation.technicalsubject.publicarea.PublicAreaFragment;
import tony.beveragesmodulation.technicalsubject.tips.TipsFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ListView lstDrawer;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerMenuAdapter mAdapter;

    private String[] drawerListName;
    private int[] drawerListValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initActionBar();
        initDrawer();
        initDrawerList();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void initDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        lstDrawer = (ListView)findViewById(R.id.left_drawer);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    private void initDrawerList() {
        drawerListName = getResources().getStringArray(R.array.drawer_list_name);
        drawerListValue = getResources().getIntArray(R.array.drawer_list_value);

        mAdapter = new DrawerMenuAdapter(this);
        mAdapter.addItem(drawerListName, drawerListValue);
        lstDrawer.setAdapter(mAdapter);
        lstDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter.getItemViewType(position) == 0) { //0:item 1:Section Header Item
                    String str = lstDrawer.getItemAtPosition(position).toString();
                    selectItem(str, position);
                }
            }
        });

        //default fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeFragment(), drawerListName[0]).commit();
    }

    private void selectItem(String str, int position) {
        Log.i(TAG, "str:" + str);

        Fragment fragment = null;
        switch (str) {
            case "主頁":
                fragment = new HomeFragment();
                break;
            case "題庫瀏覽":
                fragment = new TopicGroupFragment(getApplicationContext());
                break;
            case "模擬試題":
                fragment = new TopicMockGroupFragment(getApplicationContext());
                break;
            case "公共材料區":
                fragment = new PublicAreaFragment(getApplicationContext());
                break;
            case "調製法":
                fragment = new ModulationFragment(getApplicationContext());
                break;
            case "飲調題組":
                fragment = new DrinkRecipeGroupFragment(getApplicationContext());
                break;
            case "前置操作練習":
                fragment = new PreExerciseGroupFragment(getApplicationContext());
                break;
            case "調製流程":
                fragment = new MPDetailFragment(getApplicationContext());
                break;
            case "善後工作":
                fragment = new AfterWorkFragment(getApplicationContext());
                break;
            case "應考錦囊":
                fragment = new TipsFragment(getApplicationContext());
                break;
            case "應檢須知":
                fragment = new NoticeFragment();
                break;
            case "簡章購買、報名方式":
                fragment = new PamphletsBuyFragment();
                break;
        }

        if(fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStack();
            if(str.equals(drawerListName[0])) { //如果下一個要前往的頁面是Main Fragment，就不追加back stack
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, str);
            } else {
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, str).addToBackStack("fragBack").commit();
            }
            // 更新被選擇項目，換標題文字，關閉選單
            lstDrawer.setItemChecked(position, true);
            if(position == 0) {
                setTitle(getResources().getString(R.string.app_name));
            } else {
                setTitle(drawerListName[position]);
            }
            mDrawerLayout.closeDrawer(lstDrawer);
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_itemdetail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
            setTitle(getResources().getString(R.string.app_name));
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            confirmExit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否触发按键为back键
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed();
            return true;
        } else { // 如果不是back键正常响应
            return super.onKeyDown(keyCode, event);
        }
    }

    private void confirmExit() {// 退出確認
        AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
        ad.setTitle("離開");
        ad.setMessage("確定要離開？");
        ad.setPositiveButton("是", new DialogInterface.OnClickListener() {// 退出按鈕
            public void onClick(DialogInterface dialog, int i) {
                // TODO Auto-generated method stub
                MainActivity.this.finish();// 關閉activity
            }
        });
        ad.setNegativeButton("否", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                // 不退出不用執行任何操作
            }
        });
        ad.show();// 示對話框
    }
}

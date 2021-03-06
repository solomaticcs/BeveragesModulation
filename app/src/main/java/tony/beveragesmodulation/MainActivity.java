package tony.beveragesmodulation;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
            case "??????":
                fragment = new HomeFragment();
                break;
            case "????????????":
                fragment = new TopicGroupFragment(getApplicationContext());
                break;
            case "????????????":
                fragment = new TopicMockGroupFragment(getApplicationContext());
                break;
            case "???????????????":
                fragment = new PublicAreaFragment(getApplicationContext());
                break;
            case "?????????":
                fragment = new ModulationFragment(getApplicationContext());
                break;
            case "????????????":
                fragment = new DrinkRecipeGroupFragment(getApplicationContext());
                break;
            case "??????????????????":
                fragment = new PreExerciseGroupFragment(getApplicationContext());
                break;
            case "????????????":
                fragment = new MPDetailFragment(getApplicationContext());
                break;
            case "????????????":
                fragment = new AfterWorkFragment(getApplicationContext());
                break;
            case "????????????":
                fragment = new TipsFragment(getApplicationContext());
                break;
            case "????????????":
                fragment = new NoticeFragment();
                break;
            case "???????????????????????????":
                fragment = new PamphletsBuyFragment();
                break;
        }

        if(fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStack();
            if(str.equals(drawerListName[0])) { //????????????????????????????????????Main Fragment???????????????back stack
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, str);
            } else {
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, str).addToBackStack("fragBack").commit();
            }
            // ??????????????????????????????????????????????????????
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
        // ?????????????????????back???
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed();
            return true;
        } else { // ????????????back???????????????
            return super.onKeyDown(keyCode, event);
        }
    }

    private void confirmExit() {// ????????????
        AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
        ad.setTitle("??????");
        ad.setMessage("??????????????????");
        ad.setPositiveButton("???", new DialogInterface.OnClickListener() {// ????????????
            public void onClick(DialogInterface dialog, int i) {
                // TODO Auto-generated method stub
                MainActivity.this.finish();// ??????activity
            }
        });
        ad.setNegativeButton("???", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                // ?????????????????????????????????
            }
        });
        ad.show();// ????????????
    }
}

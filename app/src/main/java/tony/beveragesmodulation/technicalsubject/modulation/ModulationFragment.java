package tony.beveragesmodulation.technicalsubject.modulation;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;

/**
 * 調製法
 */
public class ModulationFragment extends Fragment {
    private static final String TAG = "ModulationFragment";
    private Context cxt;

    private String SQL = "SELECT `mid`,`mfunction`, `example`, `mvideourl`,`define` FROM `modulation` ORDER BY `mid` ASC";

    private ModulationItem[] modulationItems = null;

    private int index = 0;
    private TextView title, description;
    private Button videoUrlBtn;

    public ModulationFragment(Context cxt) {
        this.cxt = cxt;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(SQL, null);
        if(c.getCount() > 0) {
            modulationItems = new ModulationItem[c.getCount()];
            c.moveToFirst();
            for(int i=0;i<c.getCount();i++) {
                modulationItems[i] = new ModulationItem(c.getInt(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4));
                Log.i(TAG, "" + modulationItems[i].toString());
                c.moveToNext();
            }
        }
        c.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater, container);
    }

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_modulation, container, false);

        title = (TextView)view.findViewById(R.id.title_tv);
        Button prevBtn = (Button)view.findViewById(R.id.prev_btn);
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "上一個");
                int newIndex = index - 1;
                if(newIndex >= 0) {
                    index--;
                    setTextLayout();
                } else {
                    Toast.makeText(getActivity(), "沒有上一個！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button nextBtn = (Button)view.findViewById(R.id.result_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "下一個");
                int newIndex = index + 1;
                if(newIndex < modulationItems.length) {
                    index++;
                    setTextLayout();
                } else {
                    Toast.makeText(getActivity(), "沒有下一個！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        description = (TextView)view.findViewById(R.id.define_description);
        videoUrlBtn = (Button) view.findViewById(R.id.videourl_btn);

        setTextLayout();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mu_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.mu_exam:
                Intent intent = new Intent(getActivity(), ModulationTestActivity.class);
                startActivity(intent);
                return true;
            default:
                return true;
        }
    }

    private void setTextLayout() {
        title.setText(modulationItems[index].getFunction() + " - " + modulationItems[index].getExample());
        description.setText(modulationItems[index].getDefine());
        videoUrlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "開啟影片連結");
                Log.i(TAG, "name:" + modulationItems[index].getFunction());
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(modulationItems[index].getVideoURL())
                ));
            }
        });
    }
}

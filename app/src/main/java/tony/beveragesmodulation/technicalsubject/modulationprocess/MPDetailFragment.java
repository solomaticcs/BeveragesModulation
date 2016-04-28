package tony.beveragesmodulation.technicalsubject.modulationprocess;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import tony.beveragesmodulation.utils.Util;

/**
 * 調製流程詳細頁面
 */
public class MPDetailFragment extends Fragment {
    private static final String TAG = "MPDetailFragment";
    private Context cxt;
    private String SQL = "SELECT `id`,`function_name`,`sample_name`,`process_description` FROM `modulation_process` ORDER BY `id` ASC";
    private MPItem[] mpItems;
    private int index = 0;

    public MPDetailFragment(Context cxt) {
        this.cxt = cxt;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(SQL, null);
        if(c.getCount() > 0) {
            mpItems = new MPItem[c.getCount()];
            c.moveToFirst();
            for(int i=0;i<c.getCount();i++) {
                mpItems[i] = new MPItem(c.getInt(0),c.getString(1),c.getString(2), c.getString(3));
                c.moveToNext();
            }
        }
        c.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater, container);
    }
    private TextView nameTV,descriptionTV;
    private View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_mp, container, false);

        nameTV = (TextView)view.findViewById(R.id.title_tv);
        descriptionTV = (TextView)view.findViewById(R.id.process_description);
        Button prevBtn = (Button)view.findViewById(R.id.prev_btn);
        Button nextBtn = (Button)view.findViewById(R.id.result_btn);
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
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "下一個");
                int newIndex = index + 1;
                if(newIndex < mpItems.length) {
                    index++;
                    setTextLayout();
                } else {
                    Toast.makeText(getActivity(), "沒有下一個！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        setTextLayout();
        return view;
    }

    private void setTextLayout() {
        nameTV.setText(mpItems[index].getId() + "." + mpItems[index].getSampleName());
        descriptionTV.setText(Util.getIdCutText(mpItems[index].getProcDescription()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mp_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.mp_exam:
                Intent intent = new Intent(getActivity(), MPTestActivity.class);
                startActivity(intent);
                return true;
            default:
                return true;
        }
    }
}

package tony.beveragesmodulation.technicalsubject.tips;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;
import tony.beveragesmodulation.utils.Util;

/**
 * 應考錦囊
 */
public class TipsFragment extends Fragment {
    private static final String TAG = "TipsFragment";
    private Context cxt;

    private static final String SQL = "SELECT `id`,`function_name`,`tip` FROM tips";
    private TipsItem[] tipsItems;
    private int index = 0;
    private TextView title;
    private TextView description;

    public TipsFragment(Context cxt) {
        this.cxt = cxt;
        Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(SQL, null);
        if(c.getCount() > 0) {
            tipsItems = new TipsItem[c.getCount()];
            c.moveToFirst();
            for(int i =0;i < c.getCount();i++) {
                tipsItems[i] = new TipsItem(c.getInt(0),c.getString(1),c.getString(2));
                c.moveToNext();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater, container);
    }

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_tips, container, false);

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
                if(newIndex < tipsItems.length) {
                    index++;
                    setTextLayout();
                } else {
                    Toast.makeText(getActivity(), "沒有下一個！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        description = (TextView)view.findViewById(R.id.tip_description);
        setTextLayout();
        return view;
    }

    private void setTextLayout() {
        title.setText(tipsItems[index].getFunctionName());
        description.setText(Util.getIdCutText(tipsItems[index].getTip()));
    }
}

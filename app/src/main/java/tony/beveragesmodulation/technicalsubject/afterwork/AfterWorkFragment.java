package tony.beveragesmodulation.technicalsubject.afterwork;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import tony.beveragesmodulation.MainApp;
import tony.beveragesmodulation.R;

/**
 * 善後工作
 */
public class AfterWorkFragment extends Fragment {
    private static final String TAG = "AfterWorkFragment";
    private Context cxt;

    private static final String SQL = "SELECT `id`, `process` FROM `afterwork` ";

    private ArrayList<HashMap<String,Object>> hashMapArrayList = new ArrayList<>();

    public AfterWorkFragment(Context cxt) {
        this.cxt = cxt;
        Cursor c = MainApp.getDatabaseDAO().getDB().rawQuery(SQL, null);
        if(c.getCount() > 0) {
            c.moveToFirst();
            for(int i =0;i < c.getCount();i++) {
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("id", c.getInt(0));
                hashMap.put("process", c.getString(1));
                hashMapArrayList.add(hashMap);
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
        View view = inflater.inflate(R.layout.fragment_afterwork, container, false);
        TextView afterworkText = (TextView) view.findViewById(R.id.afterwork_text);

        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0;i < hashMapArrayList.size();i++) {
            stringBuilder.append(hashMapArrayList.get(i).get("id") + "." + hashMapArrayList.get(i).get("process"));
            if(i != hashMapArrayList.size() -1) {
                stringBuilder.append("\n\n");
            }
        }

        afterworkText.setText(stringBuilder.toString());

        return view;
    }
}

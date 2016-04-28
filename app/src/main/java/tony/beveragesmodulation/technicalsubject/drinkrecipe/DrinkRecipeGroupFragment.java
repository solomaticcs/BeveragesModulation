package tony.beveragesmodulation.technicalsubject.drinkrecipe;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import tony.beveragesmodulation.R;

/**
 * 調飲配方題組
 */
public class DrinkRecipeGroupFragment extends Fragment {
    private static final String TAG = "DRGroupFragment";
    private Context cxt;

    public DrinkRecipeGroupFragment(Context cxt) {
        this.cxt = cxt;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater, container);
    }

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_drinkrecipe_group, container, false);

        final String[] listName = getResources().getStringArray(R.array.dr_list_name);
        ListView listView = (ListView) view.findViewById(R.id.modulation_listview);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,listName);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "name:" + listName[position]);
                Intent intent = new Intent(getActivity(), DrinkRecipeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("groupID", listName[position]);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }
}

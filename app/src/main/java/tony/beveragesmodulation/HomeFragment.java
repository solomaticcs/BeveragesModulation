package tony.beveragesmodulation;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 主頁
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private EditText userNameEt;
    private Button submitBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater, container);
    }

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        userNameEt = (EditText) view.findViewById(R.id.username_et);
        submitBtn = (Button) view.findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "儲存使用者名字");
                MainApp.editUserNameField(userNameEt.getText().toString());
                Toast.makeText(getActivity(), "儲存成功！", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        String userName = MainApp.getUserNameField();
        if(!userName.equals("匿名")) {
            userNameEt.setText(userName);
        }
    }
}

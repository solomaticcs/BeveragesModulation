package tony.beveragesmodulation.information;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import tony.beveragesmodulation.R;

/**
 * 應檢須知
 */
public class NoticeFragment extends Fragment{
    private static final String TAG = "NoticeFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater, container);
    }

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        WebView wv = (WebView) view.findViewById(R.id.webView);
        wv.loadUrl("file:///android_asset/notice_page/selection.html");
        WebSettings ws = wv.getSettings();
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);

        return view;
    }
}

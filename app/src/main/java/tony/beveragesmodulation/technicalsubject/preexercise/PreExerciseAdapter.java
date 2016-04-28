package tony.beveragesmodulation.technicalsubject.preexercise;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tony.beveragesmodulation.R;
import tony.beveragesmodulation.utils.ImgUtil;

public class PreExerciseAdapter extends BaseAdapter {
    private static final String TAG = "PreExerciseAdapter";
    private Context cxt;
    private LayoutInflater li;
    private ArrayList<PreExercisePAItem> preExercisePAItems = new ArrayList<>();
    private boolean checkItemSelected = false; // 是否要確認材料是否拿對
    private boolean checkItemPlace = false; // 是否要確認材料放置位置是否正確

    public PreExerciseAdapter(Context context) {
        this.cxt = context;
        this.li = LayoutInflater.from(context);
    }

    public void updatePreExercisePAItems(ArrayList<PreExercisePAItem> preExercisePAItems, boolean checkItemSelected, boolean checkItemPlace) {
        this.preExercisePAItems = preExercisePAItems;
        this.checkItemSelected = checkItemSelected;
        this.checkItemPlace = checkItemPlace;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return preExercisePAItems.size();
    }

    @Override
    public Object getItem(int position) {
        return preExercisePAItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        TextView text;
        ImageView img;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = li.inflate(R.layout.pepa_list_item, parent, false);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.img = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 取得item
        PreExercisePAItem preExercisePAItem = (PreExercisePAItem) getItem(position);

        // 設定文字
        holder.text.setText(preExercisePAItem.getId() + "." + preExercisePAItem.getItemName());
        holder.text.setTextColor(Color.BLACK);

        // 取得drawable
        String uri = "drawable/" + preExercisePAItem.getImgName();
        int imageResource = cxt.getResources().getIdentifier(uri, null, cxt.getPackageName());
        Bitmap bitmap = ImgUtil.decodeSampledBitmapFromResource(cxt.getResources(), imageResource, 100, 100);
        // 設定圖片
        holder.img.setImageBitmap(bitmap);

        // 如果要確認項目狀態，就改變背景顏色
        if(checkItemSelected) {
            // 如果被選擇
            if (preExercisePAItem.isChecked()) {
                if (preExercisePAItem.isCorrectAns()) {
                    convertView.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    convertView.setBackgroundColor(Color.RED);
                }
            } else { // 如果沒被選擇
                if (preExercisePAItem.isCorrectAns()) {
                    convertView.setBackgroundColor(Color.YELLOW);
                } else {
                    convertView.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        } else if (checkItemPlace) {
            if (preExercisePAItem.isCorrectPlaceArea()) {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            } else {
                convertView.setBackgroundColor(Color.RED);
            }
        }

        return convertView;
    }
}

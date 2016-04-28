package tony.beveragesmodulation.view.dragndroplist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

public class DragNDropSimpleAdapter extends SimpleAdapter implements DragNDropAdapter {
    private int mPosition[];
    private int mHandler;
    private List<? extends Map<String, ?>> dataList;

    public DragNDropSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
                                  int[] to, int handler) {
        super(context, data, resource, from, to);

        mHandler = handler;
        dataList = data;
        setup(data.size());
    }

    private void setup(int size) {
        mPosition = new int[size];

        for (int i = 0; i < size; ++i)
            mPosition[i] = i;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup group) {
        return super.getDropDownView(mPosition[position], view, group);
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(mPosition[position]);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(mPosition[position]);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(mPosition[position]);
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        return super.getView(mPosition[position], view, group);
    }

    @Override
    public boolean isEnabled(int position) {
        return super.isEnabled(mPosition[position]);
    }

    @Override
    public void onItemDrag(DragNDropListView parent, View view, int position, long id) {

    }

    @Override
    public void onItemDrop(DragNDropListView parent, View view, int startPosition, int endPosition, long id) {
        int position = mPosition[startPosition];

        if (startPosition < endPosition)
            for (int i = startPosition; i < endPosition; ++i)
                mPosition[i] = mPosition[i + 1];
        else if (endPosition < startPosition)
            for (int i = startPosition; i > endPosition; --i)
                mPosition[i] = mPosition[i - 1];

        mPosition[endPosition] = position;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public void notifyDataSetChanged() {
        setup(dataList.size());
        super.notifyDataSetChanged();
    }

    @Override
    public int getDragHandler() {
        return mHandler;
    }

//
//    // ------------------------------------------
//
//    public Map<String, ?> getDataItem(int position) {
//        return dataList.get(position);
//    }
}
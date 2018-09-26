package com.user.box.data;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.uhf.uhf.R;
import com.user.box.utils.Utils;

public class GridAdapter extends BaseAdapter {
    private int[] mIcons;
    private Context mContext;
    private int mPage = -1;
    private int mSelection = -1;

    public GridAdapter(Context context, int[] icons, int page) {
       this.mContext = context;
       mPage = page;
       int start = page * Utils.MAX_GRID_VIEW_NUM;
       int end = (page + 1) * Utils.MAX_GRID_VIEW_NUM;
       int number = Math.min((icons.length - start *2),
               Utils.MAX_GRID_VIEW_NUM  * 2);
       Log.i("zxz", "start=" +start + ",end=" + end + ",number=" + number + "@" + icons.length);
       mIcons = new int[number];
       int index = 0;
       while ((start < icons.length / 2) && (start < end)) {
           mIcons[2 * index] = icons[2 * start];
           mIcons[2 * index + 1] = icons[2 * start + 1];
           index++;
           start++;
       }
    }

    public GridAdapter(Context context, int[] icons) {
        this.mContext = context;
        this.mIcons = icons;
     }

    public int getPageCount() {
        return mPage;
    }

    public void revertSelection(int select) {
        if (mSelection != select) {
            setSelection(select);
        } else {
            resetSelection();
        }
    }

    public void setSelection(int select) {
        mSelection = select;
    }

    public void resetSelection() {
        mSelection = -1;
    }

    @Override
    public int getCount() {
        return mIcons.length / 2;
    }

    @Override
    public Object getItem(int position) {
        return mIcons[position * 2];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.gridview_item, null);
            
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.item_icon);
            //viewHolder.tv_title = (TextView) convertView.findViewById(R.id.item_msg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mSelection == position) {
            viewHolder.iv_icon.setImageResource(mIcons[position * 2] - 1);
        } else {
            viewHolder.iv_icon.setImageResource(mIcons[position * 2]);
        }
        //viewHolder.tv_title.setText(mNames[position]);

        return convertView;
    }

    class ViewHolder {
        public ImageView iv_icon;
        //public TextView tv_title;
    }
}

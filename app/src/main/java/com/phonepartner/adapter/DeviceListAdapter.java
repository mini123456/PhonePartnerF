package com.phonepartner.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.phonepartner.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cwj on 2017/6/22.
 */

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder>{

    private List<BluetoothDevice> mBleArray;
   // private ViewHolder viewHolder;
    private Context mContext;
    public static int clickPosition ;
    private boolean isselect = false;

    public static void setClickPosition(int pos){
        clickPosition=pos;
    }

    public  static int getClickPosition(){
        return  clickPosition;
    }


    public DeviceListAdapter(Context context) {
       this. mBleArray =new ArrayList<>();
        mContext = context;
    }

    public void addDevice(BluetoothDevice bluetoothDevice) {


        if (!mBleArray.contains(bluetoothDevice)) {
            mBleArray.add(bluetoothDevice);
            notifyDataSetChanged();
        }else {
            return;
        }
    }

    public void clear() {
        mBleArray.clear();
        notifyDataSetChanged();
    }
    public int getCount(){
    if(mBleArray != null && mBleArray.size()>0){
        return mBleArray.size();
    }
         return  0 ;
    }

    public List<BluetoothDevice> getmBleArray() {
        return mBleArray;
    }

    public void setIsselect(boolean isselect) {
        this.isselect = isselect;
    }

    public interface OnItemClickLitener
    {
        void onItemClick(BluetoothDevice device);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_device_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.itemView.setTag(position);
        if(mBleArray != null && mBleArray.size()>0){
            final BluetoothDevice device = mBleArray.get(position);
            if(isselect){
                if(position == clickPosition){
                    holder.tvConnectState.setSelected(true);
                    holder.tvConnectState.setText("已连接");
                    Log.i("列表","1");
                    isselect=false;
                }else {
                    holder.tvConnectState.setSelected(false);
                    holder.tvConnectState.setText("连接");
                    Log.i("列表","2");
                }

            }else {
                holder.tvConnectState.setSelected(false);
                holder.tvConnectState.setText("连接");
                Log.i("列表","3");
            }

//            boolean isSelect = mBleArray.get(position).isSelect();
//            if(isSelect){
//                if((Integer)holder.itemView.getTag() == position){
//                   holder.tvConnectState.setSelected(true);
//                }else {
//                    holder.tvConnectState.setSelected(false);
//                }
//            }else {
//                Log.d("123", "onBindViewHolder: ");
//            }
            String devName = device.getName();
            holder.itemView.setTag(position);
            if (devName != null && devName.length() > 0) {
                holder.tvDeviceName.setText(devName);
            } else {
                holder.tvDeviceName.setText("unknow-device");
            }
           // holder.tvDeviceName.setText(device.getAddress());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickPosition = (Integer) v.getTag();
//                    Log.i("选中","信息："+clickPosition);
                    if((Integer)holder.itemView.getTag() == position){
                        mOnItemClickLitener.onItemClick(device);
//                        notifyDataSetChanged();
                    }
                }
            });
        }else {
            holder.itemView.setTag(position);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
//        if(mBleArray != null  && mBleArray.size()>0){
            return mBleArray.size();
//        }
//        return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeviceName, tvChangeDevice,tvConnectState;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDeviceName = (TextView) itemView.findViewById(R.id.tvDeviceName);
            tvChangeDevice = (TextView) itemView.findViewById(R.id.tvChangeDevice);
            tvConnectState = (TextView) itemView.findViewById(R.id.tvConnectState);
        }
    }
}

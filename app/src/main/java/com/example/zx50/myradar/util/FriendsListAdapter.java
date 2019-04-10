package com.example.zx50.myradar.util;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.zx50.myradar.model.Contact;
import com.example.zx50.myradar.R;

import java.util.ArrayList;

public class FriendsListAdapter extends BaseAdapter {
    Context context;
    ArrayList<Contact> friendsCollection;
    private boolean isBtnVisible = false; //为false不显示删除按钮,为true则显示

    public boolean getIsBtnVisible() {
        return isBtnVisible;
    }

    public void setIsBtnVisible(boolean isBtnVisible) {
        this.isBtnVisible = isBtnVisible;
    }

    public FriendsListAdapter(Context context, ArrayList<Contact> friendsCollection) {
        this.context = context;
        this.friendsCollection = friendsCollection;
    }
    @Override
    public int getCount() {
        return friendsCollection.size();
    }

    @Override
    public Object getItem(int position) {
        return friendsCollection.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(context == null)
            context = parent.getContext();
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_item,null);
            viewHolder = new ViewHolder();
            viewHolder.friendname = (TextView)convertView.findViewById(R.id.name_cell);
            viewHolder.btnDel = (Button)convertView.findViewById(R.id.delete_button_cell);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)convertView.getTag();
        viewHolder.friendname.setText(friendsCollection.get(position).getName());
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "petiote.ttf");
        viewHolder.friendname.setTypeface(typeface);
        viewHolder.btnDel.setTag(position);
        viewHolder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication myApplication = (MyApplication)FriendsListAdapter.this.context.getApplicationContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View dialogview = View.inflate(context, R.layout.dialog_delete, null);
                final AlertDialog dialog = builder.create();
                dialog.setView(dialogview);
                TextView numberlabel = (TextView)dialogview.findViewById(R.id.txt_friend_number_label);
                TextView number = (TextView)dialogview.findViewById(R.id.txt_friend_number);
                number.setText(friendsCollection.get(position).getNumber());
                Button dialogOk = (Button)dialogview.findViewById(R.id.btn_dialog_ok);
                dialogOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyApplication myApplication = (MyApplication)FriendsListAdapter.this.context.getApplicationContext();
                        myApplication.delFriend(friendsCollection.get(position).getNumber());
                        //friendsCollection.remove(position);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                Button dialogClose = (Button)dialogview.findViewById(R.id.btn_dialog_close);
                dialogClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        if (!isBtnVisible){
            viewHolder.btnDel.setVisibility(View.GONE);
        }
        else {
            viewHolder.btnDel.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    public void addData(String name, String number) {
        Contact friend = new Contact();
        friend.setName(name);
        friend.setNumber(number);
        friendsCollection.add(friend);
    }

    class ViewHolder {
        TextView friendname;
        Button btnDel;
    }
}

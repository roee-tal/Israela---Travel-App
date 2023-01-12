package com.example.app.modelView.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.app.R;
import com.example.app.model.objects.User;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User>
{

    public UserAdapter(Context context, int resource, List<User> userListt)
    {
        super(context,resource,userListt);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        User site = getItem(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.shape_cell_users, parent, false);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.Usern);
//        ImageView iv = (ImageView) convertView.findViewById(R.id.mainImage);

        tv.setText(site.getEmail());
//        iv.setImageResource(site.getImage());
//        this.setimageView(site.getUsername(), convertView);

        return convertView;
    }
}
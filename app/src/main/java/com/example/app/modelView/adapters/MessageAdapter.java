package com.example.app.modelView.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.app.model.objects.Message;
import com.example.app.R;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Context context, int resource, List<Message> messageList) {
        super(context, resource, messageList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message mes = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.shape_cell_users, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.Usern);

        tv.setText(mes.getText());

        return convertView;
    }
}

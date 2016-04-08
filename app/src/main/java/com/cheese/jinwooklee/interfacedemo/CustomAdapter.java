package com.cheese.jinwooklee.interfacedemo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jinwooklee on 16-04-02.
 */
public class CustomAdapter extends ArrayAdapter<HashMap<String,String>> {
    private Context context;
    private ArrayList<HashMap<String, String>> data;
    private ArrayListener arrayListener;

    public CustomAdapter(Context context, ArrayList<HashMap<String,String>> virus) {
        super(context, R.layout.custom_row, virus);
        this.context = context;
        this.data = virus;
    }

    public interface ArrayListener{
        void rowClicked(String virusname);
    }

    public void setArrayListener(ArrayListener AL){
        this.arrayListener = AL;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolderItems viewHolderItems;

        //if convertView is  null this is the first time it is initiated
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_row, parent, false);

            //initialize Viewholder class and save its position and Textview
            viewHolderItems = new ViewHolderItems();

            viewHolderItems.position = position;


            viewHolderItems.virusText = (TextView) convertView.findViewById(R.id.virusText);
            viewHolderItems.countryText = (TextView) convertView.findViewById(R.id.countryText);
            viewHolderItems.dateText = (TextView) convertView.findViewById(R.id.dateText);



            //Setting TextViews values
            viewHolderItems.virusText.setText(this.data.get(viewHolderItems.position).get("virusname"));
            viewHolderItems.countryText.setText(this.data.get(viewHolderItems.position).get("country"));
            viewHolderItems.dateText.setText(this.data.get(viewHolderItems.position).get("lastupdated"));

        }
        //if it was then brings previous convertView again.
        //This is bascially to save memory when onclick method is created
        //avoiding expensive layoutinflater method call
        else{
            viewHolderItems = (ViewHolderItems) convertView.getTag();
        }

        if(viewHolderItems == null) {
            viewHolderItems = new ViewHolderItems();

            viewHolderItems.position = position;


            viewHolderItems.virusText = (TextView) convertView.findViewById(R.id.virusText);
            viewHolderItems.countryText = (TextView) convertView.findViewById(R.id.countryText);
            viewHolderItems.dateText = (TextView) convertView.findViewById(R.id.dateText);



            //Setting TextViews values
            viewHolderItems.virusText.setText(this.data.get(viewHolderItems.position).get("virusname"));
            viewHolderItems.countryText.setText(this.data.get(viewHolderItems.position).get("country"));
            viewHolderItems.dateText.setText(this.data.get(viewHolderItems.position).get("lastupdated"));
        }




        final ViewHolderItems ViewHolderItems = viewHolderItems;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text = (TextView)v.findViewById(R.id.virusText);
                arrayListener.rowClicked((String)text.getText());
            }

        });



        return convertView;

    }

    //Item holding class and keeping track of position of  rows
    static class ViewHolderItems{

        public TextView virusText;
        public TextView countryText;
        public TextView dateText;
        public int position;

    }
}

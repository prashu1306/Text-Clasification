package com.example.hp.pd_lab;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class all_fragment extends Fragment  {
    ArrayList<String> smsMessagesList1 = new ArrayList<>();
    //Button buttonSend;
    ListView listViewAll;
    EditText editTextMessages;
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public ArrayAdapter arrayAdapter1;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all, container, false);
        getActivity().setTitle("All Messages");
        listViewAll = (ListView) rootView.findViewById(R.id.listViewAll);
        arrayAdapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, smsMessagesList1);
        listViewAll.setAdapter(arrayAdapter1);
        refresh();
        listViewAll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {

                smsMessagesList1.remove(smsMessagesList1.get(position));
                arrayAdapter1.notifyDataSetChanged();
            }
        });

        return rootView;
    }
    public void refresh()
    {
        ContentResolver contentResolver = getActivity().getContentResolver();
        if (ContextCompat.checkSelfPermission(getActivity().getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
        }
        else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst())
            return;
        arrayAdapter1.clear();
        do {
            String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
            arrayAdapter1.add(str);
        } while (smsInboxCursor.moveToNext());
    }


}





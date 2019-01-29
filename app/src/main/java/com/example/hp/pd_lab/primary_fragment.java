package com.example.hp.pd_lab;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class primary_fragment extends Fragment
{
    Context context=null;
    ArrayList<String> smsMessagesList2 = new ArrayList<>();
        ListView listViewAll2;
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public  ArrayAdapter arrayAdapter2;
    int spamCount = 0;
    int hamCount = 0;
    int yy =0;
    int xx=0;
    Map<String, Node> map = new HashMap<String, Node>();
    List<String> commonWords = new ArrayList<String>();

    public primary_fragment(){}
    public primary_fragment(Context context) {
        this.context=context;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_primary,container,false);
        getActivity().setTitle("Primary");
        listViewAll2 = (ListView) rootView.findViewById(R.id.listViewprimary);
        arrayAdapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, smsMessagesList2);
        listViewAll2.setAdapter(arrayAdapter2);
        refresh();

        return rootView;
    }
    public void refresh()
    {

        ContentResolver contentResolver = getActivity().getContentResolver();
        if (ContextCompat.checkSelfPermission(getActivity().getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst())
            return;
        arrayAdapter2.clear();

        do {

            String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
            try {
                init();
                detect(str);
            }
            catch(Exception e)
            {

            }
        } while (smsInboxCursor.moveToNext());
    }
/*private boolean isExternalStorageReadable(){
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
            || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
        Toast.makeText(getActivity(), "Yes. It's readable.", Toast.LENGTH_SHORT).show();
        return true;
    }
    else{
        return false;
    }
}*/





    public void init() throws Exception {
        try {
            //FileInputStream fis = getContext().openFileInput("common-words.txt");
            InputStream fis = context.getAssets().open("common-words.txt");
            //if (xx == 0)
                //Toast.makeText(getActivity(), "Entered init", Toast.LENGTH_SHORT).show();
            //xx++;
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                commonWords.add(line);
            }
            bufferedReader.close();
        }
        catch (FileNotFoundException ex){
            Toast.makeText(getActivity(), "FileNotFoundException", Toast.LENGTH_SHORT).show();
        }
        catch (IOException ex){
            Toast.makeText(getActivity(), "IOException", Toast.LENGTH_SHORT).show();
        }
        /*BufferedReader br = new BufferedReader( new FileReader( new File("commom-words.txt")));
        if(xx == 0)
            Toast.makeText(getActivity(),"Entered init",Toast.LENGTH_SHORT).show();
        xx++;
        String line = br.readLine();
        while(line != null) {
            commonWords.add(line);
            line = br.readLine();
        }*/

        try {
            //BufferedReader br = new BufferedReader( new FileReader( new File("E:\\spam.txt")));
            InputStream fis = context.getAssets().open("spam.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            while (line != null) {
                line = line.toLowerCase();
                String[] words = line.split(" ");
                for (String s : words) {
                    if (s.length() > 3 && !commonWords.contains(s)) {
                        spamCount++;
                        if (map.containsKey(s)) {
                            map.get(s).spamCount++;
                        } else {
                            map.put(s, new Node(1, 0));
                        }
                    }
                }
                line = br.readLine();
            }
        }
        catch(FileNotFoundException ex){

        }
        catch(IOException ex){

        }
        try {
            //br = new BufferedReader( new FileReader( new File("E:\\ham.txt")));
            InputStream fis = context.getAssets().open("ham.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            while (line != null) {
                line = line.toLowerCase();
                String[] words = line.split(" ");
                for (String s : words) {
                    if (s.length() > 3 && !commonWords.contains(s)) {
                        hamCount++;
                        if (map.containsKey(s)) {
                            map.get(s).hamCount++;
                        } else {
                            map.put(s, new Node(0, 1));
                        }
                    }
                }
                line = br.readLine();
            }

            Set<String> keys = map.keySet();
            for( String key: keys ) {
                Node node = map.get(key);
                double res = ((node.spamCount)/(double)(spamCount))/(double)(((node.spamCount)/(double)(spamCount)) + (node.hamCount)/(double)(hamCount));
                node.probability = res;
            }

            br.close();
        }
        catch(FileNotFoundException ex){

        }
        catch (IOException ex){

        }
    }

    public void detect( String s ) {

        if(yy == 0)
        Toast.makeText(getActivity(),"Inside detect",Toast.LENGTH_SHORT).show();
        yy++;
        boolean result = false;
        s = s.toLowerCase();
        String[] sArr = s.split(" ");
        TreeMap<Double, List<Double>> interestMap = new TreeMap<Double, List<Double>>(Collections.reverseOrder());
        for( String x: sArr ) {
            if(x.length()> 3 && !commonWords.contains(x)) {
                double i = 0.5;
                double p = 0.5;
                if(map.containsKey(x)) {
                    p = map.get(x).probability;
                }
                i = Math.abs(i - p);
                if( !interestMap.containsKey(i) ) {
                    List<Double> values = new ArrayList<Double>();
                    values.add(p);
                    interestMap.put(i, values);
                } else {
                    interestMap.get(i).add(p);
                }
            }
        }

        List<Double> probabilities = new ArrayList<Double>();
        int count = 0;
        Set<Double> set = interestMap.keySet();
        for( Double d: set ) {
            List<Double> list = interestMap.get(d);
            for(Double x: list) {
                count++;
                probabilities.add(x);
                if(count == 100) {
                    break;
                }
            }
            if(count == 100) {
                break;
            }
        }

        double res = 1;
        double numerator = 1;
        double denominator = 1;
        for( Double d: probabilities ) {
            numerator = numerator * d;
            denominator = denominator * (1- d);
        }
        res = numerator/(double)(numerator +denominator);
        if(res > 0.5) {
            result = true;
        }

        if( result ) {
            //System.out.println("'" +s+ "' is spam");
           // Spam_fragment.arrayAdapter3.add(s);
        } else {
            //System.out.println("'" +s+ "' is not a spam");
            arrayAdapter2.add(s);
        }
    }

    public void moveToSpam( String s ) {
        s = s.toLowerCase();
        String[] sArr = s.split(" ");
        for( String x: sArr ) {
            if(x.length()> 3 && !commonWords.contains(x)) {
                spamCount++;
                if( map.containsKey(x)) {
                    map.get(x).spamCount++;
                } else {
                    map.put(x, new Node( 1, 0));
                }
            }
        }

        Set<String> keys = map.keySet();
        for( String key: keys ) {
            Node node = map.get(key);
            double res = ((node.spamCount)/(double)(spamCount))/(double)(((node.spamCount)/(double)(spamCount)) + (node.hamCount)/(double)(hamCount));
            node.probability = res;
        }
    }
}
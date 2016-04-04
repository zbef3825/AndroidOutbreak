package com.cheese.jinwooklee.interfacedemo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class sqlData{

    private Context context;
    private SQLiteDatabase sqLiteDatabase;
    private Cursor c;

    sqlData(Context context){
        this.context = context;
        onCreate();
    }

    public void onCreate(){
        try{
            sqLiteDatabase = this.context.openOrCreateDatabase("virus_DB", Context.MODE_PRIVATE, null);
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS virusregions (virusname VARCHAR, country VARCHAR, lastupdated INT(8))");
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    public void insertRegionDatabase(String virus, String country, int lastupdate){
        try{
            sqLiteDatabase.execSQL("INSERT INTO virusregions(virusname, country, lastupdated) VALUES('" + virus + "', '" + country + "', " + String.valueOf(lastupdate) + ")");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String YYYYMMDDtoString(int month){
        String result = null;
        String dateString = String.valueOf(month);

        String monthString = dateString.substring(4, 6);
        switch (monthString){
            case "01":
                result = "January";
                break;
            case "02":
                result = "February";
                break;
            case "03":
                result = "March";
                break;
            case "04":
                result = "April";
                break;
            case "05":
                result = "May";
                break;
            case "06":
                result = "June";
                break;
            case "07":
                result = "July";
                break;
            case "08":
                result = "August";
                break;
            case "09":
                result = "September";
                break;
            case "10":
                result = "October";
                break;
            case "11":
                result = "November";
                break;
            case "12":
                result = "December";
                break;
            default:
                result = "Error";
                break;
        }
        result += " " + dateString.substring(6) + ", " + dateString.substring(0, 4);
        return result;
    }

    //Arg1 is from sqlite database and arg2 is from the server
    public Boolean lastrowsCompare(ArrayList<HashMap<String,String>> arg1, ArrayList<HashMap<String,String>> arg2){
        String arg2DateString = YYYYMMDDtoString(Integer.parseInt(arg2.get(0).get("lastupdated")));
        if(arg1.size() == 0){
            return false;
        }
        else if((arg1.get(0).get("virusname") == arg2.get(0).get("virusname")) && (arg1.get(0).get("country") == arg2.get(0).get("country")) && (arg1.get(0).get("lastupdated") == arg2DateString)){
            return true;
        }
        else {
            return false;
        }
    }

    public ArrayList<HashMap<String,String>> countrywithVirus(String virusname){
        //When you press a virus in a row, get all countries that experiences with that virus

        Cursor c;
        c = sqLiteDatabase.rawQuery("SELECT * FROM virusregions WHERE virusname like '%"+ virusname + "%' ORDER BY lastupdated DESC" , null);



        return queryResult(c);

    }

    public ArrayList<HashMap<String,String>> getVirusesfromCountry(String country){
        //When you press a country, get all virus that the country experiences
        c = sqLiteDatabase.rawQuery("SELECT * FROM virusregions WHERE country like '%"+ country + "%' ORDER BY lastupdated DESC", null);


        return queryResult(c);
    }


    public ArrayList<HashMap<String, String>> retrieveRegionDatabase(int limit){
        //this is the ArrayList that will be outputted

        if (limit == 0){
            //if limit is zero then requesters gets all the database
            //data intensive. try to avoid this
            c = sqLiteDatabase.rawQuery("SELECT * FROM virusregions ORDER BY lastupdated DESC", null);
        }
        else {
            //set limit
            c = sqLiteDatabase.rawQuery("SELECT * FROM virusregions ORDER BY lastupdated DESC LIMIT " + String.valueOf(limit), null);
        }

        return queryResult(c);
    }

    public ArrayList<HashMap<String, String>> queryResult(Cursor c){
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        //defining columnIndex for each column
        int virusnameIndex = c.getColumnIndex("virusname");
        int countryIndex = c.getColumnIndex("country");
        int lastupdateIndex = c.getColumnIndex("lastupdated");

        //Cursor is now at the first row
        c.moveToFirst();

        //loop until cursor lands at null(end of the row)
        while (!c.isAfterLast()){
            HashMap<String, String> map = new HashMap<>();
            map.put("virusname", c.getString(virusnameIndex));
            map.put("lastupdated", YYYYMMDDtoString(c.getInt(lastupdateIndex)));
            map.put("country", c.getString(countryIndex));
            result.add(map);
            c.moveToNext();
        }

        c.close();

        return result;
    }



    public void destroySQL(Context context){
        try{
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS virusregions");
            context.deleteDatabase("virus_DB");
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}

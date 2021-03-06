package github.groupa.fintech;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@SuppressLint("SimpleDateFormat")
public class CustomDateParser {

    public static String customDateParser(String getDate){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy", new Locale("en", "US"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = null;
        try {
            date = sdf.parse(getDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String resultDate = simpleDateFormat.format(date);

        return resultDate.substring(0,1).toUpperCase()+resultDate.substring(1);
    }

    public static String customDateParser(String getDate, String pattern){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("en", "US"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(getDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String resultDate=simpleDateFormat.format(date);
        if (pattern.equals("MMM"))
            resultDate=simpleDateFormat.format(date).replaceAll("\\.","");

        return resultDate;
    }
}

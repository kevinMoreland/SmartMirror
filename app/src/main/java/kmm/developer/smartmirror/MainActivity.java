package kmm.developer.morningalarm;


import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import android.text.Html;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;

import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import android.os.Handler;

public class MainActivity extends Activity {
    public static double longitude, latitude;
    public static String city;
    private static MainActivity inst;
    PendingIntent pendingIntent;
    Intent myIntent;
    private Handler handler;
    int delay = 1900000;
    //int delay = 5000;

    TextView weatherInfo, news, firstSurfSpot, secondSurfSpot;
    ImageView weatherInfoIcon;
    public static MainActivity instance(){
        //return context of MainActivity for GetData class
        return inst;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inst = this;
        setContentView(R.layout.activity_main);
        hideButtons();


        //keep tablet from going to sleep
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //gather data once initially to fill screen
        gatherData();

        handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                gatherData();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //hide soft keys and title when touched
        hideButtons();

        /*redo getLocation on touch. This gives an easy way to update the location displayed without
        constantly checking for a location change or requiring app restart*/
        getLocation();

        return super.onTouchEvent(event);
    }

    private void hideButtons() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
    private void gatherData(){
        getLocation();

        myIntent = new Intent(MainActivity.this, GetData.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, PendingIntent.FLAG_NO_CREATE);
        final GetData data = new GetData();
        data.execute();
    }
    private void getLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            city = getCityName(latitude, longitude);
        }

    }
    private String getCityName(double lattitude, double longitude) {
        //not my code, but this inputs the latitude and longitude to find city. This is called reverse geocoding
        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {

            List<Address> addresses = gcd.getFromLocation(lattitude, longitude,
                    10);

            for (Address adrs : addresses) {
                if (adrs != null) {
                    String city = adrs.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                        System.out.println("city ::  " + cityName);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;

    }
    public void setDisplayText(double windSpeed, int airTemp, String weatherCondition, int waterTemp, String topArticle, String secondArticle, String thirdArticle, double surfSizeSpot1, double surfSizeSpot2, String surfInfoSpot1, String surfInfoSpot2){
        DecimalFormat df = new DecimalFormat("#.#");
        weatherInfo = (TextView) findViewById(R.id.weatherInfo);
        news = (TextView) findViewById(R.id.article);
        weatherInfoIcon = (ImageView) findViewById(R.id.weatherInfoIcon);
        firstSurfSpot = (TextView) findViewById(R.id.firstSurfSpot);
        secondSurfSpot = (TextView) findViewById(R.id.secondSurfSpot);



        //weatherInfo.setText("Wind: " + windSpeed/2 + "Temp: " + airTemp + "Condition: " + weatherCondition + "Water: " + waterTemp);
        //SpannableString spString = new SpannableString("" +waterTemp);
        //spString.setSpan(new ForegroundColorSpan(Color.rgb(25, 173, 255)), 0, spString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //String waterTempColored = "<font color = '##19adff'>"+waterTemp+" f</font>";
        //weatherInfo.setText(airTemp + " f\n" + Html.fromHtml(waterTempColored));

        String waterTempColored = "<font color = '#19adff'>"+waterTemp+" f</font>";
        weatherInfo.setText(Html.fromHtml(airTemp + " f<br />" + waterTempColored));
        if(weatherCondition.contains("thunder") || weatherCondition.contains("storm")){
            weatherInfoIcon.setImageResource(R.drawable.thunder);
        }
        else if(weatherCondition.contains("shower") || weatherCondition.contains("rain") || weatherCondition.contains("hail") || weatherCondition.contains("snow") || weatherCondition.contains("drizzle")){
            weatherInfoIcon.setImageResource(R.drawable.rain);
        }
        else if(weatherCondition.contains("mostly cloudy") || weatherCondition.contains("partly cloudy")){
            weatherInfoIcon.setImageResource(R.drawable.partlycloudy);
        }
        else if(weatherCondition.contains("cloudy") || weatherCondition.contains("foggy") || weatherCondition.contains("haze")){
            weatherInfoIcon.setImageResource(R.drawable.cloudy);
        }
        else if(weatherCondition.contains("clear")){
            weatherInfoIcon.setImageResource(R.drawable.clear);
        }
        else{
            weatherInfoIcon.setImageResource(R.drawable.sunny);
        }
        //------------------surf info
        if(surfInfoSpot1.equals("Poor")){
            firstSurfSpot.setBackgroundColor(getResources().getColor(R.color.poor));
        }
        else if(surfInfoSpot1.equals("Poor-Fair")){
            firstSurfSpot.setBackgroundColor(getResources().getColor(R.color.poorFair));
        }
        else if(surfInfoSpot1.equals("Fair")){
            firstSurfSpot.setBackgroundColor(getResources().getColor(R.color.fair));
        }
        else if(surfInfoSpot1.equals("Good")){
            firstSurfSpot.setBackgroundColor(getResources().getColor(R.color.good));
        }

        if(surfInfoSpot2.equals("Poor")){
            secondSurfSpot.setBackgroundColor(getResources().getColor(R.color.poor));
        }
        else if(surfInfoSpot1.equals("Poor-Fair")){
            secondSurfSpot.setBackgroundColor(getResources().getColor(R.color.poorFair));
        }
        else if(surfInfoSpot1.equals("Fair")){
            secondSurfSpot.setBackgroundColor(getResources().getColor(R.color.fair));
        }
        else if(surfInfoSpot1.equals("Good")){
            secondSurfSpot.setBackgroundColor(getResources().getColor(R.color.good));
        }

        firstSurfSpot.setText("Pismo  \n" + df.format(surfSizeSpot1) + "ft");
        secondSurfSpot.setText("Morro Bay  \n"+ df.format(surfSizeSpot2) + "ft");

        //------------------------


        //deal with letting news scroll
        news.setText("     -     " + topArticle  + "     -     " + secondArticle  + "     -     " + thirdArticle);
        Animation newsScroll;

        int displayWidth = getDisplayWidth(this);
        int textWidth = getTextViewWidth(news)+500;
        if(displayWidth<textWidth) {
            newsScroll = new TranslateAnimation(
                    0, displayWidth-textWidth,
                    0, 0);
            newsScroll.setDuration(textWidth*4);    // Set duration so that it is relative to amount of text
            newsScroll.setStartOffset(0);
            newsScroll.setRepeatMode(Animation.INFINITE);
            newsScroll.setRepeatCount(Animation.INFINITE);    // Infinite animation.

            news.startAnimation(newsScroll);
        }
    }



    //found these two online
    private int getDisplayWidth(Context context) {
        int displayWidth;

        WindowManager windowManager = (WindowManager)context.getSystemService(
                Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point screenSize = new Point();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(screenSize);
            displayWidth = screenSize.x;
        } else {
            displayWidth = display.getWidth();
        }

        return displayWidth;
    }

    private int getTextViewWidth(TextView textView) {
        textView.measure(0, 0);    // Need to set measure to (0, 0).
        return textView.getMeasuredWidth();
    }

}

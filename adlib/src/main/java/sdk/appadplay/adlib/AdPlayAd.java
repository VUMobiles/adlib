package sdk.appadplay.adlib;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import sdk.appadplay.adlib.network.HttpHandler;

/**
 * Created by toukirul on 6/6/2018.
 */

public class AdPlayAd {

    private Context mContext;
    private RelativeLayout adLayout;
    private WebView mWebView;
    static VideoView videoview;

    public AdPlayAd(Context context, RelativeLayout layout) {
        this.mContext = context;
        this.adLayout = layout;
    }

    //==============================Interstitial Ad==================================//

    public void loadInterstitial(final String publisherID, final String packageName, final String requestType){

        adLayout.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSimSlot();
                String UserAgent = (new WebView(mContext)).getSettings().getUserAgentString();
                String userAgent = UserAgent.replaceAll(" ", "%20");
                String gID = getDeviceID();
                Log.d("DeviceId",gID);
                String url = "https://adsapi.adplay-mobile.com/adplaysdk?pid="+publisherID+"&useragent="+userAgent+"&type="+requestType+"&dimension=&gid="+gID+"&packagename="+packageName+"&request=interstitial";
                Log.d("ParentURL",url);

                new BackgroundTask().execute(url,"1");
            }
        }, 5000);

    }

    String url,type;
    private class BackgroundTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            url = params[0];
            type = params[1];
            HttpHandler handler = new HttpHandler();
            String response = handler.makeServiceCall(url);


            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result.isEmpty()){
                return;
            }
            try {
                JSONObject obj = new JSONObject(result);
                Log.d("Object","obj1:-----"+String.valueOf(obj));
                JSONObject obj2 = obj.getJSONObject("seatbid");
                JSONObject obj3 = obj2.getJSONObject("bid");

                String dimen = obj.getString("dim");
                Log.d("ParseData","Dim:"+dimen);
                String clickUrl = obj3.getString("nurl");
                Log.d("ParseData","DestinationUrl:"+clickUrl);
                String adUrl = obj3.getString("iurl");
                Log.d("ParseData","adUrl:"+adUrl);
                String logoUrl = obj3.getString("logo");
                Log.d("ParseData","logoUrl:"+logoUrl);
                String logoClickUrl = obj3.getString("logo_click");
                Log.d("ParseData","logoClickUrl:"+logoClickUrl);

                if (type.equals("2")){
                    //loadAd(adUrl, clickUrl, logoUrl, logoClickUrl, dimen);
                }else if (type.equals("1")){
                    createInterstitialAd(adUrl, clickUrl, logoUrl, logoClickUrl);
                }else if (type.equals("3")){
                    //loadInter(adUrl, clickUrl, logoUrl, logoClickUrl);
                }else if (type.equals("5")){
                    //loadAdHtml(adUrl, clickUrl, logoUrl, logoClickUrl, dimen);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void createInterstitialAd(String adUrl, String clickUrl, String logoUrl, String logoClickUrl) {
        Log.d("hhhhhhhhhhhhh","lol");
        adLayout.setVisibility(View.VISIBLE);

        Intent intent = new Intent(mContext, InterstitialAd.class);
        intent.putExtra("adUrl",adUrl);
        intent.putExtra("clickUrl",clickUrl);
        intent.putExtra("logoUrl",logoUrl);
        intent.putExtra("logoClickUrl",logoClickUrl);
        mContext.startActivity(intent);


    }

    private String getSimSlot() {

        try{
            sdk.appadplay.adlib.TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(mContext);
            Log.d("SIMSLOT",String.valueOf(telephonyInfo.isDualSIM()));
            if (telephonyInfo.isDualSIM()){
                return "2";
            }else {
                return "1";
            }
        }catch (RuntimeException e){
            e.printStackTrace();
        }

        return "2";
    }

    private String getDeviceID() {
        String divID = null;
        try {
            divID = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return divID;
    }

    //*******************************END of interstitial ad Method*****************************//



    //==============================Video Ad==================================//

    public void loadVideoAd(final String myPublisherId,VideoAdCallBack videoAdCallBack) {

        this.videoAdCallBackStart = videoAdCallBack;


        String UserAgent = (new WebView(mContext)).getSettings().getUserAgentString();
        String userAgent = UserAgent.replaceAll(" ", "%20");
        String bannerAdUrl = "https://adsapi.adplay-mobile.com/adplayapi?mode=sdk&pid=" + myPublisherId +
                "&fp=2&useragent=" + userAgent + "&pos=63&request=video&response=json";
        Log.d("BannerAdUrl", bannerAdUrl);

//        JsonObjectRequest request = new JsonObjectRequest(bannerAdUrl, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//
//                try {
//                    JSONObject mainObject = response.getJSONObject("seatbid");
//                    JSONObject seatBidObj = mainObject.getJSONObject("bid");
//                    String nurl = seatBidObj.getString("nurl");
//                    String videoUrl = seatBidObj.getString("video_url");
//
//                    JSONObject objVideo = seatBidObj.getJSONObject("video");
//                    String playMin = objVideo.getString("play_minutes");
//                    String adRole = objVideo.getString("role");
//                    String repeat = objVideo.getString("repeat");
//
//
//
//                    if (!nurl.equals(null) || !nurl.isEmpty() ) {
//                        playVideo(context, nurl, videoUrl, playMin, adLayout,adRole,repeat);
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("Response", error.toString());
//            }
//        });
//
//        Volley.newRequestQueue(context).add(request);

        new GetVideoAdProperties().execute(myPublisherId);

    }

    public class GetVideoAdProperties extends AsyncTask<String, Void, String> {

        String UserAgent = (new WebView(mContext)).getSettings().getUserAgentString();
        String userAgent = UserAgent.replaceAll(" ", "%20");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String pubId = params[0];

            String bannerAdUrl = "https://adsapi.adplay-mobile.com/adplayapi?mode=sdk&pid=" + pubId +
                    "&fp=2&useragent=" + userAgent + "&pos=63&request=video&response=json";
            Log.d("BannerAdUrl", bannerAdUrl);
            HttpHandler sh = new HttpHandler();
            String jsonString = sh.makeServiceCall(bannerAdUrl);


            return jsonString;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject obj = new JSONObject(result);
                JSONObject mainObject = obj.getJSONObject("seatbid");
                JSONObject seatBidObj = mainObject.getJSONObject("bid");
                String nurl = seatBidObj.getString("nurl");
                String videoUrl = seatBidObj.getString("video_url");

                JSONObject objVideo = seatBidObj.getJSONObject("video");
                String playMin = objVideo.getString("play_minutes");
                String adRole = objVideo.getString("role");
                String repeat = objVideo.getString("repeat");

                if (!nurl.equals(null) || !nurl.isEmpty() ) {
                    playVideo(mContext, nurl, videoUrl, playMin, adLayout,adRole,repeat);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint({"ResourceType", "ClickableViewAccessibility"})
    private void playVideo(final Context context, final String nurl, final String videoUrl, String playMin, final RelativeLayout adLayout, final String adRole, final String repeat) {

        adLayout.setVisibility(View.GONE);

        final RelativeLayout subLayout = new RelativeLayout(context);
        final RelativeLayout.LayoutParams subLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        final TextView txtTimeRemain = new TextView(context);
        txtTimeRemain.setTextColor(Color.WHITE);
        txtTimeRemain.setId(3);

        int time;
        int afterPlay = Integer.parseInt(playMin);

        videoview = new VideoView(context);
        videoview.setId(4);
        videoview.setClickable(true);
        videoview.requestFocus();
        videoview.setZOrderOnTop(true);


        final RelativeLayout.LayoutParams videoParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        videoParams.addRule(RelativeLayout.CENTER_VERTICAL);
        videoParams.addRule(RelativeLayout.CENTER_HORIZONTAL);


        final Button btnClose = new Button(context);
        btnClose.setText("SKIP AD");
        btnClose.setTextColor(Color.WHITE);
        btnClose.setBackgroundResource(Color.parseColor("#00000000"));
        btnClose.setId(2);

        final RelativeLayout.LayoutParams txtParams = new RelativeLayout.LayoutParams(25,25);
        txtParams.addRule(RelativeLayout.BELOW,videoview.getId());
        txtParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        txtParams.setMargins(0, 0, 10,10);

        final RelativeLayout.LayoutParams btnCloseParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        btnCloseParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


        if (adRole.equals("2")){
            // video ad will play after 15 sec
            time = 15000;
        }else {
            // video ad will play after 1 sec
            time = 1000;
        }
        videoview.setVideoURI(Uri.parse(videoUrl));

        final Handler handler = new Handler();
        final Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                adLayout.setVisibility(View.VISIBLE);
                adLayout.setBackgroundColor(Color.BLACK);
                btnClose.setVisibility(View.GONE);

                //videoview.requestFocus();

                if (videoAdCallBackStart!=null){
                    videoAdCallBackStart.isPlayingVideoAD(true);
                }
                videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        try{
                            Log.d("VideoAd","Prepare");
                            videoview.start();
                            Log.d("VideoAd","Start");
                            if (videoview.isPlaying()){
                                Log.d("VideoAd","Playing");
                                new CountDownTimer(5000, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        double remian = millisUntilFinished / 1000;
                                        int remainTime = (int) remian;
                                        String s = String.valueOf(remainTime);
                                        txtTimeRemain.setText(s);
                                        Log.d("RemainTime",s);
                                    }

                                    public void onFinish() {
                                        btnClose.setVisibility(View.VISIBLE);
                                        txtTimeRemain.setVisibility(View.GONE);
                                    }

                                }.start();

                            }
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        handler.postDelayed(myRunnable, afterPlay * 1000);

        subLayout.addView(txtTimeRemain,txtParams);
        subLayout.addView(btnClose, btnCloseParams);
        subLayout.addView(videoview, videoParams);
        adLayout.addView(subLayout, subLayoutParams);


        videoview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("vclick", "click");

                try{
                    videoview.pause();
                    videoview.stopPlayback();
                    adLayout.setVisibility(View.GONE);
                    videoview = null;
                    videoview.setVisibility(View.GONE);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

                try {
                    handler.removeCallbacks(myRunnable);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(nurl));
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoview.isPlaying()) {
                    videoview.stopPlayback();
                    videoview.setVisibility(View.GONE);
                    adLayout.setVisibility(View.GONE);
                    handler.removeCallbacks(myRunnable);
                    if (videoAdCallBackStart!=null){
                        videoAdCallBackStart.skipVideoAd(true);
                    }
                }
            }
        });

        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                adLayout.setVisibility(View.GONE);
                videoview.setVisibility(View.GONE);
                Log.d("VideoAd","Finish");
                handler.removeCallbacks(myRunnable);
                if (videoAdCallBackStart!=null){
                    videoAdCallBackStart.finishVideoAd(true);
                }
            }
        });
    }
    VideoAdCallBack videoAdCallBackStart;

    public interface VideoAdCallBack{
        public void isPlayingVideoAD(boolean isPlaying);
        public void finishVideoAd(boolean finishAd);
        public void skipVideoAd(boolean skipAd);
    }

    //*******************************END of video ad Method*****************************//
}

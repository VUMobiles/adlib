package sdk.appadplay.adlib;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

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

    public AdPlayAd(Context context, RelativeLayout layout) {
        this.mContext = context;
        this.adLayout = layout;
    }

    public void loadVideoAd(final String publisherID, final String packageName, final String requestType){

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
        }, 1000);

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
            TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(mContext);
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

}

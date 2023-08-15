package com.ericingland.randomgiftfinder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView mLabelTitle;
    private NetworkImageView mMainImage;
    private ImageLoader mImageLoader;
    private String mUrl;
    private final DatabaseHelper mDatabaseHelper = new DatabaseHelper(this);
    private ProgressDialog pDialog;
    private ArrayList<String> dictionary;
    private Drawer mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.app_long_name);
            setSupportActionBar(toolbar);
        }

        mLabelTitle = (TextView)findViewById(R.id.textTitle);
        mMainImage = (NetworkImageView) findViewById(R.id.imageMain);
        mImageLoader = HttpRequestHandler.getInstance(MainApplication.getContext()).getImageLoader();
        final String url = "http://";
        mImageLoader.get(url, ImageLoader.getImageListener(mMainImage, R.drawable.placeholder_gift, R.drawable.placeholder_gift));
        mMainImage.setImageUrl(url, mImageLoader);
        mMainImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.placeholder_gift));

        mUrl = getString(R.string.default_gift_link);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        createDictionary();
        onClickGenerate(findViewById(R.id.btnGenerate));

        //create the drawer and remember the `Drawer` result object
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_history).withIcon(FontAwesome.Icon.faw_history),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_aboutopensource).withIcon(FontAwesome.Icon.faw_info)
                        )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 0:
                                break;
                            case 1:
                                Intent historyIntent = new Intent(MainActivity.this, HistoryActivity.class);
                                MainActivity.this.startActivity(historyIntent);
                                break;
                            case 2:
                                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                                MainActivity.this.startActivity(settingsIntent);
                                break;
                            case 3:
                                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                                MainActivity.this.startActivity(aboutIntent);
                                break;
                        }

                        return false;
                    }
                })
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_share) {
            startActivity(Intent.createChooser(mCreateShareIntent(), getResources().getText(R.string.share)));
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent mCreateShareIntent() {
        // Create share intent
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, mLabelTitle.getText().toString());
        sendIntent.putExtra(Intent.EXTRA_TEXT, mUrl);
        sendIntent.setType("text/plain");

        return sendIntent;
    }

    public void onClickBrowser(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(mUrl));
        startActivity(i);
    }

    public void onClickGenerate(View v) {
        String url = createURL(getRandomWord());
        Log.d("Query", url);
        showpDialog();
        v.setEnabled(false);

        JsonObjectRequest request = new JsonObjectRequest(Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", response.toString());

                try {
                    // Parsing json object response

                    JSONObject item = response.getJSONArray("findItemsAdvancedResponse").getJSONObject(0)
                        .getJSONArray("searchResult").getJSONObject(0)
                        .getJSONArray("item").getJSONObject(0);

                    Item i = new Item();
                    String title = item.getString("title");
                    i.setTitle(title.substring(2, title.length() - 2));
                    String imageUrl = item.getString("galleryURL").replace("\\", "");
                    i.setImageUrl(imageUrl.substring(2, imageUrl.length() - 2));
                    String url = item.getString("viewItemURL").replace("\\", "");
                    i.setUrl("http://rover.ebay.com/rover/1/711-53200-19255-0/1?ff3=4&pub=5575149730&toolid=10001&campid=5337794426&customid=randomgiftfinder&mpre=" +
                            url.substring(2, url.length() - 2));

                    mImageLoader = HttpRequestHandler.getInstance(MainApplication.getContext()).getImageLoader();

                    mMainImage.setImageUrl(i.getImageUrl(), mImageLoader);

                    mUrl = i.getUrl();

                    mLabelTitle.setText(i.getTitle());

                    if (!i.getUrl().equals("http://")) {
                        mDatabaseHelper.addItem(i);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    if (e.getMessage().equals("No value for item")) {
                        Toast.makeText(getApplicationContext(),
                                "Error: No search results.\nPlease set fewer filters in Settings.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        HttpRequestHandler.getInstance(this).addToRequestQueue(request);
        v.setEnabled(true);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private String createURL(String query){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String URL = "http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsAdvanced&";
            URL += "SERVICE-VERSION=1.13.0&";
            URL += "SECURITY-APPNAME=EricIngl-a1ab-47b4-aef3-7efeec731ca2&";
            URL += "RESPONSE-DATA-FORMAT=JSON&";
            URL += "GALLERYSIZEENUM=Large&";
            URL += "geoTargeting=true&";
            URL += "networkId=9&";
            URL += "trackingId=5337794426&";
            URL += "REST-PAYLOAD&keywords=";
            URL += query.replace(" ", "%20");
        if (prefs.getBoolean("parent_filter_preference", false)) {
            URL += prefs.getString("child_filter_static_terms_preference", "") + "&";
            URL += "categoryId=" + prefs.getString("child_filter_department_preference", "1") + "&";
            URL += "itemFilter(0).name=MaxPrice&";
            URL += "itemFilter(0).value=" + prefs.getString("child_filter_max_price_preference", "999") + "&";
            URL += "itemFilter(1).name=MinPrice&";
            URL += "itemFilter(1).value=" + prefs.getString("child_filter_min_price_preference", "1");
        }

        return URL;
    }

    private void createDictionary(){
        dictionary = new ArrayList<>();

        BufferedReader dict; //Holds the dictionary file
        AssetManager am = this.getAssets();

        try {
            //dictionary.txt should be in the assets folder.
            dict = new BufferedReader(new InputStreamReader(am.open("ospd.txt")));

            String word;
            while((word = dict.readLine()) != null){
                if(word.length() < 4) {
                    dictionary.add(word);
                }
            }
            dict.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    private String getRandomWord(){
        return dictionary.get((int) (Math.random() * dictionary.size()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDrawer.setSelectionAtPosition(0);
    }
}

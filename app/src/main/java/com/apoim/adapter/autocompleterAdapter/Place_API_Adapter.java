package com.apoim.adapter.autocompleterAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.apoim.R;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mindiii-rahul on 15/6/17.
 */

public class Place_API_Adapter extends ArrayAdapter implements Filterable {
    private static final String LOG_TAG = "Error_place_search";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static  String API_KEY = "AIzaSyBTIP6X5lRNZimXfvbzAe5kYR_oPTFSbyg";
    Context context;
    private ArrayList resultList;
    private int textViewResourceId;


    public Place_API_Adapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.textViewResourceId = textViewResourceId;
        //API_KEY = context.getString(R.string.API_KEY);
        this.context = context;

    }

    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            String sb = PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON + "?key=" + API_KEY +
                    "&input=" + URLEncoder.encode(input, "utf8");
            //sb.append("&components=country:gr");

            URL url = new URL(sb);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            /**///Log.e(LOG_TAG, "ErrorAPI URL", e);
            return null;
        } catch (IOException e) {
            /**///Log.e(LOG_TAG, "ErrorPlaces API", e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            /**///Log.e("result",jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                /**///System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                /**///System.out.println("============================================================");
                String str = predsJsonArray.getJSONObject(i).getString("description") + "SPLIT" + predsJsonArray.getJSONObject(i).getString("id");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            /**///Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    public static String[] getLatLong(String cityname_from_Adapter, Context context) {
        String result[] = new String[2];
        String Latitude="", Longitude="";
        if (Geocoder.isPresent()) {
            try {
                String location = cityname_from_Adapter;
                Geocoder gc = new Geocoder(context);
                List<Address> addresses = gc.getFromLocationName(location, 2); // get the found Address Objects

                List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                for (Address a : addresses) {
                    if (a.hasLatitude() && a.hasLongitude()) {
                        Latitude = String.valueOf(a.getLatitude());
                        result[0] = Latitude;
                        Longitude = String.valueOf(a.getLongitude());
                        result[1] = Longitude;
                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));

                    }
                  Log.e("latlong",ll.toString());

                }
                Log.e("Data..........",Latitude+"  "+Longitude );
            } catch (IOException e) {
                // handle the exception
            }
        }
        return result;
    }

    @Override
    public int getCount() {
        if (resultList == null) return 0;
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return (String) resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();
                try {
                    if (constraint != null && constraint.length() > 0) {

                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());
                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                } catch (Exception e) {

                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                try {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        return filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View itemView = inflater.inflate(R.layout.list_item, parent, false);
        TextView textView = itemView.findViewById(R.id.text_cus);

        try {
            textView.setText((CharSequence) resultList.get(position));


        } catch (IndexOutOfBoundsException e) {
        }
        return itemView;

    }


}

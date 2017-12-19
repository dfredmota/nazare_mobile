package br.com.resoluteit.task;

import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.resoluteit.delegate.AdressFromGPSDelegate;
import br.com.resoluteit.util.JsonHttpRequest;


/**
 * Created by Usuario on 13/07/2016.
 */
public class AddressFromGPSTask extends AsyncTask<Double,Void,String> {

    private String TAG = "AddressLoaderTask";
    private Context mContext;
    private String errorMessage;

    private AdressFromGPSDelegate adressFromGPSDelegate;

    public AddressFromGPSTask(AdressFromGPSDelegate activity){

        this.adressFromGPSDelegate = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Double... params) {
        Double latitude = params[0];
        Double longitude = params[1];

        String endereco = "";

        List<String[]> listaEnderecos = new ArrayList<String[]>();

        JsonHttpRequest httpRequest = new JsonHttpRequest();
        try {
            httpRequest.start("http://maps.google.com/maps/api/geocode/json?address=");
            String json = httpRequest.getJsonResponse(latitude.toString() + "," + longitude.toString() + "&sensor=false");
            JSONObject jsonObject = new JSONObject(json);
            if(jsonObject.getString("status").toString().equalsIgnoreCase("OK")) {
                int j = 0;
                JSONArray results = jsonObject.getJSONArray("results");
                JSONObject r = results.getJSONObject(j);
                endereco = (String)r.get("formatted_address");

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        errorMessage = "Não conseguimos localizar seu endereço, por favor faça a busca pelo CEP!";
        return endereco;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        adressFromGPSDelegate.retrieveAdress(result);
    }
}

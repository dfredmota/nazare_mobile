package br.com.resoluteit.util;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.resoluteit.model.Usuario;


/**
 * Created by fred on 20/10/16.
 */

public class Data {


    public static void insertProdutoNaoEcontrado(SharedPreferences mPrefs,Integer qtd){

        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        prefsEditor.putInt("naoEncontrado",qtd);

        prefsEditor.commit();

    }

    public static Integer getQuantidadeProdutoNaoEncontrado(SharedPreferences mPrefs){

        Integer qtd = mPrefs.getInt("naoEncontrado",0);

        return qtd;

    }


    public static void insertUsuario(SharedPreferences mPrefs, Usuario usuario){

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gSon=  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        String json = gSon.toJson(usuario);
        prefsEditor.putString("usuario", json);
        prefsEditor.commit();

    }


    public static Usuario getUsuario(SharedPreferences mPrefs){

        Usuario user = null;

        Gson gSon=  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

        String json = mPrefs.getString("usuario", "0");

        if(json != null && !json.equals("0"))
        user = gSon.fromJson(json, Usuario.class);

        return user;
    }

    public static void loggoutUsuario(SharedPreferences mPrefs ){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.remove("usuario");
        editor.apply();
    }



}

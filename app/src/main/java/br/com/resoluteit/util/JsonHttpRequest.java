package br.com.resoluteit.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Desenvolvimento on 24/06/2016.
 */
public class JsonHttpRequest {

    private HttpURLConnection connection;
    private String urlPath;
    private boolean needsParams;

    public JsonHttpRequest() {
        needsParams = true;
    }

    public void start(String urlPath) throws IOException {
        needsParams = true;
        this.urlPath = urlPath;
        /* TODO: A forma correta é a URL ser iniciada aqui, quando o objeto não exigir que enviemos os dados na URL, esse código deverá ser alterado para o abaixo.
        *    URL url = new URL(urlPath);
        * this.connection = (HttpURLConnection) url.openConnection();
        * this.connection.setRequestMethod("GET");
        * this.connection.connect();
        */
    }

    public void startWithoutParams(String urlPath) throws IOException {
        this.urlPath = urlPath;
        URL url = new URL(this.urlPath);
        this.connection = (HttpURLConnection) url.openConnection();
        this.connection.setRequestMethod("GET");
        this.needsParams = false;
    }

    public HttpURLConnection getConnection(){
        return connection;
    }

    public String getJsonResponse(String params) throws IOException {
        if(needsParams){
            URL url = new URL(this.urlPath + params);
            this.connection = (HttpURLConnection) url.openConnection();
            this.connection.setRequestMethod("GET");
        }
        this.connection.connect();
        InputStream input = this.connection.getInputStream();
        int nRead;
        byte[] data = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        while((nRead = input.read(data, 0, data.length)) != -1) {
            outputStream.write(data,0,nRead);
        }

        return new String(outputStream.toByteArray());
    }

}

package br.com.resoluteit.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import br.com.resoluteit.model.ProdutoBean;
import resoluteit.com.br.R;


public class CodBarraAct extends AppCompatActivity {

    private Handler handler = new Handler();

    Context context;
    String re = "";
    ImageView imageProduto;
    private TextView txCodBarras;
    private TextView txTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cod_barra);


        imageProduto = (ImageView) findViewById(R.id.imageProduto);

        this.context = this;

        txCodBarras = (TextView) findViewById(R.id.txCodBarras);
        txTitulo = (TextView) findViewById(R.id.txTitulo);


        View btnScan = findViewById(R.id.scan_button);

        // Scan button
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set the last parameter to true to open front light if available

                IntentIntegrator integrator = new IntentIntegrator(CodBarraAct.this);
                integrator.setMessage("Posicione corretamente o código de barras...");
                integrator.initiateScan();

                // IntentIntegrator.initiateScan(MainActivity.this, R.layout.capture,
                //      R.id.viewfinder_view, R.id.preview_view, true);
            }
        });
    }




    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            re = scanResult.getContents();
            new DownloadImagemAsyncTask().execute("http://cosmos.bluesoft.com.br/produtos/"+re);
            //new DownloadImagemAsyncTask().execute("https://s3.amazonaws.com/bluesoft-cosmos/products/"+re);
            Log.d("code", re);
        }
        // else continue with any other code you need in the method

    }


    class DownloadImagemAsyncTask extends
            AsyncTask<String, Void, ProdutoBean> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(
                    CodBarraAct.this,
                    "Aguarde", "Carregando a  imagem...");
        }

        @Override
        protected ProdutoBean doInBackground(String... params) {
            String urlString = params[0];
            ProdutoBean produtoBean = null;
            URL u = null;
            try {
                u = new URL (urlString);
            } catch (MalformedURLException e3) {
                // TODO Auto-generated catch block
                e3.printStackTrace();
            }

            HttpURLConnection huc = null;
            try {
                huc = ( HttpURLConnection )  u.openConnection ();
            } catch (IOException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
            huc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            try {
                huc.setRequestMethod ("GET");
            } catch (ProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }  //OR  huc.setRequestMethod ("HEAD");
            int code = 0;
            try {
                huc.connect () ;
                code = huc.getResponseCode();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            if (code == 200){
                BufferedReader br = null;


                try {
                    if (200 <= huc.getResponseCode() && huc.getResponseCode() <= 299) {
                        br = new BufferedReader(new InputStreamReader((huc.getInputStream())));
                    } else {
                        br = new BufferedReader(new InputStreamReader((huc.getErrorStream())));
                    }



                    String output = "";
                    String finalTexto = "";
                    String saida = "";
                    boolean comecouEscrita = false;
                    while ((output = br.readLine()) != null) {
                        finalTexto = output;
                        //output = output + br.readLine()+"\n";
                        if (comecouEscrita == true){
                            saida = saida + finalTexto;
                            comecouEscrita = false;
                        }
                        if (finalTexto.toLowerCase().indexOf("<div class='thumbnail'>".toLowerCase()) == 0){
                            comecouEscrita = true;
                            //saida = saida + finalTexto;

                        }



                    }

                    huc.disconnect();
                    if (saida != null){

                        produtoBean = new ProdutoBean();


                        int inicioTitulo = saida.indexOf("title");
                        String titulo = saida.substring(inicioTitulo + 7);
                        int fimTitulo = titulo.indexOf("\"");
                        String tituloFinal = titulo.substring(0, fimTitulo);





                        int inicioImagem = saida.indexOf("src");
                        String imagem = saida.substring(inicioImagem + 5);
                        int fimImagem = imagem.indexOf("\"");
                        String imagemFinal = imagem.substring(0, fimImagem);
                        produtoBean.setCodigoBarras(tituloFinal.substring(0, tituloFinal.indexOf("-")));
                        produtoBean.setTitulo(tituloFinal);

                        if (imagemFinal.indexOf("assets") > 0){
                            produtoBean.setImagem(null);
                        }else{
                            URL uImagem = null;
                            try {
                                uImagem = new URL (imagemFinal);
                            } catch (MalformedURLException e3) {
                                // TODO Auto-generated catch block

                                e3.printStackTrace();
                            }

                            HttpURLConnection hucImagem = null;
                            try {
                                hucImagem = ( HttpURLConnection )  uImagem.openConnection ();
                            } catch (IOException e2) {
                                // TODO Auto-generated catch block
                                e2.printStackTrace();
                            }
                            hucImagem.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                            try {
                                hucImagem.setRequestMethod ("GET");
                            } catch (ProtocolException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }  //OR  huc.setRequestMethod ("HEAD");
                            InputStream isImagem = null;
                            try {
                                hucImagem.connect () ;
                                isImagem = hucImagem.getInputStream();
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }

                            Bitmap imagemBmp = BitmapFactory.decodeStream(isImagem);

                            produtoBean.setImagem(imagemBmp);
                            hucImagem.disconnect();
                        }

                    }
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }else{
                return null;
            }



            return produtoBean;


            //return null;
        }

        @Override
        protected void onPostExecute(ProdutoBean result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result != null){

                if (result.getImagem() == null){
                    imageProduto.setImageResource(R.drawable.pacote);
                }else{
                    imageProduto.setImageBitmap(result.getImagem());
                }
                txTitulo.setText(result.getTitulo());
                txCodBarras.setText(result.getCodigoBarras());

            } else {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(CodBarraAct.this).
                                setTitle("Alerta").
                                setMessage("Produto não catalogado, tente mais tarde novamente!").
                                setPositiveButton("OK", null);
                builder.create().show();
            }
        }
    }










}


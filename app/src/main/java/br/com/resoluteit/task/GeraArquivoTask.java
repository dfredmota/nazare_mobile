package br.com.resoluteit.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.file.NoSuchFileException;
import java.util.List;

import br.com.resoluteit.delegate.GerarArquivoDelegate;
import br.com.resoluteit.delegate.LoginDelegate;
import br.com.resoluteit.model.PesquisaPreco;
import br.com.resoluteit.model.Usuario;
import br.com.resoluteit.ws.WsDao;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;


/**
 * Created by fred on 20/10/16.
 */

public class GeraArquivoTask extends AsyncTask<Object, Boolean, Boolean> {

    private GerarArquivoDelegate gerarArquivoDelegate;

    private Context context;

    JSch jsch = new JSch();
    Session session = null;

    public GeraArquivoTask(GerarArquivoDelegate activity,Context context){

        this.gerarArquivoDelegate = activity;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Object... params) {

        boolean conectou = inicializaFTP();

        if(!conectou) {
            Toast.makeText(context, "Problemas de Conexão no FTP. Tente Mas Tarde!", Toast.LENGTH_LONG).show();
            return false;
        }

        List<PesquisaPreco> lista = ((List<PesquisaPreco>)params[0]);

        StringBuilder s = new StringBuilder();

        WsDao ws = new WsDao();

        String nomeArquivoOrigem = ws.getFileName(lista.get(0).getIdArquivo());

        nomeArquivoOrigem = "retorno_"+nomeArquivoOrigem;

        // primeiro vamos verificar se esse arquivo já existe no ftp
        ByteArrayOutputStream arquivo = getArquivo(nomeArquivoOrigem);


        // arquivo existe vamos escrever as pesquisa no final do mesmo
        if(arquivo != null){

            generateFileOnSDBytes(context,nomeArquivoOrigem,lista,arquivo.toByteArray());

        }
        // arquivo não existe vamos criar o mesmo escrever a pesquisa e enviar para o ftp
        else{

            generateFileOnSD(context,nomeArquivoOrigem,lista);

        }

        generateFileOnSD(context,nomeArquivoOrigem,lista);

        return true;

    }

    @Override
    protected void onPreExecute() {
        this.gerarArquivoDelegate.carregaDialog();
    }

    @Override
    protected void onPostExecute(Boolean sucesso) {

        session.disconnect();

        this.gerarArquivoDelegate.gerouArquivo(sucesso);
    }

    private boolean inicializaFTP(){

        try{

        session = jsch.getSession("root", "191.252.100.171", 22);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword("Valente@3873");
        session.connect();

        return true;


        } catch (JSchException e2) {
            e2.printStackTrace();
            return false;
        }

    }


    public void generateFileOnSDBytes(Context context, String sFileName, List<PesquisaPreco> lista,byte[] array) {

        try {

            File root = new File(Environment.getExternalStorageDirectory(), "nazare");

            if (!root.exists()) {
                root.mkdirs();
            }

            File gpxfile = new File(root, sFileName);

            PrintWriter writer = new PrintWriter(gpxfile, "UTF-8");

            // nesse caso como arquivo já existe vamos escrever o conteudo antigo no novo arquivo
            String conteudoArquivo = new String(array);

            writer.println(conteudoArquivo);

            for(PesquisaPreco pp : lista){

                String ean         = pp.getEan();

                String concorrente = pp.getConcorrente().substring(0,1);

                String preco       = completaPreco(pp.getPreco());

                String situacao    = pp.getSituacao();

                writer.print(ean);
                writer.print(concorrente);
                writer.print(concorrente);
                writer.print(preco);
                writer.println(situacao);
            }

            writer.flush();
            writer.close();

            // salva o arquivo no ftp
            salvaArquivoFTP(gpxfile,sFileName);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateFileOnSD(Context context, String sFileName, List<PesquisaPreco> lista) {

        try {

            File root = new File(Environment.getExternalStorageDirectory(), "nazare");

            if (!root.exists()) {
                root.mkdirs();
            }

            File gpxfile = new File(root, sFileName);
            PrintWriter writer = new PrintWriter(gpxfile, "UTF-8");

            for(PesquisaPreco pp : lista){

                String ean         = pp.getEan();

                String concorrente = pp.getConcorrente().substring(0,1);

                String preco       = completaPreco(pp.getPreco());

                String situacao    = pp.getSituacao();

                writer.print(ean);
                writer.print(concorrente);
                writer.print(concorrente);
                writer.print(preco);
                writer.println(situacao);
            }

            writer.flush();
            writer.close();

            // salva o arquivo no ftp
            salvaArquivoFTP(gpxfile,sFileName);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ByteArrayOutputStream getArquivo(String sFileName){

        ByteArrayOutputStream output = null;

        try {

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            sftpChannel.cd("/nazar");

            InputStream in = sftpChannel.get(sFileName);

            if(in ==  null){

                return null;
            }

            output = new ByteArrayOutputStream(1024);

            int c;
            while ((c = in.read()) != -1) {
                output.write(c);
            }

            in.close();
            channel.disconnect();
            sftpChannel.exit();


        } catch (JSchException e2) {
            e2.printStackTrace();
        } catch (SftpException e3) {
            e3.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;

    }


    private void salvaArquivoFTP(File file,String sFileName){

        try {

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            RandomAccessFile f = new RandomAccessFile(file, "r");
            byte[] b = new byte[(int)f.length()];
            f.readFully(b);

            InputStream myInputStream = new ByteArrayInputStream(b);

            sftpChannel.cd("/nazar");

            sftpChannel.put(myInputStream, sFileName, ChannelSftp.OVERWRITE);
            sftpChannel.exit();

            session.disconnect();
        } catch (JSchException e2) {
            e2.printStackTrace();
        } catch (SftpException e3) {
            e3.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }



    }


    private static String completaPreco(String preco) {

        preco = preco.replace("R$", "").replace(",","");

        StringBuilder sPreco = new StringBuilder(preco);

        while(sPreco.length() < 15) {

            sPreco.insert(0, "0");
        }

        return sPreco.toString();

    }
}

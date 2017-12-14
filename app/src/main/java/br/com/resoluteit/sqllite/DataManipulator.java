package br.com.resoluteit.sqllite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.jcraft.jsch.HASH;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.resoluteit.model.PesquisaPreco;


/**
 * Created by fred on 29/02/16.
 */
public class DataManipulator {

    private static Context context = null;

    static SQLiteDatabase db = null;

    private static final String DATABASE_NAME = "nazare.db";

    private static final int DATABASE_VERSION = 1;

    static final String TABLE_PESQUISA = "pesquisa_preco";

    static final String TABLE_RELATORIO = "relatorio_ean";

    private SQLiteStatement insertStmtPesquisa = null;

    private SQLiteStatement insertStmtRelatorio = null;

    String insertPesquisaSql;

    String insertRelatorioSql;

    private void inicializaSqls() {

        insertPesquisaSql = "insert into pesquisa_preco (id,concorrente,ean,secao,grupo,sub_grupo,descricao,preco," +
                "flag,id_arquivo,sincronizado,situacao) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?)";

        insertRelatorioSql = "insert into relatorio_ean (concorrente,secao,descricao,ean,ean_cadastrado,data,id_produto,sincronizado) "+
                "values(?,?,?,?,?,?,?,?)";

    }
    public DataManipulator(Context context) {

        inicializaSqls();

        DataManipulator.context = context;

        OpenHelper openHelper = new OpenHelper(DataManipulator.context);
        DataManipulator.db = openHelper.getWritableDatabase();

        // chamar esse método caso crie novas tabelas ou mude a estrutura
        //atualizaBaseDeDados(openHelper);

        // Inicializando os inserts
        this.insertStmtPesquisa = DataManipulator.db.compileStatement(insertPesquisaSql.toString());
        this.insertStmtRelatorio = DataManipulator.db.compileStatement(insertRelatorioSql.toString());

    }

    public static void atualizaBaseDeDados(OpenHelper openHelper) {

        openHelper.onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION + 1);
        db.delete(TABLE_PESQUISA, null, null);
        db.delete(TABLE_RELATORIO, null, null);

    }

    public List<PesquisaPreco> listaPraSincronizar() {

        List<PesquisaPreco> list = new ArrayList<PesquisaPreco>();

        String sql = "select * from " + TABLE_PESQUISA+" where flag='S' and sincronizado='N'";

        Cursor cursor = db.rawQuery(sql, null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {


                    String id = cursor.getString(0);
                    String concorrenteR = cursor.getString(1);
                    String ean = cursor.getString(2);
                    String secao = cursor.getString(3);
                    String grupo = cursor.getString(4);
                    String sub_grupo = cursor.getString(5);
                    String descricao = cursor.getString(6);
                    String preco = cursor.getString(7);
                    String flag = cursor.getString(8);
                    String id_arquivo = cursor.getString(9);
                    String sincronizado = cursor.getString(10);
                    String situacao = cursor.getString(11);

                    PesquisaPreco pp = new PesquisaPreco();

                    pp.setId(Integer.parseInt(id));
                    pp.setConcorrente(concorrenteR);
                    pp.setEan(ean);
                    pp.setSecao(secao);
                    pp.setGrupo(grupo);
                    pp.setSubGrupo(sub_grupo);
                    pp.setDescricao(descricao);
                    pp.setPreco(preco);
                    pp.setFlag(flag);
                    pp.setIdArquivo(Integer.parseInt(id_arquivo));
                    pp.setSincronizado(sincronizado);
                    pp.setSituacao(situacao);

                    list.add(pp);

                    x = x + 1;

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    public List<PesquisaPreco> all() {

        List<PesquisaPreco> list = new ArrayList<PesquisaPreco>();

        String sql = "select * from " + TABLE_PESQUISA;

        Cursor cursor = db.rawQuery(sql, null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {


                    String id = cursor.getString(0);
                    String concorrente = cursor.getString(1);
                    String ean = cursor.getString(2);
                    String secao = cursor.getString(3);
                    String grupo = cursor.getString(4);
                    String sub_grupo = cursor.getString(5);
                    String descricao = cursor.getString(6);
                    String preco = cursor.getString(7);
                    String flag = cursor.getString(8);
                    String id_arquivo = cursor.getString(9);
                    String sincronizado = cursor.getString(10);
                    String situacao = cursor.getString(11);

                    PesquisaPreco pp = new PesquisaPreco();

                    pp.setId(Integer.parseInt(id));
                    pp.setConcorrente(concorrente);
                    pp.setEan(ean);
                    pp.setSecao(secao);
                    pp.setGrupo(grupo);
                    pp.setSubGrupo(sub_grupo);
                    pp.setDescricao(descricao);
                    pp.setPreco(preco);
                    pp.setFlag(flag);
                    pp.setIdArquivo(Integer.parseInt(id_arquivo));
                    pp.setSincronizado(sincronizado);
                    pp.setSituacao(situacao);

                    list.add(pp);

                    x = x + 1;

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Boolean concorrenteFinalizado(String concorrenteParam) {

        List<String> list = new ArrayList<String>();

        String sql = "select * from " + TABLE_PESQUISA + " where concorrente = ? and flag='N' ";

        Cursor cursor = db.rawQuery(sql, new String[]{concorrenteParam});

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {


                    String id = cursor.getString(0);

                    list.add(id);

                    x = x + 1;

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        } catch (Exception e) {
        }

        if(list != null && !list.isEmpty()){

            return false;

        }else if(list == null || list.isEmpty()){

            return true;
        }

        return false;

    }

    public List<PesquisaPreco> listaPesquisaByConcorrente(String concorrenteParam) {

        List<PesquisaPreco> list = new ArrayList<PesquisaPreco>();

        String sql = "select * from " + TABLE_PESQUISA + " where concorrente = ? ";

        Cursor cursor = db.rawQuery(sql, new String[]{concorrenteParam});

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {


                    String id = cursor.getString(0);
                    String concorrente = cursor.getString(1);
                    String ean = cursor.getString(2);
                    String secao = cursor.getString(3);
                    String grupo = cursor.getString(4);
                    String sub_grupo = cursor.getString(5);
                    String descricao = cursor.getString(6);
                    String preco = cursor.getString(7);
                    String flag = cursor.getString(8);
                    String id_arquivo = cursor.getString(9);
                    String sincronizado = cursor.getString(10);
                    String situacao = cursor.getString(11);

                    PesquisaPreco pp = new PesquisaPreco();

                    pp.setId(Integer.parseInt(id));
                    pp.setConcorrente(concorrente);
                    pp.setEan(ean);
                    pp.setSecao(secao);
                    pp.setGrupo(grupo);
                    pp.setSubGrupo(sub_grupo);
                    pp.setDescricao(descricao);
                    pp.setPreco(preco);
                    pp.setFlag(flag);
                    pp.setIdArquivo(Integer.parseInt(id_arquivo));
                    pp.setSincronizado(sincronizado);
                    pp.setSituacao(situacao);

                    list.add(pp);

                    x = x + 1;

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        } catch (Exception e) {
        }

        return list;
    }

    public PesquisaPreco getProdutoById(String id) {

        PesquisaPreco pp = new PesquisaPreco();

        String sql = "select * from " + TABLE_PESQUISA + " where id = '"+id+"'";

        Cursor cursor = db.rawQuery(sql, null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {


                String idProd = cursor.getString(0);
                String concorrente = cursor.getString(1);
                String ean = cursor.getString(2);
                String secao = cursor.getString(3);
                String grupo = cursor.getString(4);
                String sub_grupo = cursor.getString(5);
                String descricao = cursor.getString(6);
                String preco = cursor.getString(7);
                String flag = cursor.getString(8);
                String id_arquivo = cursor.getString(9);
                String sincronizado = cursor.getString(10);
                String situacao = cursor.getString(11);

                pp.setId(Integer.parseInt(idProd));
                pp.setConcorrente(concorrente);
                pp.setEan(ean);
                pp.setSecao(secao);
                pp.setGrupo(grupo);
                pp.setSubGrupo(sub_grupo);
                pp.setDescricao(descricao);
                pp.setPreco(preco);
                pp.setFlag(flag);
                pp.setIdArquivo(Integer.parseInt(id_arquivo));
                pp.setSincronizado(sincronizado);
                pp.setSituacao(situacao);

            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        } catch (Exception e) {
        }

        return pp;
    }

    public PesquisaPreco retornaProduto(String concorrenteParam,String secaoParam,String eanParam) {

        PesquisaPreco pp = new PesquisaPreco();

        String sql = "select * from " + TABLE_PESQUISA + " where concorrente = '"+concorrenteParam+"' and secao='"+secaoParam+"'  and" +
                " ean='"+eanParam+"'";

        Cursor cursor = db.rawQuery(sql, null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {


                    String id = cursor.getString(0);
                    String concorrente = cursor.getString(1);
                    String ean = cursor.getString(2);
                    String secao = cursor.getString(3);
                    String grupo = cursor.getString(4);
                    String sub_grupo = cursor.getString(5);
                    String descricao = cursor.getString(6);
                    String preco = cursor.getString(7);
                    String flag = cursor.getString(8);
                    String id_arquivo = cursor.getString(9);
                    String sincronizado = cursor.getString(10);
                    String situacao = cursor.getString(11);

                    pp.setId(Integer.parseInt(id));
                    pp.setConcorrente(concorrente);
                    pp.setEan(ean);
                    pp.setSecao(secao);
                    pp.setGrupo(grupo);
                    pp.setSubGrupo(sub_grupo);
                    pp.setDescricao(descricao);
                    pp.setPreco(preco);
                    pp.setFlag(flag);
                    pp.setIdArquivo(Integer.parseInt(id_arquivo));
                    pp.setSincronizado(sincronizado);
                    pp.setSituacao(situacao);

                }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        } catch (Exception e) {
        }

        return pp;
    }

    public boolean updateProdutoNaoEcontrado(String id){

        String sql = "update  " + TABLE_PESQUISA + " set flag='S',situacao='s' "+
                " where id='"+id+"'";

        db.execSQL(sql);

        return true;

    }

    public boolean updateProduto(String id,String preco,String situacao){

        String sql = "update  " + TABLE_PESQUISA + " set preco='"+preco+"', flag='S',situacao='" + situacao+"' "+
                " where id='"+id+"'";

        db.execSQL(sql);

        return true;

    }

    public boolean updateProdutoSincronizado(String id){

        String sql = "update " + TABLE_PESQUISA + " set sincronizado='S' where id='"+id+"'";

        db.execSQL(sql);

        return true;

    }

    public boolean updateRelatorioSincronizado(String idProduto){

        String sql = "update " + TABLE_RELATORIO + " set sincronizado='S' where id_produto='"+idProduto+"'";

        db.execSQL(sql);

        return true;

    }

    public List<PesquisaPreco> listaPesquisaByConcorrenteAndSecao(String concorrenteParam,String secaoParam) {

        List<PesquisaPreco> list = new ArrayList<PesquisaPreco>();

        String sql = "select * from " + TABLE_PESQUISA + " where concorrente = '"+concorrenteParam+"' and secao='"+secaoParam+"' " +
                "order by flag asc";

        Cursor cursor = db.rawQuery(sql, null);

        try {

            int x = 0;

            if (cursor.moveToFirst()) {

                do {


                    String id = cursor.getString(0);
                    String concorrente = cursor.getString(1);
                    String ean = cursor.getString(2);
                    String secao = cursor.getString(3);
                    String grupo = cursor.getString(4);
                    String sub_grupo = cursor.getString(5);
                    String descricao = cursor.getString(6);
                    String preco = cursor.getString(7);
                    String flag = cursor.getString(8);
                    String id_arquivo = cursor.getString(9);
                    String sincronizado = cursor.getString(10);
                    String situacao = cursor.getString(11);


                    PesquisaPreco pp = new PesquisaPreco();

                    pp.setId(Integer.parseInt(id));
                    pp.setConcorrente(concorrente);
                    pp.setEan(ean);
                    pp.setSecao(secao);
                    pp.setGrupo(grupo);
                    pp.setSubGrupo(sub_grupo);
                    pp.setDescricao(descricao);
                    pp.setPreco(preco);
                    pp.setFlag(flag);
                    pp.setIdArquivo(Integer.parseInt(id_arquivo));
                    pp.setSincronizado(sincronizado);
                    pp.setSituacao(situacao);

                    list.add(pp);

                    x = x + 1;

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        } catch (Exception e) {
        }

        return list;
    }


    public List<String> listaConcorrentes() {

        List<String> list = new ArrayList<String>();

        String sql = "select distinct concorrente from " + TABLE_PESQUISA;

        Cursor cursor = db.rawQuery(sql, null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {


                    String concorrente = cursor.getString(0);


                    list.add(concorrente);

                    x = x + 1;

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        } catch (Exception e) {
        }

        return list;
    }

    public List<String> listaSecoesConcorrente(String concorrente) {

        List<String> list = new ArrayList<String>();

        String sql = "select distinct secao from " + TABLE_PESQUISA + " where concorrente='"+concorrente+"'";

        Cursor cursor = db.rawQuery(sql, null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {


                    String secao = cursor.getString(0);

                    list.add(secao);

                    x = x + 1;

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        } catch (Exception e) {
        }

        return list;
    }

    public Boolean verificaSeAindaHaProdutosNaoLidos() {


        List<String> list = new ArrayList<String>();

        String sql = "select * from " + TABLE_PESQUISA + " where flag='N'";

        Cursor cursor = db.rawQuery(sql, null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {

                    String id = cursor.getString(0);

                    list.add(id);

                    x = x + 1;

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        } catch (Exception e) {
        }

        if(list.size() > 0)
            return true;
        else
            return false;

    }

    public Boolean verificaSessaoCompleta(String concorrente,String secao) {


        List<String> list = new ArrayList<String>();

        String sql = "select * from " + TABLE_PESQUISA + "" +
                " where concorrente='"+concorrente+"' and secao='"+secao+"' and flag='N'";

        Cursor cursor = db.rawQuery(sql, null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {

                    String id = cursor.getString(0);

                    list.add(id);

                    x = x + 1;

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        } catch (Exception e) {
        }

        if(list.size() == 0)
            return true;
        else
            return false;

    }

    public List<HashMap<String,String>> listaRelatorioNaoSincronizado(String idProd) {

        List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();

        String sql = "select * from " + TABLE_RELATORIO + " where id_produto = '"+idProd+"' and sincronizado='N'";

        Cursor cursor = db.rawQuery(sql, null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {


                    String concorrente = cursor.getString(0);
                    String secao = cursor.getString(1);
                    String descricao = cursor.getString(2);
                    String ean = cursor.getString(3);
                    String eanCadastrado = cursor.getString(4);
                    String data = cursor.getString(5);
                    String idProduto = cursor.getString(6);
                    String sincronizado = cursor.getString(7);

                    HashMap<String,String> map = new HashMap<String,String>();

                    map.put("concorrente",concorrente);
                    map.put("secao",secao);
                    map.put("descricao",descricao);
                    map.put("ean",ean);
                    map.put("eanCadastrado",eanCadastrado);
                    map.put("data",data);
                    map.put("idProduto",idProduto);
                    map.put("sincronizado",sincronizado);

                    list.add(map);

                    x = x + 1;

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        } catch (Exception e) {
        }

        return list;
    }

    public long insertRelatorio(String concorrente,String secao,String descricao,String ean,String eanCadastrado,
                                String data,String idProd) {

        this.insertStmtRelatorio.bindString(1, concorrente);
        this.insertStmtRelatorio.bindString(2, secao);
        this.insertStmtRelatorio.bindString(3, descricao);
        this.insertStmtRelatorio.bindString(4, ean);
        this.insertStmtRelatorio.bindString(5, eanCadastrado);
        this.insertStmtRelatorio.bindString(6, data);
        this.insertStmtRelatorio.bindString(7, idProd);
        this.insertStmtRelatorio.bindString(8, "N");


        long id = this.insertStmtRelatorio.executeInsert();

        return id;

    }


    public long insertPesquisa(PesquisaPreco pp) {

        this.insertStmtPesquisa.bindString(1, pp.getId().toString());
        this.insertStmtPesquisa.bindString(2, pp.getConcorrente());
        this.insertStmtPesquisa.bindString(3, pp.getEan());
        this.insertStmtPesquisa.bindString(4, pp.getSecao());
        this.insertStmtPesquisa.bindString(5, pp.getGrupo());
        this.insertStmtPesquisa.bindString(6, pp.getSubGrupo());
        this.insertStmtPesquisa.bindString(7, pp.getDescricao());
        this.insertStmtPesquisa.bindString(8, pp.getPreco());
        this.insertStmtPesquisa.bindString(9, "N");
        this.insertStmtPesquisa.bindString(10, pp.getIdArquivo().toString());
        this.insertStmtPesquisa.bindString(11, "N");


        long id = this.insertStmtPesquisa.executeInsert();

        return id;

    }

    // deleta entregas já sincronizadas
    public boolean deletaPesquisaSincronizadas() {
        return db.delete(TABLE_PESQUISA, "sincronizado = 'S'", null) > 0;
    }

    public boolean deletaRelatoriosSincronizados() {
        return db.delete(TABLE_RELATORIO, "sincronizado = 'S'", null) > 0;
    }

    public void limpaBaseDeDados() {

        // deleta alocações já sincronizadas
        deletaPesquisaSincronizadas();
        deletaRelatoriosSincronizados();
    }


    private static class OpenHelper extends SQLiteOpenHelper {
        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE " + TABLE_PESQUISA + " (id TEXT,concorrente TEXT,ean TEXT,secao TEXT,grupo TEXT," +
                    "sub_grupo TEXT,descricao TEXT,preco TEXT,flag TEXT,id_arquivo TEXT,sincronizado TEXT,situacao TEXT)");

            db.execSQL("CREATE TABLE " + TABLE_RELATORIO + " (concorrente TEXT,secao TEXT,descricao TEXT," +
                    "ean TEXT,ean_cadastrado TEXT, data TEXT,id_produto TEXT, sincronizado TEXT)");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PESQUISA);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RELATORIO);

            onCreate(db);
        }


    }

}

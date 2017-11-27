package br.com.resoluteit.sqllite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

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

    private SQLiteStatement insertStmtPesquisa = null;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    String insertPesquisaSql;


    private void inicializaSqls() {

        insertPesquisaSql = "insert into pesquisa_preco (id,concorrente,ean,secao,grupo,sub_grupo,descricao,preco,flag,id_arquivo,sincronizado,situacao) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?)";



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

    }

    public static void atualizaBaseDeDados(OpenHelper openHelper) {

        openHelper.onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION + 1);
        db.delete(TABLE_PESQUISA, null, null);

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

    public boolean updateProduto(String concorrenteS,String secaoS,String eanS,String preco,String situacao){

        String sql = "update  " + TABLE_PESQUISA + " set preco='"+preco+"', flag='S',situacao='" + situacao+"' "+
                " where concorrente = '"+concorrenteS+"' and secao='"+secaoS+"' and ean='"+eanS+"'";

        db.execSQL(sql);

        return true;

    }

    public List<PesquisaPreco> listaPesquisaByConcorrenteAndSecao(String concorrenteParam,String secaoParam) {

        List<PesquisaPreco> list = new ArrayList<PesquisaPreco>();

        String sql = "select * from " + TABLE_PESQUISA + " where concorrente = '"+concorrenteParam+"' and secao='"+secaoParam+"'";

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
        return db.delete(TABLE_PESQUISA, "sincronizado = S", null) > 0;
    }

    public void limpaBaseDeDados() {

        // deleta alocações já sincronizadas
        deletaPesquisaSincronizadas();
    }


    private static class OpenHelper extends SQLiteOpenHelper {
        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE " + TABLE_PESQUISA + " (id TEXT,concorrente TEXT,ean TEXT,secao TEXT,grupo TEXT," +
                    "sub_grupo TEXT,descricao TEXT,preco TEXT,flag TEXT,id_arquivo TEXT,sincronizado TEXT,situacao TEXT)");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PESQUISA);
            onCreate(db);
        }


    }

}

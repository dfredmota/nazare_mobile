package br.com.resoluteit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import br.com.resoluteit.model.PesquisaPreco;
import resoluteit.com.br.R;

/**
 * Created by fredericom on 23/11/2017.
 */

public class PesquisaAdapter extends BaseAdapter {

    List<PesquisaPreco> lista;

    Context context;

    DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());

    SimpleDateFormat formatAg = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private static LayoutInflater inflater = null;

    public PesquisaAdapter(Context mainActivity, List<PesquisaPreco> prods) {
        lista = prods;
        context = mainActivity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    public void deleteItem (int position) {
        lista.remove(position);
        // remove(int) does not exist for arrays, you would have to write that method yourself or use a List instead of an array
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {

        TextView codigo;
        TextView descricao;
        TextView flag;
        TextView situacao;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView = convertView;

        if (rowView == null) {

            rowView = inflater.inflate(R.layout.li_produto, null);

            holder.codigo = (TextView) rowView.findViewById(R.id.codigo);
            holder.descricao = (TextView) rowView.findViewById(R.id.descricao);
            holder.flag = (TextView) rowView.findViewById(R.id.flag);
            holder.situacao = (TextView) rowView.findViewById(R.id.situacao);


            holder.codigo.setText(lista.get(position).getEan());
            holder.descricao.setText(lista.get(position).getDescricao());

            if(lista.get(position).getFlag() != null && lista.get(position).getFlag().equalsIgnoreCase("S"))
            holder.flag.setText("Sim");
            else if(lista.get(position).getFlag() != null && lista.get(position).getFlag().equalsIgnoreCase("N"))
            holder.flag.setText("Não");

            String situacao = lista.get(position).getSituacao();

            if(situacao != null){

                if(situacao.equalsIgnoreCase("N"))
                    holder.situacao.setText("Normal");
                if(situacao.equalsIgnoreCase("P"))
                    holder.situacao.setText("Promoção");
                if(situacao.equalsIgnoreCase("S"))
                    holder.situacao.setText("Sem Produto");
                if(situacao.equalsIgnoreCase("O"))
                    holder.situacao.setText("Outros");

            }else{
               rowView.findViewById(R.id.txt_situacao).setVisibility(View.INVISIBLE);
            }

            rowView.setTag(holder);

        }

        return rowView;
    }
}

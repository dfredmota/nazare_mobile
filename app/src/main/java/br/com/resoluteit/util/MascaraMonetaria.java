package br.com.resoluteit.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * Created by fred on 01/12/17.
 */

//removi o abstract nao sei qual era a necessidade dele
public class MascaraMonetaria implements TextWatcher {


    final EditText campo;


    public MascaraMonetaria(EditText campo) {

        super();

        this.campo = campo;

    }


    private boolean isUpdating = false;

    // Pega a formatacao do sistema, se for brasil R$ se EUA US$

    private NumberFormat nf = NumberFormat.getCurrencyInstance();


    @Override

    public void onTextChanged(CharSequence s, int start, int before,

                              int after) {

        // Evita que o método seja executado varias vezes.

        // Se tirar ele entre em loop

        if (isUpdating) {

            isUpdating = false;

            return;

        }


        isUpdating = true;

        String str = s.toString();

        // Verifica se já existe a máscara no texto.

        boolean hasMask = ((str.indexOf("R$") > -1 || str.indexOf("$") > -1) &&

                (str.indexOf(".") > -1 || str.indexOf(",") > -1));

        // Verificamos se existe máscara

        if (hasMask) {

            // Retiramos a máscara.

            str = str.replaceAll("[R$]", "").replaceAll("[,]", "")

                    .replaceAll("[.]", "");

        }


        try {

            // Transformamos o número que está escrito no EditText em

            // monetário.

            str = nf.format(Double.parseDouble(str) / 100);

            campo.setText(str);

            campo.setSelection(campo.getText().length());

        } catch (NumberFormatException e) {

            s = "";

        }

    }


    @Override

    public void beforeTextChanged(CharSequence s, int start, int count,

                                  int after) {

        // Não utilizado

    }


    @Override
    public void afterTextChanged(Editable s) {

        // Não utilizado

    }

}



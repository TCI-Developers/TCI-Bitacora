package com.tci.consultoria.tcibitacora.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.tci.consultoria.tcibitacora.Modelos.opciones;
import com.tci.consultoria.tcibitacora.R;

import java.util.ArrayList;

public class SpinnerOpc extends BaseAdapter {
    ArrayList<opciones> listOpciones;
    private Context context;


    public SpinnerOpc(ArrayList<opciones> listRegistro, Context context) {
        this.listOpciones = listRegistro;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listOpciones.size();
    }

    @Override
    public Object getItem(int position) {
        return listOpciones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.spn_type,null);
        TextView textView = (TextView) convertView.findViewById(R.id.textView);

        textView.setText(listOpciones.get(position).getOpcion());
        return convertView;
    }
}

package com.tci.consultoria.tcibitacora.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tci.consultoria.tcibitacora.Estaticas.RecyclerViewClick;
import com.tci.consultoria.tcibitacora.Modelos.Actividad;
import com.tci.consultoria.tcibitacora.R;

import java.util.ArrayList;

public class RecyclerBitacora extends RecyclerView.Adapter<RecyclerBitacora.ActividadesViewHolder> {

    ArrayList<Actividad> listRegistro;
    private RecyclerViewClick click;

    public RecyclerBitacora(ArrayList<Actividad> listRegistro, RecyclerViewClick click) {
        this.listRegistro = listRegistro;
        this.click = click;
    }


    @NonNull
    @Override
    public RecyclerBitacora.ActividadesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.actividades_row,null, false);
        return  new ActividadesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadesViewHolder actividadesViewHolder, int i) {
        actividadesViewHolder.txt_actividad.setText(listRegistro.get(i).getNombre()+" - "+listRegistro.get(i).getActRealizada());
        actividadesViewHolder.txtRazon.setText(listRegistro.get(i).getRecord());
        actividadesViewHolder.txtUpFecha.setText(listRegistro.get(i).getFechaRegistro());
    }

    @Override
    public int getItemCount() {
        return listRegistro.size();
    }

    public class ActividadesViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        TextView txt_actividad, txtRazon, txtUpFecha;
        public ActividadesViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_actividad = itemView.findViewById(R.id.txt_actividad);
            txtRazon = itemView.findViewById(R.id.txtRazon);
            txtUpFecha = itemView.findViewById(R.id.txtUpFecha);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            click.onClick(v, getAdapterPosition());
        }
    }
}

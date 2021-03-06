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

import static com.tci.consultoria.tcibitacora.MainActivity.EMPRESA;


public class RecyclerAct extends RecyclerView.Adapter<RecyclerAct.ActividadesViewHolder> {

    ArrayList<Actividad> listRegistro;
    private RecyclerViewClick click;

    public RecyclerAct(ArrayList<Actividad> listRegistro, RecyclerViewClick click) {
        this.listRegistro = listRegistro;
        this.click = click;
    }


    @NonNull
    @Override
    public ActividadesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.actividades_row,null, false);
        return new RecyclerAct.ActividadesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadesViewHolder actividadesViewHolder, int i) {
        switch (EMPRESA){
            case "tci":
                actividadesViewHolder.txt_actividad.setText(listRegistro.get(i).getNombre());
                actividadesViewHolder.txtRazon.setText("Cliente: "+listRegistro.get(i).getCliente());
                actividadesViewHolder.txtUpFecha.setText(listRegistro.get(i).getFecha());
                break;
            case "rv":
                actividadesViewHolder.txt_actividad.setText("Huerta: "+listRegistro.get(i).getNombre());
                actividadesViewHolder.txtRazon.setText("Productor: "+listRegistro.get(i).getCliente());
                actividadesViewHolder.txtUpFecha.setText("Contacto: "+listRegistro.get(i).getContacto()+
                                            "\nFecha: "+ listRegistro.get(i).getFecha());
                break;
            case "arfi":
                actividadesViewHolder.txt_actividad.setText("Huerta: "+listRegistro.get(i).getNombre());
                actividadesViewHolder.txtRazon.setText("Cliente: "+listRegistro.get(i).getCliente());
                actividadesViewHolder.txtUpFecha.setText("Fecha: "+ listRegistro.get(i).getFecha());
                break;
            case "grosa":
                actividadesViewHolder.txt_actividad.setText("Act. Reg: "+listRegistro.get(i).getNombre());
                actividadesViewHolder.txtRazon.setText("Ruta: "+listRegistro.get(i).getCliente());
                actividadesViewHolder.txtUpFecha.setText("Fecha: "+listRegistro.get(i).getFecha());
                break;

             default:
                 actividadesViewHolder.txt_actividad.setText(listRegistro.get(i).getNombre());
                 actividadesViewHolder.txtRazon.setText(listRegistro.get(i).getCliente());
                 actividadesViewHolder.txtUpFecha.setText(listRegistro.get(i).getFecha());
        }


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

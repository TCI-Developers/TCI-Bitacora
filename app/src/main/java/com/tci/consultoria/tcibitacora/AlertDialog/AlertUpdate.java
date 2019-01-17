package com.tci.consultoria.tcibitacora.AlertDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tci.consultoria.tcibitacora.Adapter.SpinnerOpc;
import com.tci.consultoria.tcibitacora.Controller.AgregarActividad;
import com.tci.consultoria.tcibitacora.Estaticas.statics;
import com.tci.consultoria.tcibitacora.Modelos.opciones;
import com.tci.consultoria.tcibitacora.R;
import com.tci.consultoria.tcibitacora.Singleton.Principal;
import java.util.ArrayList;
import java.util.List;

import static com.tci.consultoria.tcibitacora.Controller.ReporteActividades.UID;
import static com.tci.consultoria.tcibitacora.Controller.ReporteActividades.listActividades;
import static com.tci.consultoria.tcibitacora.Controller.ReporteActividades.positionAlert;
import static com.tci.consultoria.tcibitacora.MainActivity.EMPRESA;


public class AlertUpdate extends AppCompatDialogFragment {
    Spinner spnOpcion;
    EditText txtActividadRealizada,txtViaticos;
    SpinnerOpc spinnerOpc;
    private List<opciones> listaOpciones = new ArrayList<opciones>();
    private AlertUpdate.DialogListener listener;
    Principal p = Principal.getInstance();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.Theme_Dialog_Translucent);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_update,null);

        spnOpcion = view.findViewById(R.id.spnOpcion);
        txtActividadRealizada = view.findViewById(R.id.txtActividadRealizada);
        txtViaticos = view.findViewById(R.id.txtViaticos);

        llenarSpinner(builder.getContext());
        txtActividadRealizada.setText(listActividades.get(positionAlert).getActRealizada());
        txtViaticos.setText(String.valueOf(listActividades.get(positionAlert).getViaticos()));
        builder.setView(view)
                .setTitle(statics.TITTLE_ALERTDIALOG_ACTUALIZAR)
                .setMessage(statics.MESSAGE_ALERTDIALOG_ACTUALIZAR)
                .setNegativeButton(statics.BTN_ALERTDIALOG_CANCELAR, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setPositiveButton(statics.BTN_ALERTDIALOG_ACTUALIZAR, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    String opcion = spnOpcion.getSelectedItem().toString();
                    String actividad = txtActividadRealizada.getText().toString();
                    Double viaticos;
                    if(txtViaticos.getText().toString().length()==0){
                        viaticos=0.0;
                    }else{
                        viaticos = Double.parseDouble(txtViaticos.getText().toString());
                    }
                    String uid = UID.get(positionAlert);
                    listener.getTextDialogFregment(opcion,actividad,viaticos,uid,positionAlert);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        return alertDialog;
    }
    public void llenarSpinner(final Context context){
        p.databaseReference.child("Bitacora")
                .child(EMPRESA)
                .child("actividades").child("opciones")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listaOpciones.clear();
                        for(DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                            opciones opc = objSnapshot.getValue(opciones.class);
                            listaOpciones.add(opc);
                        }
                        spinnerOpc = new SpinnerOpc((ArrayList<opciones>) listaOpciones,context);
                        spnOpcion.setAdapter(spinnerOpc);
                        spnOpcion.setSelection(listActividades.get(positionAlert).getSelectopc());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (AlertUpdate.DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "Debe implementar DialogListener");
        }
    }

    public interface DialogListener {
        void getTextDialogFregment(String opcion, String actvidad,Double viativos,String UID,int position);
    }
}

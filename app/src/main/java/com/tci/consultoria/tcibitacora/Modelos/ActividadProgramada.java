package com.tci.consultoria.tcibitacora.Modelos;

public class ActividadProgramada {
    private String fecha;
    private String nombre;
    private int programing;
    private String razonSocial;
    private String record;

    public ActividadProgramada() {
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrograming() {
        return programing;
    }

    public void setPrograming(int programing) {
        this.programing = programing;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }
}

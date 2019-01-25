package com.tci.consultoria.tcibitacora.Modelos;

public class ActividadNOProgramada {
    private String actRealizada;
    private String fecha;
    private String fechaRegistro;
    private String hora;
    private Double latitud;
    private Double longitud;
    private String nombre;
    private String path;
    private int selectopc;
    private String razonSocial;
    private String record;
    private String opcion;
    private int status;
    private String url;
    private Double viaticos;
    private int programing;

    public ActividadNOProgramada() {
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public int getSelectopc() {
        return selectopc;
    }

    public void setSelectopc(int selectopc) {
        this.selectopc = selectopc;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Double getViaticos() {
        return viaticos;
    }

    public void setViaticos(Double viaticos) {
        this.viaticos = viaticos;
    }

    public String getActRealizada() {
        return actRealizada;
    }

    public void setActRealizada(String actRealizada) {
        this.actRealizada = actRealizada;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRecord() {
        return record;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public int getPrograming() {
        return programing;
    }

    public void setPrograming(int programing) {
        this.programing = programing;
    }
}

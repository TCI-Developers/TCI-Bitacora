package com.tci.consultoria.tcibitacora.Modelos;

public class Actividad {
    private String actRealizada;
    private String ruta;
    private String fecha;
    private String fechaRegistro;
    private String hora;
    private Double latitud;
    private Double longitud;
    private String nombre;
    private String path;
    private String cliente;
    private String latlong;
    private int selectopc;
    private String razonSocial;
    private String record;
    private String opcion;
    private String contacto;
    private String productor;
    private int status;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String url;
    private Double viaticos;
    private int programing;

    public Actividad() {
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

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getProductor() {
        return productor;
    }

    public void setProductor(String productor) {
        this.productor = productor;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }


    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}

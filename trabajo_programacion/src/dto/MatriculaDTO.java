package dto;

public class MatriculaDTO {
    private String nombreCliente;
    private String apellidosCliente;
    private String nombreClase;
    private String fechaInscripcion;

    // Constructor vacío (obligatorio para que Java no dé problemas)
    public MatriculaDTO() {}

    // Constructor con datos
    public MatriculaDTO(String nombreCliente, String apellidosCliente, String nombreClase, String fechaInscripcion) {
        this.nombreCliente = nombreCliente;
        this.apellidosCliente = apellidosCliente;
        this.nombreClase = nombreClase;
        this.fechaInscripcion = fechaInscripcion;
    }

    // --- GETTERS Y SETTERS (Importante que los nombres coincidan con los que pusimos en el DAO) ---
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getApellidosCliente() { return apellidosCliente; }
    public void setApellidosCliente(String apellidosCliente) { this.apellidosCliente = apellidosCliente; }

    public String getNombreClase() { return nombreClase; }
    public void setNombreClase(String nombreClase) { this.nombreClase = nombreClase; }

    public String getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(String fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
}
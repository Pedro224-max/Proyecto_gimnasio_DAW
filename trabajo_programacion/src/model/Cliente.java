package model;

public class Cliente extends Usuario {
    private double peso;
    private double porcentajeGrasa;

    public Cliente() {
        super(); // Llama al constructor de Usuario.
    }

    public Cliente(int id, String username, String password, String email, String nombre, String apellidos, String dni, String rol, double peso, double porcentajeGrasa) {
        super(id, username, password, email, nombre, apellidos, dni, rol);
        this.peso = peso;
        this.porcentajeGrasa = porcentajeGrasa;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getPorcentajeGrasa() {
        return porcentajeGrasa;
    }

    public void setPorcentajeGrasa(double porcentajeGrasa) {
        this.porcentajeGrasa = porcentajeGrasa;
    }
    
}

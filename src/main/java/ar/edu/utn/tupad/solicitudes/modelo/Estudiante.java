package ar.edu.utn.tupad.solicitudes.modelo;

public class Estudiante {

    private String legajo;
    private String nombre;
    private String email;
    private String carrera;

    public Estudiante() {
    }

    public Estudiante(String legajo, String nombre, String email, String carrera) {
        this.legajo = legajo;
        this.nombre = nombre;
        this.email = email;
        this.carrera = carrera;
    }

    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }
}

package ar.edu.utn.tupad.solicitudes.modelo;

public class Responsable {

    private int id;
    private String nombre;
    private String area;
    private String email;
    private boolean activo;

    public Responsable() {
    }

    public Responsable(int id, String nombre, String area, String email, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.area = area;
        this.email = email;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}

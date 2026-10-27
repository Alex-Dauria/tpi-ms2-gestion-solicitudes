package ar.edu.utn.tupad.solicitudes;

import ar.edu.utn.tupad.solicitudes.datos.BaseDeDatos;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SolicitudesApplication {

    public static void main(String[] args) {
        BaseDeDatos.cargar();
        SpringApplication.run(SolicitudesApplication.class, args);
    }
}

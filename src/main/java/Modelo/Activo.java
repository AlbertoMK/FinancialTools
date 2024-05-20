package Modelo;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Activo {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "nombre")
    private String nombre;

    public Activo(String nombre, int id) {
        this.nombre = nombre;
        this.id = id;
    }

    public abstract double getImporteInicial();

    public abstract double getImporteActual();

    public abstract HashMap<String, String> getJSON();

    public abstract List getFlujosCaja();

    public String getNombre() {
        return nombre;
    }

    public int getId() {
        return id;
    }
}
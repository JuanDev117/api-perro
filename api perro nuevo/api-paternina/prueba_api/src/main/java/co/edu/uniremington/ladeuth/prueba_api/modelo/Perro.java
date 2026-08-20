package co.edu.uniremington.ladeuth.prueba_api.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@JsonIgnoreProperties(value = {"codigoInterno", "hibernateLazyInitializer", "handler"},
                      allowSetters = true)
@Table(name="Perros")
public class Perro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotNull(message = "La edad debe ser necesaria")
    @Min(value = 0, message = "La edad no puede ser menor a 0")
    @Column(nullable = false)
    private Integer edad;

    @Column(nullable = true)
    private Boolean disponible;

    // Lado inverso de la relación 1:1. El lado dueño (FK perro_id) está en Ficha.
    // Al serializar un Perro, la ficha solo expone su raza (se ignoran sus demás campos).
    // allowSetters = true => al CREAR/ACTUALIZAR (deserialización), los campos de la ficha
    // sí se leen desde el JSON entrante, por lo que la creación anidada sigue funcionando.
@OneToOne(mappedBy = "perro", cascade = CascadeType.ALL)
    private Ficha ficha;

    @Column(name="codigo_interno")
    private String codigoInterno; // este campo no saldra en el DTO

    public Perro () {}

    public Perro (String nombre, Integer edad, Boolean disponible, String codigoInterno) {
        this.nombre = nombre;
        this.edad = edad;
        this.disponible = disponible;
        this.codigoInterno = codigoInterno;
    }

}

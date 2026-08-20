package co.edu.uniremington.ladeuth.prueba_api.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="Veterinarios")
public class Veterinario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del veterinario es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El apellido del veterinario es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String apellido;

    @NotBlank(message = "La cédula es obligatoria")
    @Size(min = 5, max = 15, message = "La cédula debe tener entre 5 y 15 caracteres")
    @Column(nullable = false, length = 15, unique = true)
    private String cedula;

    @Column(nullable = true, length = 20)
    private String telefono;

    @Column(nullable = true, length = 100)
    private String email;

    @Column(nullable = true, length = 100)
    private String direccion;

    @Column(nullable = true, length = 100)
    private String especialidad;

    @Column(nullable = true)
    private Boolean activo;

    public Veterinario() {}

    public Veterinario(String nombre, String apellido, String cedula, String telefono,
                        String direccion, String email, String especialidad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.especialidad = especialidad;
        this.activo = true;
    }
}

package co.edu.uniremington.ladeuth.prueba_api.modelo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="Plan_vacunacion")
public class PlanVacunaCachorro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDateTime fechaAlerta;

      @NotBlank(message = "El id del veterinario es obligatorio")
    @Size(min = 2, max = 100, message = "El id debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String idVeterinario;

    @NotBlank(message = "El nombre del veterinario es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombreVeterinario;

    @NotBlank(message = "El apellido del veterinario es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String apellidoVeterinario;

    @NotBlank(message = "El nombre de la raza es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String raza;

    @NotBlank(message = "El nombre del perro es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Size(min = 2, max = 20, message = "El sexo del perro debe tener entre 2 y 20 caracteres")
    @Column(nullable = true, length = 20)
    private String sexo;

    @Min(value = 0, message = "La edad no puede ser menor a 0")
    @Column(nullable = true)
    private Integer edad;

    @Column(nullable = true)
    private String puppy;

    @Column(nullable = true)
    private String polivalente;

    @Column(nullable = true)
    private String polivalenteDos;

    @Column(nullable = true)
    private String leptospirosis;

    @Column(nullable = true)
    private String rabia;

    @Column(nullable = true)
    private String observación;

    @Column(nullable = false)
    private Boolean activo;

    public PlanVacunaCachorro() {}

    /*public PlanVacunaCachorro(LocalDateTime fechaCreacion, String idVeterinario, String nombreVeterinario, String apellidoVeterinario,
                        String raza, String nombre, String sexo, Integer edad, String puppy, String polivalente,
                        String polivalenteDos, String leptospirosis, String rabia, String observación, Boolean activo) {
        this.fechaCreacion = fechaCreacion;
        this.idVeterinario = idVeterinario;
        this.nombreVeterinario = nombreVeterinario;
        this.apellidoVeterinario = apellidoVeterinario;
        this.raza = raza;
        this.nombre = nombre;
        this.sexo = sexo;
        this.edad = edad;
        this.puppy = puppy;
        this.polivalente = polivalente;
        this.polivalenteDos = polivalenteDos;
        this.leptospirosis = leptospirosis;
        this.rabia = rabia;
        this.observación = observación;
        this.activo = true;
    }*/

}

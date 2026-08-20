package co.edu.uniremington.ladeuth.prueba_api.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="Ficha")
public class Ficha {

// PK compartida: el id de la Ficha es el mismo que el de su Perro (relación 1:1).
    // Se asigna vía @MapsId desde la relación "perro", garantizando que coincidan.
    @Id
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String raza;
    
    @NotBlank(message = "El sexo del perro es obligatorio")
    @Size(min = 2, max = 20, message = "El sexo del perro debe tener entre 2 y 20 caracteres")
    @Column(nullable = false, length = 20)
    private String sexo;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @NotNull(message = "La esperanza de vida es obligatoria")
    @Column(nullable = false)
    private String esperanzaDeVida;

    @NotNull(message = "El peso es obligatorio")
    @Column(nullable = false)
    private String peso;

    @NotNull(message = "La altura es obligatoria")
    @Column(nullable = false)
    private String altura;

    @NotBlank(message = "El color es obligatorio")
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String colores;

    @NotBlank(message = "La textura del pelaje es obligatorio")
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String pelaje;


// Lado dueño de la relación 1:1. @MapsId hace que la PK de la Ficha (id) sea la misma
    // que la PK del Perro: así el id de la ficha SIEMPRE coincide con el id del perro.
    // La FK vive aquí (perro_id) y es a la vez la PK (nullable=false + unique=true).
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "perro_id", nullable = false, unique = true)
    private Perro perro;

    public Ficha () {}

    public Ficha (String raza, String sexo, LocalDate fechaNacimiento, String esperanzaDeVida, String peso,
                  String altura, String colores, String pelaje) {
        this.raza = raza;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.esperanzaDeVida = esperanzaDeVida;
        this.peso = peso;
        this.altura = altura;
        this.colores = colores;
        this.pelaje = pelaje;
    }
}

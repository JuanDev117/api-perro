package co.edu.uniremington.ladeuth.prueba_api.modelo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="Chequeo_general")
public class ChequeoGeneral {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]")
    @Column(nullable = true)
    private LocalDateTime fechaCreacion;

 @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]")
 @Column(nullable = true)
 private LocalDateTime fechaAlerta;

    @Size(min = 2, max = 100, message = "El id debe tener entre 2 y 100 caracteres")
    @Column(nullable = true, length = 100)
    private String idVeterinario;

    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = true, length = 100)
    private String nombreVeterinario;

    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Column(nullable = true, length = 100)
    private String apellidoVeterinario;


    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = true, length = 100)
    private String raza;

    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = true, length = 100)
    private String nombre;

    @Size(min = 2, max = 20, message = "El sexo del perro debe tener entre 2 y 20 caracteres")
    @Column(nullable = true, length = 20)
    private String sexo;

    @Positive(message = "La medida de temperatura debe ser mayor que cero")
    @Column(nullable = true)
    private Double temperatura;

    @Column(nullable = true)
    private String frecuenciaCardiaca;

    @Column(nullable = true)
    private String frecuenciaRespiratoria;

    @Column(nullable = true)
    private String llenadoCapilar;

    @Positive(message = "El peso debe ser mayor que cero")
    @Column(nullable = true)
    private Double medidaPeso;

    @Positive(message = "La medida de grasa debe ser mayor que cero")
    @Column(nullable = true)
    private Double medidaGrasa;

    @Column(nullable = true)
    private String sistemaTegumentario;

    @Column(nullable = true)
    private String cabeza;

    @Column(nullable = true)
    private String gangliosLinfaticos;

    @Column(nullable = true)
    private String auscultacion;

    @Column(nullable = true)
    private String palpacionAbdominal;

    @Column(nullable = true)
    private String movilidadReflejos;

    @Column(nullable = true)
    private String observación;

    @Column(nullable = true)
    private String diagnostico;

    @Column(nullable = false)
    private Boolean activo = true;

    public ChequeoGeneral() {}

    /*public ChequeoGeneral(LocalDateTime fechaCreacion, String idVeterinario, String nombreVeterinario, String apellidoVeterinario, 
                        String raza, String nombre, String sexo, Double temperatura, String frecuenciaCardiaca, String frecuenciaRespiratoria,
                        String llenadoCapilar, Double medidaPeso, Double medidaGrasa, String sistemaTegumentario,
                        String cabeza, String gangliosLinfaticos, String auscultacion, String palpacionAbdominal,
                         String movilidadReflejos, String observación, String diagnostico, Boolean activo) {
        this.fechaCreacion = fechaCreacion;
        this.idVeterinario = idVeterinario;
        this.nombreVeterinario = nombreVeterinario;
        this.apellidoVeterinario = apellidoVeterinario;
        this.raza = raza;
        this.nombre = nombre;
        this.sexo = sexo;
        this.temperatura = temperatura;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
        this.llenadoCapilar = llenadoCapilar;
        this.medidaPeso = medidaPeso;
        this.medidaGrasa = medidaGrasa;
        this.sistemaTegumentario = sistemaTegumentario;
        this.cabeza = cabeza;
        this.gangliosLinfaticos = gangliosLinfaticos;
        this.auscultacion = auscultacion;
        this.palpacionAbdominal = palpacionAbdominal;
        this.movilidadReflejos = movilidadReflejos;
        this.observación = observación;
        this.diagnostico = diagnostico;
        this.activo = true;
    } */

}

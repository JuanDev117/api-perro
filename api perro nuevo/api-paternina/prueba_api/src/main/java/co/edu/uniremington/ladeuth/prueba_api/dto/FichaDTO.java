package co.edu.uniremington.ladeuth.prueba_api.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Ficha completa asociada a un perro")
@Getter
@Setter
public class FichaDTO {
    private Long id;

    @Schema(description = "Raza del perro", example = "Labrador")
    private String raza;

    @Schema(description = "Sexo del perro", example = "Macho")
    private String sexo;

    @Schema(description = "Fecha de nacimiento", example = "2020-05-10")
    private LocalDate fechaNacimiento;

    @Schema(description = "Esperanza de vida", example = "entre 4 - 7 años")
    private String esperanzaDeVida;

    @Schema(description = "Peso en kg", example = "25")
    private String peso;

    @Schema(description = "Altura en cm", example = "60")
    private String altura;

    @Schema(description = "Colores del perro", example = "Marrón y Blanco")
    private String colores;

    @Schema(description = "Pelaje del perro", example = "Liso y Corto")
    private String pelaje;

    public FichaDTO () {}

    public FichaDTO (String raza, String sexo, LocalDate fechaNacimiento, String esperanzaDeVida, String peso,
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


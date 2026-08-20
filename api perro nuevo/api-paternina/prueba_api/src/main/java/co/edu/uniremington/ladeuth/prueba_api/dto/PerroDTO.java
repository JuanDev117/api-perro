package co.edu.uniremington.ladeuth.prueba_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Modelo de Perro con ficha completa para creación/actualización")
@Getter
@Setter
public class PerroDTO {

    private Long id;

    @Schema(description = "Nombre del perro", example = "Firulais")
    private String nombre;

    @Schema(description = "Edad del perro en años", example = "3")
    private Integer edad;

    @Schema(description = "Disponibilidad para adopción", example = "true")
    private Boolean disponible;

    @Schema(description = "Código interno de registro", example = "P-001")
    private String codigoInterno;

    @Schema(description = "Ficha asociada al perro")
    private FichaDTO ficha;

    public PerroDTO () {}

    public PerroDTO (String nombre, Integer edad, Boolean disponible, String codigoInterno, FichaDTO ficha) {
        this.nombre = nombre;
        this.edad = edad;
        this.disponible = disponible;
        this.codigoInterno = codigoInterno;
        this.ficha = ficha;
    }
}


package co.edu.uniremington.ladeuth.prueba_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

  @Schema(description = "Perro resumido para listados")
  @Getter
  @Setter
  public class ResumenPerroDTO {
    private Long id;
    private String nombre;
    private Integer edad;
    private Boolean disponible;

    @Schema(description = "Ficha reducida (solo id y raza)")
    private ResumenFichaDTO ficha;

    public ResumenPerroDTO () {}

    public ResumenPerroDTO (String nombre, Integer edad, Boolean disponible) {
        this.nombre = nombre;
        this.edad = edad;
        this.disponible = disponible;
        
    }
}


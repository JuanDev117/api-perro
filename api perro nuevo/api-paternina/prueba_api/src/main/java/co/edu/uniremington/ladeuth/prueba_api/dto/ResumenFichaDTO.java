package co.edu.uniremington.ladeuth.prueba_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Ficha reducida para listados")
@Getter
@Setter
public class ResumenFichaDTO {
    private Long id;
    private String raza;


     public ResumenFichaDTO () {}

    public ResumenFichaDTO (String raza) {
        this.raza = raza;
    }
} 
    


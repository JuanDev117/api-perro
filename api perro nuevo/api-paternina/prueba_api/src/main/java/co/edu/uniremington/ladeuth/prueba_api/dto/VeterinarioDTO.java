package co.edu.uniremington.ladeuth.prueba_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "veterinario quien lleva el control de los perros")
@Getter
@Setter
public class VeterinarioDTO {
    
    private Long id;

     @Schema(description = "Nombre del veterinario", example = "Franko")
    private String nombre;

     @Schema(description = "Apellido del veterinario", example = "villareal")
    private String apellido;

     @Schema(description = "Identificación del veterinario")
    private String cedula;

     @Schema(description = "Número celular del veterinario")
    private String telefono;

     @Schema(description = "Dirección de correo electronico del veterinario", example = "@gmail.com")
    private String email;

     @Schema(description = "Dirección del veterinario", example = "Cra. 4 #6 10")
    private String direccion;

     @Schema(description = "Especialización del veterinario")
    private String especialidad;

     @Schema(description = "Muestra si el veterinario esta activo o no")
    private Boolean activo;

    public VeterinarioDTO() {}

    public VeterinarioDTO(String nombre, String apellido, String cedula, String telefono,
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

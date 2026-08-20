package co.edu.uniremington.ladeuth.prueba_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "veterinario quien lleva el control de los perros")
@Getter
@Setter
public class ResumenEmpleadoDTO {

     @Schema(description = "Nombre del veterinario", example = "Franko")
    private String nombre;

     @Schema(description = "Apellido del veterinario", example = "villareal")
    private String apellido;

     @Schema(description = "Número celular del veterinario")
    private String telefono;

     @Schema(description = "Dirección de correo electronico del veterinario", example = "@gmail.com")
    private String email;

     @Schema(description = "Muestra si el veterinario esta activo o no")
    private Boolean activo;

    public ResumenEmpleadoDTO() {}

    public ResumenEmpleadoDTO(String nombre, String apellido, String telefono,
                              String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.activo = true;
    }
}

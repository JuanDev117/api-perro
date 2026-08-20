package co.edu.uniremington.ladeuth.prueba_api.servicio;

import java.util.List;
import java.util.Optional;

import co.edu.uniremington.ladeuth.prueba_api.dto.ResumenVeterinarioDTO;
import co.edu.uniremington.ladeuth.prueba_api.dto.VeterinarioDTO;

public interface VeterinarioService {

    List<ResumenVeterinarioDTO> listar();

     List<VeterinarioDTO> listarActivos(Boolean activo);

     VeterinarioDTO buscarPorCedula(String cedula);

     VeterinarioDTO buscarPorNombre( String nombre);

     VeterinarioDTO buscarPorApellido( String apellido);

     VeterinarioDTO crear(VeterinarioDTO veterinarioDTO);
    
    VeterinarioDTO actualizar(Long id, VeterinarioDTO veterinarioDTO);

    Optional<VeterinarioDTO> cambiarActivo(Long id, Boolean activo);

    boolean eliminar(Long id);



}

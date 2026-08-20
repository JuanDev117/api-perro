package co.edu.uniremington.ladeuth.prueba_api.servicio;

import java.util.List;
import java.util.Optional;

import co.edu.uniremington.ladeuth.prueba_api.dto.EmpleadoDTO;
import co.edu.uniremington.ladeuth.prueba_api.dto.ResumenEmpleadoDTO;

public interface EmpleadoService {

    List<ResumenEmpleadoDTO> listar();

     List<EmpleadoDTO> listarActivos(Boolean activo);

     EmpleadoDTO buscarPorCedula(String cedula);

     EmpleadoDTO buscarPorNombre( String nombre);

     EmpleadoDTO buscarPorApellido( String apellido);

     EmpleadoDTO crear(EmpleadoDTO empleadoDTO);
    
    EmpleadoDTO actualizar(Long id, EmpleadoDTO empleadoDTO);

    Optional<EmpleadoDTO> cambiarActivo(Long id, Boolean activo);

    boolean eliminar(Long id);



}

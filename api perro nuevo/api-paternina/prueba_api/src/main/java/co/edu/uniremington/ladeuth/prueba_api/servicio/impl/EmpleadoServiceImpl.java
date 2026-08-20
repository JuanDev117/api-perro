package co.edu.uniremington.ladeuth.prueba_api.servicio.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import co.edu.uniremington.ladeuth.prueba_api.dto.EmpleadoDTO;
import co.edu.uniremington.ladeuth.prueba_api.dto.ResumenEmpleadoDTO;
import co.edu.uniremington.ladeuth.prueba_api.excepcion.RecursoNoEncontradoException;
import co.edu.uniremington.ladeuth.prueba_api.modelo.Empleado;
import co.edu.uniremington.ladeuth.prueba_api.repositorys.EmpleadoRepository;
import co.edu.uniremington.ladeuth.prueba_api.servicio.EmpleadoService;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {
    
    private final EmpleadoRepository empleadoRepository;

    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository){
        this.empleadoRepository = empleadoRepository;
    }

    // Listar veterinarios (resumidos)
    @Override
    public List<ResumenEmpleadoDTO> listar() {
        return empleadoRepository.findAll()
                .stream()
                .map(this::convertirAResumenDTO)
                .collect(Collectors.toList());
    }

    public List<EmpleadoDTO> listarActivos(Boolean activo){
    return empleadoRepository.findByActivo(activo)
                          .stream()
                          .map(this::convertirADTO)
                          .collect(Collectors.toList());
}


    public EmpleadoDTO buscarPorCedula(String cedula) {
      Empleado empleado = empleadoRepository.findByCedula(cedula)
        .orElseThrow(() -> new RecursoNoEncontradoException("No existe un empleado con la cedula: " + cedula));
    return convertirADTO(empleado);
}


    //buscar por nombre
    public EmpleadoDTO buscarPorNombre( String nombre) {
        Empleado empleado = empleadoRepository.findByNombreContainingIgnoreCase(nombre)
                                  .orElseThrow(() -> new RecursoNoEncontradoException(
                                         "No existe un empleado con el nombre: " + nombre ));
                 return convertirADTO(empleado);
    }

    //buscar por nombre
    public EmpleadoDTO buscarPorApellido( String apellido) {
        Empleado empleado = empleadoRepository.findByApellidoContainingIgnoreCase(apellido)
                                  .orElseThrow(() -> new RecursoNoEncontradoException(
                                         "No existe un empleado con el apellido: " + apellido ));
                 return convertirADTO(empleado);
    }

    // Crear empleado
    @Override
    public EmpleadoDTO crear(EmpleadoDTO empleadoDTO) {
        Empleado empleado = convertirADominio(empleadoDTO);

        Empleado guardado = empleadoRepository.save(empleado);
        return convertirADTO(guardado);
    }

    @Override
    public EmpleadoDTO actualizar(Long id, EmpleadoDTO empleadoDTO) {
     Empleado empleadoExistente = empleadoRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("No existe un empleado con ID: " + id));

    // Actualizar datos
    empleadoExistente.setNombre(empleadoDTO.getNombre());
    empleadoExistente.setApellido(empleadoDTO.getApellido());
    empleadoExistente.setCedula(empleadoDTO.getCedula());
    empleadoExistente.setTelefono(empleadoDTO.getTelefono());
    empleadoExistente.setEmail(empleadoDTO.getEmail());
    empleadoExistente.setDireccion(empleadoDTO.getDireccion());

    Empleado actualizado = empleadoRepository.save(empleadoExistente);
    return convertirADTO(actualizado);
   }

      public Optional<EmpleadoDTO> cambiarActivo(Long id, Boolean activo){
        return empleadoRepository.findById(id).map(empleadoExistente -> {
        empleadoExistente.setActivo(activo);
        Empleado actualizado = empleadoRepository.save(empleadoExistente);
        return convertirADTO(actualizado);
         });
      }

       public boolean eliminar(Long id){
        return empleadoRepository.deleteEmpleadoById(id) > 0;
    }

    // Métodos auxiliares de conversión 
    private EmpleadoDTO convertirADTO(Empleado empleado) {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setId(empleado.getId());
        dto.setNombre(empleado.getNombre());
        dto.setApellido(empleado.getApellido());
        dto.setCedula(empleado.getCedula());
        dto.setTelefono(empleado.getTelefono());
        dto.setEmail(empleado.getEmail());
        dto.setDireccion(empleado.getDireccion());
        dto.setActivo(empleado.getActivo());
        return dto;
    }

    private Empleado convertirADominio(EmpleadoDTO dto) {
        Empleado empleado = new Empleado();
        empleado.setId(dto.getId());
        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setCedula(dto.getCedula());
        empleado.setTelefono(dto.getTelefono());
        empleado.setEmail(dto.getEmail());
        empleado.setDireccion(dto.getDireccion());
        empleado.setActivo(dto.getActivo());
        
        return empleado;
    }

     private ResumenEmpleadoDTO convertirAResumenDTO(Empleado empleado) {
        ResumenEmpleadoDTO dto = new ResumenEmpleadoDTO();
        dto.setNombre(empleado.getNombre());
       dto.setApellido(empleado.getApellido());
        dto.setTelefono(empleado.getTelefono());
        dto.setEmail(empleado.getEmail());
        dto.setActivo(empleado.getActivo());
        
        return dto;
    }

}

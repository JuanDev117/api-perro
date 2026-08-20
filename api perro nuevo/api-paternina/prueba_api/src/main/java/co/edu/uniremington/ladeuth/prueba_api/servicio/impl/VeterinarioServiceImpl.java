package co.edu.uniremington.ladeuth.prueba_api.servicio.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import co.edu.uniremington.ladeuth.prueba_api.dto.ResumenVeterinarioDTO;
import co.edu.uniremington.ladeuth.prueba_api.dto.VeterinarioDTO;
import co.edu.uniremington.ladeuth.prueba_api.excepcion.RecursoNoEncontradoException;
import co.edu.uniremington.ladeuth.prueba_api.modelo.Veterinario;
import co.edu.uniremington.ladeuth.prueba_api.repositorys.VeterinarioRepository;
import co.edu.uniremington.ladeuth.prueba_api.servicio.VeterinarioService;

@Service
public class VeterinarioServiceImpl implements VeterinarioService {
    
    private final VeterinarioRepository veterinarioRepository;

    public VeterinarioServiceImpl(VeterinarioRepository veterinarioRepository){
        this.veterinarioRepository = veterinarioRepository;
    }

    // Listar veterinarios (resumidos)
    @Override
    public List<ResumenVeterinarioDTO> listar() {
        return veterinarioRepository.findAll()
                .stream()
                .map(this::convertirAResumenDTO)
                .collect(Collectors.toList());
    }

    public List<VeterinarioDTO> listarActivos(Boolean activo){
    return veterinarioRepository.findByActivo(activo)
                          .stream()
                          .map(this::convertirADTO)
                          .collect(Collectors.toList());
}


    public VeterinarioDTO buscarPorCedula(String cedula) {
    Veterinario veterinario = veterinarioRepository.findByCedula(cedula)
        .orElseThrow(() -> new RecursoNoEncontradoException("No existe un veterinario con la cedula: " + cedula));
    return convertirADTO(veterinario);
}


    //buscar por nombre
    public VeterinarioDTO buscarPorNombre( String nombre) {
        Veterinario veterinario = veterinarioRepository.findByNombreContainingIgnoreCase(nombre)
                                  .orElseThrow(() -> new RecursoNoEncontradoException(
                                         "No existe un veterinario con el nombre: " + nombre ));
                 return convertirADTO(veterinario);
    }

    //buscar por nombre
    public VeterinarioDTO buscarPorApellido( String apellido) {
        Veterinario veterinario = veterinarioRepository.findByApellidoContainingIgnoreCase(apellido)
                                  .orElseThrow(() -> new RecursoNoEncontradoException(
                                         "No existe un veterinario con el apellido: " + apellido ));
                 return convertirADTO(veterinario);
    }

    // Crear veterinario
    @Override
    public VeterinarioDTO crear(VeterinarioDTO veterinarioDTO) {
        Veterinario veterinario = convertirADominio(veterinarioDTO);

        Veterinario guardado = veterinarioRepository.save(veterinario);
        return convertirADTO(guardado);
    }

    @Override
    public VeterinarioDTO actualizar(Long id, VeterinarioDTO veterinarioDTO) {
     Veterinario veterinarioExistente = veterinarioRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("No existe un veterinario con ID: " + id));

    // Actualizar datos
    veterinarioExistente.setNombre(veterinarioDTO.getNombre());
    veterinarioExistente.setApellido(veterinarioDTO.getApellido());
    veterinarioExistente.setCedula(veterinarioDTO.getCedula());
    veterinarioExistente.setTelefono(veterinarioDTO.getTelefono());
    veterinarioExistente.setEmail(veterinarioDTO.getEmail());
    veterinarioExistente.setDireccion(veterinarioDTO.getDireccion());
    veterinarioExistente.setEspecialidad(veterinarioDTO.getEspecialidad());

    Veterinario actualizado = veterinarioRepository.save(veterinarioExistente);
    return convertirADTO(actualizado);
   }

      public Optional<VeterinarioDTO> cambiarActivo(Long id, Boolean activo){
        return veterinarioRepository.findById(id).map(veterinarioExistente -> {
        veterinarioExistente.setActivo(activo);
        Veterinario actualizado = veterinarioRepository.save(veterinarioExistente);
        return convertirADTO(actualizado);
         });
      }

       public boolean eliminar(Long id){
        return veterinarioRepository.deleteVeterinarioById(id) > 0;
    }

    // Métodos auxiliares de conversión 
    private VeterinarioDTO convertirADTO(Veterinario veterinario) {
        VeterinarioDTO dto = new VeterinarioDTO();
        dto.setId(veterinario.getId());
        dto.setNombre(veterinario.getNombre());
        dto.setApellido(veterinario.getApellido());
        dto.setCedula(veterinario.getCedula());
        dto.setTelefono(veterinario.getTelefono());
        dto.setEmail(veterinario.getEmail());
        dto.setDireccion(veterinario.getDireccion());
        dto.setEspecialidad(veterinario.getEspecialidad());
        dto.setActivo(veterinario.getActivo());
        return dto;
    }

    private Veterinario convertirADominio(VeterinarioDTO dto) {
        Veterinario veterinario = new Veterinario();
        veterinario.setId(dto.getId());
        veterinario.setNombre(dto.getNombre());
        veterinario.setApellido(dto.getApellido());
        veterinario.setCedula(dto.getCedula());
        veterinario.setTelefono(dto.getTelefono());
        veterinario.setEmail(dto.getEmail());
        veterinario.setDireccion(dto.getDireccion());
        veterinario.setEspecialidad(dto.getEspecialidad());
        veterinario.setActivo(dto.getActivo());
        
        return veterinario;
    }

     private ResumenVeterinarioDTO convertirAResumenDTO(Veterinario veterinario) {
        ResumenVeterinarioDTO dto = new ResumenVeterinarioDTO();
        dto.setNombre(veterinario.getNombre());
       dto.setApellido(veterinario.getApellido());
        dto.setTelefono(veterinario.getTelefono());
        dto.setEmail(veterinario.getEmail());
        dto.setEspecialidad(veterinario.getEspecialidad());
        dto.setActivo(veterinario.getActivo());
        
        return dto;
    }

}

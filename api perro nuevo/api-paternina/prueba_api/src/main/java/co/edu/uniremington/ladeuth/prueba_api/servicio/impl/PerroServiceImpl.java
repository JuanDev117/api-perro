package co.edu.uniremington.ladeuth.prueba_api.servicio.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import co.edu.uniremington.ladeuth.prueba_api.dto.FichaDTO;
import co.edu.uniremington.ladeuth.prueba_api.dto.PerroDTO;
import co.edu.uniremington.ladeuth.prueba_api.dto.ResumenFichaDTO;
import co.edu.uniremington.ladeuth.prueba_api.dto.ResumenPerroDTO;
import co.edu.uniremington.ladeuth.prueba_api.excepcion.RecursoNoEncontradoException;
import co.edu.uniremington.ladeuth.prueba_api.modelo.Ficha;
import co.edu.uniremington.ladeuth.prueba_api.modelo.Perro;
import co.edu.uniremington.ladeuth.prueba_api.repositorys.FichaRepository;
import co.edu.uniremington.ladeuth.prueba_api.repositorys.PerroRepository;
import co.edu.uniremington.ladeuth.prueba_api.servicio.PerroService;

@Service
public class PerroServiceImpl implements PerroService {
 
    
    private final PerroRepository perroRepository;
    private final FichaRepository fichaRepository;

    public PerroServiceImpl(PerroRepository perroRepository, FichaRepository fichaRepository) {
        this.perroRepository = perroRepository;
        this.fichaRepository = fichaRepository;
    }

    // Listar perros (resumidos)
    @Override
    public List<ResumenPerroDTO> listar() {
        return perroRepository.findAll()
                .stream()
                .map(this::convertirAResumenDTO)
                .collect(Collectors.toList());
    }

    public List<PerroDTO> listarDisponibles(Boolean disponible){
    return perroRepository.findByDisponible(disponible)
                          .stream()
                          .map(this::convertirADTO)
                          .collect(Collectors.toList());
}


    public PerroDTO buscarPorId(Long id) {
    Perro perro = perroRepository.findById(id)
        .orElseThrow(() -> new RecursoNoEncontradoException("No existe un perro con ID: " + id));
    return convertirADTO(perro);
}


    //buscar por nombre
    public PerroDTO buscarPorNombre( String nombre) {
        Perro perro = perroRepository.findByNombreContainingIgnoreCase(nombre)
                                  .orElseThrow(() -> new RecursoNoEncontradoException(
                                         "No existe un perro con el nombre: " + nombre ));
                 return convertirADTO(perro);
    }

    // Obtener ficha completa por ID
    @Override
    public FichaDTO obtenerFicha(Long id) {
        Ficha ficha = fichaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ficha con ID: " + id));
        return convertirFichaADTO(ficha);
    }

 // Crear perro con ficha completa
    @Override
    public PerroDTO crear(PerroDTO perroDTO) {
        Perro perro = convertirADominio(perroDTO);

        // sincronizar relación 1:1
        if (perro.getFicha() != null) {
            perro.getFicha().setPerro(perro);
        }

        Perro guardado = perroRepository.save(perro);
        return convertirADTO(guardado);
    }

    @Override
    public PerroDTO actualizarSoloPerro(Long id, PerroDTO perroDTO) {
      Perro perroExistente = perroRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("No existe perro con ID: " + id));

    // Actualizar solo datos del perro
    perroExistente.setNombre(perroDTO.getNombre());
    perroExistente.setEdad(perroDTO.getEdad());
    perroExistente.setDisponible(perroDTO.getDisponible());
    perroExistente.setCodigoInterno(perroDTO.getCodigoInterno());

    // No tocar la ficha
    Perro actualizado = perroRepository.save(perroExistente);
    return convertirADTO(actualizado);
}

      public Optional<PerroDTO> cambiarDisponibilidad(Long id, Boolean disponible){
    return perroRepository.findById(id).map(perroExistente -> {
        perroExistente.setDisponible(disponible);
        Perro actualizado = perroRepository.save(perroExistente);
        return convertirADTO(actualizado);
    });
}

    public boolean eliminar(Long id){
        return perroRepository.deletePerroById(id) > 0;
    }

    // Métodos auxiliares de conversión 
    private PerroDTO convertirADTO(Perro perro) {
        PerroDTO dto = new PerroDTO();
        dto.setId(perro.getId());
        dto.setNombre(perro.getNombre());
        dto.setEdad(perro.getEdad());
        dto.setDisponible(perro.getDisponible());
        dto.setCodigoInterno(perro.getCodigoInterno());
        if (perro.getFicha() != null) {
            dto.setFicha(convertirFichaADTO(perro.getFicha()));
        }
        return dto;
    }

    private Perro convertirADominio(PerroDTO dto) {
        Perro perro = new Perro();
        perro.setId(dto.getId());
        perro.setNombre(dto.getNombre());
        perro.setEdad(dto.getEdad());
        perro.setDisponible(dto.getDisponible());
        perro.setCodigoInterno(dto.getCodigoInterno());
        if (dto.getFicha() != null) {
            perro.setFicha(convertirFichaADominio(dto.getFicha()));
        }
        return perro;
    }

    private ResumenPerroDTO convertirAResumenDTO(Perro perro) {
        ResumenPerroDTO dto = new ResumenPerroDTO();
        dto.setId(perro.getId());
        dto.setNombre(perro.getNombre());
        dto.setEdad(perro.getEdad());
        dto.setDisponible(perro.getDisponible());
        if (perro.getFicha() != null) {
            ResumenFichaDTO fichaResumen = new ResumenFichaDTO();
            fichaResumen.setId(perro.getFicha().getId());
            fichaResumen.setRaza(perro.getFicha().getRaza());
            dto.setFicha(fichaResumen);
        }
        return dto;
    }

    private FichaDTO convertirFichaADTO(Ficha ficha) {
        FichaDTO dto = new FichaDTO();
        dto.setId(ficha.getId());
        dto.setRaza(ficha.getRaza());
        dto.setFechaNacimiento(ficha.getFechaNacimiento());
        dto.setPeso(ficha.getPeso());
        dto.setAltura(ficha.getAltura());
        return dto;
    }

    private Ficha convertirFichaADominio(FichaDTO dto) {
        Ficha ficha = new Ficha();
        ficha.setId(dto.getId());
        ficha.setRaza(dto.getRaza());
        ficha.setFechaNacimiento(dto.getFechaNacimiento());
        ficha.setPeso(dto.getPeso());
        ficha.setAltura(dto.getAltura());
        return ficha;
    }

}

package co.edu.uniremington.ladeuth.prueba_api.servicio;

import java.util.List;
import java.util.Optional;

import co.edu.uniremington.ladeuth.prueba_api.dto.FichaDTO;
import co.edu.uniremington.ladeuth.prueba_api.dto.PerroDTO;
import co.edu.uniremington.ladeuth.prueba_api.dto.ResumenPerroDTO;

public interface PerroService {

    List<ResumenPerroDTO> listar();

    List<PerroDTO> listarDisponibles(Boolean disponible);

    PerroDTO buscarPorId(Long id);

    PerroDTO buscarPorNombre( String nombre);

    FichaDTO obtenerFicha(Long id);

    PerroDTO crear(PerroDTO perroDTO);

    PerroDTO actualizarSoloPerro(Long id, PerroDTO perroDTO);

    Optional<PerroDTO> cambiarDisponibilidad(Long id, Boolean disponible);

    boolean eliminar(Long id);
}

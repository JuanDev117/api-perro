package co.edu.uniremington.ladeuth.prueba_api.servicio;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import co.edu.uniremington.ladeuth.prueba_api.modelo.DesparacitarAdulto;

public interface DespaAService {
    
    List<DesparacitarAdulto> listarTodos();
    DesparacitarAdulto buscarPorNombre(String nombre);
    List<DesparacitarAdulto> buscarPorFecha(LocalDateTime fechaCreacion);
    List<DesparacitarAdulto> listarActivos(Boolean activo);
    Optional<DesparacitarAdulto> cambiarActivo(Long id, Boolean activo);
    DesparacitarAdulto agregar(DesparacitarAdulto vacuna, Long cantidad, ChronoUnit unidadTiempo);
    void programarAlerta(long cantidad, ChronoUnit unidadTiempo);

}

package co.edu.uniremington.ladeuth.prueba_api.servicio;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import co.edu.uniremington.ladeuth.prueba_api.modelo.DesparacitarCachorro;

public interface DespaCService {
    
    List<DesparacitarCachorro> listarTodos();
    DesparacitarCachorro buscarPorNombre(String nombre);
    List<DesparacitarCachorro> buscarPorFecha(LocalDateTime fechaCreacion);
    List<DesparacitarCachorro> listarActivos(Boolean activo);
    Optional<DesparacitarCachorro> cambiarActivo(Long id, Boolean activo);
    DesparacitarCachorro agregar(DesparacitarCachorro vacuna, Long cantidad, ChronoUnit unidadTiempo);
    void programarAlerta(long cantidad, ChronoUnit unidadTiempo);

}

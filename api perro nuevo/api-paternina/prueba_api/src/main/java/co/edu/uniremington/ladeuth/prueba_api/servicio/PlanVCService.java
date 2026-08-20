package co.edu.uniremington.ladeuth.prueba_api.servicio;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import co.edu.uniremington.ladeuth.prueba_api.modelo.PlanVacunaCachorro;

public interface PlanVCService {
    
    List<PlanVacunaCachorro> listarTodos();
    PlanVacunaCachorro buscarPorNombre(String nombre);
    List<PlanVacunaCachorro> buscarPorFecha(LocalDateTime fechaCreacion);
    List<PlanVacunaCachorro> listarActivos(Boolean activo);
    Optional<PlanVacunaCachorro> cambiarActivo(Long id, Boolean activo);
    PlanVacunaCachorro agregar(PlanVacunaCachorro vacuna, Long cantidad, ChronoUnit unidadTiempo);
    void programarAlerta(long cantidad, ChronoUnit unidadTiempo);

}

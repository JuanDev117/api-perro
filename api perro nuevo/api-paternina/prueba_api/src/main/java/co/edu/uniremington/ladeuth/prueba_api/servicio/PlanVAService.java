package co.edu.uniremington.ladeuth.prueba_api.servicio;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import co.edu.uniremington.ladeuth.prueba_api.modelo.PlanVacunaAdulto;


public interface PlanVAService {
    
    List<PlanVacunaAdulto> listarTodos();
    PlanVacunaAdulto buscarPorNombre(String nombre);
    List<PlanVacunaAdulto> buscarPorFecha(LocalDateTime fechaCreacion);
    List<PlanVacunaAdulto> listarActivos(Boolean activo);
    Optional<PlanVacunaAdulto> cambiarActivo(Long id, Boolean activo);
    PlanVacunaAdulto agregar(PlanVacunaAdulto vacuna, Long cantidad, ChronoUnit unidadTiempo);
    void programarAlerta(long cantidad, ChronoUnit unidadTiempo);

}

package co.edu.uniremington.ladeuth.prueba_api.repositorys;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.uniremington.ladeuth.prueba_api.modelo.PlanVacunaAdulto;

public interface PlanVAdultoRepository extends JpaRepository<PlanVacunaAdulto, Long> {
    
    Optional<PlanVacunaAdulto> findById(Long id);
    Optional<PlanVacunaAdulto> findByNombreContainingIgnoreCase(String nombre);
    List<PlanVacunaAdulto> findByActivo(Boolean activo);
    List<PlanVacunaAdulto> findByFechaCreacion(LocalDateTime fechaCreacion);
    boolean existsByFechaAlerta(LocalDateTime fecha);
    boolean existsById(Long id);

}

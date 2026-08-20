package co.edu.uniremington.ladeuth.prueba_api.repositorys;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.uniremington.ladeuth.prueba_api.modelo.PlanVacunaCachorro;

public interface PlanVCachorroRepository extends JpaRepository<PlanVacunaCachorro, Long> {
    
    Optional<PlanVacunaCachorro> findById(Long id);
    Optional<PlanVacunaCachorro> findByNombreContainingIgnoreCase(String nombre);
    List<PlanVacunaCachorro> findByActivo(Boolean activo);
    List<PlanVacunaCachorro> findByFechaCreacion(LocalDateTime fechaCreacion);
    boolean existsByFechaAlerta(LocalDateTime fecha);
    boolean existsById(Long id);

}

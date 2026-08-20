package co.edu.uniremington.ladeuth.prueba_api.repositorys;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.uniremington.ladeuth.prueba_api.modelo.DesparacitarAdulto;

public interface DespaAdultoRepository extends JpaRepository<DesparacitarAdulto, Long> {
    
    Optional<DesparacitarAdulto> findById(Long id);
    Optional<DesparacitarAdulto> findByNombreContainingIgnoreCase(String nombre);
    List<DesparacitarAdulto> findByActivo(Boolean activo);
    List<DesparacitarAdulto> findByFechaCreacion(LocalDateTime fechaCreacion);
    boolean existsByFechaAlerta(LocalDateTime fecha);
    boolean existsById(Long id);

}

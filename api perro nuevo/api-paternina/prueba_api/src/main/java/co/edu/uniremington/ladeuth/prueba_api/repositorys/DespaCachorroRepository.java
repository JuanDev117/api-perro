package co.edu.uniremington.ladeuth.prueba_api.repositorys;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.uniremington.ladeuth.prueba_api.modelo.DesparacitarCachorro;

public interface DespaCachorroRepository extends JpaRepository<DesparacitarCachorro, Long> {
    
    Optional<DesparacitarCachorro> findById(Long id);
    Optional<DesparacitarCachorro> findByNombreContainingIgnoreCase(String nombre);
    List<DesparacitarCachorro> findByActivo(Boolean activo);
    List<DesparacitarCachorro> findByFechaCreacion(LocalDateTime fechaCreacion);
    boolean existsByFechaAlerta (LocalDateTime fecha);
    boolean existsById(Long id);

}

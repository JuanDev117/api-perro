package co.edu.uniremington.ladeuth.prueba_api.repositorys;


import co.edu.uniremington.ladeuth.prueba_api.modelo.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {
    
    // Buscar veterinario por cédula
    Optional<Veterinario> findByCedula(String cedula);
    
    // Buscar veterinarios activos
    List<Veterinario> findByActivo(Boolean activo);
    
    // Buscar veterinarios por especialidad
    List<Veterinario> findByEspecialidad(String especialidad);
    
    // Buscar veterinarios por nombre 
    Optional<Veterinario> findByNombreContainingIgnoreCase(String nombre);

    // Buscar veterinarios por apellido
    Optional<Veterinario> findByApellidoContainingIgnoreCase(String apellido);

    @Modifying
    @Query("delete from Veterinario r where r.id = :id")
    int deleteVeterinarioById(@Param("id") Long id);
}

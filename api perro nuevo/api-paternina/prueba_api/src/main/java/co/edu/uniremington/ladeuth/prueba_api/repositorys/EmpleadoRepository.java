package co.edu.uniremington.ladeuth.prueba_api.repositorys;


import co.edu.uniremington.ladeuth.prueba_api.modelo.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    
    // Buscar empleados por cédula
    Optional<Empleado> findByCedula(String cedula);
    
    // Buscar empleados activos
    List<Empleado> findByActivo(Boolean activo);
    
    // Buscar empleados por nombre 
    Optional<Empleado> findByNombreContainingIgnoreCase(String nombre);

    // Buscar empleados por apellido
    Optional<Empleado> findByApellidoContainingIgnoreCase(String apellido);

    @Modifying
    @Query("delete from Empleado r where r.id = :id")
    int deleteEmpleadoById(@Param("id") Long id);
}

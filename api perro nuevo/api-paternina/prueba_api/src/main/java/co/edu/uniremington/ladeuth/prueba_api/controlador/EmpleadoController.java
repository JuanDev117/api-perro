package co.edu.uniremington.ladeuth.prueba_api.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uniremington.ladeuth.prueba_api.dto.EmpleadoDTO;
import co.edu.uniremington.ladeuth.prueba_api.dto.ResumenEmpleadoDTO;
import co.edu.uniremington.ladeuth.prueba_api.excepcion.RecursoNoEncontradoException;
import co.edu.uniremington.ladeuth.prueba_api.servicio.EmpleadoService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService){
        this.empleadoService = empleadoService;
    }

     @Operation(summary = "Listar empleados")
    @GetMapping
    public ResponseEntity<List<ResumenEmpleadoDTO>> listar() {
        List<ResumenEmpleadoDTO> empleados = empleadoService.listar();
        return ResponseEntity.ok(empleados);
    }

    
    @Operation(summary = "Listar empleados activos")
    @GetMapping("/activos")
    public ResponseEntity<List<EmpleadoDTO>> listarActivos() {
        return ResponseEntity.ok(empleadoService.listarActivos(true));
    }

    
    @Operation(summary = "Buscar empleado por cedula")
    @GetMapping("/buscar/cedula")
    public ResponseEntity<EmpleadoDTO> buscarPorCedula(@RequestParam String cedula) {
        return ResponseEntity.ok(empleadoService.buscarPorCedula(cedula));
    }

    @Operation(summary = "Buscar empleado por nombre")
    @GetMapping("/buscar/nombre")
    public ResponseEntity<EmpleadoDTO> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(empleadoService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Buscar empleado por apellido")
    @GetMapping("/buscar/apellido")
    public ResponseEntity<EmpleadoDTO> buscarPorApellido(@RequestParam String apellido) {
        return ResponseEntity.ok(empleadoService.buscarPorApellido(apellido));
    }

    
    @Operation(summary = "Crear un empleado")
    @PostMapping
    public ResponseEntity<EmpleadoDTO> crear(@Valid @RequestBody EmpleadoDTO empleadoDTO) {
        EmpleadoDTO nuevo = empleadoService.crear(empleadoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @Operation(summary = "Actualizar empleado")
    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> actualizarEmpleado(@PathVariable Long id, @RequestBody EmpleadoDTO empleadoDTO) {
        EmpleadoDTO actualizado = empleadoService.actualizar(id, empleadoDTO);
       return ResponseEntity.ok(actualizado);
    }


    @Operation(summary = "Cambiar el estado de un empleado")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<EmpleadoDTO> cambiarEstado(@PathVariable Long id, @RequestParam Boolean activo) {
        return empleadoService.cambiarActivo(id, activo)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un empleado con ID: " + id));
    }

    @Operation(summary = "Eliminar empleado por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = empleadoService.eliminar(id);
        if (eliminado) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            throw new RecursoNoEncontradoException("No existe un empleado con ID: " + id);
        }
    }

    
}

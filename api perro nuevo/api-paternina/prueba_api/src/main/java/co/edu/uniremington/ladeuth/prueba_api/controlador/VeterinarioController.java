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

import co.edu.uniremington.ladeuth.prueba_api.dto.ResumenVeterinarioDTO;
import co.edu.uniremington.ladeuth.prueba_api.dto.VeterinarioDTO;
import co.edu.uniremington.ladeuth.prueba_api.excepcion.RecursoNoEncontradoException;
import co.edu.uniremington.ladeuth.prueba_api.servicio.VeterinarioService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService veterinarioService){
        this.veterinarioService = veterinarioService;
    }

     @Operation(summary = "Listar veterinarios")
    @GetMapping
    public ResponseEntity<List<ResumenVeterinarioDTO>> listar() {
        List<ResumenVeterinarioDTO> veterinarios = veterinarioService.listar();
        return ResponseEntity.ok(veterinarios);
    }

    
    @Operation(summary = "Listar veterinarios activos")
    @GetMapping("/activos")
    public ResponseEntity<List<VeterinarioDTO>> listarActivos() {
        return ResponseEntity.ok(veterinarioService.listarActivos(true));
    }

    
    @Operation(summary = "Buscar veterinario por cedula")
    @GetMapping("/buscar/cedula")
    public ResponseEntity<VeterinarioDTO> buscarPorCedula(@RequestParam String cedula) {
        return ResponseEntity.ok(veterinarioService.buscarPorCedula(cedula));
    }

    @Operation(summary = "Buscar veterinario por nombre")
    @GetMapping("/buscar/nombre")
    public ResponseEntity<VeterinarioDTO> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(veterinarioService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Buscar veterinario por apellido")
    @GetMapping("/buscar/apellido")
    public ResponseEntity<VeterinarioDTO> buscarPorApellido(@RequestParam String apellido) {
        return ResponseEntity.ok(veterinarioService.buscarPorApellido(apellido));
    }

    
    @Operation(summary = "Crear un veterinario")
    @PostMapping
    public ResponseEntity<VeterinarioDTO> crear(@Valid @RequestBody VeterinarioDTO veterinarioDTO) {
        VeterinarioDTO nuevo = veterinarioService.crear(veterinarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @Operation(summary = "Actualizar veterinario")
    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioDTO> actualizarVeterinario(@PathVariable Long id, @RequestBody VeterinarioDTO veterinarioDTO) {
        VeterinarioDTO actualizado = veterinarioService.actualizar(id, veterinarioDTO);
       return ResponseEntity.ok(actualizado);
    }


    @Operation(summary = "Cambiar el estado de un veterinario")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<VeterinarioDTO> cambiarEstado(@PathVariable Long id, @RequestParam Boolean activo) {
        return veterinarioService.cambiarActivo(id, activo)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un veterinario con ID: " + id));
    }

    @Operation(summary = "Eliminar veterinario por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = veterinarioService.eliminar(id);
        if (eliminado) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            throw new RecursoNoEncontradoException("No existe un veterinario con ID: " + id);
        }
    }

    
}

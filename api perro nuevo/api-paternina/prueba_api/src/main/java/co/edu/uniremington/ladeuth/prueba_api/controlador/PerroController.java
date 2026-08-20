package co.edu.uniremington.ladeuth.prueba_api.controlador;

import co.edu.uniremington.ladeuth.prueba_api.dto.FichaDTO;
import co.edu.uniremington.ladeuth.prueba_api.dto.PerroDTO;
import co.edu.uniremington.ladeuth.prueba_api.dto.ResumenPerroDTO;
import co.edu.uniremington.ladeuth.prueba_api.excepcion.RecursoNoEncontradoException;
import co.edu.uniremington.ladeuth.prueba_api.servicio.PerroService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/perros")
public class PerroController {
    private final PerroService perroService;

    public PerroController(PerroService perroService){
        this.perroService = perroService;
    }

     @Operation(summary = "Listar perros (con ficha reducida)")
    @GetMapping
    public ResponseEntity<List<ResumenPerroDTO>> listar() {
        List<ResumenPerroDTO> perros = perroService.listar();
        return ResponseEntity.ok(perros);
    }

    @Operation(summary = "Listar perros disponibles")
    @GetMapping("/disponibles")
    public ResponseEntity<List<PerroDTO>> listarDisponibles(@RequestParam Boolean disponible) {
        return ResponseEntity.ok(perroService.listarDisponibles(disponible));
    }

    @Operation(summary = "Buscar perro por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PerroDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(perroService.buscarPorId(id));
    }

    @Operation(summary = "Buscar perro por nombre")
    @GetMapping("/buscar")
    public ResponseEntity<PerroDTO> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(perroService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Obtener ficha completa por ID")
    @GetMapping("/fichas/{id}")
    public ResponseEntity<FichaDTO> obtenerFicha(@PathVariable Long id) {
        FichaDTO ficha = perroService.obtenerFicha(id);
        return ResponseEntity.ok(ficha);
    }

    @Operation(summary = "Crear un perro con su ficha completa")
    @PostMapping("/crear")
    public ResponseEntity<PerroDTO> crear(@Valid @RequestBody PerroDTO perroDTO) {
        PerroDTO nuevo = perroService.crear(perroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @Operation(summary = "Actualizar perro sin modificar ficha")
    @PutMapping("/{id}")
    public ResponseEntity<PerroDTO> actualizarPerro(@PathVariable Long id, @RequestBody PerroDTO perroDTO) {
        PerroDTO actualizado = perroService.actualizarSoloPerro(id, perroDTO);
       return ResponseEntity.ok(actualizado);
    }


    @Operation(summary = "Cambiar disponibilidad de un perro")
    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<PerroDTO> cambiarDisponibilidad(@PathVariable Long id, @RequestParam Boolean disponible) {
        return perroService.cambiarDisponibilidad(id, disponible)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe perro con ID: " + id));
    }

    @Operation(summary = "Eliminar perro por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = perroService.eliminar(id);
        if (eliminado) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            throw new RecursoNoEncontradoException("No existe perro con ID: " + id);
        }
    }
}




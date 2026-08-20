package co.edu.uniremington.ladeuth.prueba_api.controlador;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uniremington.ladeuth.prueba_api.excepcion.RecursoNoEncontradoException;
import co.edu.uniremington.ladeuth.prueba_api.modelo.DesparacitarCachorro;
import co.edu.uniremington.ladeuth.prueba_api.servicio.DespaCService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/desparacitar/cachorros")
public class DespaCaController {

    private final DespaCService despaCService;

    public DespaCaController(DespaCService despaCService){
        this.despaCService = despaCService;
    }

     @GetMapping
    public ResponseEntity<List<DesparacitarCachorro>> listarTodos() {
      return ResponseEntity.ok(despaCService.listarTodos());
    }

    @Operation(summary = "Listar planes de desparacitación activos")
    @GetMapping("/activos")
    public ResponseEntity<List<DesparacitarCachorro>> listarActivos() {
        return ResponseEntity.ok(despaCService.listarActivos(true));
    }

    @Operation(summary = "Listar planes de desparacitación por fecha")
    @GetMapping("/fecha")
    public ResponseEntity<List<DesparacitarCachorro>> buscarPorFecha(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacion) {
    return ResponseEntity.ok(despaCService.buscarPorFecha(fechaCreacion));
    }


    @Operation(summary = "Buscar registro de plan de desparacitación por el nombre del perro")
    @GetMapping("/buscar/nombre")
    public ResponseEntity<DesparacitarCachorro> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(despaCService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Cambiar el estado de un plan de desparacitación")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<DesparacitarCachorro> cambiarEstado(@PathVariable Long id, @RequestParam Boolean activo) {
        return despaCService.cambiarActivo(id, activo)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un plan de desparacitación con ID: " + id));
    }

    @Operation(summary = "Crear un nuevo registro de plan de estudio")
    @PostMapping
    public ResponseEntity<DesparacitarCachorro> crear(@Valid @RequestBody DesparacitarCachorro deparacitarCachorro, @RequestParam long cantidad,
                                                      @RequestParam ChronoUnit unidad) {
        DesparacitarCachorro nuevo = despaCService.agregar(deparacitarCachorro, cantidad, unidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PostMapping("/alerta")
    public ResponseEntity<String> crearAlerta(@RequestParam long cantidad,
                                          @RequestParam String unidad) {
        ChronoUnit unidadTiempo = ChronoUnit.valueOf(unidad.toUpperCase());
          despaCService.programarAlerta(cantidad, unidadTiempo);
            return ResponseEntity.ok("Alerta programada cada " + cantidad + " " + unidadTiempo);
    }
    
}

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
import co.edu.uniremington.ladeuth.prueba_api.modelo.DesparacitarAdulto;
import co.edu.uniremington.ladeuth.prueba_api.servicio.DespaAService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/desparacitar/adultos")
public class DespaAdController {

    private final DespaAService despaAService;

    public DespaAdController(DespaAService despaAService){
        this.despaAService = despaAService;
    }

     @GetMapping
    public ResponseEntity<List<DesparacitarAdulto>> listarTodos() {
      return ResponseEntity.ok(despaAService.listarTodos());
    }

    @Operation(summary = "Listar planes de desparacitación activos")
    @GetMapping("/activos")
    public ResponseEntity<List<DesparacitarAdulto>> listarActivos() {
        return ResponseEntity.ok(despaAService.listarActivos(true));
    }

    @Operation(summary = "Listar planes de desparacitación por fecha")
    @GetMapping("/fecha")
    public ResponseEntity<List<DesparacitarAdulto>> buscarPorFecha(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacion) {
    return ResponseEntity.ok(despaAService.buscarPorFecha(fechaCreacion));
    }


    @Operation(summary = "Buscar registro de plan de desparacitación por el nombre del perro")
    @GetMapping("/buscar/nombre")
    public ResponseEntity<DesparacitarAdulto> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(despaAService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Cambiar el estado de un plan de desparacitación")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<DesparacitarAdulto> cambiarEstado(@PathVariable Long id, @RequestParam Boolean activo) {
        return despaAService.cambiarActivo(id, activo)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un plan de desparacitación con ID: " + id));
    }

    @Operation(summary = "Crear un nuevo registro de plan de desparacitación")
    @PostMapping
    public ResponseEntity<DesparacitarAdulto> crear(@Valid @RequestBody DesparacitarAdulto deparacitarAdulto, @RequestParam long cantidad,
                                                    @RequestParam ChronoUnit unidad ) {
        DesparacitarAdulto nuevo = despaAService.agregar(deparacitarAdulto, cantidad, unidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PostMapping("/alerta")
    public ResponseEntity<String> crearAlerta(@RequestParam long cantidad,
                                          @RequestParam String unidad) {
        ChronoUnit unidadTiempo = ChronoUnit.valueOf(unidad.toUpperCase());
          despaAService.programarAlerta(cantidad, unidadTiempo);
            return ResponseEntity.ok("Alerta programada cada " + cantidad + " " + unidadTiempo);
    }
    
}

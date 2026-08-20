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
import co.edu.uniremington.ladeuth.prueba_api.modelo.PlanVacunaAdulto;
import co.edu.uniremington.ladeuth.prueba_api.servicio.PlanVAService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vacunas/adultos")
public class PlanVAdController {

    private final PlanVAService planVAService;

    public PlanVAdController(PlanVAService planVAService){
        this.planVAService = planVAService;
    }

     @GetMapping
    public ResponseEntity<List<PlanVacunaAdulto>> listarTodos() {
      return ResponseEntity.ok(planVAService.listarTodos());
    }

    @Operation(summary = "Listar planes de vacunación activos")
    @GetMapping("/activos")
    public ResponseEntity<List<PlanVacunaAdulto>> listarActivos() {
        return ResponseEntity.ok(planVAService.listarActivos(true));
    }

    @Operation(summary = "Listar planes de vacunación por fecha")
    @GetMapping("/fecha")
    public ResponseEntity<List<PlanVacunaAdulto>> buscarPorFecha(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacion) {
    return ResponseEntity.ok(planVAService.buscarPorFecha(fechaCreacion));
    }

    @Operation(summary = "Buscar registro de plan de vacuna por el nombre del perro")
    @GetMapping("/buscar/nombre")
    public ResponseEntity<PlanVacunaAdulto> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(planVAService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Cambiar el estado de un plan de vacunación")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PlanVacunaAdulto> cambiarEstado(@PathVariable Long id, @RequestParam Boolean activo) {
        return planVAService.cambiarActivo(id, activo)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un plan de vacunación con ID: " + id));
    }

    @Operation(summary = "Crear un nuevo plan de vacunación")
    @PostMapping
    public ResponseEntity<PlanVacunaAdulto> crear(@Valid @RequestBody PlanVacunaAdulto planVacunaCachorro, @RequestParam long cantidad,
                                                  @RequestParam ChronoUnit unidad) {
        PlanVacunaAdulto nuevo = planVAService.agregar(planVacunaCachorro, cantidad, unidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PostMapping("/alerta")
    public ResponseEntity<String> crearAlerta(@RequestParam long cantidad,
                                          @RequestParam String unidad) {
        ChronoUnit unidadTiempo = ChronoUnit.valueOf(unidad.toUpperCase());
          planVAService.programarAlerta(cantidad, unidadTiempo);
            return ResponseEntity.ok("Alerta programada cada " + cantidad + " " + unidadTiempo);
    }
    
}

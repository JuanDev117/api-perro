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
import co.edu.uniremington.ladeuth.prueba_api.modelo.PlanVacunaCachorro;
import co.edu.uniremington.ladeuth.prueba_api.servicio.PlanVCService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vacunas/cachorros")
public class PlanVCaController {

    private final PlanVCService planVCService;

    public PlanVCaController(PlanVCService planVCService){
        this.planVCService = planVCService;
    }

     @GetMapping
    public ResponseEntity<List<PlanVacunaCachorro>> listarTodos() {
      return ResponseEntity.ok(planVCService.listarTodos());
    }

    @Operation(summary = "Listar planes de vacunación activos")
    @GetMapping("/activos")
    public ResponseEntity<List<PlanVacunaCachorro>> listarActivos() {
        return ResponseEntity.ok(planVCService.listarActivos(true));
    }

    @Operation(summary = "Listar planes de vacunación por fecha")
    @GetMapping("/fecha")
    public ResponseEntity<List<PlanVacunaCachorro>> buscarPorFecha(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacion) {
    return ResponseEntity.ok(planVCService.buscarPorFecha(fechaCreacion));
    }

    @Operation(summary = "Buscar registro de plan de vacunación por el nombre del perro")
    @GetMapping("/buscar/nombre")
    public ResponseEntity<PlanVacunaCachorro> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(planVCService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Cambiar el estado de un plan de vacunación")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PlanVacunaCachorro> cambiarEstado(@PathVariable Long id, @RequestParam Boolean activo) {
        return planVCService.cambiarActivo(id, activo)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un plan de vacunación con ID: " + id));
    }

    @Operation(summary = "Crear un nuevo registro de plan de vacunación")
    @PostMapping
    public ResponseEntity<PlanVacunaCachorro> crear(@Valid @RequestBody PlanVacunaCachorro planVacunaCachorro, @RequestParam long cantidad,
                                                    @RequestParam ChronoUnit unidad) {
        PlanVacunaCachorro nuevo = planVCService.agregar(planVacunaCachorro, cantidad, unidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PostMapping("/alerta")
    public ResponseEntity<String> crearAlerta(@RequestParam long cantidad,
                                          @RequestParam String unidad) {
        ChronoUnit unidadTiempo = ChronoUnit.valueOf(unidad.toUpperCase());
          planVCService.programarAlerta(cantidad, unidadTiempo);
            return ResponseEntity.ok("Alerta programada cada " + cantidad + " " + unidadTiempo);
    }

    
}

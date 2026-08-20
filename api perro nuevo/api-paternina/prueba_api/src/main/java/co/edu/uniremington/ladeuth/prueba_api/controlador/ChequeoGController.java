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
import co.edu.uniremington.ladeuth.prueba_api.modelo.ChequeoGeneral;
import co.edu.uniremington.ladeuth.prueba_api.servicio.ChequeoGService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chequeos")
public class ChequeoGController {

    private final ChequeoGService chequeoGService;

    public ChequeoGController(ChequeoGService chequeoGService){
        this.chequeoGService = chequeoGService;
    }

     @GetMapping
    public ResponseEntity<List<ChequeoGeneral>> listarTodos() {
      return ResponseEntity.ok(chequeoGService.listarTodos());
    }

    @Operation(summary = "Listar registros de chequeos generalaes activos")
    @GetMapping("/activos")
    public ResponseEntity<List<ChequeoGeneral>> listarActivos() {
        return ResponseEntity.ok(chequeoGService.listarActivos(true));
    }

    @Operation(summary = "Listar registros de chequeos generalaes por fecha")
    @GetMapping("/fecha")
    public ResponseEntity<List<ChequeoGeneral>> buscarPorFecha(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCreacion) {
    return ResponseEntity.ok(chequeoGService.buscarPorFecha(fechaCreacion));
    }


    @Operation(summary = "Buscar registro de cehequeo general por el nombre del perro")
    @GetMapping("/buscar/nombre")
    public ResponseEntity<ChequeoGeneral> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(chequeoGService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Cambiar el estado de registro de chequeo general")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ChequeoGeneral> cambiarEstado(@PathVariable Long id, @RequestParam Boolean activo) {
        return chequeoGService.cambiarActivo(id, activo)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un registro de chequeo con ID: " + id));
    }

    @Operation(summary = "Crear un nuevo registro de chequeo general")
    @PostMapping
    public ResponseEntity<ChequeoGeneral> crear(@Valid @RequestBody ChequeoGeneral chequeoGeneral, @RequestParam long cantidad,
                                                @RequestParam ChronoUnit unidad) {
        ChequeoGeneral nuevo = chequeoGService.agregar(chequeoGeneral, cantidad, unidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PostMapping("/alerta")
    public ResponseEntity<String> crearAlerta(@RequestParam long cantidad,
                                          @RequestParam String unidad) {
        ChronoUnit unidadTiempo = ChronoUnit.valueOf(unidad.toUpperCase());
          chequeoGService.programarAlerta(cantidad, unidadTiempo);
            return ResponseEntity.ok("Alerta programada cada " + cantidad + " " + unidadTiempo);
    }
    
}

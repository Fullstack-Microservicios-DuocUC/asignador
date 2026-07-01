package cl.duoc.mineria.asignador.controller;

import cl.duoc.mineria.asignador.model.ClasificacionMaterial;
import cl.duoc.mineria.asignador.dto.ConfiguracionRutaResponseDTO;
import cl.duoc.mineria.asignador.dto.RegistrarRutaDTO;
import cl.duoc.mineria.asignador.mapper.ConfiguracionRutaMapper;
import cl.duoc.mineria.asignador.model.ConfiguracionRuta;
import cl.duoc.mineria.asignador.service.ConfiguracionRutaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/asignador")
@RequiredArgsConstructor
@Tag(name = "Gestión de Asignación de Rutas", description = "Operaciones para administrar y consultar las rutas de destino de los materiales.")
public class ConfiguracionRutaController {

    private final ConfiguracionRutaService configuracionRutaService;
    private final ConfiguracionRutaMapper configuracionRutaMapper;

    // ENDPOINTS DE ADMINISTRACIÓN BÁSICA

    @PostMapping
    @Operation(summary = "Registrar una nueva configuración de ruta", description = "Crea una nueva regla de asignación de destino para una combinación de pala y tipo de material.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Configuración de ruta registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<ConfiguracionRutaResponseDTO> registrarRuta(@Valid @RequestBody RegistrarRutaDTO dto) {
        ConfiguracionRuta nuevaRuta = configuracionRutaService.registrarRuta(dto);
        return new ResponseEntity<>(configuracionRutaMapper.toDto(nuevaRuta), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todas las configuraciones de ruta", description = "Obtiene una lista completa de todas las reglas de asignación de destino registradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de configuraciones obtenida con éxito")
    })
    public ResponseEntity<List<ConfiguracionRutaResponseDTO>> listarTodas() {
        List<ConfiguracionRutaResponseDTO> lista = configuracionRutaService.listarTodas()
                .stream()
                .map(configuracionRutaMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // ENDPOINTS DE NEGOCIO (HISTORIAS DE USUARIO)

    /**
     * HISTORIA: Camión Autónomo y Palero
     * El microservicio CicloTransporte consume este endpoint mediante WebClient
     * para preguntar el destino del material cargado.
  */
    @GetMapping("/destino")
    @Operation(summary = "Determinar destino de un material", description = "Endpoint de negocio principal. Basado en la pala de origen y la clasificación del material, determina si el destino es CHANCADOR o RELAVE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Destino determinado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró una regla de asignación para la combinación de pala y material")
    })
    public ResponseEntity<ConfiguracionRutaResponseDTO> determinarDestino(
            @RequestParam Long palaId,
            @RequestParam ClasificacionMaterial clasificacionMaterial) {
        
        ConfiguracionRuta rutaAsignada = configuracionRutaService.determinarDestino(palaId, clasificacionMaterial);
        return ResponseEntity.ok(configuracionRutaMapper.toDto(rutaAsignada));
    }

    /**
     * HISTORIA: Falla de Pala
     * Carlos reporta la falla. Este endpoint apaga su pala y devuelve 
     * inmediatamente la configuración de la pala vecina sugerida.
  */
    @PatchMapping("/pala/{palaId}/falla")
    @Operation(summary = "Reportar falla en pala y obtener ruta alternativa", description = "Marca una pala como no operativa y sugiere una pala vecina como alternativa para la misma clasificación de material.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Falla reportada y ruta alternativa sugerida"),
            @ApiResponse(responseCode = "404", description = "No se encontró la pala original o no hay una alternativa disponible")
    })
    public ResponseEntity<ConfiguracionRutaResponseDTO> reportarFallaYRedistribuir(
            @PathVariable Long palaId,
            @RequestParam ClasificacionMaterial clasificacionMaterial) {
        
        ConfiguracionRuta nuevaRutaSugerida = configuracionRutaService.reportarFallaYRedistribuir(palaId, clasificacionMaterial);
        return ResponseEntity.ok(configuracionRutaMapper.toDto(nuevaRutaSugerida));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una configuración de ruta por ID", description = "Busca y devuelve una regla de asignación específica a partir de su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuración encontrada"),
            @ApiResponse(responseCode = "404", description = "Configuración no encontrada con el ID proporcionado")
    })
    public ResponseEntity<ConfiguracionRutaResponseDTO> obtenerPorId(@PathVariable Long id) {
        ConfiguracionRuta ruta = configuracionRutaService.obtenerPorId(id);
        return ResponseEntity.ok(configuracionRutaMapper.toDto(ruta));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una configuración de ruta", description = "Modifica una regla de asignación existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuración actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Configuración no encontrada con el ID proporcionado")
    })
    public ResponseEntity<ConfiguracionRutaResponseDTO> actualizarRuta(
            @PathVariable Long id, 
            @Valid @RequestBody RegistrarRutaDTO dto) {
        ConfiguracionRuta rutaActualizada = configuracionRutaService.actualizarRuta(id, dto);
        return ResponseEntity.ok(configuracionRutaMapper.toDto(rutaActualizada));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una configuración de ruta", description = "Elimina una regla de asignación del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Configuración eliminada exitosamente")
    })
    public ResponseEntity<Void> eliminarRuta(@PathVariable Long id) {
        configuracionRutaService.eliminarRuta(id);
        return ResponseEntity.noContent().build(); // Retorna un 204 No Content, el estándar para DELETE
    }
}
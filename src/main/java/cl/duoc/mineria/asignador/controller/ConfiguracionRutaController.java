package cl.duoc.mineria.asignador.controller;

import cl.duoc.mineria.asignador.dto.ConfiguracionRutaResponseDTO;
import cl.duoc.mineria.asignador.dto.RegistrarRutaDTO;
import cl.duoc.mineria.asignador.mapper.ConfiguracionRutaMapper;
import cl.duoc.mineria.asignador.model.ConfiguracionRuta;
import cl.duoc.mineria.asignador.service.ConfiguracionRutaService;
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
public class ConfiguracionRutaController {

    private final ConfiguracionRutaService configuracionRutaService;
    private final ConfiguracionRutaMapper configuracionRutaMapper;

    // ENDPOINTS DE ADMINISTRACIÓN BÁSICA

    @PostMapping
    public ResponseEntity<ConfiguracionRutaResponseDTO> registrarRuta(@Valid @RequestBody RegistrarRutaDTO dto) {
        ConfiguracionRuta nuevaRuta = configuracionRutaService.registrarRuta(dto);
        return new ResponseEntity<>(configuracionRutaMapper.toDto(nuevaRuta), HttpStatus.CREATED);
    }

    @GetMapping
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
    public ResponseEntity<ConfiguracionRutaResponseDTO> determinarDestino(
            @RequestParam Long palaId,
            @RequestParam String clasificacionMaterial) {
        
        ConfiguracionRuta rutaAsignada = configuracionRutaService.determinarDestino(palaId, clasificacionMaterial);
        return ResponseEntity.ok(configuracionRutaMapper.toDto(rutaAsignada));
    }

    /**
     * HISTORIA: Falla de Pala
     * Carlos reporta la falla. Este endpoint apaga su pala y devuelve 
     * inmediatamente la configuración de la pala vecina sugerida.
  */
    @PatchMapping("/pala/{palaId}/falla")
    public ResponseEntity<ConfiguracionRutaResponseDTO> reportarFallaYRedistribuir(
            @PathVariable Long palaId,
            @RequestParam String clasificacionMaterial) {
        
        ConfiguracionRuta nuevaRutaSugerida = configuracionRutaService.reportarFallaYRedistribuir(palaId, clasificacionMaterial);
        return ResponseEntity.ok(configuracionRutaMapper.toDto(nuevaRutaSugerida));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConfiguracionRutaResponseDTO> obtenerPorId(@PathVariable Long id) {
        ConfiguracionRuta ruta = configuracionRutaService.obtenerPorId(id);
        return ResponseEntity.ok(configuracionRutaMapper.toDto(ruta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConfiguracionRutaResponseDTO> actualizarRuta(
            @PathVariable Long id, 
            @Valid @RequestBody RegistrarRutaDTO dto) {
        ConfiguracionRuta rutaActualizada = configuracionRutaService.actualizarRuta(id, dto);
        return ResponseEntity.ok(configuracionRutaMapper.toDto(rutaActualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRuta(@PathVariable Long id) {
        configuracionRutaService.eliminarRuta(id);
        return ResponseEntity.noContent().build(); // Retorna un 204 No Content, el estándar para DELETE
    }
}
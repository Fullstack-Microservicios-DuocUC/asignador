package cl.duoc.mineria.asignador.service;

import cl.duoc.mineria.asignador.dto.RegistrarRutaDTO;
import cl.duoc.mineria.asignador.exception.RutaNotFoundException;
import cl.duoc.mineria.asignador.mapper.ConfiguracionRutaMapper;
import cl.duoc.mineria.asignador.model.ClasificacionMaterial;
import cl.duoc.mineria.asignador.model.ConfiguracionRuta;
import cl.duoc.mineria.asignador.repository.ConfiguracionRutaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfiguracionRutaService {

    private final ConfiguracionRutaRepository configuracionRutaRepository;
    private final ConfiguracionRutaMapper configuracionRutaMapper;

    // METODOS CRUD BÁSICOS

    @Transactional
    public ConfiguracionRuta registrarRuta(RegistrarRutaDTO dto) {
        ConfiguracionRuta nuevaRuta = configuracionRutaMapper.toEntity(dto);
        return configuracionRutaRepository.save(nuevaRuta);
    }

    @Transactional(readOnly = true)
    public List<ConfiguracionRuta> listarTodas() {
        return configuracionRutaRepository.findAll();
    }

    // METODOS DE NEGOCIO (HISTORIAS DE USUARIO)

    /**
     * HISTORIA DE USUARIO: Palero / Camión Autónomo
     * Cuando el camión se carga, el microservicio CicloTransporte llama aquí 
     * para saber si el material va a CHANCADO o RELAVES.
  */
    @Transactional(readOnly = true)
    public ConfiguracionRuta determinarDestino(Long palaId, ClasificacionMaterial clasificacionMaterial) {
        return configuracionRutaRepository.findByPalaIdAndClasificacionMaterialAndRutaHabilitadaTrue(palaId, clasificacionMaterial)
                .orElseThrow(() -> new RutaNotFoundException("No se encontró una ruta habilitada para la Pala ID " + palaId + " y material " + clasificacionMaterial));
    }

    /**
     * HISTORIA DE USUARIO: Falla de Pala y Redistribución
     * Cuando Carlos reporta que su pala falló, el sistema apaga todas las rutas 
     * de esa pala y busca automáticamente una "pala vecina" que trabaje el mismo material.
  */
    @Transactional
    public ConfiguracionRuta reportarFallaYRedistribuir(Long palaId, ClasificacionMaterial clasificacionMaterial) {
        
        List<ConfiguracionRuta> rutasAfectadas = configuracionRutaRepository.findByPalaId(palaId);
        if (rutasAfectadas.isEmpty()) {
            throw new RutaNotFoundException("No existen rutas configuradas para la Pala ID " + palaId);
        }
        
        for (ConfiguracionRuta ruta : rutasAfectadas) {
            ruta.setRutaHabilitada(false);
        }
        configuracionRutaRepository.saveAll(rutasAfectadas);

        List<ConfiguracionRuta> palasVecinas = configuracionRutaRepository
                .findByClasificacionMaterialAndRutaHabilitadaTrueAndPalaIdNot(clasificacionMaterial, palaId);
        
        if (palasVecinas.isEmpty()) {
            throw new RutaNotFoundException(
                    "¡ALERTA CRÍTICA! La Pala " + palaId + " falló y no existen palas vecinas " +
                    "habilitadas para recibir el material: " + clasificacionMaterial);
        }

        return palasVecinas.get(0);

    }
    
    @Transactional(readOnly = true)
    public ConfiguracionRuta obtenerPorId(Long id) {
        return configuracionRutaRepository.findById(id)
                .orElseThrow(() -> new RutaNotFoundException("No se encontró ninguna configuración de ruta con el ID: " + id));
    }

    @Transactional
    public ConfiguracionRuta actualizarRuta(Long id, RegistrarRutaDTO dto) {
        // 1. Buscamos si existe
        ConfiguracionRuta rutaExistente = obtenerPorId(id);
        
        // 2. Mapeamos la nueva información (reutilizando la lógica del mapper pero seteando el ID para que actualice y no cree uno nuevo)
        ConfiguracionRuta datosActualizados = configuracionRutaMapper.toEntity(dto);
        datosActualizados.setId(rutaExistente.getId()); 
        
        // Mantener el estado de habilitación que ya tenía (no queremos que un PUT accidentalmente reviva una ruta fallida)
        datosActualizados.setRutaHabilitada(rutaExistente.getRutaHabilitada());
        
        // 3. Guardamos
        return configuracionRutaRepository.save(datosActualizados);
    }

    @Transactional
    public void eliminarRuta(Long id) {
        if (!configuracionRutaRepository.existsById(id)) {
            throw new RutaNotFoundException("No se puede eliminar: No se encontró la ruta con ID: " + id);
        }
        configuracionRutaRepository.deleteById(id);
    }
}
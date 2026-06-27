package cl.duoc.mineria.asignador.mapper;

import cl.duoc.mineria.asignador.dto.ConfiguracionRutaResponseDTO;
import cl.duoc.mineria.asignador.dto.RegistrarRutaDTO;
import cl.duoc.mineria.asignador.model.ConfiguracionRuta;
import cl.duoc.mineria.asignador.model.Destino;
import org.springframework.stereotype.Component;

@Component
public class ConfiguracionRutaMapper {

    public ConfiguracionRuta toEntity(RegistrarRutaDTO dto) {
        if (dto == null) {
            return null;
        }

        ConfiguracionRuta ruta = new ConfiguracionRuta();
        ruta.setPalaId(dto.getPalaId());
        ruta.setClasificacionMaterial(dto.getClasificacionMaterial());
        
        try {
            ruta.setDestinoAsignado(Destino.valueOf(dto.getDestinoAsignado().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Destino inválido. Las opciones permitidas son CHANCADO o RELAVES.");
        }
        
        ruta.setRutaHabilitada(true);
        return ruta;
    }

    public ConfiguracionRutaResponseDTO toDto(ConfiguracionRuta entity) {
        if (entity == null) {
            return null;
        }

        ConfiguracionRutaResponseDTO dto = new ConfiguracionRutaResponseDTO();
        dto.setId(entity.getId());
        dto.setPalaId(entity.getPalaId());
        
        if (entity.getClasificacionMaterial() != null) {
            dto.setClasificacionMaterial(entity.getClasificacionMaterial().name());
        }
        
        if (entity.getDestinoAsignado() != null) {
            dto.setDestinoAsignado(entity.getDestinoAsignado().name());
        }
        
        dto.setRutaHabilitada(entity.getRutaHabilitada());
        
        return dto;
    }
}
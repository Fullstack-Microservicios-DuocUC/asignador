package cl.duoc.mineria.asignador.repository;

import cl.duoc.mineria.asignador.model.ConfiguracionRuta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracionRutaRepository extends JpaRepository<ConfiguracionRuta, Long> {

    // 1. Busca la regla activa para una pala y un material específicos (Para el flujo del palero)
    Optional<ConfiguracionRuta> findByPalaIdAndClasificacionMaterialAndRutaHabilitadaTrue(Long palaId, String clasificacionMaterial);

    // 2. Busca todas las rutas vinculadas a una pala (Para deshabilitarlas en lote cuando Carlos reporte una falla)
    List<ConfiguracionRuta> findByPalaId(Long palaId);

    // 3. Busca rutas activas de otras palas para el mismo material (Para la redistribución inteligente a palas vecinas)
    List<ConfiguracionRuta> findByClasificacionMaterialAndRutaHabilitadaTrueAndPalaIdNot(String clasificacionMaterial, Long palaId);
}
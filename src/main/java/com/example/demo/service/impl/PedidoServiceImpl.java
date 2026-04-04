package com.example.demo.service.impl;

import com.example.demo.dto.PedidoDto;
import com.example.demo.model.Negocio;
import com.example.demo.dto.EstadisticasPedidoDto;
import com.example.demo.model.Pedido;
import com.example.demo.model.Usuario;
import com.example.demo.repository.NegocioRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.UbicacionPedidoRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importación única
import java.time.Duration;

import com.example.demo.model.UbicacionPedido;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional // ✅ Esto permite borrar datos (GPS) sin errores
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private UbicacionPedidoRepository ubicacionPedidoRepository;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private NegocioRepository negocioRepository;

    private static final double NEGOCIO_LAT = 19.451840;
    private static final double NEGOCIO_LON = -99.172550;

    @Override
    public PedidoDto crearPedido(Pedido nuevoPedido) {
        // Validación del Negocio
        if (nuevoPedido.getNegocio() != null && nuevoPedido.getNegocio().getIdLicencia() != null) {
            Negocio negocioReal = negocioRepository.findById(nuevoPedido.getNegocio().getIdLicencia())
                    .orElseThrow(() -> new RuntimeException("Negocio no encontrado"));
            nuevoPedido.setNegocio(negocioReal);
        }

        // Estado inicial obligatorio: PENDIENTE
        nuevoPedido.setEstadoReal("PENDIENTE");

        Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);

        // 🛑 HE COMENTADO ESTO PARA QUE NO SE ASIGNE SOLO AL ID 6
        // pedidoGuardado = intentarAsignacionAutomatica(pedidoGuardado);

        return convertirADto(pedidoGuardado);
    }

    // Este método ya no se usa automáticamente, pero lo dejamos por si lo quieres usar después
    private Pedido intentarAsignacionAutomatica(Pedido pedido) {
        List<Usuario> repartidoresCandidatos = usuarioRepository.findByNegocio_IdLicenciaAndRol_Rol(
                pedido.getNegocio().getIdLicencia(), "REPARTIDOR");

        Usuario mejorCandidato = null;
        double menorDistancia = Double.MAX_VALUE;
        double RADIO_MAXIMO_KM = 10.0;
        double origenLat = NEGOCIO_LAT;
        double origenLon = NEGOCIO_LON;

        for (Usuario repartidor : repartidoresCandidatos) {
            if ("DISPONIBLE".equals(repartidor.getEstatus()) &&
                    repartidor.getLatitudActual() != null &&
                    repartidor.getLongitudActual() != null) {

                double distancia = calcularDistancia(origenLat, origenLon,
                        repartidor.getLatitudActual(), repartidor.getLongitudActual());

                if (distancia < menorDistancia && distancia <= RADIO_MAXIMO_KM) {
                    menorDistancia = distancia;
                    mejorCandidato = repartidor;
                }
            }
        }

        if (mejorCandidato != null) {
            pedido.setRepartidorAsignado(mejorCandidato);
            pedido.setEstadoReal("EN_CURSO"); // OJO: Si usas esto, cambia a EN_CURSO

            mejorCandidato.setEstatus("OCUPADO");
            usuarioRepository.save(mejorCandidato);
            return pedidoRepository.save(pedido);
        }
        return pedido;
    }

    @Override
    public PedidoDto asignarRepartidor(Integer numOrd, Integer idRepartidor) {
        Pedido pedido = pedidoRepository.findById(numOrd)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        Usuario repartidor = usuarioRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

        pedido.setRepartidorAsignado(repartidor);
        pedido.setEstadoReal("EN_CAMINO");

        // 👇 EL CRONÓMETRO INICIA AQUÍ 👇
        pedido.setFechaHoraRecogida(LocalDateTime.now());

        repartidor.setEstatus("OCUPADO");
        usuarioRepository.save(repartidor);

        return convertirADto(pedidoRepository.save(pedido));
    }

    @Override
    public PedidoDto actualizarEstatus(Integer idPedido, String nuevoEstatus) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstadoReal(nuevoEstatus);

        if ("EN_CAMINO".equalsIgnoreCase(nuevoEstatus) && pedido.getFechaHoraRecogida() == null) {
            pedido.setFechaHoraRecogida(LocalDateTime.now());
        }

        if ("ENTREGADO".equalsIgnoreCase(nuevoEstatus)) {
            pedido.setFechaHoraEntrega(LocalDateTime.now());

            if (pedido.getRepartidorAsignado() != null) {
                Usuario repartidor = pedido.getRepartidorAsignado();
                repartidor.setEstatus("DISPONIBLE"); // Liberamos al repartidor
                usuarioRepository.save(repartidor);
            }
        }

        return convertirADto(pedidoRepository.save(pedido));
    }

    @Override
    public PedidoDto actualizarEstado(Integer numOrd, String nuevoEstado) {
        return actualizarEstatus(numOrd, nuevoEstado);
    }

    @Override
    public List<PedidoDto> obtenerPedidosPorRepartidor(Integer idRepartidor) {
        return pedidoRepository.findByRepartidorAsignadoIdAndEstadoReal(idRepartidor, "EN_CAMINO")
                .stream().map(this::convertirADto).collect(Collectors.toList());
    }

    @Override
    public List<PedidoDto> getPedidosPorNegocio(Integer idLicencia) {
        return pedidoRepository.findByNegocio_IdLicencia(idLicencia)
                .stream().map(this::convertirADto).collect(Collectors.toList());
    }

    @Override
    public void eliminarPedido(Integer numOrd) {
        pedidoRepository.deleteById(numOrd);
    }

    @Override
    public PedidoDto actualizarPedido(Integer numOrd, PedidoDto dto) {
        return null;
    }

    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public List<PedidoDto> obtenerHistorialRepartidor(Integer idRepartidor) {
        List<Pedido> pedidos = pedidoRepository.findHistorialPorRepartidor(idRepartidor);
        return pedidos.stream().map(this::convertirADto).collect(Collectors.toList());
    }

    @Override
    public List<PedidoDto> obtenerHistorialNegocio(Integer idLicencia) {
        List<Pedido> pedidos = pedidoRepository.findHistorialPorNegocio(idLicencia);
        return pedidos.stream().map(this::convertirADto).collect(Collectors.toList());
    }

    public EstadisticasPedidoDto obtenerEstadisticas(Integer numOrd) {
        Pedido pedido = pedidoRepository.findById(numOrd)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // 1. Calcular el tiempo exacto
        long minutos = 0;
        if (pedido.getFechaHoraRecogida() != null && pedido.getFechaHoraEntrega() != null) {
            minutos = Duration.between(pedido.getFechaHoraRecogida(), pedido.getFechaHoraEntrega()).toMinutes();
        }

        // 2. Calcular la distancia real sumando los puntos GPS
        double distanciaTotal = 0.0;
        // IMPORTANTE: Necesitas crear este método en tu UbicacionPedidoRepository para que ordene por tiempo
        List<UbicacionPedido> ruta = ubicacionPedidoRepository.findByPedido_NumOrdOrderByTimestampAsc(numOrd);

        if (ruta != null && ruta.size() > 1) {
            for (int i = 0; i < ruta.size() - 1; i++) {
                UbicacionPedido puntoA = ruta.get(i);
                UbicacionPedido puntoB = ruta.get(i + 1);

                distanciaTotal += calcularDistancia(
                        puntoA.getLatitud(), puntoA.getLongitud(),
                        puntoB.getLatitud(), puntoB.getLongitud()
                );
            }
        }

        return new EstadisticasPedidoDto(minutos, distanciaTotal);
    }

    // --- CONVERSOR A DTO ---
    private PedidoDto convertirADto(Pedido pedido) {
        PedidoDto dto = new PedidoDto();

        dto.setNumOrd(pedido.getNumOrd());
        dto.setDescripcion(pedido.getDescripcion());
        dto.setEstadoReal(pedido.getEstadoReal());
        dto.setDestino(pedido.getDestino());

        if (pedido.getNegocio() != null) {
            dto.setIdLicencia(pedido.getNegocio().getIdLicencia());
            dto.setNombreNegocio(pedido.getNegocio().getNomEmp());
        }

        if (pedido.getRepartidorAsignado() != null) {
            dto.setIdRepartidor(pedido.getRepartidorAsignado().getId());
            dto.setNombreRepartidor(pedido.getRepartidorAsignado().getNombre());
            dto.setLatitud(pedido.getRepartidorAsignado().getLatitudActual());
            dto.setLongitud(pedido.getRepartidorAsignado().getLongitudActual());
        }

        dto.setFechaHoraCreacion(pedido.getFechaHoraCreacion());
        dto.setFechaHoraRecogida(pedido.getFechaHoraRecogida());
        dto.setFechaHoraEntrega(pedido.getFechaHoraEntrega());
        dto.setNombreCliente(pedido.getNombreCliente());
        dto.setTelefonoCliente(pedido.getTelefonoCliente());
        dto.setLatitudDestino(pedido.getLatitudDestino());
        dto.setLongitudDestino(pedido.getLongitudDestino());

        return dto;
    }
}
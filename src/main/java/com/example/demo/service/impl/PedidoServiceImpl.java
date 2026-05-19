package com.example.demo.service.impl;

import com.example.demo.dto.PedidoDto;
import com.example.demo.model.Negocio;
import com.example.demo.dto.DashboardNegocioDto;
import com.example.demo.model.Pedido;
import com.example.demo.model.Usuario;
import com.example.demo.repository.NegocioRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private NegocioRepository negocioRepository;

    @Override
    public PedidoDto crearPedido(PedidoDto dto) {
        if (dto.getIdLicencia() == null) {
            throw new RuntimeException("El ID de la licencia del negocio es obligatorio.");
        }

        Negocio negocio = negocioRepository.findById(dto.getIdLicencia())
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado"));

        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setDescripcion(dto.getDescripcion());
        nuevoPedido.setDestino(dto.getDestino());
        nuevoPedido.setNombreCliente(dto.getNombreCliente());
        nuevoPedido.setTelefonoCliente(dto.getTelefonoCliente());
        nuevoPedido.setLatitudDestino(dto.getLatitudDestino());
        nuevoPedido.setLongitudDestino(dto.getLongitudDestino());
        nuevoPedido.setNegocio(negocio);
        nuevoPedido.setEstadoReal("PENDIENTE");

        return convertirADto(pedidoRepository.save(nuevoPedido));
    }

    @Override
    public PedidoDto actualizarPedido(Integer numOrd, PedidoDto dto) {
        Pedido pedido = pedidoRepository.findById(numOrd)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if ("ENTREGADO".equalsIgnoreCase(pedido.getEstadoReal())) {
            throw new RuntimeException("No se puede modificar un pedido que ya fue entregado.");
        }

        pedido.setDescripcion(dto.getDescripcion());
        pedido.setDestino(dto.getDestino());
        pedido.setNombreCliente(dto.getNombreCliente());
        pedido.setTelefonoCliente(dto.getTelefonoCliente());

        if (dto.getLatitudDestino() != null) pedido.setLatitudDestino(dto.getLatitudDestino());
        if (dto.getLongitudDestino() != null) pedido.setLongitudDestino(dto.getLongitudDestino());

        return convertirADto(pedidoRepository.save(pedido));
    }

    @Override
    public PedidoDto asignarRepartidor(Integer numOrd, Integer idRepartidor) {
        Pedido pedido = pedidoRepository.findById(numOrd)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        Usuario repartidor = usuarioRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

        pedido.setRepartidorAsignado(repartidor);
        pedido.setEstadoReal("EN_CAMINO");
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

            if (pedido.getFechaHoraRecogida() != null) {
                long minutos = java.time.Duration.between(pedido.getFechaHoraRecogida(), pedido.getFechaHoraEntrega()).toMinutes();
                pedido.setMinutosTranscurridos(minutos > 0 ? minutos : 1L);
            } else {
                pedido.setMinutosTranscurridos(15L); // Promedio estimado
            }

            if (pedido.getLatitudDestino() != null && pedido.getLongitudDestino() != null) {
                double latOrigen = (pedido.getNegocio() != null && pedido.getNegocio().getLatitud() != null)
                        ? pedido.getNegocio().getLatitud() : 19.451840;
                double lonOrigen = (pedido.getNegocio() != null && pedido.getNegocio().getLongitud() != null)
                        ? pedido.getNegocio().getLongitud() : -99.172550;

                double distanciaKm = calcularDistancia(latOrigen, lonOrigen, pedido.getLatitudDestino(), pedido.getLongitudDestino());
                pedido.setKilometrosRecorridos(Math.round((distanciaKm * 1.3) * 100.0) / 100.0);
            } else {
                pedido.setKilometrosRecorridos(2.5);
            }

            if (pedido.getRepartidorAsignado() != null) {
                Usuario repartidor = pedido.getRepartidorAsignado();
                repartidor.setEstatus("DISPONIBLE");
                usuarioRepository.save(repartidor);
            }
        }

        return convertirADto(pedidoRepository.save(pedido));
    }

    @Override
    public void eliminarPedido(Integer numOrd) {
        pedidoRepository.deleteById(numOrd);
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
    public List<PedidoDto> obtenerHistorialRepartidor(Integer idRepartidor) {
        return pedidoRepository.findHistorialPorRepartidor(idRepartidor)
                .stream().map(this::convertirADto).collect(Collectors.toList());
    }

    @Override
    public List<PedidoDto> obtenerHistorialNegocio(Integer idLicencia) {
        return pedidoRepository.findHistorialPorNegocio(idLicencia)
                .stream().map(this::convertirADto).collect(Collectors.toList());
    }

    @Override
    public DashboardNegocioDto generarDashboardAnalitico(Integer idLicencia) {
        List<Pedido> pedidosValidos = pedidoRepository.findByNegocio_IdLicencia(idLicencia).stream()
                .filter(p -> "ENTREGADO".equalsIgnoreCase(p.getEstadoReal()))
                .filter(p -> p.getMinutosTranscurridos() != null && p.getMinutosTranscurridos() > 0)
                .filter(p -> p.getKilometrosRecorridos() != null && p.getKilometrosRecorridos() > 0)
                .collect(Collectors.toList());

        return construirDashboard(pedidosValidos);
    }

    @Override
    public DashboardNegocioDto generarDashboardAnaliticoRepartidor(Integer idRepartidor) {
        List<Pedido> pedidosValidos = pedidoRepository.findHistorialPorRepartidor(idRepartidor).stream()
                .filter(p -> p.getMinutosTranscurridos() != null && p.getMinutosTranscurridos() > 0)
                .filter(p -> p.getKilometrosRecorridos() != null && p.getKilometrosRecorridos() > 0)
                .collect(Collectors.toList());

        return construirDashboard(pedidosValidos);
    }

    // --- MÉTODOS PRIVADOS AUXILIARES ---

    private DashboardNegocioDto construirDashboard(List<Pedido> pedidosValidos) {
        DashboardNegocioDto dashboard = new DashboardNegocioDto();
        dashboard.setTotalPedidosEntregados(pedidosValidos.size());

        if (pedidosValidos.isEmpty()) {
            dashboard.setPromedioTiempoEntrega(0.0);
            dashboard.setTotalKilometrosRecorridos(0.0);
            dashboard.setHistorialReciente(Collections.emptyList());
            return dashboard;
        }

        double totalKm = pedidosValidos.stream().mapToDouble(Pedido::getKilometrosRecorridos).sum();
        double promedioTiempo = pedidosValidos.stream().mapToDouble(Pedido::getMinutosTranscurridos).average().orElse(0.0);

        dashboard.setTotalKilometrosRecorridos(Math.round(totalKm * 100.0) / 100.0);
        dashboard.setPromedioTiempoEntrega(Math.round(promedioTiempo * 100.0) / 100.0);

        dashboard.setHistorialReciente(pedidosValidos.stream()
                .limit(15)
                .map(this::convertirADto)
                .collect(Collectors.toList()));

        return dashboard;
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
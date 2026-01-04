package com.example.demo.service.impl;

import com.example.demo.dto.PedidoDto;
import com.example.demo.dto.UbicacionDto; // Necesario para manejar coords
import com.example.demo.model.Negocio;
import com.example.demo.model.Pedido;
import com.example.demo.model.Usuario;
import com.example.demo.repository.NegocioRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.PedidoService;
import com.example.demo.service.UbicacionService; // Necesitamos consultar ubicaciones
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private NegocioRepository negocioRepository;

    @Autowired
    private UbicacionService ubicacionService; // Inyectamos el servicio de ubicación

    // Coordenadas fijas del Negocio (Popotla) - Idealmente esto iría en la tabla Negocio
    private static final double NEGOCIO_LAT = 19.451840;
    private static final double NEGOCIO_LON = -99.172550;

    @Override
    public PedidoDto crearPedido(Pedido nuevoPedido) {
        Negocio negocio = negocioRepository.findById(nuevoPedido.getNegocio().getIdLicencia())
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado"));

        nuevoPedido.setNegocio(negocio);
        nuevoPedido.setEstado("PENDIENTE");

        // Guardamos primero para tener un ID
        Pedido guardado = pedidoRepository.save(nuevoPedido);

        // --- ASIGNACIÓN AUTOMÁTICA ---
        // Intentamos buscar un repartidor cercano de inmediato
        intentarAsignacionAutomatica(guardado);

        return convertirADto(guardado);
    }

    @Override
    public PedidoDto asignarRepartidor(Integer numOrd, Integer idRepartidor) {
        // 1. Buscar Pedido
        Pedido pedido = pedidoRepository.findById(numOrd)
                .orElseThrow(() -> new RuntimeException("Pedido " + numOrd + " no encontrado"));

        // 2. Validar que el pedido no tenga ya un repartidor (Opcional, según tus reglas)
        if (pedido.getRepartidorAsignado() != null) {
            throw new RuntimeException("El pedido ya tiene al repartidor " + pedido.getRepartidorAsignado().getNombre());
        }

        // 3. Buscar Repartidor y Validar Estatus
        Usuario repartidor = usuarioRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("Repartidor " + idRepartidor + " no encontrado"));

        // ¡Importante! Verificar que sea del mismo negocio
        if (!repartidor.getNegocio().getIdLicencia().equals(pedido.getNegocio().getIdLicencia())) {
            throw new RuntimeException("El repartidor no pertenece al negocio del pedido");
        }

        // 4. Asignar y cambiar estado
        pedido.setRepartidorAsignado(repartidor);
        pedido.setEstado("EN_CAMINO"); // O "ASIGNADO" si prefieres un paso intermedio

        // 5. Guardar
        Pedido guardado = pedidoRepository.save(pedido);

        // (Opcional) Aquí podrías notificar al repartidor si tuvieras sockets/firebase

        return convertirADto(guardado);
    }

    private void intentarAsignacionAutomatica(Pedido pedido) {
        // 1. Obtener repartidores del MISMO negocio que estén DISPONIBLES
        List<Usuario> repartidoresCandidatos = usuarioRepository.findByNegocio_IdLicenciaAndRol_Rol(
                pedido.getNegocio().getIdLicencia(), "REPARTIDOR"); // Asegúrate de filtrar por Estatus="DISPONIBLE" si puedes

        Usuario mejorCandidato = null;
        double menorDistancia = Double.MAX_VALUE;
        double RADIO_MAXIMO_KM = 10.0;

        // Coordenadas del Negocio (Donde se recoge el pedido)
        // Idealmente usa: pedido.getNegocio().getLatitud()... por ahora usaremos las constantes que tenías
        double origenLat = NEGOCIO_LAT;
        double origenLon = NEGOCIO_LON;

        System.out.println("--- Buscando Repartidor Automático ---");

        for (Usuario repartidor : repartidoresCandidatos) {
            // 2. Validar: ¿Está disponible? ¿Tiene ubicación registrada?
            if ("DISPONIBLE".equals(repartidor.getEstatus()) &&
                    repartidor.getLatitudActual() != null &&
                    repartidor.getLongitudActual() != null) {

                // 3. Calcular distancia entre el Negocio y el Repartidor
                double distancia = calcularDistancia(
                        origenLat, origenLon,
                        repartidor.getLatitudActual(), repartidor.getLongitudActual()
                );

                System.out.println("Candidato: " + repartidor.getNombre() + " a " + String.format("%.2f", distancia) + "km");

                if (distancia < menorDistancia && distancia <= RADIO_MAXIMO_KM) {
                    menorDistancia = distancia;
                    mejorCandidato = repartidor;
                }
            }
        }

        // 4. Asignar si encontramos a alguien
        if (mejorCandidato != null) {
            pedido.setRepartidorAsignado(mejorCandidato);
            pedido.setEstado("EN_CAMINO"); // O "ASIGNADO"

            // ¡Importante! Cambiar estatus del repartidor a OCUPADO para que no le caigan 2 pedidos
            mejorCandidato.setEstatus("OCUPADO");
            usuarioRepository.save(mejorCandidato);

            pedidoRepository.save(pedido);
            System.out.println("¡ASIGNADO A: " + mejorCandidato.getNombre() + "!");
        } else {
            System.out.println("No hay repartidores disponibles cerca.");
        }
    }

    // Fórmula de Haversine para calcular distancia en KM
    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la tierra en km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public PedidoDto actualizarEstado(Integer numOrd, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(numOrd)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(nuevoEstado);

        Pedido guardado = pedidoRepository.save(pedido);
        return convertirADto(guardado);
    }

    @Override
    public List<PedidoDto> getPedidosPorRepartidor(Integer idRepartidor) {
        return pedidoRepository.findByRepartidorAsignado_Id(idRepartidor)
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PedidoDto> getPedidosPorNegocio(Integer idLicencia) {
        return pedidoRepository.findByNegocio_IdLicencia(idLicencia)
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Override
    public PedidoDto actualizarPedido(Integer numOrd, PedidoDto pedidoDto) {
        Pedido pedido = pedidoRepository.findById(numOrd)
                .orElseThrow(() -> new RuntimeException("No encontrado"));

        pedido.setDescripcion(pedidoDto.getDescripcion());
        pedido.setDestino(pedidoDto.getDestino());
        return convertirADto(pedidoRepository.save(pedido));
    }

    @Override
    public void eliminarPedido(Integer numOrd) {
        pedidoRepository.deleteById(numOrd);
    }

    private PedidoDto convertirADto(Pedido pedido) {
        PedidoDto dto = new PedidoDto();
        dto.setNumOrd(pedido.getNumOrd());
        dto.setDescripcion(pedido.getDescripcion());
        dto.setEstado(pedido.getEstado());
        dto.setDestino(pedido.getDestino());
        dto.setFechaDeEntrega(pedido.getFechaDeEntrega());

        dto.setIdLicencia(pedido.getNegocio().getIdLicencia());
        dto.setNombreNegocio(pedido.getNegocio().getNomEmp());

        if (pedido.getRepartidorAsignado() != null) {
            dto.setIdRepartidor(pedido.getRepartidorAsignado().getId());
            dto.setNombreRepartidor(pedido.getRepartidorAsignado().getNombre());
        }

        return dto;
    }
}
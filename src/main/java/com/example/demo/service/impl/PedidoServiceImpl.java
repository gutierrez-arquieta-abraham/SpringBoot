package com.example.demo.service.impl;

import com.example.demo.dto.PedidoDto;
import com.example.demo.model.Negocio;
import com.example.demo.dto.DashboardNegocioDto;
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

import java.time.LocalDateTime;
import java.util.Collections;
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
        List<Usuario> repartidoresCandidatos = usuarioRepository.findByNegocio_IdLicenciaAndRol_RolAndActivoTrue(
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

            // --- 1. CÁLCULO DE TIEMPO (IA Heurística Básica) ---
            if (pedido.getFechaHoraRecogida() != null) {
                // Calculamos la diferencia exacta en minutos
                long minutos = java.time.Duration.between(pedido.getFechaHoraRecogida(), pedido.getFechaHoraEntrega()).toMinutes();
                // Evitamos que guarde 0 si el repartidor presionó los botones muy rápido en las pruebas
                pedido.setMinutosTranscurridos(minutos > 0 ? minutos : 1L);
            } else {
                // Si el repartidor olvidó marcar "EN_CAMINO", asignamos un promedio estimado
                pedido.setMinutosTranscurridos(15L);
            }

            // --- 2. CÁLCULO DE DISTANCIA ---
            if (pedido.getLatitudDestino() != null && pedido.getLongitudDestino() != null) {
                // Usamos una ubicación de origen por defecto (o la del negocio si ya la tienes en el objeto)
                double latOrigen = 19.451840;
                double lonOrigen = -99.172550;

                if(pedido.getNegocio() != null && pedido.getNegocio().getLatitud() != null){
                    latOrigen = pedido.getNegocio().getLatitud();
                    lonOrigen = pedido.getNegocio().getLongitud();
                }

                // Usamos tu método existente para calcular la línea recta
                double distanciaKm = calcularDistancia(latOrigen, lonOrigen, pedido.getLatitudDestino(), pedido.getLongitudDestino());

                // TRUCO DE ANÁLISIS DE DATOS: Multiplicamos por 1.3 para simular calles reales.
                // La distancia en línea recta no sirve en la ciudad (Factor de desvío urbano).
                double distanciaReal = distanciaKm * 1.3;

                pedido.setKilometrosRecorridos(Math.round(distanciaReal * 100.0) / 100.0);
            } else {
                pedido.setKilometrosRecorridos(2.5); // Valor predeterminado de seguridad
            }

            // --- 3. LIBERAR AL REPARTIDOR ---
            if (pedido.getRepartidorAsignado() != null) {
                Usuario repartidor = pedido.getRepartidorAsignado();
                repartidor.setEstatus("DISPONIBLE");
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
        // En lugar de return null; pon esto:
        throw new UnsupportedOperationException("Este método aún no está implementado en la fase actual del proyecto.");
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

    @Override
    public DashboardNegocioDto obtenerEstadisticas(Integer numOrd) {
        return null;
    }

    // Este es el método que vas a presumir en tu clase de Análisis de Datos
    public DashboardNegocioDto generarDashboardAnalitico(Integer idLicencia) {

        // 1. EXTRAER DATOS: Obtenemos TODO el historial del negocio
        // Suponiendo que tienes un método en tu repository que trae los pedidos por ID de negocio
        List<Pedido> todosLosPedidos = pedidoRepository.findByNegocio_IdLicencia(idLicencia);

        // 2. LIMPIEZA DE DATOS (Data Cleansing):
        // Solo nos interesan los pedidos que SÍ se entregaron y que tienen datos válidos de tiempo/distancia
        List<Pedido> pedidosValidos = todosLosPedidos.stream()
                .filter(p -> p.getEstadoReal() != null && p.getEstadoReal().equalsIgnoreCase("ENTREGADO"))
                .filter(p -> p.getMinutosTranscurridos() != null && p.getMinutosTranscurridos() > 0)
                .filter(p -> p.getKilometrosRecorridos() != null && p.getKilometrosRecorridos() > 0)
                .collect(Collectors.toList());

        DashboardNegocioDto dashboard = new DashboardNegocioDto();
        dashboard.setTotalPedidosEntregados(pedidosValidos.size());

        if (pedidosValidos.isEmpty()) {
            // Manejo de valores nulos si es un negocio nuevo sin ventas
            dashboard.setPromedioTiempoEntrega(0.0);
            dashboard.setTotalKilometrosRecorridos(0.0);
            dashboard.setHistorialReciente(Collections.emptyList());
            return dashboard;
        }

        // 3. AGREGACIÓN Y CÁLCULO ESTADÍSTICO
        // Suma total de kilómetros recorridos por toda la flota
        double totalKm = pedidosValidos.stream()
                .mapToDouble(Pedido::getKilometrosRecorridos)
                .sum();
        dashboard.setTotalKilometrosRecorridos(Math.round(totalKm * 100.0) / 100.0); // Redondeo a 2 decimales

        // Promedio matemático simple: Suma de todos los tiempos / cantidad de pedidos
        double promedioTiempo = pedidosValidos.stream()
                .mapToDouble(Pedido::getMinutosTranscurridos)
                .average()
                .orElse(0.0);
        dashboard.setPromedioTiempoEntrega(Math.round(promedioTiempo * 100.0) / 100.0);

        // 4. EXTRACCIÓN DE MUESTRA PARA GRÁFICAS
        // Convertimos a DTO solo los últimos 15 pedidos para no saturar la red del celular
        List<PedidoDto> ultimosPedidos = pedidosValidos.stream()
                // Si tienes un campo fecha, aquí deberías ordenarlo: .sorted(Comparator.comparing(Pedido::getFecha).reversed())
                .limit(15)
                .map(this::convertirADto) // Usa tu método existente que convierte Entidad a Dto
                .collect(Collectors.toList());

        dashboard.setHistorialReciente(ultimosPedidos);

        return dashboard;
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
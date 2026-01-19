package com.example.demo.service.impl;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.UsuarioDto;
import com.example.demo.model.*; // Importamos todos los modelos (Pedido, UbicacionPedido, etc)
import com.example.demo.repository.*; // Importamos todos los repositorios
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private NegocioRepository negocioRepository;

    // --- AGREGAMOS ESTOS DOS REPOSITORIOS NUEVOS ---
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private UbicacionPedidoRepository ubicacionPedidoRepository;
    // -----------------------------------------------

    @Override
    public List<UsuarioDto> obtenerRepartidoresPorNegocio(Integer idLicencia) {
        // ROL_ID 2 = Repartidor
        List<Usuario> repartidores = usuarioRepository.findByRol_IdAndNegocio_IdLicencia(2, idLicencia);
        return repartidores.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioDto vincularRepartidorPorCodigo(Integer idUsuario, String codigoConexion) {
        Negocio negocio = negocioRepository.findByCodigoConexion(codigoConexion)
                .orElseThrow(() -> new RuntimeException("Código no válido o expirado."));

        Usuario repartidor = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado."));

        repartidor.setNegocio(negocio);
        repartidor.setEstatus("DISPONIBLE");

        return convertirADto(usuarioRepository.save(repartidor));
    }

    @Override
    public UsuarioDto registrarUsuario(Usuario nuevoUsuario, String nombreRol) {
        Rol rol = rolRepository.findByRol(nombreRol)
                .orElseThrow(() -> new RuntimeException("Error: Rol '" + nombreRol + "' no encontrado."));

        if (nuevoUsuario.getNegocio() != null && nuevoUsuario.getNegocio().getIdLicencia() != null) {
            Negocio negocioReal = negocioRepository.findById(nuevoUsuario.getNegocio().getIdLicencia())
                    .orElseThrow(() -> new RuntimeException("Error: Negocio no encontrado."));
            nuevoUsuario.setNegocio(negocioReal);
        } else {
            // Si es gestor o admin, quizá no necesite negocio al inicio, pero para repartidor sí.
            // Ajusta según tu lógica.
            // throw new RuntimeException("Error: El usuario debe pertenecer a un negocio.");
        }

        nuevoUsuario.setRol(rol);
        nuevoUsuario.setEstatus("DISPONIBLE");
        nuevoUsuario.setContrasena(passwordEncoder.encode(nuevoUsuario.getContrasena()));

        try {
            Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
            return convertirADto(usuarioGuardado);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new RuntimeException("El correo o RFC ya están registrados.");
        }
    }

    @Override
    public UsuarioDto login(LoginDto loginDto) {
        Usuario usuario = usuarioRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos"));

        if (passwordEncoder.matches(loginDto.getContrasena(), usuario.getContrasena())) {
            return convertirADto(usuario);
        } else {
            throw new RuntimeException("Email o contraseña incorrectos");
        }
    }

    @Override
    public UsuarioDto actualizarUsuario(Integer id, UsuarioDto usuarioDto) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario ID " + id + " no encontrado"));

        usuarioExistente.setNombre(usuarioDto.getNombre());
        usuarioExistente.setEmail(usuarioDto.getEmail());
        usuarioExistente.setRfc(usuarioDto.getRfc());

        Usuario actualizado = usuarioRepository.save(usuarioExistente);
        return convertirADto(actualizado);
    }

    @Override
    public void actualizarContrasena(Integer id, String nuevaContrasena) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario ID " + id + " no encontrado"));

        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(usuario);
    }

    @Override
    public void eliminarUsuario(Integer id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public UsuarioDto getUsuarioById(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario ID " + id + " no encontrado"));
        return convertirADto(usuario);
    }

    @Override
    public void vincularNegocio(Integer idUsuario, Integer idLicencia) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Negocio negocio = negocioRepository.findById(idLicencia)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado"));

        usuario.setNegocio(negocio);
        usuarioRepository.save(usuario);
    }

    @Override
    public void cambiarEstatus(Integer id, String nuevoEstatus) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setEstatus(nuevoEstatus);
        usuarioRepository.save(usuario);
    }

    @Override
    public void actualizarUbicacionRepartidor(Integer idUsuario, Double lat, Double lon) {
        // Este método parece duplicado con actualizarUbicacion,
        // pero lo dejamos por compatibilidad si lo usas en otro lado.
        Usuario repartidor = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

        repartidor.setLatitudActual(lat);
        repartidor.setLongitudActual(lon);
        usuarioRepository.save(repartidor);
    }

    @Override
    public UsuarioDto actualizarEstatus(Integer idUsuario, String nuevoEstatus) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

        if (nuevoEstatus != null && !nuevoEstatus.isEmpty()) {
            usuario.setEstatus(nuevoEstatus);
        }
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return convertirADto(usuarioGuardado);
    }

    // --- AQUÍ ESTÁ LA MAGIA DEL HISTORIAL ---
    @Override
    public void actualizarUbicacion(Integer idRepartidor, Double latitud, Double longitud) {

        // 1. Actualizar la ubicación actual del Repartidor (Para que se vea en el mapa en vivo)
        Usuario repartidor = usuarioRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

        repartidor.setLatitudActual(latitud);
        repartidor.setLongitudActual(longitud);
        usuarioRepository.save(repartidor);

        // 2. BUSCAR PEDIDOS ACTIVOS ("EN_CAMINO")
        // Usamos el método personalizado que agregamos al PedidoRepository
        List<Pedido> pedidosEnCamino = pedidoRepository.findByRepartidorAsignadoIdAndEstadoReal(idRepartidor, "EN_CAMINO");

        // 3. SI HAY PEDIDOS, GUARDAR EL RASTRO EN LA TABLA HISTORIAL
        if (!pedidosEnCamino.isEmpty()) {
            for (Pedido p : pedidosEnCamino) {
                // Creamos el registro usando el constructor con Lombok
                // (La fecha se pone sola gracias a @CreationTimestamp)
                UbicacionPedido rastro = new UbicacionPedido(p, latitud, longitud);

                ubicacionPedidoRepository.save(rastro);

                System.out.println("📍 Historial guardado para Pedido #" + p.getNumOrd() + " [" + latitud + ", " + longitud + "]");
            }
        }
    }

    // --- MÉTODO PRIVADO ÚNICO PARA CONVERTIR A DTO ---
    private UsuarioDto convertirADto(Usuario usuario) {
        UsuarioDto dto = new UsuarioDto();

        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRfc(usuario.getRfc());
        dto.setEstatus(usuario.getEstatus());
        dto.setLatitudActual(usuario.getLatitudActual());
        dto.setLongitudActual(usuario.getLongitudActual());

        if (usuario.getRol() != null) {
            dto.setRolId(usuario.getRol().getId());
        }

        if (usuario.getNegocio() != null) {
            dto.setIdLicencia(usuario.getNegocio().getIdLicencia());
        } else {
            dto.setIdLicencia(null);
        }

        return dto;
    }
}
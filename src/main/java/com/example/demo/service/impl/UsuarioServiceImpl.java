package com.example.demo.service.impl;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.UsuarioDto;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private NegocioRepository negocioRepository;
    @Autowired
    private PedidoRepository pedidoRepository;

    // AQUÍ ESTÁ LA CORRECCIÓN: Usamos el repo unificado que creamos antes
    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Override
    public UsuarioDto registrarUsuario(UsuarioDto dto, String nombreRol) {
        Rol rol = rolRepository.findByRol(nombreRol.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Error: Rol '" + nombreRol + "' no encontrado."));

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(dto.getNombre());
        nuevoUsuario.setEmail(dto.getEmail());
        nuevoUsuario.setRfc(dto.getRfc());
        nuevoUsuario.setTelefono(dto.getTelefono());
        nuevoUsuario.setRol(rol);
        nuevoUsuario.setEstatus("DISPONIBLE");
        nuevoUsuario.setActivo(true);

        // En el registro (LoginDto) no viaja la contraseña en el UsuarioDto normalmente,
        // asumiendo que agregaste 'contrasena' temporalmente al DTO o la recibes de otra forma.
        // OJO: Asegúrate de que el DTO que usas para registrar tenga el campo contraseña.
        // Aquí lo dejaremos explícito para que no te falle la encriptación.
        // Si tu DTO no tiene getContrasena(), debes agregárselo.

        nuevoUsuario.setContrasena(passwordEncoder.encode(dto.getContrasenaTransitoria())); // <- IMPORTANTE: Agrega este campo a tu UsuarioDto (solo para lectura)

        if (dto.getIdLicencia() != null) {
            Negocio negocioReal = negocioRepository.findById(dto.getIdLicencia())
                    .orElseThrow(() -> new RuntimeException("Error: Negocio no encontrado."));
            nuevoUsuario.setNegocio(negocioReal);
        }

        try {
            return convertirADto(usuarioRepository.save(nuevoUsuario));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new RuntimeException("El correo o RFC ya están registrados en el sistema.");
        }
    }

    @Override
    public UsuarioDto login(LoginDto loginDto) {
        Usuario usuario = usuarioRepository.findByEmailAndActivoTrue(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos"));

        if (passwordEncoder.matches(loginDto.getContrasena(), usuario.getContrasena())) {
            UsuarioDto dto = convertirADto(usuario);

            // --- AQUÍ SE GENERA LA CREDENCIAL ---
            String tokenGenerado = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol().getRol());
            dto.setToken(tokenGenerado); // Se lo inyectamos al DTO de respuesta

            return dto;
        } else {
            throw new RuntimeException("Email o contraseña incorrectos");
        }
    }

    @Override
    public UsuarioDto actualizarUsuario(Integer id, UsuarioDto usuarioDto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario ID " + id + " no encontrado"));

        usuario.setNombre(usuarioDto.getNombre());
        usuario.setEmail(usuarioDto.getEmail());
        usuario.setRfc(usuarioDto.getRfc());
        usuario.setTelefono(usuarioDto.getTelefono());

        return convertirADto(usuarioRepository.save(usuario));
    }

    @Override
    public void recuperarContrasenaPorEmail(String email, String nuevaContrasena) {
        Usuario usuario = usuarioRepository.findByEmailAndActivoTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(usuario);
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
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(false);
        usuario.setEstatus("FUERA_SERVICIO");
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
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
    public UsuarioDto vincularRepartidorPorCodigo(Integer idUsuario, String codigoConexion) {
        Negocio negocio = negocioRepository.findByCodigoConexionAndActivoTrue(codigoConexion)
                .orElseThrow(() -> new RuntimeException("Código de conexión no válido o expirado."));
        Usuario repartidor = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado."));

        repartidor.setNegocio(negocio);
        repartidor.setEstatus("DISPONIBLE");

        return convertirADto(usuarioRepository.save(repartidor));
    }

    @Override
    public UsuarioDto actualizarEstatus(Integer idUsuario, String nuevoEstatus) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (nuevoEstatus != null && !nuevoEstatus.isEmpty()) {
            usuario.setEstatus(nuevoEstatus.toUpperCase());
        }
        return convertirADto(usuarioRepository.save(usuario));
    }

    @Override
    public void actualizarUbicacion(Integer idRepartidor, Double latitud, Double longitud) {
        Usuario repartidor = usuarioRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

        repartidor.setLatitudActual(latitud);
        repartidor.setLongitudActual(longitud);
        usuarioRepository.save(repartidor);

        List<Pedido> pedidosEnCamino = pedidoRepository.findByRepartidorAsignadoIdAndEstadoReal(idRepartidor, "EN_CAMINO");

        if (!pedidosEnCamino.isEmpty()) {
            for (Pedido p : pedidosEnCamino) {
                UbicacionPedido rastro = new UbicacionPedido(p, latitud, longitud);
                ubicacionRepository.save(rastro); // USAMOS EL REPO CORRECTO AQUÍ
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDto> obtenerRepartidoresPorNegocio(Integer idLicencia) {
        // En lugar de hardcodear el ID 2, filtramos por el nombre del rol que es más seguro
        List<Usuario> repartidores = usuarioRepository.findByNegocio_IdLicenciaAndRol_RolAndActivoTrue(idLicencia, "REPARTIDOR");
        return repartidores.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    // --- EL CONVERSOR ARREGLADO ---
    private UsuarioDto convertirADto(Usuario usuario) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRfc(usuario.getRfc());
        dto.setEstatus(usuario.getEstatus());
        dto.setLatitudActual(usuario.getLatitudActual());
        dto.setLongitudActual(usuario.getLongitudActual());
        dto.setTelefono(usuario.getTelefono());

        if (usuario.getRol() != null) {
            dto.setRolId(usuario.getRol().getId());
            // ¡EL BUG DEL ROL INVISIBLE ESTÁ ARREGLADO AQUÍ!
            dto.setRol(usuario.getRol().getRol());
        }

        if (usuario.getNegocio() != null) {
            dto.setIdLicencia(usuario.getNegocio().getIdLicencia());
        }

        return dto;
    }
}
package com.example.demo.service.impl;

import com.example.demo.dto.LoginDto;
import com.example.demo.dto.UsuarioDto;
import com.example.demo.model.Negocio;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.NegocioRepository;
import com.example.demo.repository.RolRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // ¡Importante!
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
    private PasswordEncoder passwordEncoder; // ¡Arreglado!

    @Autowired
    private NegocioRepository negocioRepository; // <--- ¡Asegúrate de tener este Autowired arriba!

    @Override
    public UsuarioDto registrarUsuario(Usuario nuevoUsuario, String nombreRol) {
        // 1. Buscar el Rol
        Rol rol = rolRepository.findByRol(nombreRol)
                .orElseThrow(() -> new RuntimeException("Error: Rol '" + nombreRol + "' no encontrado."));

        // 2. BUSCAR Y VINCULAR EL NEGOCIO (¡Esto faltaba!)
        // El JSON trae un objeto "negocio" con "idLicencia", pero no existe en el contexto de Hibernate.
        if (nuevoUsuario.getNegocio() != null && nuevoUsuario.getNegocio().getIdLicencia() != null) {
            Negocio negocioReal = negocioRepository.findById(nuevoUsuario.getNegocio().getIdLicencia())
                    .orElseThrow(() -> new RuntimeException("Error: Negocio no encontrado."));
            nuevoUsuario.setNegocio(negocioReal);
        } else {
            // Si es un REPARTIDOR o GESTOR, obligatoriamente debe tener negocio
            throw new RuntimeException("Error: El usuario debe pertenecer a un negocio.");
        }

        // 3. Asignar rol y encriptar contraseña
        nuevoUsuario.setRol(rol);
        nuevoUsuario.setEstatus("DISPONIBLE"); // Asignar estatus por defecto
        nuevoUsuario.setContrasena(passwordEncoder.encode(nuevoUsuario.getContrasena()));

        // 4. Guardar
        try {
            Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
            return convertirAUsuarioDto(usuarioGuardado);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // AHORA SÍ detectamos duplicados reales
            throw new RuntimeException("El correo o RFC ya están registrados.");
        }
    }

    @Override
    public UsuarioDto login(LoginDto loginDto) {
        // 1. Buscar al usuario por email
        Usuario usuario = usuarioRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos"));

        // 2. Comprobar contraseña
        if (passwordEncoder.matches(loginDto.getContrasena(), usuario.getContrasena())) {
            // ¡Login exitoso! Devolvemos el DTO
            return convertirAUsuarioDto(usuario);
        } else {
            // Contraseña incorrecta
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
        // NO TOCAMOS la contraseña aquí

        Usuario actualizado = usuarioRepository.save(usuarioExistente);
        return convertirAUsuarioDto(actualizado);
    }

    @Override
    public void actualizarContrasena(Integer id, String nuevaContrasena) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario ID " + id + " no encontrado"));

        // 🔑 Cifrar la nueva contraseña
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(usuario);
    }

    @Override
    public void eliminarUsuario(Integer id) {
        usuarioRepository.deleteById(id);
    }

    // --- MÉTODO PRIVADO PARA CONVERTIR A DTO ---
    private UsuarioDto convertirAUsuarioDto(Usuario usuario) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRfc(usuario.getRfc());
        dto.setRol(usuario.getRol().getRol()); // Obtenemos "GESTOR" o "REPARTIDOR"
        dto.setIdLicencia(usuario.getNegocio().getIdLicencia());
        return dto;
    }

    @Override
    public List<UsuarioDto> obtenerRepartidoresPorNegocio(Integer idLicencia) {
        // 1. Usamos el repositorio (que SÍ existe aquí)
        List<Usuario> repartidores = usuarioRepository.findByNegocio_IdLicenciaAndRol_Rol(idLicencia, "REPARTIDOR");

        // 2. Convertimos a DTO usando tu método existente
        return repartidores.stream()
                .map(this::convertirAUsuarioDto)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioDto getUsuarioById(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario ID " + id + " no encontrado"));

        // Asumiendo que el método convertirADto(Usuario) existe en tu clase:
        return convertirAUsuarioDto(usuario);
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

        // Validamos que sea un estatus permitido (Opcional pero recomendado)
        // if (!List.of("DISPONIBLE", "DESCANSO", "FUERA_SERVICIO").contains(nuevoEstatus)) {
        //    throw new RuntimeException("Estatus inválido");
        // }

        usuario.setEstatus(nuevoEstatus);
        usuarioRepository.save(usuario);
    }
    @Override
    public void actualizarUbicacionRepartidor(Integer idUsuario, Double lat, Double lon) {
        Usuario repartidor = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

        repartidor.setLatitudActual(lat);
        repartidor.setLongitudActual(lon);
        usuarioRepository.save(repartidor);
    }
}
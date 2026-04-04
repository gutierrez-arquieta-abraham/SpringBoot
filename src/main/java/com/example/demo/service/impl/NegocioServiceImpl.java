package com.example.demo.service.impl;

import java.util.Random;
import com.example.demo.dto.NegocioDto;
import com.example.demo.model.Negocio;
import com.example.demo.model.Usuario;
import com.example.demo.repository.NegocioRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.NegocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NegocioServiceImpl implements NegocioService {

    @Autowired
    private NegocioRepository negocioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- PANTALLA 1: VALIDACIÓN ---
    // Este es el método CLAVE para tu ConexionLicenciaFragment.
    // Recibe "DIT-..." y devuelve el ID (1).
    @Override
    public Integer obtenerIdPorCodigo(String codigo) {
        // Buscamos el objeto Negocio por su código de licencia (VARCHAR)
        Optional<Negocio> negocio = negocioRepository.findByCodigoLicenciaAndActivoTrue(codigo);

        // Si existe, devolvemos solo su ID. Si no, devolvemos null.
        return negocio.map(Negocio::getIdLicencia).orElse(null);
    }

    // --- PANTALLA 2: ACTUALIZAR Y GENERAR CÓDIGO REPARTIDOR ---
    @Override
    public NegocioDto actualizarNegocio(Integer id, NegocioDto negocioDto) {
        Negocio negocioExistente = negocioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado con ID: " + id));

        // 1. Actualizamos datos básicos del formulario
        negocioExistente.setNomEmp(negocioDto.getNomEmp());
        negocioExistente.setRfcEnc(negocioDto.getRfcEnc());
        negocioExistente.setDireccion(negocioDto.getDireccion());
        negocioExistente.setZonaCobertura(negocioDto.getZonaCobertura());

        negocioExistente.setLatitud(negocioDto.getLatitud());
        negocioExistente.setLongitud(negocioDto.getLongitud());

        // 2. GENERACIÓN AUTOMÁTICA DEL CÓDIGO DE REPARTIDOR (DIT-####-####-NG)
        // Esto es para que aparezca en la Pantalla 3
        if (negocioExistente.getCodigoConexion() == null || negocioExistente.getCodigoConexion().isEmpty()) {
            negocioExistente.setCodigoConexion(generarCodigoRepartidor());
        }

        Negocio actualizado = negocioRepository.save(negocioExistente);
        return convertirADto(actualizado);
    }

    // --- BÚSQUEDA INTELIGENTE POR EMAIL (CARGA AUTOMÁTICA) ---
    @Override
    public NegocioDto obtenerNegocioPorEmailUsuario(String email) {
        System.out.println("--- INICIO BÚSQUEDA ---");
        System.out.println("Buscando usuario con email: " + email);

        // 1. Buscar Usuario
        Usuario usuario = usuarioRepository.findByEmailAndActivoTrue(email)
                .orElseThrow(() -> {
                    System.out.println("ERROR: Usuario no encontrado en la BD.");
                    return new RuntimeException("Usuario no encontrado");
                });

        String rfcUsuario = usuario.getRfc();
        System.out.println("Usuario encontrado: " + usuario.getNombre());
        System.out.println("RFC del Usuario: '" + rfcUsuario + "'");

        if (rfcUsuario == null || rfcUsuario.trim().isEmpty()) {
            System.out.println("ERROR: El campo RFC del usuario está vacío o nulo.");
            throw new RuntimeException("Este usuario no tiene RFC registrado");
        }

        // 2. Buscar Negocio (Usamos trim() para eliminar espacios invisibles)
        System.out.println("Buscando negocio con RFC_enc: '" + rfcUsuario.trim() + "'");

        Negocio negocio = negocioRepository.findByRfcEncAndActivoTrue(rfcUsuario.trim())
                .orElseThrow(() -> {
                    System.out.println("ERROR: No se encontró ningún negocio con ese RFC.");
                    return new RuntimeException("No se encontró negocio para el RFC: " + rfcUsuario);
                });

        System.out.println("¡ÉXITO! Negocio encontrado: ID " + negocio.getIdLicencia());
        return convertirADto(negocio);
    }

    // --- MÉTODOS CRUD BÁSICOS ---

    @Override
    public NegocioDto crearNegocio(Negocio negocio) {
        // Generar Código de Licencia (Para el Gestor) si no existe
        if (negocio.getCodigoLicencia() == null || negocio.getCodigoLicencia().isEmpty()) {
            negocio.setCodigoLicencia(generarCodigo("LC"));
        }
        // Generar Código de Conexión (Para los Repartidores) si no existe
        if (negocio.getCodigoConexion() == null || negocio.getCodigoConexion().isEmpty()) {
            negocio.setCodigoConexion(generarCodigo("NG"));
        }
        Negocio guardado = negocioRepository.save(negocio);
        return convertirADto(guardado);
    }

    @Override
    public NegocioDto getNegocioById(Integer id) {
        Negocio negocio = negocioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado"));
        return convertirADto(negocio);
    }

    @Override
    public List<NegocioDto> getAllNegocios() {
        return negocioRepository.findAll()
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarNegocio(Integer id) {
        Negocio negocio = negocioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado"));

        // Solo lo desactivamos
        negocio.setActivo(false);
        negocioRepository.save(negocio);
    }

    // --- MÉTODO ANTIGUO DE VALIDACIÓN (Si lo necesitas) ---
    @Override
    public NegocioDto validarLicencia(String codigoLicencia) {
        Negocio negocio = negocioRepository.findByCodigoLicenciaAndActivoTrue(codigoLicencia)
                .orElseThrow(() -> new RuntimeException("Licencia '" + codigoLicencia + "' no encontrada"));
        return convertirADto(negocio);
    }

    // --- MÉTODOS AUXILIARES PRIVADOS ---

    private String generarCodigo(String sufijo) {
        return "DIT-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase() +
                "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase() + "-" + sufijo;
    }

    private String generarCodigoRepartidor() {
        Random random = new Random();
        int parte1 = 1000 + random.nextInt(9000); // 1000 a 9999
        int parte2 = 1000 + random.nextInt(9000);
        return "DIT-" + parte1 + "-" + parte2 + "-NG";
    }

    private NegocioDto convertirADto(Negocio negocio) {
        NegocioDto dto = new NegocioDto();
        dto.setIdLicencia(negocio.getIdLicencia());
        dto.setNomEmp(negocio.getNomEmp());
        dto.setRfcEnc(negocio.getRfcEnc());
        dto.setDireccion(negocio.getDireccion());
        dto.setZonaCobertura(negocio.getZonaCobertura()); // Mapeo correcto del entero
        dto.setCodigoLicencia(negocio.getCodigoLicencia());
        dto.setCodigoConexion(negocio.getCodigoConexion());
        return dto;
    }
}
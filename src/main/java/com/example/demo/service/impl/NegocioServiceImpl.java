package com.example.demo.service.impl;

import com.example.demo.dto.NegocioDto;
import com.example.demo.model.Negocio;
import com.example.demo.model.Usuario;
import com.example.demo.repository.NegocioRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.NegocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class NegocioServiceImpl implements NegocioService {

    @Autowired
    private NegocioRepository negocioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Integer obtenerIdPorCodigo(String codigo) {
        return negocioRepository.findByCodigoLicenciaAndActivoTrue(codigo)
                .map(Negocio::getIdLicencia)
                .orElse(null);
    }

    @Override
    public NegocioDto actualizarNegocio(Integer id, NegocioDto negocioDto) {
        Negocio negocioExistente = negocioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado con ID: " + id));

        negocioExistente.setNomEmp(negocioDto.getNomEmp());
        negocioExistente.setRfcEnc(negocioDto.getRfcEnc());
        negocioExistente.setDireccion(negocioDto.getDireccion());
        negocioExistente.setZonaCobertura(negocioDto.getZonaCobertura());
        negocioExistente.setLatitud(negocioDto.getLatitud());
        negocioExistente.setLongitud(negocioDto.getLongitud());

        // --- EL PARCHE OBLIGATORIO ---
        // Forzamos a que si tiene un código con formato viejo, lo regenere con el nuevo estándar.
        if (negocioExistente.getCodigoConexion() == null || !negocioExistente.getCodigoConexion().startsWith("DIT-")) {
            negocioExistente.setCodigoConexion(generarCodigoSeguro("NG"));
        }

        // De paso hacemos lo mismo con la licencia por si las moscas
        if (negocioExistente.getCodigoLicencia() == null || !negocioExistente.getCodigoLicencia().startsWith("DIT-")) {
            negocioExistente.setCodigoLicencia(generarCodigoSeguro("LC"));
        }

        return convertirADto(negocioRepository.save(negocioExistente));
    }

    @Override
    public NegocioDto obtenerNegocioPorEmailUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmailAndActivoTrue(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRfc() == null || usuario.getRfc().trim().isEmpty()) {
            throw new RuntimeException("Este usuario no tiene RFC registrado");
        }

        Negocio negocio = negocioRepository.findByRfcEncAndActivoTrue(usuario.getRfc().trim())
                .orElseThrow(() -> new RuntimeException("No se encontró negocio para el RFC: " + usuario.getRfc()));

        return convertirADto(negocio);
    }

    @Override
    public NegocioDto crearNegocio(NegocioDto negocioDto) {
        // Convertimos el DTO a Entidad ANTES de tocar la base de datos
        Negocio nuevoNegocio = new Negocio();
        nuevoNegocio.setNomEmp(negocioDto.getNomEmp());
        nuevoNegocio.setRfcEnc(negocioDto.getRfcEnc());
        nuevoNegocio.setDireccion(negocioDto.getDireccion());
        nuevoNegocio.setZonaCobertura(negocioDto.getZonaCobertura());
        nuevoNegocio.setLatitud(negocioDto.getLatitud());
        nuevoNegocio.setLongitud(negocioDto.getLongitud());
        nuevoNegocio.setActivo(true);

        // Estrategia de generación unificada
        nuevoNegocio.setCodigoLicencia(generarCodigoSeguro("LC"));
        nuevoNegocio.setCodigoConexion(generarCodigoSeguro("NG"));

        return convertirADto(negocioRepository.save(nuevoNegocio));
    }

    @Override
    public NegocioDto getNegocioById(Integer id) {
        Negocio negocio = negocioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado"));
        return convertirADto(negocio);
    }

    @Override
    public List<NegocioDto> getAllNegocios() {
        return negocioRepository.findByActivoTrue() // Usar solo los activos en lugar de findAll
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarNegocio(Integer id) {
        Negocio negocio = negocioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado"));
        negocio.setActivo(false);
        negocioRepository.save(negocio);
    }

    @Override
    public NegocioDto validarLicencia(String codigoLicencia) {
        Negocio negocio = negocioRepository.findByCodigoLicenciaAndActivoTrue(codigoLicencia)
                .orElseThrow(() -> new RuntimeException("Licencia '" + codigoLicencia + "' no encontrada o inactiva"));
        return convertirADto(negocio);
    }

    // --- MÉTODOS AUXILIARES CENTRALIZADOS ---

    // Una sola fuente de verdad para los códigos. Nada de mezclar Math.random con UUID.
    private String generarCodigoSeguro(String sufijo) {
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "DIT-" + randomStr.substring(0, 4) + "-" + randomStr.substring(4, 8) + "-" + sufijo;
    }

    private NegocioDto convertirADto(Negocio negocio) {
        NegocioDto dto = new NegocioDto();
        dto.setIdLicencia(negocio.getIdLicencia());
        dto.setNomEmp(negocio.getNomEmp());
        dto.setRfcEnc(negocio.getRfcEnc());
        dto.setDireccion(negocio.getDireccion());

        // Manejo seguro de nulos al leer
        dto.setZonaCobertura(negocio.getZonaCobertura() != null ? negocio.getZonaCobertura() : 0);
        dto.setLatitud(negocio.getLatitud() != null ? negocio.getLatitud() : 0.0);
        dto.setLongitud(negocio.getLongitud() != null ? negocio.getLongitud() : 0.0);

        dto.setCodigoLicencia(negocio.getCodigoLicencia());
        dto.setCodigoConexion(negocio.getCodigoConexion());

        return dto;
    }
}
package com.duoc.bff_movil.service;

import com.duoc.bff_movil.dto.TransaccionDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransaccionService {

    private final RestTemplate restTemplate;

    public TransaccionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "transaccionService", fallbackMethod = "fallbackTransacciones")
    @Retry(name = "transaccionService")
    public List<TransaccionDto> obtenerTransacciones() {
        TransaccionDto[] transacciones = restTemplate.getForObject(
                "http://ms-transacciones/api/transacciones", TransaccionDto[].class);

        if (transacciones == null) return List.of();

        // limitar info para el móvil
        return Arrays.stream(transacciones)
                .map(t -> {
                    TransaccionDto dto = new TransaccionDto();
                    dto.setId(t.getId());
                    dto.setMonto(t.getMonto());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // fallback si falla la llamada
    public List<TransaccionDto> fallbackTransacciones(Exception e) {
        System.out.println("fallback movil llamado: " + e.getMessage());
        return List.of();
    }
}

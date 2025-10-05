package com.duoc.bff_movil.controller;

import com.duoc.bff_movil.dto.CuentaAnualDto;
import com.duoc.bff_movil.dto.InteresDto;
import com.duoc.bff_movil.dto.TransaccionDto;
import com.duoc.bff_movil.service.TransaccionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bff/movil")
public class MovilController {

    private final RestTemplate restTemplate;
    private final TransaccionService transaccionService;

    public MovilController(RestTemplate restTemplate, TransaccionService transaccionService) {
        this.restTemplate = restTemplate;
        this.transaccionService = transaccionService;
    }

    @GetMapping("/transacciones")
    public List<TransaccionDto> getTransaccionesMovil() {
        return transaccionService.obtenerTransacciones();
    }

    @GetMapping("/intereses")
    public List<InteresDto> getInteresesMovil() {
        InteresDto[] intereses = restTemplate.getForObject(
                "http://ms-intereses/api/intereses", InteresDto[].class);
        if (intereses == null) return List.of();
        return Arrays.stream(intereses)
                .map(i -> {
                    InteresDto dto = new InteresDto();
                    dto.setCuentaId(i.getCuentaId());
                    dto.setSaldoFinal(i.getSaldoFinal());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/cuentas_anuales")
    public List<CuentaAnualDto> getCuentasAnualesMovil() {
        CuentaAnualDto[] cuentas = restTemplate.getForObject(
                "http://ms-cuentas-anuales/api/cuentas_anuales", CuentaAnualDto[].class);
        if (cuentas == null) return List.of();
        return Arrays.stream(cuentas)
                .map(c -> {
                    CuentaAnualDto dto = new CuentaAnualDto();
                    dto.setCuentaId(c.getCuentaId());
                    dto.setMonto(c.getMonto());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}

package com.example.demo.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.EstadoPedido;
import com.example.demo.model.Pedido;
import com.example.demo.service.PedidoService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    // RF7 - registrar pedido
    @PostMapping
    public Mono<ResponseEntity<Pedido>> registrar(@RequestBody Pedido pedido) {
        return service.registrar(pedido)
                .map(saved -> ResponseEntity.ok(saved));
    }

    // RF8 - consultar pedido
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Pedido>> consultar(@PathVariable Long id) {
        return service.consultar(id)
                .map(ResponseEntity::ok);
    }

    // RF9/RF10 - confirmación y estado
    @GetMapping("/{id}/estado")
    public Mono<ResponseEntity<String>> estado(@PathVariable Long id) {
        return service.estado(id)
                .map(ResponseEntity::ok);
    }

    // opcional: actualizar estado
    @PutMapping("/{id}/estado")
    public Mono<ResponseEntity<Pedido>> actualizarEstado(@PathVariable Long id,
                                                         @RequestParam EstadoPedido estado) {
        return service.actualizarEstado(id, estado)
                .map(ResponseEntity::ok);
    }
}
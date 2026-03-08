package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.model.EstadoPedido;
import com.example.demo.model.Pedido;
import com.example.demo.repo.PedidoRepository;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class PedidoService {

    private final PedidoRepository repo;

    public PedidoService(PedidoRepository repo) {
        this.repo = repo;
    }

    // RF7 - registrar pedido
    public Mono<Pedido> registrar(Pedido pedido) {
        return Mono.fromCallable(() -> {
            pedido.setEstado(EstadoPedido.EN_PREPARACION);
            pedido.setConfirmacion("Pedido registrado correctamente");
            return repo.save(pedido);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // RF8 - consultar pedido
    public Mono<Pedido> consultar(Long id) {
        return Mono.fromCallable(() ->
                repo.findById(id).orElseThrow(() -> new RuntimeException("Pedido no encontrado"))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    // RF10 - ver estado del pedido
    public Mono<String> estado(Long id) {
        return Mono.fromCallable(() -> {
            Pedido pedido = repo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            return pedido.getEstado().name();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // opcional: actualizar estado
    public Mono<Pedido> actualizarEstado(Long id, EstadoPedido estado) {
        return Mono.fromCallable(() -> {
            Pedido pedido = repo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            pedido.setEstado(estado);
            return repo.save(pedido);
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
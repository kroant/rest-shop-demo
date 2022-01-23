package cz.kromer.restshopdemo.controller;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.kromer.restshopdemo.dto.OrderDto;
import cz.kromer.restshopdemo.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class OrderController {

    OrderService orderService;

    @GetMapping
    List<OrderDto> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    OrderDto getById(@PathVariable("id") UUID id) {
        return orderService.getById(id);
    }

    @PostMapping
    OrderDto save(@Valid @RequestBody OrderDto order) {
        return orderService.getById(orderService.save(order));
    }

    @PutMapping("/{id}/cancel")
    void cancel(@PathVariable("id") UUID id) {
        orderService.cancel(id);
    }

    @PutMapping("/{id}/pay")
    void pay(@PathVariable("id") UUID id) {
        orderService.pay(id);
    }
}

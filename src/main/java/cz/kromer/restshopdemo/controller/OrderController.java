package cz.kromer.restshopdemo.controller;

import cz.kromer.restshopdemo.api.OrdersApi;
import cz.kromer.restshopdemo.dto.CreateOrderDto;
import cz.kromer.restshopdemo.dto.OrderResponseDto;
import cz.kromer.restshopdemo.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class OrderController implements OrdersApi {

    OrderService orderService;

    public List<OrderResponseDto> getAllOrders() {
        return orderService.findAll();
    }

    public OrderResponseDto getOrderById(UUID id) {
        return orderService.getById(id);
    }

    public OrderResponseDto saveOrder(CreateOrderDto order) {
        return orderService.getById(orderService.save(order));
    }

    public void cancelOrder(UUID id) {
        orderService.cancel(id);
    }

    public void payOrder(UUID id) {
        orderService.pay(id);
    }
}

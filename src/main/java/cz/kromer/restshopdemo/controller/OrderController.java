package cz.kromer.restshopdemo.controller;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.kromer.restshopdemo.dto.OrderDto;
import cz.kromer.restshopdemo.dto.error.ErrorResponseDto;
import cz.kromer.restshopdemo.service.OrderService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = OrderDto.class)) })
    @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema(implementation = ErrorResponseDto.class)) })
    OrderDto getById(@PathVariable("id") UUID id) {
        return orderService.getById(id);
    }

    @PostMapping
    @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = OrderDto.class)) })
    @ApiResponse(responseCode = "400", content = { @Content(schema = @Schema(implementation = ErrorResponseDto.class)) })
    OrderDto save(@Valid @RequestBody OrderDto order) {
        return orderService.getById(orderService.save(order));
    }

    @PutMapping("/{id}/cancel")
    @ApiResponse(responseCode = "200", content = { @Content })
    @ApiResponse(responseCode = "400", content = { @Content(schema = @Schema(implementation = ErrorResponseDto.class)) })
    @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema(implementation = ErrorResponseDto.class)) })
    void cancel(@PathVariable("id") UUID id) {
        orderService.cancel(id);
    }

    @PutMapping("/{id}/pay")
    @ApiResponse(responseCode = "200", content = { @Content })
    @ApiResponse(responseCode = "400", content = { @Content(schema = @Schema(implementation = ErrorResponseDto.class)) })
    @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema(implementation = ErrorResponseDto.class)) })
    void pay(@PathVariable("id") UUID id) {
        orderService.pay(id);
    }
}

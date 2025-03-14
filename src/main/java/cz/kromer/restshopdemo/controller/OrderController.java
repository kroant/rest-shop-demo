package cz.kromer.restshopdemo.controller;

import cz.kromer.restshopdemo.dto.CreateOrderDto;
import cz.kromer.restshopdemo.dto.OrderResponseDto;
import cz.kromer.restshopdemo.dto.error.ErrorResponseDto;
import cz.kromer.restshopdemo.service.OrderService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
class OrderController {

    OrderService orderService;

    @GetMapping
    List<OrderResponseDto> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = OrderResponseDto.class)) })
    @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema(implementation = ErrorResponseDto.class)) })
    OrderResponseDto getById(@PathVariable UUID id) {
        return orderService.getById(id);
    }

    @PostMapping
    @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = OrderResponseDto.class)) })
    @ApiResponse(responseCode = "400", content = { @Content(schema = @Schema(implementation = ErrorResponseDto.class)) })
    OrderResponseDto save(@Valid @RequestBody CreateOrderDto order) {
        return orderService.getById(orderService.save(order));
    }

    @PutMapping("/{id}/cancel")
    @ResponseStatus(NO_CONTENT)
    @ApiResponse(responseCode = "204", content = { @Content })
    @ApiResponse(responseCode = "400", content = { @Content(schema = @Schema(implementation = ErrorResponseDto.class)) })
    @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema(implementation = ErrorResponseDto.class)) })
    void cancel(@PathVariable UUID id) {
        orderService.cancel(id);
    }

    @PutMapping("/{id}/pay")
    @ResponseStatus(NO_CONTENT)
    @ApiResponse(responseCode = "204", content = { @Content })
    @ApiResponse(responseCode = "400", content = { @Content(schema = @Schema(implementation = ErrorResponseDto.class)) })
    @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema(implementation = ErrorResponseDto.class)) })
    void pay(@PathVariable UUID id) {
        orderService.pay(id);
    }
}

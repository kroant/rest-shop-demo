package cz.kromer.restshopdemo.dto;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderProductDto {

    @NotNull
    UUID id;

    String name;
}

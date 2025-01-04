package cz.kromer.restshopdemo.entity;

import cz.kromer.restshopdemo.dto.QuantityUnit;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(of = { "id", "name" })
@Entity
@SQLRestriction("deleted = false")
public class Product {

    @Id
    @GeneratedValue
    UUID id;

    String name;

    @Enumerated(STRING)
    QuantityUnit unit;

    BigDecimal price;

    BigDecimal stock;

    boolean deleted;
}

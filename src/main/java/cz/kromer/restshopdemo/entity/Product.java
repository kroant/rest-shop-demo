package cz.kromer.restshopdemo.entity;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import org.hibernate.annotations.SQLRestriction;

import cz.kromer.restshopdemo.dto.QuantityUnit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

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

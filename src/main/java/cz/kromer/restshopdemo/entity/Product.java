package cz.kromer.restshopdemo.entity;

import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Where;

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
@Where(clause = "deleted = false")
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

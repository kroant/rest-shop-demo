package cz.kromer.restshopdemo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
@Getter
@Setter
@EqualsAndHashCode(of = { "order", "product" })
@ToString(of = { "product", "amount" })
@Entity
public class OrderItem {

    @Id
    @ManyToOne
    Order order;

    @Id
    @ManyToOne
    Product product;

    BigDecimal amount;
}

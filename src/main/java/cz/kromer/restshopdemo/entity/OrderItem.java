package cz.kromer.restshopdemo.entity;

import static lombok.AccessLevel.PRIVATE;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = PRIVATE)
@Getter
@Setter
@EqualsAndHashCode(of = { "order", "product" })
@ToString(of = { "product", "amount" })
@Entity
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    Order order;

    @Id
    @ManyToOne
    Product product;

    BigDecimal amount;
}

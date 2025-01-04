package cz.kromer.restshopdemo.entity;

import cz.kromer.restshopdemo.dto.OrderState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(of = { "id", "state" })
@Entity
@Table(name = "order_")
public class Order {

    @Id
    @GeneratedValue
    UUID id;

    @Enumerated(STRING)
    OrderState state;

    BigDecimal price;

    @OneToMany(mappedBy = "order", cascade = { PERSIST })
    List<OrderItem> items;

    @CreationTimestamp
    @Column(updatable = false)
    Instant createdOn;
}

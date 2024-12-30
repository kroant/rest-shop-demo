package cz.kromer.restshopdemo.entity;

import static cz.kromer.restshopdemo.dto.OrderState.NEW;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import cz.kromer.restshopdemo.dto.OrderState;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

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

    @Column(updatable = false)
    Instant createdOn;

    @PrePersist
    private void prePersist() {
        setState(NEW);
        setCreatedOn(Instant.now());

        if (!isEmpty(getItems())) {
            getItems().forEach(item -> item.setOrder(this));
        }
    }
}

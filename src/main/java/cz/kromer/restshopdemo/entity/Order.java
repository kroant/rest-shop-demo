package cz.kromer.restshopdemo.entity;

import static cz.kromer.restshopdemo.dto.OrderState.NEW;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

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

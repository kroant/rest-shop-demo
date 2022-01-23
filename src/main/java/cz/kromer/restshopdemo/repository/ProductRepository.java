package cz.kromer.restshopdemo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cz.kromer.restshopdemo.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
}

package com.example.kkBazar.repository.addProduct;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kkBazar.entity.product.ProductList;

public interface ProductListRepository extends JpaRepository<ProductList, Long> {

}

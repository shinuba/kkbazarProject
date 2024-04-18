package com.example.kkBazar.entity.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "add_to_cart")
public class AddToCart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long addToCartId;
	private long productListId;
	private long userId;
	private long productVarientImagesId;

	public long getAddToCartId() {
		return addToCartId;
	}
	public void setAddToCartId(long addToCartId) {
		this.addToCartId = addToCartId;
	}
	public long getProductListId() {
		return productListId;
	}
	public void setProductListId(long productListId) {
		this.productListId = productListId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getProductVarientImagesId() {
		return productVarientImagesId;
	}
	public void setProductVarientImagesId(long productVarientImagesId) {
		this.productVarientImagesId = productVarientImagesId;
	}
	public AddToCart() {
		super();
	}
	
    

	
	
}
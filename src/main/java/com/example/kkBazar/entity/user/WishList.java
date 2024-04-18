package com.example.kkBazar.entity.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "wish_list")
public class WishList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long wishListId;
	private long productListId;
	private long userId;
	private long productVarientImagesId;
	private boolean status;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public long getProductVarientImagesId() {
		return productVarientImagesId;
	}

	public void setProductVarientImagesId(long productVarientImagesId) {
		this.productVarientImagesId = productVarientImagesId;
	}

	public long getWishListId() {
		return wishListId;
	}

	public void setWishListId(long wishListId) {
		this.wishListId = wishListId;
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

	public WishList() {
		super();
	}

}

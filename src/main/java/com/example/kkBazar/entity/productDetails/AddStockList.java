package com.example.kkBazar.entity.productDetails;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "add_stock_list")
public class AddStockList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long stockListId;
	private double quantity;
	private long productListId;
	public long getStockListId() {
		return stockListId;
	}
	public void setStockListId(long stockListId) {
		this.stockListId = stockListId;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public long getProductListId() {
		return productListId;
	}
	public void setProductListId(long productListId) {
		this.productListId = productListId;
	}
	public AddStockList() {
		super();
	}

	
}

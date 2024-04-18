package com.example.kkBazar.entity.product;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "product_varient")
public class ProductVarient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productVarientId;
	private String varientName;
	private String varientValue;
	private boolean deleted;


	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Long getProductVarientId() {
		return productVarientId;
	}

	public void setProductVarientId(Long productVarientId) {
		this.productVarientId = productVarientId;
	}

	public String getVarientName() {
		return varientName;
	}

	public void setVarientName(String varientName) {
		this.varientName = varientName;
	}

	public String getVarientValue() {
		return varientValue;
	}

	public void setVarientValue(String varientValue) {
		this.varientValue = varientValue;
	}

	public ProductVarient() {
		super();
	}

}

package com.example.kkBazar.entity.productDetails;


import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "add_stock")
public class AddStock {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long stockId;
	@Column(columnDefinition = "DATE")
	private LocalDate date;
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "stockId", referencedColumnName = "stockId")
	private List<AddStockList> addStockList;
	public long getStockId() {
		return stockId;
	}
	public void setStockId(long stockId) {
		this.stockId = stockId;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public List<AddStockList> getAddStockList() {
		return addStockList;
	}
	public void setAddStockList(List<AddStockList> addStockList) {
		this.addStockList = addStockList;
	}
	public AddStock() {
		super();
	}

	

}

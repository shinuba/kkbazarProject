package com.example.kkBazar.entity.user;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "order_item_list")
public class OrderItemList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private long orderItemListId;
	private double quantity;
	private long productListId;
	private String orderStatus;
	@Column(columnDefinition = "DATE")
	private LocalDate date;
	@Column(columnDefinition = "MEDIUMTEXT")
	private String reason;
	private double totalPrice;
	private boolean delivered;
	private boolean cancelled;
	private boolean confirmed;
	private double totalAmount;
	private boolean returnPending;
	private boolean returnAccepted;
	private boolean returnRejected;

	
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public boolean isReturnPending() {
		return returnPending;
	}

	public void setReturnPending(boolean returnPending) {
		this.returnPending = returnPending;
	}

	public boolean isReturnAccepted() {
		return returnAccepted;
	}

	public void setReturnAccepted(boolean returnAccepted) {
		this.returnAccepted = returnAccepted;
	}

	public boolean isReturnRejected() {
		return returnRejected;
	}

	public void setReturnRejected(boolean returnRejected) {
		this.returnRejected = returnRejected;
	}

	public long getOrderItemListId() {
		return orderItemListId;
	}

	public void setOrderItemListId(long orderItemListId) {
		this.orderItemListId = orderItemListId;
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

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public boolean isDelivered() {
		return delivered;
	}

	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public OrderItemList() {
		super();
	}

}

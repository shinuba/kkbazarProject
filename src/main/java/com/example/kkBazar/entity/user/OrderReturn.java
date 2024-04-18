package com.example.kkBazar.entity.user;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "order_return")
public class OrderReturn {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long orderReturnId;
	private String reasonForReturn;
	private long orderItemListId;
	private long userId;
	@Column(columnDefinition = "DATE")
	private LocalDate date;
	private String returnStatus;
	private boolean accepted;
	private boolean rejected;
	public long getOrderReturnId() {
		return orderReturnId;
	}
	public void setOrderReturnId(long orderReturnId) {
		this.orderReturnId = orderReturnId;
	}
	public String getReasonForReturn() {
		return reasonForReturn;
	}
	public void setReasonForReturn(String reasonForReturn) {
		this.reasonForReturn = reasonForReturn;
	}
	public long getOrderItemListId() {
		return orderItemListId;
	}
	public void setOrderItemListId(long orderItemListId) {
		this.orderItemListId = orderItemListId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public String getReturnStatus() {
		return returnStatus;
	}
	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
	}
	public boolean isAccepted() {
		return accepted;
	}
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
	public boolean isRejected() {
		return rejected;
	}
	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}
	public OrderReturn() {
		super();
	}
	
	
	
}

package com.example.kkBazar.entity.addProduct;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="varientList")
public class VarientList {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long varientListId;
	private String varientListName;
	public long getVarientListId() {
		return varientListId;
	}
	public void setVarientListId(long varientListId) {
		this.varientListId = varientListId;
	}
	public String getVarientListName() {
		return varientListName;
	}
	public void setVarientListName(String varientListName) {
		this.varientListName = varientListName;
	}
	public VarientList() {
		super();
	}
	
}

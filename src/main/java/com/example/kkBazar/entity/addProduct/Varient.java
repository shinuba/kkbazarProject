package com.example.kkBazar.entity.addProduct;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name="varient")
public class Varient {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long varientId;
	private String varientName;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "varientId", referencedColumnName = "varientId")
	private List<VarientList> varientLists;

	public long getVarientId() {
		return varientId;
	}

	public void setVarientId(long varientId) {
		this.varientId = varientId;
	}

	public String getVarientName() {
		return varientName;
	}

	public void setVarientName(String varientName) {
		this.varientName = varientName;
	}

	public List<VarientList> getVarientLists() {
		return varientLists;
	}

	public void setVarientLists(List<VarientList> varientLists) {
		this.varientLists = varientLists;
	}

	public Varient() {
		super();
	}

	
	
}

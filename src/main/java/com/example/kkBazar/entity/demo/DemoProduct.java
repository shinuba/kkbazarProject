package com.example.kkBazar.entity.demo;

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
@Table(name = "demo_pro")
public class DemoProduct {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long demoId;
	private String productName;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "demoId", referencedColumnName = "demoId")
	private List<DemoProductImages> demoProductImages;

//	@OneToMany(cascade = CascadeType.ALL)
//	@JoinColumn(name = "demoId", referencedColumnName = "demoId")
//	private List<ImagesToDelete> imagesToDelete;
//
//	public List<ImagesToDelete> getImagesToDelete() {
//		return imagesToDelete;
//	}
//
//	public void setImagesToDelete(List<ImagesToDelete> imagesToDelete) {
//		this.imagesToDelete = imagesToDelete;
//	}

	public long getDemoId() {
		return demoId;
	}

	public void setDemoId(long demoId) {
		this.demoId = demoId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public List<DemoProductImages> getDemoProductImages() {
		return demoProductImages;
	}

	public void setDemoProductImages(List<DemoProductImages> demoProductImages) {
		this.demoProductImages = demoProductImages;
	}

	public DemoProduct() {
		super();
	}

}

package com.example.kkBazar.entity.demo;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "demo_pro_images")
public class DemoProductImages {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long demoProImagesId;
	@Column(columnDefinition = "MEDIUMTEXT")
	private String url;
	@JsonIgnore
	private Blob file;
	private boolean deleted;

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Long getDemoProImagesId() {
		return demoProImagesId;
	}

	public void setDemoProImagesId(Long demoProImagesId) {
		this.demoProImagesId = demoProImagesId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Blob getFile() {
		return file;
	}

	public void setFile(Blob file) {
		this.file = file;
	}

	public DemoProductImages() {
		super();
	}

}

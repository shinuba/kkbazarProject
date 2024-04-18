package com.example.kkBazar.entity.dashboard;

import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "dsahboard2")
public class Dashboard2 {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long dashboard2Id;
	private String title;
	private long categoryId;
	private String description;
	@JsonIgnore
	private Blob fileUpload;
	private String url;

	public Blob getFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(Blob fileUpload) {
		this.fileUpload = fileUpload;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getDashboard2Id() {
		return dashboard2Id;
	}

	public void setDashboard3Id(long dashboard2Id) {
		this.dashboard2Id = dashboard2Id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public void setDashboard2Id(long dashboard2Id) {
		this.dashboard2Id = dashboard2Id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Dashboard2() {
		super();
	}

}

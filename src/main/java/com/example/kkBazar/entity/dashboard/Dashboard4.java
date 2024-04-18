package com.example.kkBazar.entity.dashboard;

import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "dashboard4")
public class Dashboard4 {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long dashboard4Id;
	private long categoryId;
	private String title;
	@JsonIgnore
	private Blob fileUpload;
	private String url;
	private boolean status;

	public long getDashboard4Id() {
		return dashboard4Id;
	}

	public void setDashboard4Id(long dashboard4Id) {
		this.dashboard4Id = dashboard4Id;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

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

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Dashboard4() {
		super();
	}

}
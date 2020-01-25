package com.gamify.elearning.entity;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ideyatech.opentides.core.entity.BaseEntity;

@Entity
@Table(name = "Course_unit")
public class Unit extends BaseEntity {

	@Column(name = "unit_name")
	private String unitName;

	private String fileName;

	private String fileType;

	@Lob
	private String textDescription;

	private String imageLocation;
	
	@Lob
	private byte[] data;


	@Lob
	private String FileBase64;
	
	

	public String getFileBase64() {
		return FileBase64;
	}

	public void setFileBase64(String fileBase64) {
		FileBase64 = fileBase64;
	}

	@JsonBackReference
	@ManyToOne
	Curriculum curriculum;

	public Unit() {
		super();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public Curriculum getCurriculum() {
		return curriculum;
	}

	public void setCurriculum(Curriculum curriculum) {
		this.curriculum = curriculum;
	}

	public String getTextDescription() {
		return textDescription;
	}

	public void setTextDescription(String textDescription) {
		this.textDescription = textDescription;
	}

	public String getImageLocation() {
		return imageLocation;
	}

	public void setImageLocation(String imageLocation) {
		this.imageLocation = imageLocation;
	}

	public Unit(String unitName, String fileName, String fileType, String textDescription, String imageLocation,
			byte[] data, Curriculum curriculum) {
		super();
		this.unitName = unitName;
		this.fileName = fileName;
		this.fileType = fileType;
		this.textDescription = textDescription;
		this.imageLocation = imageLocation;
		this.data = data;
		this.curriculum = curriculum;
	}

	@Override
	public String toString() {
		return "Unit [unitName=" + unitName + ", fileName=" + fileName + ", fileType=" + fileType + ", textDescription="
				+ textDescription + ", imageLocation=" + imageLocation + ", data=" + Arrays.toString(data)
				+ ", curriculum=" + curriculum + "]";
	}

	

}

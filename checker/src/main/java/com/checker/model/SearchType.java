package com.checker.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "SEARCH_TYPE")
@NoArgsConstructor
public class SearchType {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "SEARCH_NAME")
	private String name;

	@Transient
	private String status;

	@Transient
	private String createdDate;

	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		JSONObject data = new JSONObject();
		data.put("id", this.getId());
		data.put("name", this.getName());
		data.put("status", this.getStatus());
		data.put("createdDate", this.getCreatedDate());
		return data.toJSONString();
	}

	public SearchType(JSONObject jsonObject) {
		super();
		this.id = (Long) jsonObject.get("id");
		this.name = (String) jsonObject.get("name");
		this.status = (String) jsonObject.get("status");
		this.createdDate = (String) jsonObject.get("createdDate");
	}

}

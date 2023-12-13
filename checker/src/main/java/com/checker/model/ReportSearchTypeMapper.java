package com.checker.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.checker.util.StringToSetSearchTypeConverter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "REPORT_SEARCH_TYPE_MAPPER")
public class ReportSearchTypeMapper{

	@Id
	@Column(name = "REPORT_ID")
	private int id;
	
	@OneToOne
	@PrimaryKeyJoinColumn
	private Report report;

	@Convert(converter = StringToSetSearchTypeConverter.class)
	@Column(name = "SEARCH_TYPE_DATA", insertable=false, updatable=false)
	private Set<SearchType> searchTypes;

}

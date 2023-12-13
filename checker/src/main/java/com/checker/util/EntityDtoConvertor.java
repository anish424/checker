package com.checker.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.checker.dto.AdjudicationStatus;
import com.checker.dto.CandidateDto;
import com.checker.dto.ChargeDto;
import com.checker.dto.NoticeDto;
import com.checker.dto.SearchTypeDto;
import com.checker.dto.SignupDto;
import com.checker.dto.Status;
import com.checker.model.Candidate;
import com.checker.model.Charge;
import com.checker.model.Notice;
import com.checker.model.Recruiter;
import com.checker.model.Report;
import com.checker.model.SearchType;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EntityDtoConvertor {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private SimpleDateFormat format = new SimpleDateFormat(CheckerConstants.DATE_FORMAT);

	public CandidateDto convertToDto(Candidate entry, Boolean onlyCandidate) {
		modelMapper.addConverter(new StringToDateConverter());
		CandidateDto dto = modelMapper.map(entry, CandidateDto.class);
		Report report = entry.getReport();
		if (report != null) {
			AdjudicationStatus adjudicationStatus = AdjudicationStatus.getStatusFromCode(report.getAdjudication());
			dto.setAdjudicationStatus(adjudicationStatus);
			dto.setStatus(Status.getStatusFromCode(report.getReportStatus()));
			Set<SearchType> searchTypes = report.getMapper().getSearchTypes();
			List<SearchType> list = new ArrayList<>(searchTypes);
			Collections.sort(list, new SortSearchType());
			dto.setFirstSearchCompletionDate(list.get(0).getCreatedDate());
			if (Boolean.FALSE.equals(onlyCandidate)) {
				setReport(dto, report);
				setCharges(dto, list);
			}
			dto.setReportCompletedDate(list.get(list.size() - 1).getCreatedDate());
			setReportTurnAroundTime(dto, list);
		}

		return dto;
	}

	private void setReport(CandidateDto dto, Report report) {
		dto.setEmployeePackage(report.getReportPackage());
		dto.setReportCreatedDate(format.format(report.getCreatedDate()));
		dto.setReportCompletedDate(null);
	}

	private void setCharges(CandidateDto dto, List<SearchType> list) {
		List<SearchTypeDto> searchTypeDtoList = list.stream().map(s -> modelMapper.map(s, SearchTypeDto.class))
				.collect(Collectors.toList());
		dto.setSearchtypeList(searchTypeDtoList);
	}

	private void setReportTurnAroundTime(CandidateDto dto, List<SearchType> list) {
		try {
			Duration duration = Duration.between(format.parse(list.get(0).getCreatedDate()).toInstant(),
					format.parse(list.get(list.size() - 1).getCreatedDate()).toInstant());
			dto.setReportCompletedDate(duration.toDays() + " days, " + duration.toHours() % 24 + " hours");
		} catch (ParseException e) {
			log.error("failed to convert candidate Entity to dto", e);
		}
	}

	public ChargeDto convertToDto(Charge entry) {
		return modelMapper.map(entry, ChargeDto.class);
	}

	public Charge convertToEntity(ChargeDto entry) {
		return modelMapper.map(entry, Charge.class);
	}

	public Notice convertToEntity(NoticeDto entry) {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return modelMapper.map(entry, Notice.class);
	}

	public Recruiter convertToEntity(SignupDto request) {
		Recruiter entity = modelMapper.map(request, Recruiter.class);
		entity.setCreateDate(new Timestamp(System.currentTimeMillis()));
		entity.setPassword(passwordEncoder.encode(request.getPassword()));
		entity.setActive(true);
		return entity;
	}

}

@Slf4j
class SortSearchType implements Comparator<SearchType> {
	SimpleDateFormat format = new SimpleDateFormat(CheckerConstants.DATE_FORMAT);

	// Method of this class
	// @Override
	public int compare(SearchType obj1, SearchType obj2) {

		// Returning the value after comparing the objects
		// this will sort the data in Ascending order
		try {
			return format.parse(obj1.getCreatedDate()).compareTo(format.parse(obj2.getCreatedDate()));
		} catch (ParseException e) {
			log.error("failed to parse dates {} {}", obj1.getCreatedDate(), obj2.getCreatedDate(), e);
			return 0;
		}
	}
}

@Slf4j
class StringToDateConverter implements Converter<String, Date> {

	private final SimpleDateFormat formatter = new SimpleDateFormat(CheckerConstants.DATE_FORMAT);

	@Override
	public Date convert(MappingContext<String, Date> mappingContext) {
		String source = mappingContext.getSource();
		try {
			return formatter.parse(source);
		} catch (ParseException e) {
			log.error("failed to parse date {}", source, e);
			return null;
		}
	}
}

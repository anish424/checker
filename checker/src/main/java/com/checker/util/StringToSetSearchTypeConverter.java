package com.checker.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.checker.model.SearchType;
import com.checker.repository.SearchTypeRepository;

import lombok.extern.slf4j.Slf4j;

@Converter
@Component
@Slf4j
public class StringToSetSearchTypeConverter implements AttributeConverter<Set<SearchType>, String> {

	public StringToSetSearchTypeConverter(@Lazy SearchTypeRepository searchTypeRepository) {
		this.searchTypeRepository = searchTypeRepository;
	}

	// @Autowired
	private SearchTypeRepository searchTypeRepository;

	@Override
	public String convertToDatabaseColumn(Set<SearchType> searchTypes) {
		if (!Optional.ofNullable(searchTypes).isPresent())
			return "";
		return searchTypes.stream().map(SearchType::toString).collect(Collectors.joining(","));
	}

	@Override
	public Set<SearchType> convertToEntityAttribute(String joined) {
		if (!Optional.ofNullable(joined).filter(s -> !s.isEmpty()).isPresent())
			return new HashSet<>();

		final List<Long> ids = new ArrayList<>();
		Set<SearchType> set = Arrays.asList(joined.split("[{}]")).stream().filter(this::filter)
				.map(s -> valueOf(s, ids)).collect(Collectors.toSet());
		List<SearchType> list = searchTypeRepository.findByIds(ids);
		final Map<Long, String> map = new HashMap<>();
		list.stream().forEach(entry -> map.put(entry.getId(), entry.getName()));

		set.stream().forEach(entry -> entry.setName(map.get(entry.getId())));
		return set;
	}

	private boolean filter(String value) {
		return !(value.strip().equals(",") || value.strip().equals(""));
	}

	public SearchType valueOf(String value, List<Long> ids) {
		value = "{" + value + "}";

		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(value);
			SearchType type = new SearchType(jsonObject);
			ids.add(type.getId());
			return type;
		} catch (Exception e) {
			log.error("failed to parse json to SearchType {}", value, e);
			return null;
		}
	}

}
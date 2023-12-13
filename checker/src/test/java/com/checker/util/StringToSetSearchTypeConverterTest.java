package com.checker.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.checker.model.SearchType;
import com.checker.repository.SearchTypeRepository;

class StringToSetSearchTypeConverterTest {

	@InjectMocks
	StringToSetSearchTypeConverter converter;

	@Mock
	SearchTypeRepository searchTypeRepository;

	@BeforeEach
	void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void convertToEntityAttributeTest() {
		assertEquals(0, converter.convertToEntityAttribute("").size());

		List<SearchType> list = getSearchTypeList();
		when(searchTypeRepository.findByIds(any())).thenReturn(list);

		String joined = "{\"createdDate\":\"09/09/2023\",\"name\":\"SSN Verification\",\"id\":226,\"status\":\"Clear\"},{\"createdDate\":\"09/20/2023\",\"name\":\"Sex Offender\",\"id\":227,\"status\":\"Clear\"},{\"createdDate\":\"09/11/2023\",\"name\":\"Global Watchlist\",\"id\":228,\"status\":\"Clear\"},{\"createdDate\":\"09/18/2023\",\"name\":\"Federal Criminal\",\"id\":229,\"status\":\"Clear\"},{\"createdDate\":\"09/12/2023\",\"name\":\"County Criminal\",\"id\":230,\"status\":\"Consider\"}";
		assertEquals(5, converter.convertToEntityAttribute(joined).size());

		final String data = "{\"createdDate\":\"09/09/2023\",\"name\":\"SSN Verification\",\"id\":226,\"status\"=\"Clear\"},{\"createdDate\":\"09/20/2023\",\"name\":\"Sex Offender\",\"id\":227,\"status\":\"Clear\"},{\"createdDate\":\"09/11/2023\",\"name\":\"Global Watchlist\",\"id\":228,\"status\":\"Clear\"},{\"createdDate\":\"09/18/2023\",\"name\":\"Federal Criminal\",\"id\":229,\"status\":\"Clear\"},{\"createdDate\":\"09/12/2023\",\"name\":\"County Criminal\",\"id\":230,\"status\":\"Consider\"}";
		assertThrows(Exception.class, () -> converter.convertToEntityAttribute(data),
				"Expected doThing() to throw, but it didn't");

	}

	private List<SearchType> getSearchTypeList() {
		List<SearchType> list = new ArrayList<>();
		SearchType type = new SearchType();
		type.setId(226l);
		type.setName("type1");

		SearchType type1 = new SearchType();
		type1.setId(227l);
		type1.setName("type2");

		SearchType type2 = new SearchType();
		type2.setId(228l);
		type2.setName("type2");

		SearchType type3 = new SearchType();
		type3.setId(229l);
		type3.setName("type3");

		SearchType type4 = new SearchType();
		type4.setId(330l);
		type4.setName("type4");
		list.add(type4);
		list.add(type3);
		list.add(type2);
		list.add(type1);
		list.add(type);
		return list;
	}

	@Test
	void convertToDatabaseColumnTest() {
		List<SearchType> list = getSearchTypeList();
		assertNotNull(converter.convertToDatabaseColumn(new HashSet<>(list)));
		assertEquals("", converter.convertToDatabaseColumn(null));
	}

}

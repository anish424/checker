package com.checker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.checker.model.SearchType;

public interface SearchTypeRepository extends JpaRepository<SearchType, Long>{

	@Query( "select type from SearchType type where id in :ids" )
	List<SearchType> findByIds(List<Long> ids);
}

package com.capelotto.ibovespaBot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.capelotto.ibovespaBot.model.Ibovespa;

@Repository
public interface IbovespaRepository  extends JpaRepository<Ibovespa, Long>{
	//public Optional<Ibovespa> findByPontuacao (String pontuacao);
	
	@Query(value = "SELECT * FROM ibovespa ORDER BY id DESC LIMIT 1;", nativeQuery = true)
	Optional<Ibovespa> findLastIbovespa();

}

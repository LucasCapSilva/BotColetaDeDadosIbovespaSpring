package com.capelotto.ibovespaBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.capelotto.ibovespaBot.util.IbovespaBot;


@SpringBootApplication
public class IbovespaBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(IbovespaBotApplication.class, args);
		IbovespaBot scraping = new IbovespaBot();
		scraping.obtemInformaçõesGoogle();
	}

}

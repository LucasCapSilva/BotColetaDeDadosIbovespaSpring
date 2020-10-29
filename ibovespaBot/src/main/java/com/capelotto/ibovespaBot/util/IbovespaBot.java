package com.capelotto.ibovespaBot.util;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.capelotto.ibovespaBot.model.Compra;
import com.capelotto.ibovespaBot.model.Ibovespa;
import com.capelotto.ibovespaBot.repository.CompraRepository;
import com.capelotto.ibovespaBot.repository.IbovespaRepository;
import com.mchange.v2.cfg.PropertiesConfigSource.Parse;


@Component
@EnableScheduling
@Service
public class IbovespaBot {

	private static final Logger LOGGER = LoggerFactory.getLogger(IbovespaBot.class);

	private static final String NOME_DA_EMPRESA = "div[class=HfMth]";
	
	private static final String URL_IBOVESPA_PETR4 = "https://www.google.com/search?sxsrf=ALeKk03NCR6OIsNayMGYJqIEDcGXWEqztw%3A1603813670846&ei=JkGYX4qWM8Si5OUP-OuL4Ac&q=ibovespa+petrobras&oq=ibovespa+petrobras&gs_lcp=CgZwc3ktYWIQDFAAWABgVWgAcAB4AIABAIgBAJIBAJgBAKoBB2d3cy13aXo&sclient=psy-ab&ved=0ahUKEwiKwYvBj9XsAhVEEbkGHfj1AnwQ4dUDCA0"; 

	private static final String PONTUACAO_DA_BOLSA = "span[jsname=vWLAgc]";
	
	private static final String VARIACAO_DIA = "span[jsname=qRSVye]";
	
	@Autowired
	private IbovespaRepository ibovespaRepository;
	
	@Autowired
	private CompraRepository compraRepository;

	private final long MINUT = 60 * 60;
		
	@Scheduled(fixedDelay = MINUT)
	public void obtemInformaçõesGoogle() {
		Document document = null;

		Ibovespa ibovespa = new Ibovespa();

		try {
			LOGGER.info(URL_IBOVESPA_PETR4);
			// conecta no site
			document = Jsoup.connect(URL_IBOVESPA_PETR4).get();

			String title = document.title();
			String nome = document.select(NOME_DA_EMPRESA).first().text();
			String pontuacao = document.select(PONTUACAO_DA_BOLSA).first().text();
			String variacao = document.select(VARIACAO_DIA).first().text();
			
			
			LOGGER.info(title);
			System.out.println(nome);
			System.out.println(pontuacao);
			System.out.println(variacao);
			
			Ibovespa ibovespaModel = new Ibovespa();
			
			Compra compra = new Compra();
			
			ibovespaModel.setNome(nome);
			ibovespaModel.setPontuacao(Float.parseFloat(pontuacao.replaceAll(",", ".")));
			
			ibovespaModel.setVariacao(variacao);
	
			compra.setNome(nome);
			compra.setComprado(true);
			compra.setValorAcao(Float.parseFloat(pontuacao.replaceAll(",", ".")));
			
			
			Optional<Ibovespa> ibovespaValidation=ibovespaRepository.findLastIbovespa();
			
			if (ibovespaModel.getPontuacao()<19.99) {
				compraRepository.save(compra);
				LOGGER.info("Compre essa ação agora que ela esta barata");
			}
			
			if (!ibovespaValidation.isPresent()||!ibovespaModel.getVariacao().equals(ibovespaValidation.get().getVariacao())){
				ibovespaRepository.save(ibovespaModel);
				if (ibovespaModel.getPontuacao()>=20.00) {
					LOGGER.info("venda essa ação agora");
				}
			}
		
			
			
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			
		}

		
	}


}

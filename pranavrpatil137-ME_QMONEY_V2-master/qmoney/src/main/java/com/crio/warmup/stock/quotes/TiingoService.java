
package com.crio.warmup.stock.quotes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.management.RuntimeErrorException;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }
  

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest

  // public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws Exception {

  //   if(from.compareTo(to) != 0)
  //   throw new RuntimeException();

  //   TiingoCandle[] candles=null;
  //   try {
  //     String candleString = restTemplate.getForObject(buildUrl(symbol, to, from), 
  //                           String.class );
  //     candles = new ObjectMapper().registerModule(new JavaTimeModule())
  //               .readValue(candleString, TiingoCandle[].class);

  //     if(candles == null || candleString == null)
  //     throw new Exception("Invalid respose");

  //   }
  //   catch(JsonMappingException e)
  //   {
  //     throw new Exception(e.getMessage());
  //   }
  //   catch(JsonProcessingException e)
  //   {
  //     throw new Exception(e.getMessage());

  //   }
  //   List<Candle> Candles = Arrays.asList(candles);

  //   Collections.sort(Candles, (c1,c2) -> {
  //     return c1.getDate().compareTo(c2.getDate());
  //   });
    
  //   return Candles;
  // }
  //CHECKSTYLE:OFF
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
  throws JsonProcessingException, StockQuoteServiceException {

    TiingoCandle[] result = null;

    String url=buildUrl(symbol,from,to);
  try{

    try{
     String tiingoCandle = restTemplate
    .getForObject(url , String.class);
      
    result = getObjectMapper().readValue(tiingoCandle, TiingoCandle[].class);

    }
      catch(JsonMappingException e) {
        throw new StockQuoteServiceException("Problem while Mapping data from Tiingo",e);
    } catch(JsonParseException e) {
        throw new StockQuoteServiceException("Problem while Parsing Json file from Tiingo API",e);
    }

    }
    catch(NullPointerException e) {
       throw new StockQuoteServiceException("Invalid responce from Tiingo API",e);
    }

    return Arrays.asList(result);
}

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.
  private static final String TOKEN = "286eb5d1a276ac0baac6300452aab76459e073a0";

  //  tiingo --> 1 --> "286eb5d1a276ac0baac6300452aab76459e073a0"
//tiingo API --> 2 --> "e04fc3dcb838c74a8f867b553396b5391feb1832"


  protected String buildUrl(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https:api.tiingo.com/tiingo/daily/"+symbol+"/prices?"
                   + "startDate="+startDate+"&endDate="+endDate+
                   "&token="+TOKEN;

    return uriTemplate;
  
}


}


// package com.crio.warmup.stock.quotes;

// import static java.time.temporal.ChronoUnit.DAYS;
// import static java.time.temporal.ChronoUnit.SECONDS;
// import com.crio.warmup.stock.dto.AlphavantageCandle;
// import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
// import com.crio.warmup.stock.dto.Candle;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.JsonMappingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
// import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.Comparator;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;
// import org.springframework.web.client.RestTemplate;

// // API KEY     -->      RHX6DPE21L0ZNV9Q

// // ENDPOINT   -->  https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=IBM&outputsize=full&apikey=RHX6DPE21L0ZNV9Q

// public class AlphavantageService implements StockQuotesService {

//   RestTemplate restTemplate;

//   public AlphavantageService(RestTemplate restTemplate){
//     this.restTemplate = restTemplate;
//   }
  
//   public List<Candle> getStockQuote(String symbol,LocalDate from, LocalDate to) throws Exception {

//     Map<LocalDate,AlphavantageCandle> dailyResponse = null;
//     try {
//       String apiResponce = restTemplate.getForObject(buildUrl(symbol), 
//                             String.class );
//       dailyResponse = new ObjectMapper().registerModule(new JavaTimeModule())
//                      .readValue(apiResponce, AlphavantageDailyResponse.class).getCandles();

//       if(dailyResponse == null || apiResponce == null)
//       throw new Exception("Invalid respose");

//     }
//     catch(JsonMappingException e)
//     {
//       throw new Exception(e.getMessage());
//     }
//     catch(JsonProcessingException e)
//     {
//       throw new Exception(e.getMessage());

//     }
    
//     List<Candle> AlphavantageStocks = new ArrayList<>();
//     for( LocalDate d = from; !d.isAfter(to); d.plusDays(1)){
     
//       AlphavantageStocks.add(dailyResponse.get(d));
//     }

    

//     Collections.sort(AlphavantageStocks, (c1,c2) -> {
//       return c1.getDate().compareTo(c2.getDate());
//     });
    
//     return AlphavantageStocks;
//   }
//   // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
//   //  Implement the StockQuoteService interface as per the contracts. Call Alphavantage service
//   //  to fetch daily adjusted data for last 20 years.
//   //  Refer to documentation here: https://www.alphavantage.co/documentation/
//   //  --
//   //  The implementation of this functions will be doing following tasks:
//   //    1. Build the appropriate url to communicate with third-party.
//   //       The url should consider startDate and endDate if it is supported by the provider.
//   //    2. Perform third-party communication with the url prepared in step#1
//   //    3. Map the response and convert the same to List<Candle>
//   //    4. If the provider does not support startDate and endDate, then the implementation
//   //       should also filter the dates based on startDate and endDate. Make sure that
//   //       result contains the records for for startDate and endDate after filtering.
//   //    5. Return a sorted List<Candle> sorted ascending based on Candle#getDate
//   //  IMP: Do remember to write readable and maintainable code, There will be few functions like
//   //    Checking if given date falls within provided date range, etc.
//   //    Make sure that you write Unit tests for all such functions.
//   //  Note:
//   //  1. Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
//   //  2. Run the tests using command below and make sure it passes:
//   //    ./gradlew test --tests AlphavantageServiceTest
//   //CHECKSTYLE:OFF
//     //CHECKSTYLE:ON
//   // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
//   //  1. Write a method to create appropriate url to call Alphavantage service. The method should
//   //     be using configurations provided in the {@link @application.properties}.
//   //  2. Use this method in #getStockQuote.
//   private String buildUrl(String symbol) {

//     return "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="+symbol+
//            "&outputsize=full&apikey=RHX6DPE21L0ZNV9Q";

//   } //can make TimeSeries as Static variable so that we can easily change function, token also dont hardCode everything





package com.crio.warmup.stock.quotes;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

/*my code */
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.web.client.RestTemplate;
/*my code end */
public class AlphavantageService implements StockQuotesService {

  private RestTemplate restTemplate;
  protected AlphavantageService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement the StockQuoteService interface as per the contracts. Call Alphavantage service
  //  to fetch daily adjusted data for last 20 years.
  //  Refer to documentation here: https://www.alphavantage.co/documentation/
  //  --
  //  The implementation of this functions will be doing following tasks:
  //    1. Build the appropriate url to communicate with third-party.
  //       The url should consider startDate and endDate if it is supported by the provider.
  //    2. Perform third-party communication with the url prepared in step#1
  //    3. Map the response and convert the same to List<Candle>
  //    4. If the provider does not support startDate and endDate, then the implementation
  //       should also filter the dates based on startDate and endDate. Make sure that
  //       result contains the records for for startDate and endDate after filtering.
  //    5. Return a sorted List<Candle> sorted ascending based on Candle#getDate
  //  IMP: Do remember to write readable and maintainable code, There will be few functions like
  //    Checking if given date falls within provided date range, etc.
  //    Make sure that you write Unit tests for all such functions.
  //  Note:
  //  1. Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
  //  2. Run the tests using command below and make sure it passes:
  //    ./gradlew test --tests AlphavantageServiceTest
  //CHECKSTYLE:OFF
    //CHECKSTYLE:ON
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  1. Write a method to create appropriate url to call Alphavantage service. The method should
  //     be using configurations provided in the {@link @application.properties}.
  //  2. Use this method in #getStockQuote.
  class StartDateComparator implements Comparator<TiingoCandle>{
    @Override
    public int compare(TiingoCandle t1,TiingoCandle t2){
       if(t1.getDate().isBefore(t2.getDate()))return -1;
       return 1;
    }  
 }

  public static String getToken(){
    return "E64ORN8I6UES4SSO";
  }
  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {

   // https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=IBM&outputsize=full&apikey=<your_API_key>
    String url=" https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol="+symbol+"&outputsize=full&apikey="+getToken();
    return url;
   
}
 @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException, StockQuoteServiceException {
        String url=buildUri(symbol,from,to);
        // AlphavantageCandle alphavantageCandle[] = restTemplate
        // .getForObject(url , AlphavantageDailyResponse.class).getCandles();
        // return Arrays.asList(alphavantageCandle);
        List<TiingoCandle> tiingoCandleslist=new ArrayList<>();
       
try {     
    
      Map<LocalDate, AlphavantageCandle> candles = restTemplate
        .getForObject(url , AlphavantageDailyResponse.class).getCandles();
  
  
      
        
        for (Map.Entry<LocalDate, AlphavantageCandle> entry : candles.entrySet()){
          // System.out.println("Key = " + entry.getKey() +
          // ", Value = " + entry.getValue());
          // check the date is between start date and end date or not
          if(entry.getKey().isAfter(from.minusDays(1)) && entry.getKey().isBefore(to.plusDays(1)))
          {
           
              TiingoCandle t=new TiingoCandle();
              t.setOpen(entry.getValue().getOpen());
              t.setClose(entry.getValue().getClose());
              t.setHigh(entry.getValue().getHigh());
              t.setLow(entry.getValue().getLow());
              t.setDate(entry.getKey());
              tiingoCandleslist.add(t);

              //use Entry because you can have method getKey also so more control over map

          }
        } 
   
  } 
  catch (NullPointerException e) {
      throw new StockQuoteServiceException("Invalid responce from AlphaVAntage API",e);
    }
        
      // return tiingoCandleslist.stream().sorted(Comparator.comparing(Candle::getDate)).collect(Collectors.toList());
         Collections.sort(tiingoCandleslist,new StartDateComparator());
        List<Candle> ans = new ArrayList<>();
        for(TiingoCandle val : tiingoCandleslist){
          ans.add(val);
        }

      //  return List.of(tiingoCandleslist);
         return ans;
        
  }
   
 
  
}




package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.lang.Runtime;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {


  RestTemplate restTemplate;// = new RestTemplate();
  StockQuotesService stockQuotesService;


  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  protected PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService = stockQuotesService;
  }


  List<AnnualizedReturn> annualList;

  

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturnParallel(
      List<PortfolioTrade> portfolioTrades, LocalDate endDate, int numThreads)
      throws InterruptedException, StockQuoteServiceException {
    // TODO Auto-generated method stub
      //public static int MAX_Ts = Runtime.getRuntime().availableProcessors();

      final ExecutorService pool = Executors.newFixedThreadPool(numThreads);
      annualList = new ArrayList<>();

      List<Future<AnnualizedReturn>> futureReturnsList = new ArrayList<Future<AnnualizedReturn>>();


      for(PortfolioTrade trade : portfolioTrades) {

      Callable<AnnualizedReturn> callableTask = () -> {

        List<Candle> candle = null;
        try {
          candle = stockQuotesService.
                       getStockQuote(trade.getSymbol(), trade.getPurchaseDate(),endDate);

          if(candle == null)
          throw new Exception("Invalid respose...");
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.getMessage();
        }
    
        AnnualizedReturn r = calculateAnnualReturns(
                            endDate,
                            trade, 
                            getOpeningPriceOnStartDate(candle),
                            getClosingPriceOnEndDate(candle)
                            );
        return r;       
      };

      Future<AnnualizedReturn> future = pool.submit(callableTask);
      futureReturnsList.add(future);
    }

   for (Future<AnnualizedReturn> future : futureReturnsList) {

      try {
        annualList.add(future.get());
      } catch (ExecutionException e) {
        throw new StockQuoteServiceException("Error while Fetching data ", e); 
      }
    }
  

    Collections.sort(annualList, (a1,a2) -> {

      return Double.compare(a2.getAnnualizedReturn(),a1.getAnnualizedReturn());
    });

    return annualList;
  }



   @Override
   public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate){

        annualList = new ArrayList<>();
        for(PortfolioTrade trade : portfolioTrades) {

        List<Candle> candleList = null;
        try {
          candleList = stockQuotesService.
                       getStockQuote(trade.getSymbol(), trade.getPurchaseDate(),endDate);

          if(candleList == null)
          throw new Exception("Invalid respose...");
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.getMessage();
        }
    
        AnnualizedReturn r = calculateAnnualReturns(
                            endDate,
                            trade, 
                            getOpeningPriceOnStartDate(candleList),
                            getClosingPriceOnEndDate(candleList)
                            );
        
        annualList.add(r);
        }
        
    Collections.sort(annualList,(a1,a2) -> {

      return Double.compare(a2.getAnnualizedReturn(),a1.getAnnualizedReturn());
    });

    return annualList;

  }

  

  public AnnualizedReturn calculateAnnualReturns(LocalDate endDate,PortfolioTrade trade, 
  double openPrice, double closePrice) {

      Double totalReturn = (closePrice - openPrice) / openPrice;

      double total_num_years = getTotalYears(trade.getPurchaseDate(), endDate);

      Double annualized_returns = Math.pow(
                                (1 + totalReturn) ,
                                (1 / total_num_years)
                                ) - 1;
      
      return new AnnualizedReturn(trade.getSymbol(), annualized_returns,totalReturn);

  }



  public static double getTotalYears(LocalDate d1, LocalDate d2) {
        return (double)ChronoUnit.DAYS.between( d1 , d2 )/365.24;
       }

  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF








  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

   //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.

  static Double getOpeningPriceOnStartDate(List<Candle> candles) {    
    return candles.get(0).getOpen();     
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
     return candles.get(candles.size()-1).getClose();
  }


   public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {

        Candle[] result = restTemplate.getForObject(
          buildUri(symbol,from,to),
          TiingoCandle[].class);
    
          List<Candle> candles = Arrays.stream(result).collect(Collectors.toList()); 

          return candles;
  }

  private static final String TOKEN = "286eb5d1a276ac0baac6300452aab76459e073a0";
  
  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
      String uriTemplate = "https:api.tiingo.com/tiingo/daily/"+symbol+"/prices?"
                     + "startDate="+startDate+"&endDate="+endDate+
                     "&token="+TOKEN;

      return uriTemplate;
    
  }
//  tiingo --> 1 --> "286eb5d1a276ac0baac6300452aab76459e073a0"
//tiingo API --> 2 --> "e04fc3dcb838c74a8f867b553396b5391feb1832"

 


  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.

}

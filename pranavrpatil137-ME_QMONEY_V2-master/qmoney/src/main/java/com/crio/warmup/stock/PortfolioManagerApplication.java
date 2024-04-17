
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {

  static RestTemplate restTemplate = new RestTemplate();

  //static final String TOKEN = "286eb5d1a276ac0baac6300452aab76459e073a0";

//  tiingo --> 1 --> "286eb5d1a276ac0baac6300452aab76459e073a0"
//tiingo API --> 2 --> "e04fc3dcb838c74a8f867b553396b5391feb1832"

public static String getToken() {
  return "286eb5d1a276ac0baac6300452aab76459e073a0";
}


    //LocalDate.now()

  static List<PortfolioTrade> tradeList;










  // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>



  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Follow the instructions provided in the task documentation and fill up the correct values for
  //  the variables provided. First value is provided for your reference.
  //  A. Put a breakpoint on the first line inside mainReadFile() which says
  //    return Collections.emptyList();
  //  B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  //  following the instructions to run the test.
  //  Once you are able to run the test, perform following tasks and record the output as a
  //  String in the function below.
  //  Use this link to see how to evaluate expressions -
  //  https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  //  1. evaluate the value of "args[0]" and set the value
  //     to the variable named valueOfArgument0 (This is implemented for your reference.)
  //  2. In the same window, evaluate the value of expression below and set it
  //  to resultOfResolveFilePathArgs0
  //     expression ==> resolveFileFromResources(args[0])
  //  3. In the same window, evaluate the value of expression below and set it
  //  to toStringOfObjectMapper.
  //  You might see some garbage numbers in the output. Dont worry, its expected.
  //    expression ==> getObjectMapper().toString()
  //  4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  //  second place from top to variable functionNameFromTestFileInStackTrace
  //  5. In the same window, you will see the line number of the function in the stack trace window.
  //  assign the same to lineNumberFromTestFileInStackTrace
  //  Once you are done with above, just run the corresponding test and
  //  make sure its working as expected. use below command to do the same.
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

   public static List<String> debugOutputs() {

     String valueOfArgument0 = "trades.json";
     String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/pranavrpatil137-ME_QMONEY_V2/qmoney/bin/main/trades.json";
     String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@6150c3ec";
     String functionNameFromTestFileInStackTrace =  "mainReadFile";
     String lineNumberFromTestFileInStackTrace = "29";


    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace});
  } 


  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    
    List<TotalReturnsDto> totalReturnsDtos = new ArrayList<>();

    for(PortfolioTrade trade : readTradesFromJson(args[0])) {
   
    TiingoCandle tiingoCandle[] = restTemplate.getForObject(
                                  prepareUrl(trade,LocalDate.parse(args[1])
                                , getToken())
                                , TiingoCandle[].class);
      
    TotalReturnsDto objectDto  = new TotalReturnsDto(trade.getSymbol()
                                ,tiingoCandle[tiingoCandle.length - 1].getClose());

    totalReturnsDtos.add(objectDto);   
    }
  
    List<String> sortedSymbolsByClosingPrice = totalReturnsDtos.stream()
                              .sorted((c1,c2) -> {
                               return Double.compare(c1.getClosingPrice(),c2.getClosingPrice());})
                              .map(r -> r.getSymbol())
                              .collect(Collectors.toList());

    return sortedSymbolsByClosingPrice;
  }
  

  public static List<String> mainReadFile(String args[]) throws IOException, URISyntaxException {

    List<String> tradesSymbols = readTradesFromJson(args[0]).stream()
                                .map(t -> t.getSymbol())
                                .collect(Collectors.toList());
    return tradesSymbols;
  }

  // TODO:
  //  After refactor, make sure that the tests pass by using these two commands
  //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    
  PortfolioTrade[] trades = getObjectMapper().readValue(
                            resolveFileFromResources(filename),
                            PortfolioTrade[].class
                            );
  
  return Arrays.stream(trades).collect(Collectors.toList());  
  }


  // TODO:
  //  Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
     //return "https://api.tiingo.com/tiingo/daily/"+trade.getSymbol()+"/prices"+"?endDate="+endDate+"&?token="+token;

     return "https://api.tiingo.com/tiingo/daily/"+ 
             trade.getSymbol() +"/prices?startDate="+ trade.getPurchaseDate()
             +"&endDate="+ endDate +"&token=" + token;
  }

  

//"https://api.tiingo.com/tiingo/daily/aapl/prices?endDate=2019-01-02&token=e04fc3dcb838c74a8f867b553396b5391feb1832



  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.




  // TODO:
  //  Ensure all tests are passing using below command
  //  ./gradlew test --tests ModuleThreeRefactorTest
  static Double getOpeningPriceOnStartDate(List<Candle> candles) {    
    return candles.get(0).getOpen();     
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
     return candles.get(candles.size()-1).getClose();
  }


  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
   
    Candle[] result = restTemplate.getForObject(
                      prepareUrl(trade, endDate, getToken()),
                      TiingoCandle[].class);
   /*  ResponseEntity<List<Candle>> response = restTemplate.exchange(
      url, 
      HttpMethod.GET, 
      null, new ParameterizedTypeReference<List<Candle>>(){}
      ); */
  List<Candle> candles = Arrays.stream(result).collect(Collectors.toList()); 

  return candles;
  }


  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {

        List<AnnualizedReturn> annualList = new ArrayList<>();
        tradeList = readTradesFromJson(args[0]);

        for(PortfolioTrade trade : tradeList) {

        List<Candle> candleList = fetchCandles(trade, LocalDate.parse(args[1]),getToken());
    
        AnnualizedReturn r = calculateAnnualizedReturns(
                            LocalDate.parse(args[1]),
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

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

 public static double getTotalYears(LocalDate d1, LocalDate d2) {
  /* Period period = d1.until(d2);
  int yearsBetween = period.getYears();
  System.out.println("yearsBetween:"+yearsBetween); */
  return (double)ChronoUnit.DAYS.between( d1 , d2 )/365.24;
 }

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {

      Double totalReturn = (sellPrice - buyPrice) / buyPrice;

      double total_num_years = getTotalYears(trade.getPurchaseDate(), endDate);

      Double annualized_returns = Math.pow(
                                (1 + totalReturn) ,
                                (1 / total_num_years)
                                ) - 1;
      
      return new AnnualizedReturn(trade.getSymbol(), annualized_returns,totalReturn);
  }



 /*  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());
    //printJsonObject(mainReadFile(args));
    //printJsonObject(mainReadQuotes(args));
    printJsonObject(mainCalculateSingleReturn(args));
*/


















 /* public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString()); */







  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
       String file = args[0];
       LocalDate endDate = LocalDate.parse(args[1]);
       //String contents = readFileAsString(file);
       ObjectMapper objectMapper = getObjectMapper();
       PortfolioTrade portfolioTrades[] =  objectMapper.readValue(file, PortfolioTrade[].class);

       RestTemplate restTemplate = new RestTemplate();
       PortfolioManager portfolioManager =  PortfolioManagerFactory.getPortfolioManager(restTemplate);
       return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
  }


  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    

  }


}


  //}
//}


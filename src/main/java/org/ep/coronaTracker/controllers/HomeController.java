package org.ep.coronaTracker.controllers;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.ep.coronaTracker.models.DayWiseModel;
import org.ep.coronaTracker.models.LocationStats;
import org.ep.coronaTracker.models.LocationStatsDateWise;
import org.ep.coronaTracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class HomeController {
    final String VIRUS_DATA_BY_DAY = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/";
    private List<LocationStatsDateWise> locationStatusByDate = new ArrayList<>();

    @Autowired
    CoronaVirusDataService coronaVirusDataService;
    @Autowired
    DayWiseModel dayWiseModel;

    @RequestMapping("/")
    //@GetMapping("/")  // Get Mapping works for only Get Requests, RequestMapping maps all GET, POST ....
    private String getHome(Model model) {
        model.addAttribute("day", new DayWiseModel());
        model.addAttribute("locationStatus", coronaVirusDataService.getLocationStatus());
        List<LocationStats> locationStatus = coronaVirusDataService.getLocationStatus();
        int totalConfirmedCount = coronaVirusDataService.getLocationStatus().stream().mapToInt(value -> Integer.parseInt(value.getLatestConfirmedStat())).sum();
        model.addAttribute("totalConfirmedCase", totalConfirmedCount);

        return "homePage";
    }


    @PostMapping("/dateWise")
    public String fetchVirusDetailsOfDate(Model model, @ModelAttribute DayWiseModel dayWiseModel) throws IOException, InterruptedException, ParseException {
        System.err.println(dayWiseModel.getDay());

        Date tmpFormatteddate = new SimpleDateFormat("yyyy-mm-DD").parse(dayWiseModel.getDay());
        String formattedDate = new SimpleDateFormat("mm-DD-yyyy").format(tmpFormatteddate);
        System.err.println(formattedDate);

        ArrayList<LocationStatsDateWise> newLocationStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_BY_DAY + formattedDate + ".csv")).build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        // System.out.println(response.body());

        StringReader csvReader = new StringReader(response.body());
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(csvReader);
        for (CSVRecord record : records) {
            LocationStatsDateWise locationStatsDateWise = new LocationStatsDateWise();
            locationStatsDateWise.setProvince(record.get("Province/State"));
            locationStatsDateWise.setCountry(record.get("Country/Region"));
            locationStatsDateWise.setLastUpdated(record.get("Last Update"));
            locationStatsDateWise.setConfirmed(record.get("Confirmed"));
            locationStatsDateWise.setDeaths(record.get("Deaths"));
            locationStatsDateWise.setRecovered(record.get("Recovered"));
            newLocationStats.add(locationStatsDateWise);

            //  System.err.println(locationStatsDateWise);
        }
        this.locationStatusByDate = newLocationStats;
        // this.locationStatusByDate.forEach(System.err::println);

        model.addAttribute("dateWiseList", locationStatusByDate);
        return "dateWiseReport";
    }

    /*Used to handle error caused by submitting without choosing a date
    TO:DO- Handle the cases when user selects future dates.
    * */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String recordNotFound() {
        return "error";
    }
}

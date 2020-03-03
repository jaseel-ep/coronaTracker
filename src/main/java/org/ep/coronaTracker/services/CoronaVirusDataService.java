package org.ep.coronaTracker.services;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.ep.coronaTracker.models.LocationStats;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDataService {

    private List<LocationStats> locationStatus = new ArrayList<>();


    final String VIRUS_DATA = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";

    public List<LocationStats> getLocationStatus() {
        return locationStatus;
    }

    /* PostConstruct --> runs this method when the application is run*/
    @PostConstruct
    /* Use Schedule here to run this method automatically in a scheduled interval of time*/

    public void fetchVirusDetails() throws IOException, InterruptedException {

        ArrayList<LocationStats> newLocationStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA)).build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        // System.out.println(response.body());

        StringReader csvReader = new StringReader(response.body());

        /* Using Apache Commons CSV to parse CSV (comma separated values) file from GitHub
        See : https://commons.apache.org/proper/commons-csv/
        * */
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(csvReader);
        for (CSVRecord record : records) {
            LocationStats locationStats = new LocationStats();
            locationStats.setProvince(record.get("Province/State"));
            locationStats.setCountry(record.get("Country/Region"));
            locationStats.setLatestConfirmedStat(record.get(record.size() - 1));
            newLocationStats.add(locationStats);
        }
        this.locationStatus = newLocationStats;
        //   this.locationStatus.forEach(System.err::println);


    }


}

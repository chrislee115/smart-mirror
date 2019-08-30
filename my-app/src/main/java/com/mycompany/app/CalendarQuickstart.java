package com.mycompany.app;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;

public class CalendarQuickstart {
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = CalendarQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                                                                                   HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
        .setAccessType("offline")
        .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
    //is this finished;
    public boolean oneWeekAway(String date) {
        return false;
    }
    public static void writeToFile(List<Event> items, PrintWriter printWriter, String name,
                                    Date weekAway) {
        SimpleDateFormat timedEvent = new SimpleDateFormat("EEEE, MM/dd 'at' hh:mm a");
        SimpleDateFormat allDay = new SimpleDateFormat("EEEE, MM/dd");
        String time;
        Date temp;
        boolean hasEvents = false;
        if (!items.isEmpty()) {
            //printWriter.println(ZonedDateTime.now() + "zone date");
            printWriter.print("HEADER: " + name);
            //System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                DateTime end = event.getEnd().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                    //86400000 is a day in milliseconds
                    //the all day events are for some reason off by a day
                    temp = new Date(event.getStart().getDate().getValue() + 86400000);
                    time = allDay.format(temp);
                }
                else {
                    temp = new Date(event.getStart().getDateTime().getValue());
                    time = timedEvent.format(temp);
                }
                //convert the DateTime object into a gregCal to allow comparison
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(temp);
                //Only print the events that occur within a week of currentDate
                if (temp.before(weekAway)) {
                    if (!hasEvents) { printWriter.println(""); }
                    hasEvents = true;
                    System.out.println(time);
                    printWriter.printf("%s EventName: %s \n", 
                                       time, event.getSummary());
                }
            }
            if (!hasEvents) {
                printWriter.print("- No Upcoming Events \n");
            }
        }
    }
    public static void main(String... args) throws IOException, GeneralSecurityException {
        CalendarQuickstart object = new CalendarQuickstart();
        object.waitMethod();
    }
    private synchronized void waitMethod()  throws IOException, GeneralSecurityException {
        while (true) {
                // Get a date that is a week away
        Date weekAway = new Date(System.currentTimeMillis());
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(weekAway);
        calendar.add(java.util.Calendar.DATE, 7);
        weekAway.setTime(calendar.getTime().getTime());
                                        
        
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
        .setApplicationName(APPLICATION_NAME)
        .build();

        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        Events general = service.events().list("primary")
        .setMaxResults(10)
        .setTimeMin(now)
        .setOrderBy("startTime")
        .setSingleEvents(true)
        .execute();
        
        Events funKtion = service.events().list("umich.edu_anpmdd59qq6gopmdt3l9lp8kdg@group.calendar.google.com")
        .setMaxResults(10)
        .setTimeMin(now)
        .setOrderBy("startTime")
        .setSingleEvents(true)
        .execute();
        
        Events ktp = service.events().list("umich.edu_slf0shn0ott48uiegpr8uth3h4@group.calendar.google.com")
        .setMaxResults(10)
        .setTimeMin(now)
        .setOrderBy("startTime")
        .setSingleEvents(true)
        .execute();
        
        Events holidays = service.events().list("en.usa#holiday@group.v.calendar.google.com")
        .setMaxResults(10)
        .setTimeMin(now)
        .setOrderBy("startTime")
        .setSingleEvents(true)
        .execute();
        
        Events bdays = service.events().list("addressbook#contacts@group.v.calendar.google.com")
        .setMaxResults(10)
        .setTimeMin(now)
        .setOrderBy("startTime")
        .setSingleEvents(true)
        .execute();
        
        FileWriter fileWriter = new FileWriter("/home/pi/Desktop/calendar/events.txt");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        
        List<Event> generalItems = general.getItems();
        List<Event> funKItems = funKtion.getItems();
        List<Event> ktpItems = ktp.getItems();
        List<Event> holidayItems = holidays.getItems();
        List<Event> bdayItems = bdays.getItems();
        
        writeToFile(generalItems, printWriter, " General ", weekAway);
        writeToFile(funKItems, printWriter, " FunKtion ", weekAway);
        writeToFile(ktpItems, printWriter, " KTP ", weekAway);
        writeToFile(holidayItems, printWriter, " Holidays ", weekAway);
        writeToFile(bdayItems, printWriter, " Birthdays ", weekAway);
        /*
        if (eventItems.isEmpty()) {
            printWriter.println("No upcoming events found.");
            //System.out.println("No upcoming events found.");
        } else {
            printWriter.println("Upcoming events:");
            //System.out.println("Upcoming events");
            for (Event event : eventItems) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                printWriter.printf("%s (%s)\n", event.getSummary(), start);
                //System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }*/
        printWriter.close();
        try {
                this.wait(15000);
        } catch (InterruptedException e) {
                e.printStackTrace();
        }
}
    }
}

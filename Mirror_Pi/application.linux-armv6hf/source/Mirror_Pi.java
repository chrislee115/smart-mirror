import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Mirror_Pi extends PApplet {


//TODO add a no events text
//TODO add all the weather pictures
//MIRROR SPECS
//dont forget to change in setup as well
int mWidth = 1920;
int mHeight = 1080;
//FONT SPECS
PFont lightFont;
PFont regFont;
PFont boldFont;
int bigSize = 200;
int regSize = 50;
int smSize = 24;

//GREETING SPECS
float greetingX = 30;
float greetingY = 80;
float nameX = 30;
float nameY = 160;
String name = "Chris";

//TIME SPECS
int hour, minute;
float timeY = 175;
float timeX = mWidth - 100;
float ampmX = mWidth - 30;

//DATE SPECS
String months[] = {"January", "February", "March", "April", "May", "June", 
                  "July", "August", "September", "October", "November", "December"};
String daysWeek[] = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};  
int daysInMonth[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
int daysInLYMonth[] = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
float dateX = mWidth - 30;
float dateY = 250;
//for the day of the week calculation
float dow; // day of week
int monthKeys[] = {1, 4, 4, 0, 2, 5, 0, 3, 6, 1, 4, 6};
int centuryKeys[] = {4, 2, 0, 6};

//WEATHER SPECS
String wdata[];
String condit; 
PImage clear, cloudy, hail, mist, rain, snow, sunny, thunderstorm, windy;
//TODO: find more images
String weatherTypes[] = {"thunder", "wind", "rain", "cloud", "hail", 
                          "mist",  "snow", "sun", "clear"};
String weatherText;
float weatherPX = mWidth - 100;
float weatherPY = 390;
float weatherTX = mWidth - 65;
float weatherTY = 480;

//NOW PLAYING SPECS
String songData[];

//CALENDAR SPECS
String events[];
String timeStart, timeEnd, timeStamp;
String prevHeader;
int dateStart, dateEnd;
int eventYear, eventMonth, eventDay;
float eventX = 30;
float defEventY = 250;
float eventY;
boolean isLYear = (year() % 4 == 0);
int eventPage = 0;
int updateCal = 0;
int updateSpot = 0;
int prevTime = 0;
PImage bg;
static public void main(String args[]) {
  PApplet.main("Mirror_Pi");
}
public void setup(){
  
  //size(1920, 1080);
  lightFont = createFont("Open Sans Light", 72);
  regFont = createFont("Open Sans Regular", 72);
  boldFont = createFont("Open Sans SemiBold", 72);
  textFont(lightFont, regSize);
  launch("/home/pi/Desktop/widget-start.desktop");
  
  clear = loadImage("/home/pi/Desktop/Mirror_Pi/Sunny.png");
  cloudy = loadImage("/home/pi/Desktop/Mirror_Pi/Cloudy.png");
  hail = loadImage("/home/pi/Desktop/Mirror_Pi/Hail.png");
  mist = loadImage("/home/pi/Desktop/Mirror_Pi/Mist.png");
  rain = loadImage("/home/pi/Desktop/Mirror_Pi/Rain.png"); 
  snow = loadImage("/home/pi/Desktop/Mirror_Pi/Snow.png");
  sunny = loadImage("/home/pi/Desktop/Mirror_Pi/Sunny.png");
  thunderstorm = loadImage("/home/pi/Desktop/Mirror_Pi/Thunderstorm.png");
  windy = loadImage("/home/pi/Desktop/Mirror_Pi/Windy.png");
  
  bg = loadImage("/home/pi/Desktop/northern-lights.jpeg");
}

public void draw(){
  background(bg);
  int texcol = 255;
  int fillcol = 0;
  weather();
  calendar();
  nowPlaying();
  fill(fillcol);
  noStroke();
  rectMode(CORNER);
   
  //EVENTS ANIMATION
  fill(255);
    
    //greeting + name
  textSize(regSize);
  textAlign(LEFT);
  if (hour() < 6 || hour() > 20) {
    text("Good Evening,", greetingX, greetingY);
  } else if (hour() < 12) {
    text("Good Morning,", greetingX, greetingY);
  } else if (hour() < 17) {
    text("Good Afternoon,", greetingX, greetingY);
  } else { 
    text("Good Evening,", greetingX, greetingY);
  }
  textFont(boldFont);
  text(name, nameX, nameY);
 
  textAlign(RIGHT);
     //time stuff
  textFont(lightFont, bigSize);
  fill(texcol);
  hour = standardizeHour(hour());
  minute = minute();
  if (minute > 9) {
    text(hour + ":" + minute, timeX, timeY);
  } 
  else {
    text(hour + ":" + "0"  + minute, timeX, timeY);
  }
    
  textFont(boldFont, regSize);
  if (hour() > 12) { 
    text("PM", ampmX, timeY);
  }
  else {
    text("AM", ampmX, timeY);
  }
   
  textFont(lightFont, regSize);
    //month day date display
  text(months[month() - 1] + " " + day(), dateX, dateY);
     //DAY
  text(getDOW(year(), month(), day()) + ", " + months[month() - 1]+ " " + day(), dateX, dateY);
  //////////////////////////////////////////////////
  //EVENTS
}  
//fail safe, click  top left to close program
public void mousePressed() {
  if (mouseX >= 0 && mouseX < 300) {
    if (mouseY >= 0 && mouseY < 300) {
      exit();
    }
  }
}
public void nowPlaying() {
  songData = loadStrings("/home/pi/Desktop/my-app/now-playing.txt");
  //0 is name, 1 is artist, 2 is album
  textAlign(CENTER);
  if (songData.length > 0) {
    textSize(smSize + 5);
    text("Now Playing", width / 2, height - 130);
    textSize(smSize);
    text(songData[0], width / 2, height - 100);
    text(songData[1], width / 2, height - 70);
    text(songData[2], width / 2, height - 40);
  }
}
public String getDOW(int y, int m, int d) {
  // day of the week formula, key value method
    dow = ((y % 100) / 4) + d + monthKeys[m - 1];
    if (y / 4 == 0) {
      dow--;
    }
    dow += centuryKeys[(y - 1700) / 100];
    dow += y % 100;    
    return daysWeek[(int)dow % 7];
}
public int standardizeHour(int hour_in){
  hour_in %= 12;
  if (hour_in == 0) {
      return 12;
  }
  return hour_in;
}
int defWeatherY = 550;
public void weather(){
  textAlign(RIGHT);
  PImage imageTypes[] = {clear, cloudy, hail, mist, rain, snow, sunny, thunderstorm,
                       windy};
  //THIS IS WHERE YOU WILL NEED TO FIND A WEATHER SOURCE FOR YOUR CITY
  wdata = loadStrings("/home/pi/Desktop/my-app/weather.txt");
  //weather listed in this order: conditions (0), temp (1), hi/lo (2), humidity (3)
  //windspeed(4), rain (5), snow (6)
  if (wdata.length == 0) { 
    return;
  }
  condit = wdata[0];
  fill(255);
  textSize(smSize);
   //ICONS FOR WEATHER CONDITION
  //weather image 
  //weather type
  float tempX = width - 30;
  float tempY = defWeatherY;
  for (int i = 0; i < weatherTypes.length; i++) {
    if(condit.toLowerCase().contains(weatherTypes[i])) {
      imageMode(CENTER);
      image(imageTypes[i], weatherPX + 15, weatherPY);
      //"CONDITIONS: "is 11 characters
      textFont(boldFont, smSize);
      text(condit.substring(11), tempX, weatherTY);
      break;
    }
  }
  //temperature stuff
  fill(255);
  textFont(boldFont, regSize);
  //temp: has 6 characters
  text(wdata[1].substring(6), tempX, tempY);
  tempY += 45;
  for (int i = 2; i < wdata.length; ++i) {
    if (i % 2 == 0) {
      textFont(boldFont, smSize);
      text(wdata[i], tempX, tempY);
      tempY += 25; 
    }
    else { 
      textFont(regFont, smSize - 4);
      text(wdata[i], tempX, tempY);
      tempY += 30;
    }
  }
}
public void printEvents(int start, int end) {
  eventY = defEventY;
  for (int i = start; i < end; i++) {
    String event = events[i];
    //if the events from a certain category carry over 
    int splitIndex;
    //saves index of date time, while comparing 
    //if the current line is not an event
    //DateTime is present in every event, not in any header
    if ((splitIndex = event.indexOf("DateTime:")) == -1) {
      textFont(boldFont, smSize + 10);
      text(event, eventX, eventY);  
      eventY += 35;
      prevHeader = event;
    }
    else {
      if (i == start) { 
        textFont(boldFont, smSize + 10);
        text(prevHeader, eventX, eventY);
        eventY += 35;
      }
      else {
        int split2 = event.indexOf("EventName:");
        //category
        textFont(lightFont, smSize);
        text(event.substring(0, splitIndex), eventX, eventY);
        eventY += 30;
        //DateTime: is 9 characters
        //date/time
        text(event.substring(splitIndex + 9, split2), eventX + 15, eventY);
        eventY += 30;
        //EventName: is 10 characters
        //event name
        textFont(regFont, smSize);
        text(event.substring(split2 + 10), eventX + 30, eventY);
        eventY += 50;
      }
    }
  }
}
public void calendar(){
   //google calendar api implementation, pull from events.txt  
   events = loadStrings("/home/pi/Desktop/my-app/events.txt");
   //13 is the maximum number of events that can come up without the
   //text flying off the screen
     
   int cutOff = 8;
   //fix this later
   textAlign(LEFT);
   if (events.length == 0) { 
     textFont(regFont, smSize + 5);
     text("No upcoming events.", eventX, eventY);
     return;
   }
   else {
     //last page
     if (eventPage >= ((events.length - 1) / cutOff)) {
       printEvents(eventPage * cutOff, events.length);
     }
     //everything but last page
     else {
       printEvents(eventPage * cutOff, (eventPage * cutOff) + cutOff);
     }
     //change page every 10 seconds
     if (millis() - prevTime >= (10 * 1000)) {
       prevTime = millis();
       eventPage = (eventPage + 1) % (((events.length - 1) / cutOff) + 1);
     }
     textFont(regFont, smSize);
     text(eventPage + 1 + "/" + ceil((events.length) / (float)cutOff), 20, height - 50);
   }
}
  public void settings() {  fullScreen(); }
}

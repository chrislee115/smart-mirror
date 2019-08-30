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
String weatherTypes[] = {"clear", "cloudy", "hail", "mist", "rain", "snow", "sunny",
                        "thunderstorm", "windy"};
String weatherText;
float weatherPX = mWidth - 100;
float weatherPY = 390;
float weatherTX = mWidth - 65;
float weatherTY = 480;

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
boolean hasEvents;
int eventPage = 0;
int updateTime = 0;
int prevTime = 0;
static public void main(String args[]) {
  PApplet.main("Mirror_Pi");
}
public void setup(){
  
  //size(1920, 1080);
  lightFont = createFont("Open Sans Light", 72);
  regFont = createFont("Open Sans Regular", 72);
  boldFont = createFont("Open Sans SemiBold", 72);
  textFont(lightFont, regSize);
  launch("/home/pi/Desktop/calendar-startup.desktop");
    
  clear = loadImage("/home/pi/Desktop/Mirror_Pi/Sunny.jpg");
  cloudy = loadImage("/home/pi/Desktop/Mirror_Pi/Cloudy.jpg");
  hail = loadImage("/home/pi/Desktop/Mirror_Pi/Hail.jpg");
  mist = loadImage("/home/pi/Desktop/Mirror_Pi/Mist.jpg");
  rain = loadImage("/home/pi/Desktop/Mirror_Pi/Rain.jpg"); 
  snow = loadImage("/home/pi/Desktop/Mirror_Pi/Snow.jpg");
  sunny = loadImage("/home/pi/Desktop/Mirror_Pi/Sunny.jpg");
  thunderstorm = loadImage("/home/pi/Desktop/Mirror_Pi/Thunderstorm.jpg");
  windy = loadImage("/home/pi/Desktop/Mirror_Pi/Windy.jpg");
}

public void draw(){
  if (millis() - updateTime >= 15000) {
    launch("/home/pi/Desktop/calendar-startup.desktop");
    updateTime = millis();
  }
  background(0);
  fill(0);
  stroke(255);
  rect(10,10,width-20,height-20);
  int texcol = 255;
  int fillcol = 0;
  weather();
  calendar();
  fill(fillcol);
  noStroke();
  rectMode(CORNER);
   
  //EVENTS ANIMATION
  fill(255);
    
    //greeting + name
  textSize(regSize);
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
  textAlign(LEFT);
  stroke(255);
  //EVENTS
}  
public void mousePressed() {
  if (mouseX >= 0 && mouseX < 300) {
    if (mouseY >= 0 && mouseY < 300) {
      exit();
    }
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
public int findSource(String data[], String keyword){
  //for weather, the source string is the second string containing the keyword
  int weatherIndex = -1;
  boolean foundFirst = false;
  for (int i = 200; i < 230; i++) {
    if (data[i].contains(keyword)) {
      if (!foundFirst) {
        foundFirst = true;
      }
      else {
        return i;
      }
    }
  }
  return weatherIndex;
}
public String getText(String str) {
  //reset weatherText string
  weatherText = "";
  int index = str.lastIndexOf("text:");
  //to skip "text:""
  index += 6;
  while (str.charAt(index) != '"') {
    weatherText += str.charAt(index);
    index++;
  }
  return weatherText;
}
public void weather(){
  PImage imageTypes[] = {clear, cloudy, hail, mist, rain, snow, sunny, thunderstorm,
                       windy};
  //THIS IS WHERE YOU WILL NEED TO FIND A WEATHER SOURCE FOR YOUR CITY
  wdata = loadStrings("https://www.accuweather.com/en/us/troy-mi/48098/weather-forecast/338798");
  condit = wdata[findSource(wdata, "acm_RecentLocationsCarousel")];
  fill(255);
  textSize(smSize);
   //ICONS FOR WEATHER CONDITION
 
  
  //weather image 
  //weather type
  for (int i = 0; i < weatherTypes.length; i++) {
    if(condit.toLowerCase().contains(weatherTypes[i])) {
      imageMode(CENTER);
      image(imageTypes[i], weatherPX, weatherPY);
      textAlign(RIGHT);
      text(getText(condit), weatherTX, weatherTY);
      break;
    }
  }
  textAlign(LEFT);
  //temperature stuff
  int tempIndex = condit.indexOf("temp:");
  String temperature = ""; 
 
  while (condit.charAt(tempIndex) != ',') {
    if ('0' <= condit.charAt(tempIndex) && condit.charAt(tempIndex) <= '9') {
      temperature += condit.charAt(tempIndex);
    }
    tempIndex++;
  }
  
  fill(255);
  textFont(boldFont, regSize);
  float tempX = width - 260;
  float tempY = 420;
  text(temperature + "°", tempX, tempY);
}
public void printEvents(int start, int end) {
  eventY = defEventY;
  for (int i = start; i < end; i++) {
    String event = events[i];
    if (i == start && !event.contains("HEADER:")) {
      textFont(regFont, smSize + 10);
      text(prevHeader + "Cont.", eventX, eventY);
      eventY += 35;
      end -= 2;
    }
    //if the current line is a header and contains no upcoming events
    if (event.contains("HEADER:")) {
      if(!event.contains("No Upcoming Events")) {
         textFont(regFont, smSize + 10);
         text(event.substring(event.indexOf(":") + 2), eventX, eventY);
         prevHeader = event.substring(event.indexOf(":") + 2);
         eventY += 35;
         hasEvents = false;
      }
    }
    else {
      int splitIndex = event.indexOf("EventName");
      textFont(lightFont, smSize);
      text(event.substring(0, splitIndex), eventX + 30, eventY);
      eventY += 30;
      textFont(regFont, smSize);
      text(event.substring(splitIndex + 11, event.length()), eventX + 50, eventY);
      eventY += 50;
    }
  }
}
public void calendar(){
   //google calendar api implementation, pull from events.txt  
   events = loadStrings("/home/pi/Desktop/calendar/events.txt");
   //13 is the maximum number of events that can come up without the
   //text flying off the screen
   int cutOff = 12;
   //fix this later
   if (events.length <= cutOff) {
     printEvents(0, events.length);
   }
   else {
     if (eventPage >= ((events.length - 1) / cutOff)) {
       printEvents(eventPage * cutOff, events.length);
     }
     else {
       printEvents(eventPage * cutOff, (eventPage * cutOff) + cutOff);
     }
     if (millis() - prevTime >= 7000) {
       prevTime = millis();
       eventPage = (eventPage + 1) % (((events.length - 1) / cutOff) + 1);
     }
     textFont(lightFont, smSize);
     text(eventPage + 1 + "/" + (events.length) / cutOff, 20, height - 20);
   }
}
  public void settings() {  fullScreen(); }
}

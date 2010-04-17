// EchoNestBasics.pde
// echonestp5
// 
// The Echo Nest API Processing Wrapper
// http://the.echonest.com/
// Copyright (C) 2010 melka - Kamel Makhloufi
// http://melka.one.free.fr/blog/

// Includes code by Vlad Patryshev
// http://www.myjavatools.com/

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.


// This is just a project to show how to use the library.
// For more infos about the different classes, check the JavaDoc
// in <sketchbook>/libraries/processingp5/reference/index.html



// !! IMPORTANT NOTICE !!
// I did not like the fact that the app was hanging while downloading
// datas, so the Echo Nest library uses a different thread to make
// the requests on The Echo Nest Server. It automatically downloads 
// all the data and sends a notification to your Processing sketch to
// inform it that everything is ready.
// I also added some code to save the ENTrack objects to disk. For now,
// it works only on Mac OS X (haven't tried on the other platforms but
// I'm sure it won't work, don't know what the error will be though).
// The files are saved in /Library/Application Support/Processing/EchoNest/
// They are named <md5 of the file>.enp5 and are the ENTrack objects already
// parsed etc...
// Just call the analysis as usual, if the file is already saved on disk,
// it'll use it instead of going online and retrieving the data from
// the EchoNest server. If you need a new analysis, just delete the .enp5
// file from disk.
// Finally, I think I solved for good the CODE 11 & CODE 5 errors some of us
// were having. It was due to the MD5 computation giving wrong hashes. I've
// just finished uploading a batch of files to the EchoNest server and I had
// no error while I had some problematic files with the old code.

import com.melka.echonest.*;
import com.melka.echonest.ENTrack.*;

// Enter here your Echo Nest API Key
String APIKey = "<your api key>";
// This is an mp3 file to upload.
// Put the file in your sketch's data folder
String fileToUpload = "<audio>.mp3";

EchoNest nest;
ENTrack t;

boolean loaded = false;

// VARIABLES INIT 
int nbLines = 6;
float canvasWidth = 800;
float canvasHeight = 600;

float imgWidth = canvasWidth;
float imgHeight = canvasHeight;

float lineHeight = canvasHeight/nbLines;
float lineDuration, secondLength;
float startTime, endTime;

PFont font;
String waitPoints = "";
int s = 0;

void setup() {
  size((int)imgWidth,(int)imgHeight);
  background(255);
  font = createFont("", 32);
  textFont(font,20);
  EchoNest nest = new EchoNest(this,APIKey,fileToUpload, 3);
  // A modification in the analysis from The Echo Nest might bring
  // ERROR CODE 11 with some mp3 files. The error may also be due to
  // the MD5 computation code used in a previous version of the lib.
  // If it's your case, change the previous line with this one.
  // EchoNest nest = new EchoNest(this,APIKey,fileToUpload,1);
  // The last parameter is the analysis_version param. Default = 3.
}

void draw() {
  background(255);
  if (loaded) {
    drawSoundscape();
  } else {
    fill(0);
    if (millis()/1000 >= s+1) {
      s++;
      waitPoints += ".";
    }
    text("LOADING DATA"+waitPoints,10,25);
  }
}

// This function is the notification sent by the library
// after it finished loading all the datas for your track.
void ENTrackLoaded(ENTrack track) {
  t = track;
  loaded = true;
}

// This function is a new notification sent by the library
// to warn you that a file failed.
void ENTrackFailed(ENTrack track) {
  System.err.println("ANALYSIS FAILED");
}

void drawSoundscape() {
  lineDuration = t.duration.duration/nbLines;
  secondLength = canvasWidth/lineDuration;
  
  strokeCap(SQUARE);
  smooth();
  noStroke();
  fill(255);
  for (int i=0;i<t.segments.length;i++) {
    drawSegment(i);
  }
}

void drawSegment(int i) {
  float start = t.segments[i].start*secondLength;
  float end = start+t.segments[i].duration*secondLength;
  float ln = 1;
  while (start>canvasWidth*ln) {
    ln+=1;
  }

  if (i%2 != 0) {
    fill(245);
  } else {
    fill(255);
  }

  start = start-((ln-1)*canvasWidth);
  end = end-((ln-1)*canvasWidth);
  noStroke();
  rect(start,(ln-1)*lineHeight,end-start,lineHeight);
  
  for (int j=0; j<12;j++) {
    float val = t.segments[i].pitches[j];
    colorMode(HSB,12);
    stroke(j,8,12,6);
    colorMode(RGB,255);
    strokeWeight(0.5); 
    float h = (ln*lineHeight-val*lineHeight);
    line(start,h,end,h);
  }

  noStroke();
  float dX = 0;
  if (end > canvasWidth) {
    dX = end - canvasWidth;
    end = canvasWidth;
    rect(0,ln*lineHeight,dX,lineHeight);
    for (int j=0; j<12;j++) {
      float val = t.segments[i].pitches[j];
      colorMode(HSB,12);
      stroke(j,8,12,6);
      colorMode(RGB,255);
      strokeWeight(0.5); 
      float h = (ln+1)*lineHeight-val*lineHeight;
      line(0,h,dX,h);
    }
  }
}


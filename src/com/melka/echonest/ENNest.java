//
// ENNest.java
// echonestp5
// 
// The Echo Nest API Processing Wrapper
// http://the.echonest.com/
// Copyright (C) 2009 melka - Kamel Makhloufi
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

package com.melka.echonest;

import processing.core.PApplet;
import processing.xml.*;
import com.myjavatools.web.ClientHttpRequest;
import java.security.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This library is a wrapper that simplify the use of the Echo Nest API.
 * 
 * The Echo Nest API allows analysis of music files based on the "Musical Brain".
 * 
 * From The Echo Nest web site : "The Echo Nest's APIs are based on the "Musical Brain",
 * a one-of-a-kind machine learning platform that actually listens to music and reads 
 * about music from every corner of the web. We're using the Musical Brain to power enhanced 
 * music search, recommendation and interactivity for online music services."
 * 
 * More info : http://the.echonest.com
 * 
 * @example EchoNestBasics 
 * @author melka 
 */

public class ENNest {

	/**
	 * Echo Nest Return Code : -1
	 * An unknown error has occurred.
	 */	
	public static final int UNKNOWN_ERROR = -1;
	/**
	 * Echo Nest Return Code : 0
	 * Success.
	 */	
	public static final int SUCCESS = 0;
	/**
	 * Echo Nest Return Code : 1
	 * The API Key you entered is invalid.
	 */	
	public static final int INVALID_KEY = 1;
	/**
	 * Echo Nest Return Code : 2
	 * Your API Key do not allow you to call this method.
	 */	
	public static final int NOT_ALLOWED = 2;
	/**
	 * Echo Nest Return Code : 3
	 * Too many requests made.
	 */	
	public static final int LIMIT_EXCEEDED = 3;
	/**
	 * Echo Nest Return Code : 4
	 * A parameter is missing.
	 */	
	public static final int MISSING_PARAM = 4;
	/**
	 * Echo Nest Return Code : 5
	 * An invalid parameter has been used.
	 */	
	public static final int INVALID_PARAM = 5;
	
	PApplet parent;
	
	/**
	 * Base URL for the Echo Nest API
	 */
	private String baseUrl;
	/**
	 * API Key to use for making the requests.
	 */
	private String apiKey;
	/**
	 * An artist ID, if know.
	 */	
	private String artistId;
	/**
	 * An audio file MD5 hash.
	 */
	private String trackMD5;
	/**
	 * Path to the file to analysis
	 */
	private String filePath; // A Track ID if known. Example Track ID : music://id.echonest.com/~/TR/TRLFPPE11C3F10749F

	/**
	 * Analysis version
	 */
	private int analysis_version;
	
	private ENTrack track;
	
	public final String VERSION = "0.1.2";

	public ENNest() {
	}
	/**
	 * Initialization of the EchoNest object.
	 * 
	 * @param parent The processing sketch. Always use "this" (ie:nest.init(this,ApiKey)).
	 * @param ApiKey Your Echo Nest API key. http://developer.echonest.com/account/register/ to register.
	 * @param TrackMD5 Optional, a file's MD5 hash if it has already been analyzed.
	 * @param Version, version of the analysis to use.
	 * @return An EchoNest object you'll use to upload a file.
	 */
	
	public ENNest init(PApplet parent, String ApiKey, String TrackMD5, int Version) {
		System.out.println("* The Echo Nest Processing Wrapper Library *");
		System.out.println("* (c) melka 2010 // Licence : GPLv3        *");
		this.parent = parent;
		setBaseUrl("http://developer.echonest.com/api/");
		setApiKey(ApiKey);
		setArtistId(null);
		setTrackMD5(TrackMD5);
		setAnalysisVersion(Version);
		if (getApiKey() == "") {
			System.out.println(">> WARNING");
			System.out.println(">> API KEY NOT SET");
			System.out.println(">> YOU CANNOT DO ANYTHING WITHOUT THE KEY");
			System.out.println(">> PLEASE GET SURE TO USE <EchoNest>.validateApiKey() AFTER SETTING IT");
		} else {
			validateApiKey();
		}
		parent.registerDispose(this);
		return this;
	}
	
	public ENNest init (PApplet parent, String ApiKey, String TrackMD5) {
		return init(parent,ApiKey,"",3);
	}
	
	public ENNest init (PApplet parent, String ApiKey) {
		return init(parent,ApiKey,"");
	}
	
	public ENNest init (PApplet parent) {
		return init(parent,"");
	}
	
	
	/**
	 * Tests the API Key against the server to get sure the key can be used.
	 * @return boolean
	 */
	public boolean validateApiKey () {
		XMLElement data = new XMLElement(parent,baseUrl+"get_duration?api_key="+apiKey+"&version=3&analysis_version=3");
		if (Integer.parseInt(data.getChild("status").getChild("code").getContent()) == INVALID_KEY) {
			System.err.println(">> INVALID KEY");
			return false;
		} else {
			System.out.println(">> VALID API KEY");
			return true;
		}		
	}

	
	/**
	 * Upload a track to The Echo Nest's analyzer for analysis and later retrieval of track information.
	 * It first computes an MD5 hash of the file to check if it exists on the Echo Nest's server.
	 * If file exists, returns true.
	 * If not, tries to upload the file to the server then returns true if analysis is successful.
	 * If an error occurs, returns false.
	 * 
	 * @param filePath Absolute or relative path of the file to upload.
	 * @return A boolean, true is successful, false if an error occurs. 
	 */
	public boolean uploadFile(String filePath) {
		String fullPath = parent.dataPath(filePath);
		setFilePath(fullPath);
		try {
			String h = fileHash(getFilePath());
			setTrackMD5(h);
			XMLElement data = new XMLElement(parent,baseUrl+"get_duration?api_key="+apiKey+"&md5="+trackMD5+"&version=3&analysis_version=3");
			if (Integer.parseInt(data.getChild("status").getChild("code").getContent()) == INVALID_PARAM) {
				System.out.println(">> UNKNOWN FILE, BEGINNING UPLOAD");
				return uploadData();
			} else {
				System.out.println(">> FILE ALREADY ANALYZED");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Upload the file data (byte[]) to the Echo Nest's server for analysis.
	 * @return A boolean, true if successful, false if an error occurred.
	 */
	private boolean uploadData() {
		File f = new File(getFilePath());
		try {
			System.out.println(">> Starting Upload");
			ClientHttpRequest http = new ClientHttpRequest(baseUrl+"upload");
			http.setParameter("api_key",apiKey);
			http.setParameter("version","3");
			http.setParameter("analysis_version","3");
			http.setParameter("file",f);
			http.setParameter("wait","Y");
			InputStream is = http.post();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			XMLElement data = new XMLElement(sb.toString());
			if (Integer.parseInt(data.getChild("status").getChild("code").getContent()) == SUCCESS) {
				System.out.println(">> UPLOAD COMPLETE");
				return true;
			} else {
				System.err.println(">> ERROR. CODE "+Integer.parseInt(data.getChild("status").getChild("code").getContent()));
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Compute an MD5 hash for a given file.
	 * 
	 * Changed this method, was giving bad results. ie : Bradley Strider / Bradley's Beat.
	 * MD5 of file is 004f22f350579edab0a965cb88a472d1, the old method was getting rid of the
	 * leading 00, giving a string of length 30 instead of 32. This will solve some problems
	 * for some file (always requiring upload even though it was already analyzed)
	 * 
	 * @param path Absolute path of the file to hash.
	 * @return Computed MD5 hash.
	 * @throws Exception
	 */
	public static String fileHash(String path) throws Exception {
		File f = new File(path);
		FileInputStream fs = new FileInputStream(f);
		byte[] b = new byte[(int) f.length()];
		fs.read(b);
				
		MessageDigest h = MessageDigest.getInstance("MD5");
		h.update(b);
		byte messageDigest[] = h.digest();
		
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < messageDigest.length; i++)
        {
            String hex = Integer.toHexString(0xFF & messageDigest[i]);
            if (hex.length() == 1)
            {
                hexString.append('0');
            }
            hexString.append(hex);
        }
		String md5val = hexString.toString();
		return md5val;
	}
	
	/**
	 * Initializes an ENTrack object for analysis retrieval.
	 * Use this functions once uploadFile has returned true.
	 * 
	 * @return An ENTrack object.
	 */
	public ENTrack initTrack() {
		track = new ENTrack(parent, getApiKey(), getTrackMD5());
		track.setArtistId(getArtistId());
		track.setBaseUrl(getBaseUrl());
		track.setFilePath(getFilePath());
		System.out.println(">> TRACK READY");
		return track;
	}
	
	/**
	 * Return the version of the library.
	 * 
	 * @return Version of the library
	 */
	public String version() {
		return VERSION;
	}
	
	public void dispose() {
		// anything in here will be called automatically when 
		// the parent applet shuts down. for instance, this might
		// shut down a thread used by this library.
		// note that this currently has issues, see bug #183
		// http://dev.processing.org/bugs/show_bug.cgi?id=183
	}
	
	
	// GETTERS/SETTERS
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getApiKey() {
		return apiKey;
	}
	
	public void setArtistId(String artistId) {
		this.artistId = artistId;
	}
	
	public String getArtistId() {
		return artistId;
	}

	public void setTrackMD5(String trackMD5) {
		this.trackMD5 = trackMD5;
	}

	public String getTrackMD5() {
		return trackMD5;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public void setAnalysisVersion(int version) {
		this.analysis_version = version;
	}
	
	public int getAnalysisVersion() {
		return analysis_version;
	}
}

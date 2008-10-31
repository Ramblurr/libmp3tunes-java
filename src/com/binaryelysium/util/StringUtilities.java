/**
 * Copyright (c) 2008, Janni Kovacs
 * All rights reserved.
 *
 * Redistribution and use of this software in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:

 * * Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the
 * following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * 
 * * Neither the name of Janni Kovacs nor the names of its
 * contributors may be used to endorse or promote products
 * derived from this software without specific prior
 * written permission of Janni Kovacs.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package com.binaryelysium.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utilitiy class with methods to calculate an md5 hash and to encode URLs.
 * Originally from Java package net.roarsoftware.util
 * @author Janni Kovacs <jannikovacs@gmail.com>
 * @author Casey Link <unnamedrambler@gmail.com>
 */
public class StringUtilities {

	private static MessageDigest digest;

	public static String md5(String s) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(s.getBytes("UTF-8"));
			BigInteger number = new BigInteger(1, messageDigest);
			String md5 = number.toString(16);
			while (md5.length() < 32)
				md5 = "0" + md5;
			return md5;
		} catch (NoSuchAlgorithmException e) {
			//TODO error handle
		} catch (UnsupportedEncodingException e) {
			//TODO error handle
		}
		return null;
		
	}

	public static String encode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// utf-8 always available
		}
		return null;
	}

	public static String decode(String s) {
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// utf-8 always available
		}
		return null;
	}

	/**
	 * Creates a Map out of an array with Strings.
	 * 
	 * @param strings
	 *            input strings, key-value alternating
	 * @return a parameter map
	 */
	public static Map<String, String> map(String... strings) {
		if (strings.length % 2 != 0)
			throw new IllegalArgumentException("strings.length % 2 != 0");
		Map<String, String> mp = new HashMap<String, String>();
		for (int i = 0; i < strings.length; i += 2) {
			mp.put(strings[i], strings[i + 1]);
		}
		return mp;
	}
}

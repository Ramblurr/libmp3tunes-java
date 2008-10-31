package com.binaryelysium.mp3tunes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

public class AccountData {
	String mEmail;
	String mNickName;
	String mFirstName;
	String mLastName;
	long mMaxLockerSize;
	long mCurrentLockerSize;
	long mMaxFileSize;
	String mLockerType;
	boolean mExpired;
	Collection<Subscription> mSubscriptions = new ArrayList<Subscription>();

	public class Subscription {
		public String mName;
		public String mDescription;
		public String mActivateDate;
		public String mExpireDate;

		private Subscription() {
		}
	}

	private AccountData() {
	}

	private Subscription subscriptionFromResult(Result result) {
		try {
			if (!result.getParser().getName().equals("item"))
				return null;

			Subscription s = new Subscription();
			int event = result.getParser().nextTag();
			boolean loop = true;
			while (loop) {
				String name = result.getParser().getName();
				switch (event) {
				case XmlPullParser.START_TAG:
					if (name.equals("name")) {
						s.mName = result.getParser().nextText();
					} else if (name.equals("expireDate")) {
						s.mExpireDate = result.getParser().nextText();
					} else if (name.equals("description")) {
						s.mDescription = result.getParser().nextText();
					} else if (name.equals("activateDate")) {
						s.mActivateDate = result.getParser().nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					if (name.equals("item"))
						loop = false;
					break;
				}
				event = result.getParser().next();
			}
			return s;
		} catch (Exception e) {
		}
		return null;
	}

	public static AccountData accountDataFromResult(Result result) {
		try {
			result.getParser().nextTag();
			if (!result.getParser().getName().equals("user"))
				return null;

			AccountData d = new AccountData();
			int event = result.getParser().nextTag();
			boolean loop = true;
			while (loop) {
				String name = result.getParser().getName();
				switch (event) {
				case XmlPullParser.START_TAG:
					if (name.equals("email")) {
						d.mEmail = result.getParser().nextText();
					} else if (name.equals("nickName")) {
						d.mNickName = result.getParser().nextText();
					} else if (name.equals("firstName")) {
						d.mFirstName = result.getParser().nextText();
					} else if (name.equals("lastName")) {
						d.mLastName = result.getParser().nextText();
					} else if (name.equals("maxLockerSize")) {
						d.mMaxLockerSize = Long.parseLong(result.getParser()
								.nextText());
					} else if (name.equals("currentLockerSize")) {
						d.mCurrentLockerSize = Long.parseLong(result
								.getParser().nextText());
					} else if (name.equals("maxFileSize")) {
						d.mMaxFileSize = Long.parseLong(result.getParser()
								.nextText());
					} else if (name.equals("lockerType")) {
						d.mLockerType = result.getParser().nextText();
					} else if (name.equals("expired")) {
						d.mExpired = result.getParser().nextText().equals("1");
					} else if (name.equals("item")) {
						d.mSubscriptions.add(d.subscriptionFromResult(result));
					}
					break;
				case XmlPullParser.END_TAG:
					if (name.equals("user"))
						loop = false;
					break;
				}
				event = result.getParser().next();
			}
			return d;
		} catch (Exception e) {
		}
		return null;
	}

	public static AccountData getAccountData(Session session) {
		String m = "accountData";
		Map<String, String> params = new HashMap<String, String>();
		Result result = Caller.getInstance().call(m, session, params);
		return AccountData.accountDataFromResult(result);
	}
}

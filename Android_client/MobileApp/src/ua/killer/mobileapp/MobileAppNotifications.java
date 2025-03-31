package ua.killer.mobileapp;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ua.killer.mobileapp.application.Constants;
import ua.killer.mobileapp.application.Utils;
import ua.killer.mobileapp.cache.BitmapHttpLoaderCache;
import ua.killer.mobileapp.components.Photo;
import ua.killer.mobileapp.configs.Configs;
import ua.killer.mobileapp.listeners.BitmapHttpLoaderListener;
import ua.killer.mobileapp.listeners.NotificationsCompleteListener;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MobileAppNotifications {
	private JSONObject json, counters, contents;
	private NotificationsCompleteListener listener;
	private Configs configs;
	private Context mContext;
	private Editor configsEditor;

	public static final int NOTIF_MAIL = 100,
			NOTIF_DISCUSSIONS = 2,
			NOTIF_NOTIFICATION = 3,
			NOTIF_TAPE = 4,
			NOTIF_FRIENDS = 5,
			NOTIF_GUESTS = 6;
	
	public MobileAppNotifications(Context _mContext) {
		this.mContext = _mContext;
		configs = new Configs(mContext);
	}
	
	public void setNotificationsCompleteListener(NotificationsCompleteListener _listener) {
		this.listener = _listener;
	}

	public void handleJSON(JSONObject json) {
		this.setJson(json);
		
		try {
			counters = json.getJSONObject("counters");
			contents = json.getJSONObject("contents");
		} catch (JSONException e) {
//			e.printStackTrace();
		}
		
		configsEditor = configs.getEditor();
		try {
			
			// сообщения
			final int countMail = counters.getInt("mail");
			if (countMail > 0 && configs.showMail) {
				JSONArray mailContent = contents.getJSONArray("mail");
				for (int i = 0; i < mailContent.length(); i++) {
					final JSONObject message = mailContent.getJSONObject(i);
					final JSONObject messageUser = message.getJSONObject("user");
					final JSONObject messageUserAvatar = messageUser.getJSONObject("avatar");
					final int messageUserID = messageUser.getInt("id");
					final int messageID = message.getInt("id");
					if (messageID <= configs.lastMessageID)
						continue;
					
					Photo avatar = Photo.parse(messageUserAvatar);
					Log.v("myLogs", "url: " + avatar.getUrl());
					BitmapHttpLoaderCache bmLoader = new BitmapHttpLoaderCache(avatar.getHash(), avatar.getUrl(), mContext, new BitmapHttpLoaderListener() {
						
						@Override
						public void onLoadStart() {}
		
						@Override
						public void onLoadFinish(Bitmap bitmap) {
							PendingIntent contentIntent = PendingIntent.getActivity(mContext, 1, new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(String.format(Constants.URL_MAIL_KONT, messageUserID))), 0);
							try {
								String barTitle = "", notifTitle = "", notifText = "";
								barTitle = "Сообщение от " + messageUser.getString("nick");
								notifTitle = messageUser.getString("nick");
								notifText = message.getString("message");
								sendNotif(R.drawable.mail, bitmap, barTitle, notifTitle, notifText, contentIntent, NOTIF_MAIL + messageUser.getInt("id"));
								configs.lastMessageID = Math.max(configs.lastMessageID, messageID);
							} catch (JSONException e) {
							}
						}
						
					});
					try {
						bmLoader.load();
					} catch (IOException e) {
					}
				}
					
			}
			configsEditor.putInt("lastMessageID", configs.lastMessageID);
			
			int countDiscussions = counters.getInt("discussions");
			if (countDiscussions > configs.lastCountDiscussions && configs.showDiscussions) {
				PendingIntent contentIntent = PendingIntent.getActivity(mContext, 1, new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(Constants.URL_DISCUSSIONS)), 0);
				String barTitle = countDiscussions + " " + Utils.strDeclension(countDiscussions, "новое обсуждение", "новых обсуждения", "новых обсуждений");
				String notifTitle = "Обсуждения";
				String notifText = barTitle;
				sendNotif(R.drawable.discussions, barTitle, notifTitle, notifText, contentIntent, NOTIF_DISCUSSIONS);
			}
			configsEditor.putInt("lastCountDiscussions", countDiscussions);
			
			int countNotification = counters.getInt("notification");
			if (countNotification > configs.lastCountNotification && configs.showNotification) {
				PendingIntent contentIntent = PendingIntent.getActivity(mContext, 1, new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(Constants.URL_NOTIFICATION)), 0);
				String barTitle = countNotification + " " + Utils.strDeclension(countNotification, "новое уведомление", "новых уведомления", "новых уведомлений");
				String notifTitle = "Уведомления";
				String notifText = barTitle;
				sendNotif(R.drawable.notification, barTitle, notifTitle, notifText, contentIntent, NOTIF_NOTIFICATION);
			}
			configsEditor.putInt("lastCountNotification", countNotification);
			
			int countTape = counters.getInt("tape");
			if (countTape > configs.lastCountTape && configs.showTape) {
				PendingIntent contentIntent = PendingIntent.getActivity(mContext, 1, new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(Constants.URL_TAPE)), 0);
				String barTitle = countTape + " " + Utils.strDeclension(countTape, "новое событие", "новых события", "новых событий") + " в ленте";
				String notifTitle = "Лента";
				String notifText = barTitle;
				sendNotif(R.drawable.tape, barTitle, notifTitle, notifText, contentIntent, NOTIF_TAPE);
			}
			configsEditor.putInt("lastCountTape", countTape);
			
			final int countFriends = counters.getInt("friends");
			if (countFriends > 0 && configs.showFriends) {
				JSONArray friendsContent = contents.getJSONArray("friends");

				for (int i = 0; i < friendsContent.length(); i++) {
					final JSONObject friend = friendsContent.getJSONObject(i);
					final JSONObject friendUser = friend.getJSONObject("user");
					final JSONObject friendUserAvatar = friendUser.getJSONObject("avatar");
					final int friendID = friend.getInt("id");
					if (friendID <= configs.lastNewFriendID)
						continue;
					
					Photo avatar = Photo.parse(friendUserAvatar);
					
					BitmapHttpLoaderCache bmLoader = new BitmapHttpLoaderCache(avatar.getHash(), avatar.getUrl(), mContext, new BitmapHttpLoaderListener() {
		
						@Override
						public void onLoadStart() {}
		
						@Override
						public void onLoadFinish(Bitmap bitmap) {
							try {
								PendingIntent contentIntent = PendingIntent.getActivity(mContext, 1, new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(Constants.URL_NEW_FRIENDS)), 0);
								String barTitle = "", notifTitle = "", notifText = "";
								barTitle = friendUser.getString("nick") + " хочет стать другом";
								notifTitle = friendUser.getString("nick");
								notifText = "Хочет стать Вашим другом";
								sendNotif(R.drawable.friends, bitmap, barTitle, notifTitle, notifText, contentIntent, NOTIF_FRIENDS + friendUser.getInt("id"));
								configs.lastNewFriendID = Math.max(configs.lastNewFriendID, friendID);
							} catch (JSONException e) {
							}
						}
						
					});
					try {
						bmLoader.load();
					} catch (IOException e) {
					}
				}
			}
			configsEditor.putInt("lastNewFriendID", configs.lastNewFriendID);
			
			final int countGuests = counters.getInt("guests");
			if (countGuests > 0 && configs.showGuests) {
				JSONArray guestsContent = contents.getJSONArray("guests");
				
				for (int i = 0; i < guestsContent.length(); i++) {
					final JSONObject guest = guestsContent.getJSONObject(i);
					final JSONObject guestUser = guest.getJSONObject("user");
					final JSONObject guestUserAvatar = guestUser.getJSONObject("avatar");
					final int guestID = guest.getInt("id");
					if (guestID <= configs.lastNewGuestID)
						continue;
					
					Photo avatar = Photo.parse(guestUserAvatar);
					BitmapHttpLoaderCache bmLoader = new BitmapHttpLoaderCache(avatar.getHash(), avatar.getUrl(), mContext, new BitmapHttpLoaderListener() {
		
						@Override
						public void onLoadStart() {}
		
						@Override
						public void onLoadFinish(Bitmap bitmap) {
							try {
								PendingIntent contentIntent = PendingIntent.getActivity(mContext, 1, new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(Constants.URL_NEW_GUESTS)), 0);
								String barTitle = "", notifTitle = "", notifText = "";
								barTitle = guestUser.getString("nick") + " посетил Вашу страничку";
								notifTitle = guestUser.getString("nick");
								notifText = "Посетил Вашу страничку";
								sendNotif(R.drawable.guests, bitmap, barTitle, notifTitle, notifText, contentIntent, NOTIF_GUESTS);
								configs.lastNewGuestID = Math.max(configs.lastNewGuestID, guestID);
							} catch (JSONException e) {
							}
						}
						
					});
					try {
						bmLoader.load();
					} catch (IOException e) {
					}
				}
			}
			configsEditor.putInt("lastNewGuestID", configs.lastNewGuestID);
		} catch (Exception e) {
//			e.printStackTrace();
			Log.v("myLogs", e.toString());
		}
		
		configsEditor.commit();
		
		if (listener != null)
			listener.onComplete();
	}
	
	public void sendNotif(int icon, String barText, String title, String text, PendingIntent pIntent, int notifId) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
		builder.setContentText(text).setContentTitle(title)
		.setTicker(barText)
		.setSmallIcon(icon)
		.setWhen(System.currentTimeMillis())
		.setContentIntent(pIntent);

		if (configs.playSound)
			builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		
		if (configs.playVibration)
			builder.setVibrate(new long[] {0, 1000});
		
		builder.setAutoCancel(true);
		
		NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(notifId, builder.build());
	}
	
	public void sendNotif(int icon, Bitmap largeIcon, String barText, String title, String text, PendingIntent pIntent, int notifId) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
		builder.setContentText(text).setContentTitle(title)
		.setTicker(barText)
		.setLargeIcon(largeIcon)
		.setSmallIcon(icon)
		.setWhen(System.currentTimeMillis())
		.setContentIntent(pIntent);
		
		if (configs.playSound)
			builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		
		if (configs.playVibration)
			builder.setVibrate(new long[] {0, 1000});
		
		builder.setAutoCancel(true);
		
		NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(notifId, builder.build());
	}

	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}
}

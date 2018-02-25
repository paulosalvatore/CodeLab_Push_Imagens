package paulosalvatore.com.br.codelab_push_imagens;

/**
 * Created by paulo on 25/02/2018.
 */

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

	private static final String TAG = "MyFMService";
	private NotificationManager notificationManager;
	private final int NOTIFY_ID = 1000;

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		// Handle data payload of FCM messages.
		RemoteMessage.Notification notification = remoteMessage.getNotification();
		Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
		Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());
		Log.d(TAG, "FCM Notification Message: " + notification);

		if (notification != null) {
			String titulo = notification.getTitle();
			String body = notification.getBody();
			Map<String, String> data = remoteMessage.getData();

			Log.d(TAG, "FCM Notification Title: " + titulo);
			Log.d(TAG, "FCM Notification Body: " + body);

			criarNotificacao(notification, data);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void criarNotificacao(RemoteMessage.Notification remoteNotification, Map<String, String> data) {
		String titulo = remoteNotification.getTitle();

		if (titulo == null) {
			titulo = getResources().getString(R.string.app_name);
		}

		String conteudo = remoteNotification.getBody();

		/*
		 * Parâmetro 'data' pode receber diversas informações personalizadas
		 * Para receber alguma informação, basta usar o método 'get' passando
		 * a chave desejada como argumento e receberá a informação contida
		 * no valor.
		 * Exemplo:
		 * String atributo = data.get("atributo");
		 */

		// O ID da Notificação serve para diferenciar notificações.
		// Enviar o mesmo ID sobrepõe alguma que estiver ativa.
		int notificacaoId = NOTIFY_ID;

		if (data.get("id") != null) {
			notificacaoId = Integer.valueOf(data.get("id"));
		}

		Intent intent;
		PendingIntent pendingIntent;
		NotificationCompat.Builder builder;

		if (notificationManager == null) {
			notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		}

		// Sistema de notificações foi melhorado a partir do Android Oreo (API 26)
		// Passa a ter algumas features adicionais como o sistema de canais, por exemplo
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			String name = getResources().getString(R.string.default_notification_channel_id); // The user-visible name of the channel.
			String id = "CodeLabPushImagens 1";
			String description = "CodeLab Push Imagens - Channel"; // The user-visible description of the channel.

			NotificationChannel mChannel = notificationManager.getNotificationChannel(id);

			if (mChannel == null) {
				int importance = NotificationManager.IMPORTANCE_HIGH;
				mChannel = new NotificationChannel(id, name, importance);
				mChannel.setDescription(description);
				mChannel.enableVibration(true);
				mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
				notificationManager.createNotificationChannel(mChannel);
			}

			builder = new NotificationCompat.Builder(this, id);

			intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

			builder.setContentTitle(titulo) // Necessário
					.setSmallIcon(android.R.drawable.ic_popup_reminder) // Necessário
					.setContentText(conteudo) // Necessário
					.setDefaults(Notification.DEFAULT_ALL)
					.setAutoCancel(true)
					.setContentIntent(pendingIntent)
					.setTicker(titulo)
					.setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
		} else {
			builder = new NotificationCompat.Builder(this);

			intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

			builder.setContentTitle(titulo) // Necessário
					.setSmallIcon(android.R.drawable.ic_popup_reminder) // Necessário
					.setContentText(conteudo) // Necessário
					.setDefaults(Notification.DEFAULT_ALL)
					.setAutoCancel(true)
					.setContentIntent(pendingIntent)
					.setTicker(titulo)
					.setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
					.setPriority(Notification.PRIORITY_HIGH);
		}

		Notification notification = builder.build();
		notificationManager.notify(notificacaoId, notification);
	}
}

package paulosalvatore.com.br.codelab_push_imagens;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

	private NotificationManager notificationManager;
	private final int NOTIFY_ID = 1000;
	private final int RESULT_LOAD_IMAGE = 1;
	private final int PERMISSION_REQUEST_READ_STORAGE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button buttonLoadImage = findViewById(R.id.btCarregarImagem);
		buttonLoadImage.setOnClickListener(new View.OnClickListener() {

			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void onClick(View arg0) {
				if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions(MainActivity.this,
							new String[]{
									Manifest.permission.READ_EXTERNAL_STORAGE
							},
							PERMISSION_REQUEST_READ_STORAGE);
				} else {
					carregarImagem();
				}
			}
		});
	}

	public void pushNotification(View view) {
		criarNotificacao("Título", "Conteúdo");
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void criarNotificacao(String titulo, String conteudo) {
		Intent intent;
		PendingIntent pendingIntent;
		NotificationCompat.Builder builder;

		if (notificationManager == null) {
			notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		}

		// Sistema de notificações foi modificado a partir do Android Oreo (API 26)
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

			builder.setContentTitle(titulo) // required
					.setSmallIcon(android.R.drawable.ic_popup_reminder) // required
					.setContentText(conteudo) // required
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

			builder.setContentTitle(titulo) // required
					.setSmallIcon(android.R.drawable.ic_popup_reminder) // required
					.setContentText(conteudo) // required
					.setDefaults(Notification.DEFAULT_ALL)
					.setAutoCancel(true)
					.setContentIntent(pendingIntent)
					.setTicker(titulo)
					.setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
					.setPriority(Notification.PRIORITY_HIGH);
		}

		Notification notification = builder.build();
		notificationManager.notify(NOTIFY_ID, notification);
	}

	private void carregarImagem() {
		Intent i = new Intent(
				Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = {MediaStore.Images.Media.DATA};

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			ImageView imageView = findViewById(R.id.mImageView);
			imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_REQUEST_READ_STORAGE: {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.d("Permissão", "Permissão concedida.");
					carregarImagem();
				} else {
					Log.d("Permissão", "Permissão negada.");
				}
			}
		}
	}
}

package paulosalvatore.com.br.codelab_push_imagens;

/**
 * Created by paulo on 25/02/2018.
 */

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

	private static final String TAG = "MyFirebaseIIDService";
	private static final String FRIENDLY_ENGAGE_TOPIC = "friendly_engage";

	/**
	 * The Application's current Instance ID token is no longer valid and thus a new one must be requested.
	 */
	@Override
	public void onTokenRefresh() {
		// Caso precise realizar alguma ação com o token (gravar em algum lugar, por exemplo)
		// esse é o lugar exato para fazer isso.
		String token = FirebaseInstanceId.getInstance().getToken();
		Log.d(TAG, "FCM Token: " + token);

		// Uma vez que o token é gerado, escrevemos ele no tópico
		FirebaseMessaging.getInstance().subscribeToTopic(FRIENDLY_ENGAGE_TOPIC);
	}

	// Pegar o Token do FMS mais atualizado
	// FirebaseInstanceId.getInstance().getToken()
	// Esse método retorna null se o token ainda não tiver sido gerado.
	/*
	 * Esse token pode mudar quando:
	 * o aplicativo exclui o código da instância;
	 * o aplicativo é restaurado em um novo dispositivo;
	 * o usuário desinstala/reinstala o app;
	 * o usuário limpa os dados do app.
	 */
}
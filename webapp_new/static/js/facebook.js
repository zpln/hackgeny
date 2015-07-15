window.fbAsyncInit = function() {
	FB.init({
		appId			:	'1455321434789277',
		xfbml			:	true,
		version			:	'v2.3',
		status		 	:	true
	});

	FB.Event.subscribe('auth.statusChange', function(response) {
		// TODO: dont display button, or even hall page, until this function is run. Because maybe,
		// if the user is already logged in, we want to go directly to main pgae
		if (response.status === 'connected') {
			if (response.authResponse) {
				login_success(response.authResponse);
			}
		} else {
			console.log('User not already logged in, display login button');
		}
	});

};

(function(d, s, id){
	var js, fjs = d.getElementsByTagName(s)[0];
	if (d.getElementById(id)) {return;}
	js = d.createElement(s); js.id = id;
	js.src = "//connect.facebook.net/en_US/sdk.js";
	fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));

function login_success(authResponse) {
	console.log('Welcome!	Fetching your information.... ');
	FB.api('/me', function(response) {
		console.log('Good to see you, ' + response.name + '.');

		if (fb_login_dispatch != "undefined") {
			fb_login_dispatch(response);
		}


	});
}


function fb_login() {
	FB.getLoginStatus(function(response) {
	if (response.status === 'connected') {
		if (response.authResponse) {
			login_success(response.authResponse);
		}
	} else if (response.status === 'not_authorized') {
		// TODO: what to do in this case?
		console.log('unauthorized');
	} else {
		FB.login(function(response) {
		if (response.authResponse) {
			// Do nothing, the auth.statusChange event dispatcher will handle that
		} else {
			console.log('User cancelled login or did not fully authorize.');
		}
		});
	}
	});
}
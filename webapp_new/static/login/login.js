function fb_login_dispatch(response) {
	$.post(db_server_url + "fb_login", {fb_user_id: response.id});
}
function fb_login_dispatch(response) {
	$.post("/fb_login", {fb_user_id: response.id},
		function(data) {
			alert(data);
		}
	);
}
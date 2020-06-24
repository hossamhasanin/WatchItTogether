
jwplayer().on("play", function() {
 //You can call JavaScriptInterface here.
 App.showToast(jwplayer().getPosition());
});
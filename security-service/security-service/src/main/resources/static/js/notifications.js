$(document).ready(function () {
	let username = $("#username").val();
  var eventSource = new EventSource("/notifications/subscribe/" + username);

  eventSource.addEventListener("newMessage", function (event) {
    var message = JSON.parse(event.data);

    var senderElement = $("#" + message.senderRecipient.sender);
    var image = message.image;
    var file = message.file;
    var sender = message.senderRecipient.sender;
    var recipient = message.senderRecipient.recipient;
    var fileName = message.text;

    if (senderElement.attr("data-clicked") === "true") {
    	$("#messages-list").append(
             '<li class="recipientMessage">' + message.text + "</li>"
        );
    } else {
      senderElement.addClass("new-message");
    }

    $("#messages").scrollTop($("#messages")[0].scrollHeight);

    eventSource.addEventListener("error", function (event) {
      console.log("Error:", event.currentTarget.readyState);
      if (event.currentTarget.readyState == EventSource.CLOSED) {
      } else {
        eventSource.close();
      }
    });

    window.onbeforeunload = function () {
      eventSource.close();
    };
  });

    eventSource.addEventListener("missedMessages", function (event) {
      var notifications = JSON.parse(event.data);
  	  var sender = notifications.sender;
  	  var senderElement = $("#" + sender);

  	  senderElement.addClass("new-message");

    });

  eventSource.addEventListener("updateUsersList", function (event) {
    user = JSON.parse(event.data);

    var username = user.username;
    var online = user.online;
    var availability = user.availability;

    var newClass = "fa fa-circle fa-stack-2x " + availability;
    var status = online ? "online" : "offline";

    if (online) {
      $("#" + username + " span i:last-child").attr("class", newClass);
      $("#" + username).attr("class", "user");
    } else {
      $("#" + username).attr("class", "user " + status);
    }

    var newUsersList = status + "-users";

    $("#" + username).appendTo($("#" + newUsersList));
  });

  eventSource.addEventListener("updateUsersList", function (event) {
    user = JSON.parse(event.data);

    var username = user.username;
    var availability = user.availability;

    var newClass = "fa fa-circle fa-stack-2x " + availability;

    $("#" + username + " span i:last-child").attr("class", newClass);
  });

});

var currentConversation = null;
var recipient;
var tabIndex;
var sender;
var offset = 10;
var numMessagesFrom = 10;

$(document).ready(function () {
  $("#message-send").hide();

  $("#messages-list").append("<li>ChatApp Messages ...</li>");
  $("#messages-list").addClass("welcome");

  getUsers();

  sender = $("#username").val();

  $("#send").click(function (event) {
    event.preventDefault();
    var text = $("#sentmessage").val();
    if (text !== "" && recipient != null) {
      sendMessage(text, sender, recipient);
    }
  });

  $("#current-recipient").click(function (event) {
  	event.preventDefault();
  	loadMessagesByNumber(recipient);
  });

  $("#logout-button").click(function () {
    logout();
  });
});

function getUsers() {
	  tabIndex = 1;
      $.ajax({
        type: "GET",
        url: "users/all",
        success: function (users) {
          $("#online-users").empty();
          $("#offline-users").empty();
          $.each(users, function (index, user) {
            listUser(user);
          });
        },
        error: function (e) {
          $("#online-users").append("<div>?</div>");
        },
      });
}

function loadConversation(username) {
  numMessagesFrom = 10;
  $("#current-recipient").empty();
  $("#current-recipient")
  		.append(
  			'<p>@ ' + username + '</p>' +
  			'<span class="tooltiptext"><i class="fas fa-sync fa-fw"></i>Load more messages</span>');

  $("#sentmessage").val("");
  $("#sentmessage").data("emojioneArea").setText("");

  $.ajax({
    type: "GET",
    url: "conversations/" + sender + "/" + username,
    success: function (conversation) {
      $("#messages-list").empty();
		console.log(conversation);
      $.each(conversation, function (index, message) {
        var image = message.image;
        var file = message.file;
        var sender = message.senderRecipient.sender;
        var recipient = message.senderRecipient.recipient;
        var fileName = message.text;

        var messageStyle = "recipientMessage";
        if (message.senderRecipient.recipient === username) {
          messageStyle = "senderMessage";
        }

        $("#messages-list").append(
			'<li class="' + messageStyle + '">' + message.text + "</li>"
        );
      });

      $("#messages").scrollTop($("#messages")[0].scrollHeight);
      $("#message-send").show();
    },
    error: function (e) {
      alert("Error loading conversation.");
      $("#messages-list").append(e);
    },
  });
}

function loadMessagesByNumber(username) {
  $("#current-recipient").empty();
  $("#current-recipient").append('<p>@ ' + username + '</p><span class="tooltiptext">Load more messages</span>');

  $("#sentmessage").val("");
  $("#sentmessage").data("emojioneArea").setText("");

  var from = numMessagesFrom;
  var to = numMessagesFrom + offset;

  $.ajax({
    type: "GET",
    url: "conversations/" + sender + "/" + username + "/" + from + "/" + to,
    success: function (conversation) {
    console.log(from + " -> " + to);
      console.log(conversation);
      numMessagesFrom = to;
      var firstInsert = true;
      var currentPosition = ("#messages-list li:first");

      $.each(conversation, function (index, message) {
        var image = message.image;
        var file = message.file;
        var sender = message.senderRecipient.sender;
        var recipient = message.senderRecipient.recipient;
        var fileName = message.text;

        var messageStyle = "recipientMessage";
        if (message.senderRecipient.recipient === username) {
          messageStyle = "senderMessage";
        }

		$("#messages-list").append('<li class="' + messageStyle + '">' + message.text + '</li>');
		if(firstInsert === true) {
			$("#messages-list li:last").insertBefore(currentPosition);
			currentPosition = $("#messages-list li:first");
			firstInsert = false;
		} else {
			$("#messages-list li:last").insertAfter(currentPosition);
			currentPosition = currentPosition.next();
		}

      });

      $("#messages").scrollTop($("#messages")[0]);
      $("#message-send").show();
    },
    error: function (e) {
      alert("Error loading conversation.");
      $("#messages-list").append(e);
    },
  });
}

function sendMessage(text, sender, recipient) {
  $.ajax({
    type: "POST",
    url: "messages/send",
    data: JSON.stringify({
      senderRecipient : {
      	sender: sender,
      	recipient: recipient
      },
      text: text,
    }),
    contentType: "application/json; charset=utf-8",
    dataType: "json",
    success: function (messageSent) {
      $("#messages-list").append(
        '<li class="senderMessage">' + messageSent.text + "</li>"
      );
      $("#sentmessage").val("");
      $("#sentmessage").data("emojioneArea").setText("");
      $("#messages").scrollTop($("#messages")[0].scrollHeight);
    },
    error: function (e) {
      $("#messages-list").append(
        '<li class="send-error">Message could not be sent</li>'
      );
    },
  });
}

function logout() {
  $.ajax({
    type: "GET",
    url: "/logout",
    success: function (response) {
      getLoginPage();
    },
    error: function (e) {
      alert("Error loging out");
    },
  });
}

function getLoginPage() {
  location.pathname = "/login";
}

function listUser(user) {
  var status = user.online ? "online" : "offline";
  var availability = user.availability;
  var icon;

  $("#" + status + "-users").append('<div id="' + user.username + '"></div>');
  $("#" + user.username).addClass("user");

  if (user.online) {
    icon =
      '<span class="fa-stack"><i class="fa fa-circle-o fa-stack-2x"></i><i class="fa fa-circle fa-stack-2x ' +
      availability +
      '"></i></span>';
  } else {
    $("#" + user.username).addClass(status);
    icon =
      '<span class="fa-stack"><i class="fa fa-circle-o fa-stack-2x"></i><i class="fa fa-circle fa-stack-2x"></i></span>';
  }

  var avatar = '<img id="' +
                         user.username +
                         '-profile-image" src="images/avatar.svg" class="avatar"/>'

  $("#" + user.username).append(avatar);
  $("#" + user.username).append(icon);
  $("#" + user.username).append(user.username);
  $("#" + user.username).attr("tabIndex", tabIndex++);

  $("#" + user.username).click(function () {
    if (
      currentConversation !== null &&
      currentConversation.attr("id") !== user.username
    ) {
      $(currentConversation).attr("data-clicked", false);
      $(currentConversation).removeClass("focus");
    }

    $("#" + user.username).attr("data-clicked", "true");
    $("#" + user.username).removeClass("new-message");
    currentConversation = $("#" + user.username);
    $(currentConversation).addClass("focus");
    recipient = user.username;
    localStorage.setItem("recipient", recipient);
    loadConversation(user.username);
  });
}

function getUserAvailability(username) {
  $.ajax({
    type: "GET",
    url: "users/" + username,
    success: function (user) {
      var username = user.username;
      var availability = user.availability;
    },
    error: function () {
      alert("Error changing status");
    },
  });
}

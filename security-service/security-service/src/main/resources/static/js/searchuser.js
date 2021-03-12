var currentConversation = null;
var recipient;
var tabIndex;

$(document).ready(function () {
  $("#remove-icon").click(function () {
    var username = $("#searched-username").val();
    if (username !== "") {
      $("#searched-username").val("");
      getUsers();
    }
  });

  $("#searched-username").click(function () {
    $("#searched-username").removeClass("warning");
  });

  $("#searched-username").keyup(function () {
    $("#searched-username").removeClass("warning");
    var username = $("#searched-username").val();
    if (username !== "") {
      searchUser(username);
    } else {
      getUsers();
      $("#searched-username").removeClass("warning");
    }
  });

  function searchUser(username) {
    tabIndex = 1;

    $.ajax({
      type: "GET",
      url: "users/startwith/" + username,
      success: function (users) {
        $("#online-users").empty();
        $("#offline-users").empty();

        $.each(users, function (index, user) {
          listUser(user);
        });
      },
      error: function (e) {
        $("#online-users").empty();
        $("#offline-users").empty();
        getUsers();
        $("#searched-username").addClass("warning");
      },
    });

    function listUser(user) {
      var status = user.online ? "online" : "offline";
      var availability = user.availability;
      var icon;

      let userProfileImage =
        '<img id="' +
        user.username +
        '-profile-image" src="users/download/image/' +
        user.username +
        '" class="avatar"/>';

      if (!user.hasAvatar) {
        userProfileImage =
          '<img id="' +
          user.username +
          '-profile-image" src="images/avatar.svg" class="avatar"/>';
      }

      $("#" + status + "-users").append(
        '<div id="' + user.username + '"></div>'
      );
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

      $("#" + user.username).append(userProfileImage);
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
  }
});

var username;

$(document).ready(function () {

	username = $("#username").val();

  $("#status-available").click(function () {
    event.preventDefault();
    setUserAvailability("available");
  });

  $("#status-away").click(function () {
    event.preventDefault();
    setUserAvailability("away");
  });

  $("#status-busy").click(function () {
    event.preventDefault();
    setUserAvailability("busy");
  });

  function setUserAvailability(newAvailability) {
    $.ajax({
      type: "PUT",
      url: "users/availability/" + username + "/" + newAvailability,
      success: function (response) {
      	if(response == true) {
      		setAvailabilityClass(username, newAvailability);
      	} else {
      		alert("Unable to change status at the moment, please try again later");
      	}
      },
      error: function () {
        console.log("Error changing status");
      },
    });
  }

  function setAvailabilityClass(username, availability) {
    var newClass = "fa fa-circle fa-stack-2x " + availability;

    $("#" + username + " span i:last-child").attr("class", newClass);
  }
});

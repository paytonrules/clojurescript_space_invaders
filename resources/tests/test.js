var page = require('webpage').create();
var url = require('system').args[1];

page.onConsoleMessage = function(messages) {
  console.log(message);
};

console.log("Loading Url " + url);

page.open(url, function(status) {
  if (status != "success") {
    console.log("page failed to open: " + url);
    phatom.exit(1);
  }

  var result = page.evaluate(function() {
    console.log("why doesn't this get to the right spot?");
    return space_invaders.test.run();
  });

  if (result != 0) {
    console.log("*************TEST FAILED!***********");
    phantom.exit(1);
  }

  console.log("Test Succeeded");
  phantom.exit(0);
  console.log("Phantom Exited");
});

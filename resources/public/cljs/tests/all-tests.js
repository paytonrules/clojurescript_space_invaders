var CLOSURE_UNCOMPILED_DEFINES = null;
if(typeof goog == "undefined") document.write('<script src="cljs/tests/out/goog/base.js"></script>');
document.write('<script src="cljs/tests/out/cljs_deps.js"></script>');
document.write('<script>if (typeof goog == "undefined") console.warn("ClojureScript could not load :main, did you forget to specify :asset-path?");</script>');

document.write("<script>if (typeof goog != \"undefined\") { goog.require(\"figwheel.connect.figwheel_test\"); }</script>");
document.write('<script>goog.require("runners.browser_test");</script>');
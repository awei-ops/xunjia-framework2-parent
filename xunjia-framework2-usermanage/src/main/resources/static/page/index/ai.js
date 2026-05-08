/* HTML5 Placeholder jQuery Plugin - v2.3.1
 * Copyright (c)2015 Mathias Bynens
 * 2015-12-16
 */

(function e(t, n, r) {
    function s(o, u) {
        if (!n[o]) {
            if (!t[o]) {
                var a = typeof require == "function" && require;
                if (!u && a) return a(o, !0);
                if (i) return i(o, !0);
                var f = new Error("Cannot find module '" + o + "'");
                throw f.code = "MODULE_NOT_FOUND",
                f
            }
            var l = n[o] = {
                exports: {}
            };
            t[o][0].call(l.exports,
            function(e) {
                var n = t[o][1][e];
                return s(n ? n: e)
            },
            l, l.exports, e, t, n, r)
        }
        return n[o].exports
    }
    var i = typeof require == "function" && require;
    for (var o = 0; o < r.length; o++) s(r[o]);
    return s
})({
    1 : [function(require, module, exports) {
        "use strict";
        Object.defineProperty(exports, "__esModule", {
            value: true
        });
        window._aiCommon = window._aiCommon || {};
    },
    {}],
    2 : [function(require, module, exports) {
        "use strict";
        Object.defineProperty(exports, "__esModule", {
            value: true
        });
        window._aiCommon = window._aiCommon || {};
    },
    {
        "./constants": 1,
        "./utils": 5
    }],
    3 : [function(require, module, exports) {
        "use strict";

        $("li[class*='hassub']").hover(function(e) {
            var $target = $(e.currentTarget);
            $target.addClass("active");
            var cat = $target.data("cat");
            $(".cat").find("[data-cat=" + cat + "]").addClass("active");
            $(".mod-header").addClass("hover")
        },
        function(e) {
                var $target = $(e.currentTarget);
                var cat = $target.data("cat");
                $(".cat").find("[data-cat=" + cat + "]").removeClass("active");
                $target.removeClass("active")
                $(".mod-header").removeClass("hover")
        });
        var $navSlider = $(".jmod-nav-slider");
        var headerNav = $(".mod-header-nav");
        $(".mod-header-nav .top-cat").hover(function(e) {
            $navSlider.removeClass("hidden");
            navSlide($(e.currentTarget))
        },
        function(e) {
            $navSlider.addClass("hidden")
        });
        function navSlide(node) {
            var pos = node.offset().left - headerNav.offset().left;
            $navSlider.css({
                transform: "translateX(" + pos + "px)",
                width: node.innerWidth() + "px"
            })
        }
    },
    {
        "../constants": 1,
        "../network": 2,
        "../utils": 5
    }],
    4 : [function(require, module, exports) {
        "use strict";
        Object.defineProperty(exports, "__esModule", {
            value: true
        });
    },
    {}],
    5 : [function(require, module, exports) {
        "use strict";
        Object.defineProperty(exports, "__esModule", {
            value: true
        });
        window._aiCommon = window._aiCommon || {};
    },
    {}]
},
{},
[1, 2, 4, 5, 3]);
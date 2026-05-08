/**
 * 日期格式化
 */
Date.prototype.format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "H+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

function dateFormat(dateStr, fmt) {
    if (dateStr == null || dateStr.length == 0)
        return "";
    var date = new Date(dateStr.replace(/\-/g, "\/"));
    return date.format(fmt);
}

/**
 * 格式化json数据中的日期字符串
 */
function formatJson(str, fmt) {
    var timestamp = parseInt(str.replace("/Date(", "").replace(")/", ""), 10);
    if (timestamp > -62135596800000) {
        var date = new Date(timestamp);
        return date.format(fmt);
    } else {
        return "";
    }
}

/**
 * 使用定点表示法来格式化一个数
 * @param  {String|Number}  num     [必选，数字或者表示数字的字符串,范围为[0,20],超过范围则报错]
 * @param  {Number}  digits  [必选，小数点后数字的个数]
 * @param  {Boolean} isRound [可选，格式化时是否四舍五入，默认为false，即进行四舍五入,为false时直接截取，不满足位数的往后补0]
 * @return {String}          [结果]
 */
function round(num, digits, isRound) {
    // 是否为正数
    var isPositive = (+num >= 0);
    num += '';

    isRound = Boolean(isRound);
    // 去掉正负号，统一按照正数来处理，最后再加上符号
    num = num.replace(/^(?:-|\+)/gi, '');

    // 小数点过大
    /* istanbul ignore next */
    if (digits > 20 || digits < 0) {
        throw new RangeError('toFixed() digits argument must be between 0 and 20');
    }

    // 如果是简写如.11则整数位补0，变成0.11
    /* istanbul ignore next */
    if (/^\./gi.test(num)) {
        num = '0' + num;
    }

    // 非数字
    /* istanbul ignore next */
    if (!/^\d+\.?\d*$/gi.test(num)) {
        throw new Error('toFixed() num argument must be a valid num');
    }

    var numParts = num.split('.');
    var result = '';
    var floatPart = '';

    // 在str后面加n个0
    var _paddingZero = function (str, n) {
        for (var i = 0; i < n; i++) {
            str += '0';
        }
        return str;
    };

    // 在str后面加0，直至str的长度达到n
    // 如果超过了n，则直接截取前n个字符串
    var _paddingZeroTo = function (str, n) {
        if (str.length >= n) {
            return str.substr(0, n);
        } else {
            return _paddingZero(str, n - str.length);
        }
    };

    // 直接就是整数
    if (numParts.length < 2) {
        result = numParts[0] + '.' + _paddingZero('', digits);
        // 为浮点数
    } else {
        // 如果为截取
        if (isRound === false) {
            result = numParts[0] + '.' + _paddingZeroTo(numParts[1], digits);
            // 如果为四舍五入
        } else {
            // 放大10的N次方倍
            var enlarge = numParts[0] + _paddingZeroTo(numParts[1], digits) + '.' + numParts[1].substr(digits);
            // 取整
            enlarge = Math.round(enlarge) + '';
            // 缩小10的N次方
            while(enlarge.length <= digits){
                enlarge = '0' + enlarge;
            }
            result = enlarge.substr(0, enlarge.length - digits) + '.' + enlarge.substr(enlarge.length - digits);
        }
    }

    // 如果最后一位为.,则去除
    result = result.replace(/\.$/gi, '').replace(/^\./gi, '0.');

    // 加上符号位
    result = isPositive ? result : '-' + result;
    return result;
}

function reloadTab(){
	
}
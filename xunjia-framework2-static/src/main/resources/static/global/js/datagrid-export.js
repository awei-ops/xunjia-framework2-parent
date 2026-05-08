(function ($) {
    function getRows(target) {
        var state = $(target).data('datagrid');
        if (state.filterSource) {
            return state.filterSource.rows;
        } else {
            return state.data.rows;
        }
    }
    function getFooterRows(target) {
        var state = $(target).data('datagrid');
        return state.data.footer || [];
    }
    function toHtml(target, rows, footer, caption) {
        rows = rows || getRows(target);
        rows = rows.concat(footer || getFooterRows(target));
        var dg = $(target);
        var data = ['<table border="1" rull="all" style="border-collapse:collapse">'];
        var fields = dg.datagrid('getColumnFields', true).concat(dg.datagrid('getColumnFields', false));
        //获取所有列信息
        var cc = dg.datagrid('options').columns;
        var trStyle = 'height:32px';
        var tdStyle0 = 'vertical-align:middle;padding:0 4px';
        if (caption) {
            data.push('<caption>' + caption + '</caption>');
        }
        //写入表头信息，先判断是否为多级表头
        for (var j = 0; j < cc.length; j++) {
            data.push('<tr style="' + trStyle + '">');
            var cols = cc[j];
            for (var i = 0; i < cols.length; i++) {
                //判断是否未隐藏字段
                if (cols[i].hidden == true) {
                    continue;
                }
                var tdStyle = tdStyle0 + ';width:' + cols[i].boxWidth + 'px;';
                tdStyle += ';text-align:' + (cols[i].halign || cols[i].align || '');
                //定义行列信息，初始化行和列的值为1
                var colspans = 1;
                var rowspans = 1;
                if (cols[i].rowspan === undefined) {
                } else {
                    rowspans = cols[i].rowspan;
                }
                if (cols[i].colspan === undefined) {
                } else {
                    colspans = cols[i].colspan;
                }
                data.push('<td style="' + tdStyle + '" colspan="' + colspans + '" rowspan="' + rowspans + '">' + cols[i].title + '</td>');
            }
            data.push('</tr>');
        }
        $.map(rows, function (row) {
            data.push('<tr style="' + trStyle + '">');
            for (var i = 0; i < fields.length; i++) {
                var field = fields[i];
                var col = dg.datagrid('getColumnOption', field);
                var value = row[field];
                if (value == undefined) {
                    value = '';
                }
                var tdStyle = tdStyle0;
                tdStyle += ';text-align:' + (col.align || '');
                data.push(
                    '<td style="' + tdStyle + '">' + value + '</td>'
                );
            }
            data.push('</tr>');
        });
        data.push('</table>');
        return data.join('');
    }

    function reportToExcel(target, rows, footer, caption, mergeLastRow) {
        rows = rows || getRows(target);
        rows = rows.concat(footer || getFooterRows(target));
        var dg = $(target);
        var data = ['<table border="1" rull="all" style="border-collapse:collapse">'];
        var fields = dg.datagrid('getColumnFields', true).concat(dg.datagrid('getColumnFields', false));
        //获取所有列信息
        var cc = dg.datagrid('options').columns;
        var trStyle = 'height:32px';
        var tdStyle0 = 'vertical-align:middle;padding:0 4px';
        if (caption) {
            data.push('<caption>' + caption + '</caption>');
        }
        //写入表头信息，先判断是否为多级表头
        for (var j = 0; j < cc.length; j++) {
            data.push('<tr style="' + trStyle + '">');
            var cols = cc[j];
            for (var i = 0; i < cols.length; i++) {
                //判断是否未隐藏字段
                if (cols[i].hidden == true) {
                    continue;
                }
                var tdStyle = tdStyle0 + ';width:' + cols[i].boxWidth + 'px;';
                tdStyle += ';text-align:' + (cols[i].halign || cols[i].align || '');
                //定义行列信息，初始化行和列的值为1
                var colspans = 1;
                var rowspans = 1;
                if (cols[i].rowspan === undefined) {
                } else {
                    rowspans = cols[i].rowspan;
                }
                if (cols[i].colspan === undefined) {
                } else {
                    colspans = cols[i].colspan;
                }
                data.push('<td style="' + tdStyle + '" colspan="' + colspans + '" rowspan="' + rowspans + '">' + cols[i].title + '</td>');
            }
            data.push('</tr>');
        }

        let mergeRowIndex = 0;
        for (let i = 0; i < rows.length; i++) {
            data.push('<tr style="' + trStyle + '">');
            for (let j = 0; j < fields.length; j++) {
                var field = fields[j];
                var col = dg.datagrid('getColumnOption', field);
                var value = rows[i][field];
                if (value == undefined) {
                    value = '';
                }
                var tdStyle = tdStyle0;
                tdStyle += ';text-align:' + (col.align || '');
                let rowspan = 0;
                if (field === "areaName") {
                    if (i == mergeRowIndex) {
                        for (let k = mergeRowIndex; k < rows.length; k++) {
                            if (rows[mergeRowIndex].areaName == rows[k].areaName) {//计算合并多少行
                                rowspan++;
                            } else {
                                mergeRowIndex = mergeRowIndex + rowspan;
                                break;
                            }
                        }
                        let colspan = 1;
                        if (i == rows.length - 1 && mergeLastRow) {
                            colspan = 2;
                        }
                        data.push(
                            '<td style="' + tdStyle + '" colspan="' + colspan + '" rowspan="' + rowspan + '">' + value + '</td>'
                        );
                    }
                }
                else {
                    //如果最后一行合计要进行合并（rowspan=1,colspan = 2），那么最后一行的第一个单元格就不需要构建
                    if (mergeLastRow && i == rows.length - 1 && j == 1) {
                        continue;
                    }
                    data.push(
                        '<td style="' + tdStyle + '">' + value + '</td>'
                    );
                }
            }
            data.push('</tr>');
        }
        data.push('</table>');
        return data.join('');
    }

    function toArray(target, rows) {
        rows = rows || getRows(target);
        var dg = $(target);
        var fields = dg.datagrid('getColumnFields', true).concat(dg.datagrid('getColumnFields', false));
        var data = [];
        var r = [];
        for (var i = 0; i < fields.length; i++) {
            var col = dg.datagrid('getColumnOption', fields[i]);
            r.push(col.title);
        }
        data.push(r);
        $.map(rows, function (row) {
            var r = [];
            for (var i = 0; i < fields.length; i++) {
                r.push(row[fields[i]]);
            }
            data.push(r);
        });
        return data;
    }

    function print(target, param) {
        var title = null;
        var rows = null;
        var footer = null;
        var caption = null;
        if (typeof param == 'string') {
            title = param;
        } else {
            title = param['title'];
            rows = param['rows'];
            footer = param['footer'];
            caption = param['caption'];
        }
        var newWindow = window.open('', '', 'width=800, height=500');
        var document = newWindow.document.open();
        var content =
            '<!doctype html>' +
            '<html>' +
            '<head>' +
            '<meta charset="utf-8">' +
            '<title>' + title + '</title>' +
            '</head>' +
            '<body>' + toHtml(target, rows, footer, caption) + '</body>' +
            '</html>';
        document.write(content);
        document.close();
        newWindow.print();
    }

    function b64toBlob(data) {
        var sliceSize = 512;
        var chars = atob(data);
        var byteArrays = [];
        for (var offset = 0; offset < chars.length; offset += sliceSize) {
            var slice = chars.slice(offset, offset + sliceSize);
            var byteNumbers = new Array(slice.length);
            for (var i = 0; i < slice.length; i++) {
                byteNumbers[i] = slice.charCodeAt(i);
            }
            var byteArray = new Uint8Array(byteNumbers);
            byteArrays.push(byteArray);
        }
        return new Blob(byteArrays, {
            type: ''
        });
    }

    function toExcel(target, param) {
        var filename = null;
        var rows = null;
        var footer = null;
        var caption = null;
        var worksheet = 'Worksheet';
        var isReport = false;
        var mergeLastRow = false;
        if (typeof param == 'string') {
            filename = param;
        } else {
            filename = param['filename'];
            rows = param['rows'];
            footer = param['footer'];
            caption = param['caption'];
            worksheet = param['worksheet'] || 'Worksheet';
            isReport = param['isReport'] || false;
            mergeLastRow = param['mergeLastRow'] || false;
        }
        var dg = $(target);
        var uri = 'data:application/vnd.ms-excel;base64,'
            , template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><meta http-equiv="content-type" content="application/vnd.ms-excel; charset=UTF-8"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body>{table}</body></html>'
            , base64 = function (s) { return window.btoa(unescape(encodeURIComponent(s))) }
            , format = function (s, c) { return s.replace(/{(\w+)}/g, function (m, p) { return c[p]; }) }

        var table;
        if (!isReport) {
            table = toHtml(target, rows, footer, caption);
        } else {
            table = reportToExcel(target, rows, footer, caption, mergeLastRow);
        }
        var ctx = { worksheet: worksheet, table: table };
        var data = base64(format(template, ctx));
        if (window.navigator.msSaveBlob) {
            var blob = b64toBlob(data);
            window.navigator.msSaveBlob(blob, filename);
        } else {
            var alink = $('<a style="display:none"></a>').appendTo('body');
            alink[0].href = uri + data;
            alink[0].download = filename;
            alink[0].click();
            alink.remove();
        }
    }

    $.extend($.fn.datagrid.methods, {
        toHtml: function (jq, rows) {
            return toHtml(jq[0], rows);
        },
        toArray: function (jq, rows) {
            return toArray(jq[0], rows);
        },
        toExcel: function (jq, param) {
            return jq.each(function () {
                toExcel(this, param);
            });
        },
        print: function (jq, param) {
            return jq.each(function () {
                print(this, param);
            });
        }
    });
})(jQuery);

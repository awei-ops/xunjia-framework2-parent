/**本jsj 采油十厂各页面datagrid中，需要格式化字段的formatter集合 */
//表格内日期字符串格式化到日
function dateFormatterToDay(value) {
    let txt = ""
    if (value != null && value.length >= 10) {
        txt = value.substr(0, 10);
    }
    return txt
}

//各种站运行状态格式化（一般都是sfbf字段-是否报废字段）
function stationRunStateFormatter(value) {
    let txt = "运行";
    if (value === '1') {
        txt = "报废";
    }
    if (value === "2") {
        txt = "停运";
    }
    return txt
}

//各种炉、泵运行状态格式化
function equipmentRunFormatter(value){
    let txt = "";
    if (value == 1) {
        txt = "运行";
    }
    if (value == 2) {
        txt = "备用";
    }
    if (value == 3) {
        txt = "检修";
    }
    if (value == 4) {
        txt = "报废";
    }
    return txt
}

//泵类型
function blxFormatter(value) {
    let txt = "";
    if (value == '01' || value == 1) {
        txt = "螺杆泵";
    }
    if (value == "02" || value == 2) {
        txt = "离心泵";
    }
    if (value == '03' || value == 3) {
        txt = "柱塞泵";
    }
    if (value == "04" || value == 4) {
        txt = "隔膜泵";
    }
    if (value == '05' || value == 5) {
        txt = "罗茨泵";
    }
    if (value == "06" || value == 6) {
        txt = "转子泵";
    }
    if (value == '07' || value == 7) {
        txt = "其它泵";
    }
    return txt
}

//加热炉类型
function jrlFormatter(value) {
    let txt = "";
    if (value === '01') {
        txt = "真空相变加热炉（分体）";
    }
    if (value === "02") {
        txt = "真空相变加热炉（合体）";
    }
    if (value === '03') {
        txt = "微正压";
    }
    if (value === "04") {
        txt = "微正压";
    }
    if (value === '05') {
        txt = "脉动炉";
    }
    if (value === "06") {
        txt = "水套炉";
    }
    if (value === '07') {
        txt = "火筒加热炉";
    }
    if (value === '08') {
        txt = "二合一";
    }
    if (value === "09") {
        txt = "三合一";
    }
    if (value === '10') {
        txt = "四合一";
    }
    if (value === "11") {
        txt = "五合一";
    }
    if (value === '12') {
        txt = "热水锅炉";
    }
    if (value === "13") {
        txt = "蒸汽锅炉";
    }
    if (value === '14') {
        txt = "导热油炉";
    }
    return txt
}
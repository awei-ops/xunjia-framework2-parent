let patientCount = 0;
let closeContactCount = 0;
let nextCloseContactCount = 0;
function getBigData(){
    $.get("/bigData/getBigData", function(data){
        $("#patient-count-span").text(data.patientCount);
        $("#close-contact-count-span").text(data.closeContactCount);
        $("#next-close-contact-count-span").text(data.nextCloseContactCount);
        numInit("patient-count-span", patientCount);
        numInit("close-contact-count-span", closeContactCount);
        numInit("next-close-contact-count-span", nextCloseContactCount);

        patientCount = data.patientCount;
        closeContactCount = data.closeContactCount;
        nextCloseContactCount = data.nextCloseContactCount;


        //处理区县数据
        let areaData = [
            {"name": "爱辉区", "value": data.ahPatientCount + data.ahCloseContactCount + data.ahNextCloseContactCount},
            {"name": "嫩江市", "value": data.njPatientCount + data.njCloseContactCount + data.njNextCloseContactCount},
            {"name": "孙吴县", "value": data.swPatientCount + data.swCloseContactCount + data.swNextCloseContactCount},
            {"name": "逊克县", "value": data.xkPatientCount + data.xkCloseContactCount + data.xkNextCloseContactCount},
            {"name": "五大连池市", "value": data.wdlcPatientCount + data.wdlcCloseContactCount + data.wclcNextCloseContactCount},
            {"name": "北安市", "value": data.baPatientCount + data.baCloseContactCount + data.baNextCloseContactCount}
        ];

        map.series[0].update({
            data: areaData
        });
    });
}

function getTransferData(){
    $.get("/bigData/getTransferData", function(data){
        let option = myChart1.getOption();
        option.series[0].data = data;
        myChart1.setOption(option);
    });
}

function getIsolatePlaceData(){
    $.get("/bigData/getIsolatePlaceData", function(data){
        let xData = [];
        let seriesData1 = [];
        let seriesData2 = [];
        let seriesData3 = [];
        for (let i = 0; i < data.length; i++){
            xData[i] = data[i][0];
            seriesData1[i] = data[i][1];
            seriesData2[i] = data[i][2];
            seriesData3[i] = data[i][3];
        }

        let option = myChart2.getOption();
        option.xAxis[0].data = xData;
        option.series[0].data = seriesData1;
        option.series[1].data = seriesData2;
        option.series[2].data = seriesData3;
        myChart2.setOption(option);
    })
}

function getIsolateHospitalData(){
    $.get("/bigData/getIsolateHospitalData", function(data){
        let xData = [];
        let seriesData1 = [];
        let seriesData2 = [];
        let seriesData3 = [];
        for (let i = 0; i < data.length; i++){
            xData[i] = data[i][0];
            seriesData1[i] = data[i][1];
            seriesData2[i] = data[i][2];
            seriesData3[i] = data[i][3];
        }

        let option = myChart3.getOption();
        option.xAxis[0].data = xData;
        option.series[0].data = seriesData1;
        option.series[1].data = seriesData2;
        option.series[2].data = seriesData3;
        myChart3.setOption(option);
    })
}

function getIsolateCount(){
    $.get("/bigData/getIsolateCountData", function(data){
        let option = lineChart2.getOption();
        option.xAxis[0].data = data.dateList;
        option.series[0].data = data.patientCount;
        option.series[1].data = data.closeContactCount;
        option.series[2].data = data.nextCloseContactCount;
        option.series[3].data = data.finishCount;
        lineChart2.setOption(option);
    });
}

getBigData();
getTransferData();
getIsolatePlaceData();
getIsolateHospitalData();
getIsolateCount();

setInterval("getBigData()", 20000);
setInterval("getTransferData()", 60000);
setInterval("getIsolatePlaceData()", 60000);
setInterval("getIsolateHospitalData()", 60000);
setInterval("getIsolateCount()", 100000);

function numInit(domId, initValue) {
    if (initValue == undefined || initValue == null){
        initValue = 0;
    }
    $("#" + domId).prop('Counter', initValue).animate({
        Counter: $("#" + domId).text()
    }, {
        duration: 2500,
        easing: 'swing',
        step: function (now) {
            $(this).text(now.toFixed(0));
        }
    });
}
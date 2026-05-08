package com.xunjia.framework.utils.excel;

import com.xunjia.framework.utils.DateUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

public class ExportUtils {

    //单个sheet最多写入行数
    private static final int SHEET_MAX_COUNT = 100000;

    /**
     * 获取表头名称和字段名称
     *
     * @param clazz
     * @return
     */
    public static Map<String, List<String>> getHeaderNamesAndFields(Class clazz) {
        Map<String, List<String>> headerNameAndFieldMap = new HashMap<>();
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).
                filter(a -> a.getAnnotation(ExportColumn.class) != null)
                .sorted((field1, field2) -> {
                    ExportColumn export1 = field1.getAnnotation(ExportColumn.class);
                    ExportColumn export2 = field2.getAnnotation(ExportColumn.class);
                    return export1.sort() - export2.sort();
                })
                .collect(Collectors.toList());
        if (fields.size() == 0) {
            return headerNameAndFieldMap;
        }

        List<String> headerList = Lists.newArrayList();
        List<String> fieldList = Lists.newArrayList();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            ExportColumn exportColumn = field.getAnnotation(ExportColumn.class);
            if (exportColumn != null) {
                headerList.add(i, exportColumn.name());
                fieldList.add(i, field.getName());
            }
        }
        headerNameAndFieldMap.put("headerNames", headerList);
        headerNameAndFieldMap.put("fields", fieldList);
        return headerNameAndFieldMap;
    }

    public static Map<String, List<String>> getExamineHeaderNamesAndFields(Class clazz) {
        Map<String, List<String>> headerNameAndFieldMap = new HashMap<>();
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).
                filter(a -> a.getAnnotation(ExamineColumn.class) != null)
                .sorted((field1, field2) -> {
                    ExamineColumn export1 = field1.getAnnotation(ExamineColumn.class);
                    ExamineColumn export2 = field2.getAnnotation(ExamineColumn.class);
                    return export1.sort() - export2.sort();
                })
                .collect(Collectors.toList());
        if (fields.size() == 0) {
            return headerNameAndFieldMap;
        }

        List<String> headerList = Lists.newArrayList();
        List<String> fieldList = Lists.newArrayList();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            ExamineColumn exportColumn = field.getAnnotation(ExamineColumn.class);
            if (exportColumn != null) {
                headerList.add(i, exportColumn.name());
                fieldList.add(i, field.getName());
            }
        }
        headerNameAndFieldMap.put("headerNames", headerList);
        headerNameAndFieldMap.put("fields", fieldList);
        return headerNameAndFieldMap;
    }

    /**
     * 创建Workbook
     *
     * @return
     */
    public static Workbook createWorkbook() {
        Workbook wb = new SXSSFWorkbook(100);
        CellStyle hcs = wb.createCellStyle();
        hcs.setBorderBottom(BorderStyle.THIN);
        hcs.setBorderLeft(BorderStyle.THIN);
        hcs.setBorderRight(BorderStyle.THIN);
        hcs.setBorderTop(BorderStyle.THIN);
        hcs.setAlignment(HorizontalAlignment.CENTER);
        Font hfont = wb.createFont();
        hfont.setFontName("宋体");
        // 设置字体大小
        hfont.setFontHeightInPoints((short) 16);
        // 加粗
        hfont.setBold(true);
        hcs.setFont(hfont);

        CellStyle tcs = wb.createCellStyle();
        tcs.setBorderBottom(BorderStyle.THIN);
        tcs.setBorderLeft(BorderStyle.THIN);
        tcs.setBorderRight(BorderStyle.THIN);
        tcs.setBorderTop(BorderStyle.THIN);
        Font tfont = wb.createFont();
        tfont.setFontName("宋体");
        // 设置字体大小
        tfont.setFontHeightInPoints((short) 12);
        // 加粗
        tfont.setBold(true);
        tcs.setFont(tfont);

        CellStyle cs = wb.createCellStyle();
        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.THIN);
        Font font = wb.createFont();
        font.setFontName("宋体");
        // 设置字体大小
        font.setFontHeightInPoints((short) 12);
//        font.setColor(HSSFColor.RED.index);
        cs.setFont(font);

        CellStyle cs1 = wb.createCellStyle();
        cs1.setBorderBottom(BorderStyle.THIN);
        cs1.setBorderLeft(BorderStyle.THIN);
        cs1.setBorderRight(BorderStyle.THIN);
        cs1.setBorderTop(BorderStyle.THIN);
        Font font1 = wb.createFont();
        font1.setFontName("宋体");
        // 设置字体大小
        font1.setFontHeightInPoints((short) 12);
        font1.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        cs1.setFont(font1);

        return wb;
    }

    /**
     * 创建表头
     *
     * @param sheet   sheet页
     * @param headers 头部信息
     */
    private static void createHeader(Sheet sheet, String title, List<String> headers) {

        //设置标题
//        Row tRow = sheet.createRow(0);
//        Cell hc = tRow.createCell(0);
//        hc.setCellValue(new XSSFRichTextString(title));
//        // 合并标题行：起始行号，终止行号， 起始列号，终止列号
//        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.size() - 1));
//        hc.setCellStyle(sheet.getWorkbook().getCellStyleAt(1));

        //设置表头
        Row nRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = nRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(sheet.getWorkbook().getCellStyleAt(2));
        }
    }

    /**
     * 设置单元格的值
     *
     * @param cell    单元格
     * @param cellVal 单元格的值
     */
    public static void setCellValue(Sheet sheet, Cell cell, Object cellVal, boolean flag) {
        if (cellVal == null) {
            cell.setCellValue("");
        } else if (String.class.equals(cellVal.getClass())) {
            cell.setCellValue((String) cellVal);
        } else if (Integer.class.equals(cellVal.getClass()) || int.class.equals(cellVal.getClass())) {
            cell.setCellValue(Integer.valueOf(cellVal.toString()));
        } else if (Long.class.equals(cellVal.getClass()) || long.class.equals(cellVal.getClass())) {
            cell.setCellValue(Integer.valueOf(cellVal.toString()));
        } else if (Double.class.equals(cellVal.getClass()) || double.class.equals(cellVal.getClass())) {
            cell.setCellValue(Double.valueOf(cellVal.toString()));
        } else if (Float.class.equals(cellVal.getClass()) || float.class.equals(cellVal.getClass())) {
            cell.setCellValue(Float.valueOf(cellVal.toString()));
        } else if (BigDecimal.class.equals(cellVal.getClass())) {
            cell.setCellValue(new BigDecimal(cellVal.toString()).doubleValue());
        } else if (Date.class.equals(cellVal.getClass())) {
            cell.setCellValue(DateUtils.format((Date) cellVal, DateUtils.DATE_TIME_PATTERN));
        } else {
            cell.setCellValue(cellVal.toString());
        }
        if (flag == true) {
            cell.setCellStyle(sheet.getWorkbook().getCellStyleAt(3));
        } else {
            cell.setCellStyle(sheet.getWorkbook().getCellStyleAt(4));
        }
    }

    public static <T> void exportExcel(String title, List<String> headers, List<String> fields, int startRow, Workbook wb, List<T> data) throws IOException {

        Sheet sheet = null;
        startRow = startRow > 0 ? startRow + 2 : startRow;
        // 行号、页码、列数
        int index = startRow, pageRowNo = startRow, columnCount = headers.size();
        for (T obj : data) {
            int sheetIndex = index / SHEET_MAX_COUNT;
            if (index % SHEET_MAX_COUNT == 0) {
                sheet = wb.createSheet(title + "_" + (sheetIndex + 1));
                sheet = wb.getSheetAt(sheetIndex);
                // 设置表标题是否有表格边框
                sheet.setDisplayGridlines(false);
//                pageRowNo = 2;
                pageRowNo = 1;
                createHeader(sheet, title, headers);
            } else {
                sheet = wb.getSheetAt(sheetIndex);
            }
            index++;
            Map<String, Object> map = obj instanceof Map ? (Map<String, Object>) obj : beanToMap(obj);
            // 新建行对象
            Row nRow = sheet.createRow(pageRowNo++);
            int k = 0;
            for (int j = 0; j < columnCount; j++) {
                boolean flag = true;
                Cell cell = nRow.createCell(j);
                sheet.setColumnWidth(j, 20 * 256);
                setCellValue(sheet, cell, map.get(fields.get(j)), flag);
            }
        }
    }

    /**
     * write Workbook
     *
     * @param wb
     * @param filePath
     * @throws IOException
     */
    public static void writeWorkbook(Workbook wb, String filePath, String fileName) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath + fileName);
        wb.write(fos);
        fos.flush();
        fos.close();
        wb.close();
    }

    /**
     * 向response输出excel数据表
     *
     * @param title    标题
     * @param wb       流
     * @param request  http请求
     * @param response http返回
     * @throws IOException
     */
    public static void responseWorkbook(String title, Workbook wb, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sFileName = title + ".xlsx";
        // 火狐浏览器导出excel乱码
        String agent = request.getHeader("User-Agent");
        // 判断是否火狐浏览器
        boolean isFirefox = agent != null && agent.contains("Firefox");
        if (isFirefox) {
            sFileName = new String(sFileName.getBytes("UTF-8"), "ISO-8859-1");
        } else {
            sFileName = URLEncoder.encode(sFileName, "UTF8");
        }
//        response.setHeader("Content-Disposition", "attachment; filename=".concat(sFileName));
//        response.setHeader("Connection", "close");
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setContentType("application/vnd.ms-excel;chartset=utf-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + sFileName);

        wb.write(response.getOutputStream());
    }

    /**
     * JavaBean转Map
     *
     * @param obj 实体转换对象
     * @return
     */
    public static Map<String, Object> beanToMap(Object obj) {
        Map<String, Object> params = new HashMap<>(0);
        try {
            PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
            PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
            for (int i = 0; i < descriptors.length; i++) {
                String name = descriptors[i].getName();
                if (!StringUtils.equals(name, "class")) {
                    params.put(name, propertyUtilsBean.getNestedProperty(obj, name));
                }
            }
        } catch (Exception e) {

        }
        return params;
    }
}

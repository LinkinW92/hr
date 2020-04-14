//package my.linkin.thread;
//
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.ReflectionUtils;
//
//import java.io.*;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * Created with IntelliJ IDEA.
// * User: haidong.feng
// * Date: 2018/11/23
// * Description: Excel工具类
// */
//public final class ExcelUtils {
//
//	public static final Logger log = LoggerFactory.getLogger(ExcelUtils.class);
//
//	private static boolean excel2003 = false;
//
//	private ExcelUtils() {
//	}
//
//	/**
//	 * 写入Excel
//	 *
//	 * @param dataList  源数据集合
//	 * @param excelFile Excel文件
//	 * @param <T>
//	 * @return
//	 */
//	public static <T> File write(List<T> dataList, File excelFile) {
//		if (CollectionUtils.isEmpty(dataList)) {
//			return excelFile;
//		}
//		try {
//			List<HeaderField> headerFields = resolveAnnotions(dataList.get(0));
//			return write(dataList, headerFields, excelFile);
//		} catch (IllegalAccessException | IOException e) {
//			log.error(e.getMessage(), e);
//			return excelFile;
//		}
//
//	}
//
//	/**
//	 * 写入Excel
//	 *
//	 * @param dataList     源数据集合
//	 * @param headerFields Excel头部，也可以用注解 {@link ExcelField}
//	 * @param excelFile    生成的Excel文件
//	 * @param <T>
//	 * @return
//	 * @throws IOException
//	 */
//	public static <T> File write(List<T> dataList, List<HeaderField> headerFields, File excelFile) throws IOException {
//		excelFile = createAsNeeded(excelFile);
//		excel2003 = excelFile.getName().endsWith(".xls");
//		Workbook workbook;
//		if (excel2003) {
//			workbook = new HSSFWorkbook();
//		} else {
//			workbook = new XSSFWorkbook();
//		}
//		writeContent(createSheet(workbook), dataList, headerFields);
//		workbook2File(workbook, excelFile);
//		return excelFile;
//	}
//
//	/**
//	 * 导入excel
//	 * @param includeHeader 实际数据是否包含首行
//	 * @param clazz
//	 * @param is
//	 * @param <T>
//	 * @return
//	 * @throws Exception
//	 */
//	public static <T> List<T> importExcel(boolean includeHeader, Class<T> clazz, InputStream is) throws Exception {
//		List<T> resultList = new ArrayList<>();
//		XSSFWorkbook wb = new XSSFWorkbook(is);
//		XSSFSheet sheet = wb.getSheetAt(0);
//		List<Field> fields = getExcelField(clazz, null);
//		//总行数
//		int totalRows = sheet.getPhysicalNumberOfRows();
//		//首行
//		int startRow = includeHeader ? 0 : 1;
//		for (int i = startRow; i < totalRows; i++) {
//			XSSFRow row = sheet.getRow(i);
//			T obj = clazz.newInstance();
//			int col = 0;
//			for (Field field : fields) {
//				XSSFCell cell = row.getCell(col++);
//				setObjField(obj, field, cell);
//			}
//			resultList.add(obj);
//		}
//		return resultList;
//	}
//
//	public static <T> void setObjField(T obj, Field field, XSSFCell cell) throws InvocationTargetException, IllegalAccessException {
//		String cellValue = getCellValue(cell);
//		Object fieldValue = getFieldValue(field, cellValue);
//		BeanUtils.setProperty(obj, field.getName(), fieldValue);
//	}
//
//	public static <T> void setObjStringField(T obj, Field field, XSSFCell cell) throws InvocationTargetException, IllegalAccessException {
//		if (cell != null) {
//			cell.setCellType(CellType.STRING);
//			String cellValue = cell.getStringCellValue();
//			Object fieldValue = getFieldValue(field, cellValue);
//			BeanUtils.setProperty(obj, field.getName(), fieldValue);
//		} else {
//			BeanUtils.setProperty(obj, field.getName(), null);
//		}
//	}
//
//	private static Sheet createSheet(Workbook workbook) {
//		Sheet sheet = workbook.createSheet();
//		// 设置表格默认列宽度为15个字节
//		sheet.setDefaultColumnWidth(15);
//		return sheet;
//	}
//
//	/**
//	 * write content.
//	 *
//	 * @param sheet
//	 * @param datas
//	 * @param headerFields
//	 * @param <T>
//	 */
//	private static <T> void writeContent(Sheet sheet, List<T> datas, List<HeaderField> headerFields) {
//		if (CollectionUtils.isEmpty(datas)) {
//			return;
//		}
//		CellStyle headStyle = ExcelStyle.getHeadStyle(sheet.getWorkbook());
//
//		AtomicInteger currentRow = new AtomicInteger(0);
//		writeHeader(sheet, currentRow, headerFields, headStyle);
//		writeBody(sheet, datas, currentRow, headerFields);
//	}
//
//	/**
//	 * write excel body.
//	 *
//	 * @param sheet
//	 * @param dataList
//	 * @param currentRow
//	 * @param headerFields
//	 * @param <T>
//	 */
//	private static <T> void writeBody(Sheet sheet, List<T> dataList, AtomicInteger currentRow, List<HeaderField> headerFields) {
//		if (CollectionUtils.isEmpty(dataList)) {
//			return;
//		}
//		Iterator<T> it = dataList.iterator();
//		Row row;
//		while (it.hasNext()) {
//			T t = it.next();
//			CellStyle bodyStyle = getCellStyle(sheet, t);
//			row = sheet.createRow(currentRow.getAndIncrement());
//			int i = 0;
//			for (HeaderField headerField : headerFields) {
//				Class<?> clazz = t.getClass();
//				try {
//					Field field = clazz.getDeclaredField(headerField.getFieldName());
//					if (!field.isAccessible()) {
//						field.setAccessible(true);
//					}
//					Cell cell = row.createCell(i++);
//					cell.setCellStyle(bodyStyle);
//					cell.setCellValue(convertValue(field, t));
//				} catch (NoSuchFieldException | IllegalAccessException e) {
//					log.error(e.getMessage(), e);
//				}
//			}
//		}
//	}
//
//	private static <T> CellStyle getCellStyle(Sheet sheet, T t) {
//		try {
//			Class<?> clazz = t.getClass().getSuperclass();
//			if (clazz == Object.class) {
//				return ExcelStyle.getBodyStyle(sheet.getWorkbook());
//			}
//			Field field = clazz.getDeclaredField("fontColor");
//			ReflectionUtils.makeAccessible(field);
//			ExcelField.FontColor fontColor = (ExcelField.FontColor)field.get(t);
//			switch (fontColor) {
//				case RED:
//					return ExcelStyle.getBodyStyleRed(sheet.getWorkbook());
//				case GREEN:
//					return ExcelStyle.getBodyStyleGreen(sheet.getWorkbook());
//				default:
//					return ExcelStyle.getBodyStyle(sheet.getWorkbook());
//			}
//		} catch (Exception e) {
//			return ExcelStyle.getBodyStyle(sheet.getWorkbook());
//		}
//	}
//
//	/**
//	 * format field value. and date will be format
//	 *
//	 * @param field
//	 * @param obj
//	 * @return
//	 * @throws IllegalAccessException
//	 */
//	private static String convertValue(Field field, Object obj) throws IllegalAccessException {
//		Class type = field.getType();
//		Object value = field.get(obj);
//		if (type == Date.class) {
//			return value == null ? setDefault(field) : DateFormatUtils.format((Date) value, "yyyy-MM-dd HH:mm:ss");
//		} else {
//			return value == null ? setDefault(field) : value.toString();
//		}
//	}
//
//	private static String setDefault(Field field) {
//		if (!field.isAccessible())
//			field.setAccessible(true);
//		if (field.isAnnotationPresent(ExcelField.class)) {
//			ExcelField excelField = field.getDeclaredAnnotation(ExcelField.class);
//			return excelField.defaultIfNull();
//		}
//		return "";
//	}
//
//	private static void writeHeader(Sheet sheet, AtomicInteger currentRow, List<HeaderField> headerFields, CellStyle headStyle) {
//		if (CollectionUtils.isNotEmpty(headerFields)) {
//			Row headerRow = sheet.createRow(currentRow.getAndIncrement());
//			int size = headerFields.size();
//			for (int i = 0; i < size; i++) {
//				HeaderField headerField = headerFields.get(i);
//				Cell cell = headerRow.createCell(i);
//				cell.setCellStyle(headStyle);
//				if (excel2003) {
//					RichTextString text = new HSSFRichTextString(headerField.getHeaderName());
//					cell.setCellValue(text);
//				} else {
//					RichTextString text = new XSSFRichTextString(headerField.getHeaderName());
//					cell.setCellValue(text);
//				}
//			}
//		}
//	}
//
//	/**
//	 * create excel file if not exists
//	 *
//	 * @param file
//	 * @return
//	 * @throws IOException
//	 */
//	private static File createAsNeeded(File file) throws IOException {
//		if (null == file) {
//			return null;
//		}
//		if (!file.exists()) {
//			file.createNewFile();
//		}
//		return file;
//	}
//
//	private static void workbook2File(Workbook workbook, File file) {
//		OutputStream out = null;
//		try {
//			out = new FileOutputStream(file);
//			workbook.write(out);
//			out.flush();
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//		} finally {
//			if (out != null) {
//				IOUtils.closeQuietly(out);
//			}
//		}
//	}
//
//
//	/**
//	 * Excel表头字段
//	 */
//	public static final class HeaderField {
//		/**
//		 * 表头字段名
//		 */
//		private String headerName;
//		/**
//		 * 对应实体属性名
//		 */
//		private String fieldName;
//
//		public HeaderField(String headerName, String fieldName) {
//			this.headerName = headerName;
//			this.fieldName = fieldName;
//		}
//
//		public String getHeaderName() {
//			return headerName;
//		}
//
//		public void setHeaderName(String headerName) {
//			this.headerName = headerName;
//		}
//
//		public String getFieldName() {
//			return fieldName;
//		}
//
//		public void setFieldName(String fieldName) {
//			this.fieldName = fieldName;
//		}
//	}
//
//	/**
//	 * 根据注解 {@link ExcelField} 解析需要导出的属性
//	 *
//	 * @param obj
//	 * @param <T>
//	 * @return
//	 * @throws IllegalAccessException
//	 */
//	private static <T> List<HeaderField> resolveAnnotions(T obj) throws IllegalAccessException {
//		if (null == obj) {
//			return null;
//		}
//		Field[] fields = obj.getClass().getDeclaredFields();
//		if (null == fields || fields.length == 0) {
//			return null;
//		}
//		List<HeaderField> headerFields = new ArrayList<>();
//		for (Field field : fields) {
//			if (!field.isAccessible())
//				field.setAccessible(true);
//			if (field.isAnnotationPresent(ExcelField.class)) {
//				ExcelField excelField = field.getDeclaredAnnotation(ExcelField.class);
//				String alias = excelField.alias();
//				String name = field.getName();
//				headerFields.add(new HeaderField(alias, name));
//			}
//		}
//		return headerFields;
//	}
//
//	private static String getCellValue(XSSFCell cell) {
//		String cellValue = "";
//		if (null != cell) {
//			switch (cell.getCellType()) {
//				case XSSFCell.CELL_TYPE_NUMERIC:
//					if (DateUtil.isCellDateFormatted(cell)) {
//						Date theDate = cell.getDateCellValue();
//						cellValue = com.zhangmen.market.common.DateUtil.format(theDate, "yyyy-MM-dd HH:mm:ss");
//					} else {
//						double tmp = cell.getNumericCellValue();
//						//判断小数位是否有值
//						if (tmp % 1 == 0) {
//							cellValue = String.valueOf((int) tmp);
//						} else {
//							cellValue = String.valueOf(tmp);
//						}
//					}
//					break;
//				case XSSFCell.CELL_TYPE_STRING:
//					cellValue = cell.getStringCellValue();
//					break;
//				case XSSFCell.CELL_TYPE_BOOLEAN:
//					cellValue = cell.getBooleanCellValue() + "";
//					break;
//				case XSSFCell.CELL_TYPE_ERROR:
//					cellValue = "ILLEGAL_VALUE";
//					break;
//				case XSSFCell.CELL_TYPE_FORMULA:
//					cellValue = cell.getCellFormula() + "";
//					break;
//				case XSSFCell.CELL_TYPE_BLANK:
//				default:
//					break;
//			}
//		}
//		return cellValue;
//	}
//
//	public static List<Field> getExcelField(Class<?> clazz, List<Field> fieldList) {
//		if (fieldList == null) {
//			fieldList = new ArrayList<>();
//		}
//		Field[] fields = clazz.getDeclaredFields();
//		for (Field field : fields) {
//			if (field.isAnnotationPresent(ExcelField.class)) {
//				fieldList.add(field);
//			}
//		}
//		if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
//			getExcelField(clazz.getSuperclass(), fieldList);
//		}
//		return fieldList;
//	}
//
//	private static Object getFieldValue(Field field, String cellValue) {
//		if (StringUtils.isBlank(cellValue)) {
//			return null;
//		}
//		Object obj = null;
//		String fieldType = field.getType().getName();
//		switch (fieldType) {
//			case "char":
//			case "java.lang.Character":
//			case "java.lang.String":
//				obj = cellValue;
//				break;
//			case "java.util.Date":
//				obj = com.zhangmen.market.common.DateUtil.parseStr2Date("yyyy-MM-dd HH:mm:ss", cellValue);
//				break;
//			case "java.time.LocalDateTime":
//				obj = LocalDateTime.parse(cellValue, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//				break;
//			case "java.lang.Integer":
//				obj = Integer.valueOf(cellValue);
//				break;
//			case "int":
//			case "float":
//			case "double":
//			case "java.lang.Double":
//			case "java.lang.Float":
//			case "java.lang.Long":
//			case "java.lang.Short":
//			case "java.math.BigDecimal":
//				obj = new BigDecimal(cellValue);
//				break;
//			default:
//				break;
//		}
//		return obj;
//	}
//}

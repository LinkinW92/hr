package my.linkin.thread;


import com.zhangmen.market.common.BizException;
import com.zhangmen.market.common.ExcelField;
import com.zhangmen.market.common.ResultCode;
import com.zhangmen.market.util.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Auther: chunhui.wu
 * @Date: 2019/8/12 13:05
 * @Description: 输入excel文件流, 每一个对应一个对象R (R需要加ExcelField注解才能获取到对应列信息)
 */
@Slf4j
public abstract class AbstractExcelProducer<R> extends DataProducer<R> {
    private DataProcessCenter center;
    private MultipartFile excelEntity;
    private Class<R> clz;

    public AbstractExcelProducer(DataProcessCenter center, MultipartFile excelEntity, Class<R> clz) {
        this.center = center;
        this.excelEntity = excelEntity;
        this.clz = clz;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            this.produce();
        }
    }

    @Override
    public R produce() {
        try {
            XSSFWorkbook wb = new XSSFWorkbook(excelEntity.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);
            doCheckHeader(sheet.getRow(0));
            List<Field> fields = ExcelUtils.getExcelField(clz, null);
            int totalRows = sheet.getPhysicalNumberOfRows();
            for (int i = 1; i < totalRows; i++) {
                XSSFRow row = sheet.getRow(i);
                R obj = clz.newInstance();
                int col = 0;
                for (Field field : fields) {
                    XSSFCell cell = row.getCell(col++);
                    ExcelUtils.setObjStringField(obj, field, cell);
                }
                this.center.produce(obj);
            }
        } catch (IOException e) {
            log.error("读入Excel文件异常:{}", e);
            this.center.onError(new Exception("文件读取异常"));
        } catch (Exception e) {
            log.error("【生产者】生产元素异常:{}", e);
            this.center.onError(new Exception("文件读取异常"));
        } finally {
            this.center.countDown();//线程计数-1
            this.center.stopConsumer();
            Thread.currentThread().interrupt();
        }
        return null;
    }


    /*
     * 当前版本,要求excel导入的列的顺序与对应实体类的属性声明顺序相同
     */
    private void doCheckHeader(XSSFRow header) {
        List<String> headers = new ArrayList<>();
        for (Field field : clz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(ExcelField.class)) {
                headers.add(field.getAnnotation(ExcelField.class).alias());
            }
        }
        List<String> cellNames = new ArrayList<>();
        Iterator<Cell> cells = header.cellIterator();
        while (cells.hasNext()) {
            cellNames.add(cells.next().getStringCellValue().trim());
        }
        if (cellNames.size() < headers.size() || !cellNames.containsAll(headers)) {
            throw new BizException(ResultCode.BIZ_EXCEPTION, "导入文件格式不正确,请参照导入模板");
        }
    }
}
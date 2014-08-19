package easy.test.source.workbook;

import org.apache.poi.ss.usermodel.Workbook;

import easy.test.source.TestDataSource;
import easy.test.util.WorkbookUtils;

public class WorkbookTestDataSource implements TestDataSource {

    private Workbook workbook;

    public WorkbookTestDataSource(Workbook workbook) {
        this.workbook = workbook;
    }

    @Override
    public String[][] getDataSet(String name) {
        return WorkbookUtils.getDataSet(name, workbook);
    }

}

package easy.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WorkbookUtils {

    private static Pattern namedCellFormulaPattern = Pattern.compile("^'?([^']+)'?\\!(\\$[A-Z]+)\\$(\\d+)$");

    private static final DataFormatter dataFormatter = new DataFormatter();

    public static Workbook load(File file) {
        Workbook workbook = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);

            if (file.getName().contains("xlsx")) {
                try {
                    workbook = new XSSFWorkbook(fileInputStream);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    workbook = new HSSFWorkbook(fileInputStream);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (FileNotFoundException e1) {
            throw new RuntimeException(e1);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }

        return workbook;
    }

    public static String[][] getDataSet(String cellName, Workbook workbook) {
        Cell namedCellStart = WorkbookUtils.getNamedCell(workbook, cellName);
        Cell namedCellEnd = WorkbookUtils.getNamedCell(workbook, cellName + "End");

        int cStart = namedCellStart.getColumnIndex();
        int rStart = namedCellStart.getRowIndex() + 1;
        int cEnd = namedCellEnd.getColumnIndex();
        int rEnd = namedCellEnd.getRowIndex() - 1;

        String[][] result = new String[rEnd - rStart + 1][cEnd - cStart + 1];

        Sheet currentSheet = namedCellStart.getSheet();
        for (int i = rStart; i <= rEnd; i++) {
            Row row = currentSheet.getRow(i);
            if (WorkbookUtils.isRowEmpty(row)) {
                continue;
            }

            for (int i2 = cStart; i2 <= cEnd; i2++) {
                Cell cell = row.getCell(i2);
                String cellValue = WorkbookUtils.getCellAsString(cell);
                if (StringUtils.isBlank(cellValue)) {
                    continue;
                }
                result[i - rStart][i2 - cStart] = cellValue;
            }
        }
        return result;
    }

    private static String getCellAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        String result;
        switch (cell.getCellType()) {
        case (Cell.CELL_TYPE_STRING):
            result = cell.getStringCellValue();
            break;
        case (Cell.CELL_TYPE_BOOLEAN):
            result = String.valueOf(cell.getBooleanCellValue());
            break;
        case (Cell.CELL_TYPE_FORMULA):
            FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
            Cell valueCell = evaluator.evaluateInCell(cell);
            result = getCellAsString(valueCell);
            break;
        default:
            return dataFormatter.formatCellValue(cell);
        }

        return result;
    }

    private static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (short i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && StringUtils.isNotBlank(getCellAsString(cell))) {
                return false;
            }
        }
        return true;
    }

    private static Cell getNamedCell(Workbook workbook, String name) {
        Name workbookName = workbook.getName(name);
        if (workbookName == null) {
            throw new RuntimeException("Workbook contains no name definition for name " + name);
        }
        String locationFormula = workbookName.getRefersToFormula();

        Matcher m = namedCellFormulaPattern.matcher(locationFormula);
        if (m.matches()) {
            CellReference cellRef = new CellReference(m.group(2) + m.group(3));
            return workbook.getSheet(m.group(1)).getRow(cellRef.getRow()).getCell(cellRef.getCol());
        } else {
            throw new RuntimeException("Invalid cell location " + locationFormula + " for named cell " + name);
        }
    }

}

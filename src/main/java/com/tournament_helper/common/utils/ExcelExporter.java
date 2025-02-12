package com.tournament_helper.common.utils;

import com.tournament_helper.common.constants.Teams;
import com.tournament_helper.domain.SearchedMatchDetails;
import com.tournament_helper.domain.Stats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {

    public static void exportToExcel(List<SearchedMatchDetails> matchesDetails, String filePath) {
        Workbook workbook;
        File file = new File(filePath);

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = new XSSFWorkbook(fis);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        } else {
            workbook = new XSSFWorkbook();
        }

        int sheetCount = workbook.getNumberOfSheets();
        String sheetName;
        if(sheetCount != 0) {
            sheetName = "Match " + (sheetCount);
        } else {
            sheetName = "Match 1";
        }

        Sheet sheet = workbook.createSheet(sheetName);

        Row headerRow = sheet.createRow(0);
        String[] columns = {"Placement", "Team ID", "Match End", "Hero", "Player", "Kills", "Deaths", "Assists", "KDA", "Damage Done", "Damage Taken", "Heal Done"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(createHeaderStyle(workbook));
        }

        CellStyle teamStyleGrey = createColoredStyle(workbook, IndexedColors.GREY_25_PERCENT.getIndex());
        CellStyle teamStyleWhite = createColoredStyle(workbook, IndexedColors.WHITE.getIndex());
        CellStyle teamStyle = teamStyleGrey;

        int rowNum = 1;
        int playerCount = 0;

        for (SearchedMatchDetails match : matchesDetails) {
            Row row = sheet.createRow(rowNum++);
            int rowIndex = 0;
            Stats stats = match.getStats();
            row.createCell(rowIndex).setCellValue(match.getPlacement());
            row.createCell(++rowIndex).setCellValue(Teams.getTeamById(match.getTeamId() + 1));
            row.createCell(++rowIndex).setCellValue(match.getMatchEnd());
            row.createCell(++rowIndex).setCellValue(match.getHunter().toString());
            row.createCell(++rowIndex).setCellValue(match.getPlayer().getUniqueDisplayName());
            row.createCell(++rowIndex).setCellValue(stats.getKills());
            row.createCell(++rowIndex).setCellValue(stats.getDeaths());
            row.createCell(++rowIndex).setCellValue(stats.getAssists());
            row.createCell(++rowIndex).setCellValue(( (double) stats.getKills() + stats.getAssists()) / (stats.getDeaths() == 0 ? 1 : stats.getDeaths()));
            row.createCell(++rowIndex).setCellValue(stats.getDamageDone());
            row.createCell(++rowIndex).setCellValue(stats.getDamageTaken());
            row.createCell(++rowIndex).setCellValue(stats.getHealingGiven());

            if(playerCount % 4 == 0) {
                if(teamStyle == teamStyleGrey) {
                    teamStyle = teamStyleWhite;
                } else {
                    teamStyle = teamStyleGrey;
                }
            }
            for (int i = 0; i < columns.length; i++) {
                row.getCell(i).setCellStyle(teamStyle);
            }

            playerCount++;
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
            System.out.println("Planilha Excel criada com sucesso: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
                ExcelScoreCalculator.updateTotalSheet(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private static CellStyle createColoredStyle(Workbook workbook, short colorIndex) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(colorIndex);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}

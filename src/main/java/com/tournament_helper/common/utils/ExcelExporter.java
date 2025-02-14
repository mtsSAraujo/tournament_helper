package com.tournament_helper.common.utils;

import com.tournament_helper.common.constants.Hunters;
import com.tournament_helper.common.constants.Teams;
import com.tournament_helper.domain.ChampsColorsNUrl;
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
            sheetName = "Match " + ((sheetCount) - 2);
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
            generateHuntersNColorsSheet(filePath);
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

    private static void generateHuntersNColorsSheet(String filePath) {
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

        // Se já existir planilha, nao cria novamente
        if(workbook.getSheet("HuntersColorsAndImages") != null) {
            return;
        }

        Sheet sheet = workbook.createSheet("HuntersColorsAndImages");
        Row headerRow = sheet.createRow(0);
        String[] columns = {"Hero", "Color", "GifImageURL", "SplashImageURL"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(createHeaderStyle(workbook));
        }

        final String PLACE_HOLDER = "place_holder";

        final List<ChampsColorsNUrl> CHAMPS_AND_COLORS_DATA = List.of(
                ChampsColorsNUrl.builder().hunter(Hunters.BRALL).color("#A52A2A").gifUrl("https://drive.google.com/thumbnail?id=1ZgyngwbBsoLZUSgiDdPGfhbwKpCbIo1F&sz=w1000").splashUrl(PLACE_HOLDER).build(),
                ChampsColorsNUrl.builder().hunter(Hunters.GHOST).color("#00A693").gifUrl("https://drive.google.com/thumbnail?id=1TIOERE0lHD2AHzIoYUwyuIeR3FeFh6rf&sz=w1000").splashUrl(PLACE_HOLDER).build(),
                ChampsColorsNUrl.builder().hunter(Hunters.JIN).color("#B5B5B5").gifUrl("https://drive.google.com/thumbnail?id=1O70KvWKCm3zQ_YpM6pl3WsdBbGuGaLOg&sz=w1000").splashUrl(PLACE_HOLDER).build(),
                ChampsColorsNUrl.builder().hunter(Hunters.JOULE).color("#FFD700").gifUrl("https://drive.google.com/thumbnail?id=14FSGuNoVT3rW4w_MMOGiiEGxrRXYAz4y&sz=w1000").splashUrl(PLACE_HOLDER).build(),
                ChampsColorsNUrl.builder().hunter(Hunters.MYTH).color("#228B22").gifUrl("https://drive.google.com/thumbnail?id=112VU11PkR2o0Pz4msot0pxYfJzwNFHx7&sz=w1000").splashUrl(PLACE_HOLDER).build(),
                ChampsColorsNUrl.builder().hunter(Hunters.SHIV).color("#2F4F4F").gifUrl("https://drive.google.com/thumbnail?id=1UITQHhp5IzfkvISrgx9oVuTaV5B1Hy8L&sz=w1000").splashUrl(PLACE_HOLDER).build(),
                ChampsColorsNUrl.builder().hunter(Hunters.SHRIKE).color("#E8D8C4").gifUrl("https://drive.google.com/thumbnail?id=1gbbXV89EbaBsKnY1SwCSSctaHL4CyHi8&sz=w1000").splashUrl(PLACE_HOLDER).build(),
                ChampsColorsNUrl.builder().hunter(Hunters.BISHOP).color("#FF8C00").gifUrl("https://drive.google.com/thumbnail?id=1dz_Tzph4RNw-BmA_OXDBlVaz1zZgRbbw&sz=w1000").splashUrl(PLACE_HOLDER).build(),
                ChampsColorsNUrl.builder().hunter(Hunters.KINGPIN).color("#D2738A").gifUrl("https://drive.google.com/thumbnail?id=10B9r50utOji5aNQwsgWcx6zmIof8LuMG&sz=w1000").splashUrl(PLACE_HOLDER).build(),
                ChampsColorsNUrl.builder().hunter(Hunters.FELIX).color("#D52A2A").gifUrl("https://drive.google.com/thumbnail?id=16DNChqIGjEykXGMSoeDIr_UohoVsdaxw&sz=w1000").splashUrl(PLACE_HOLDER).build(),
                ChampsColorsNUrl.builder().hunter(Hunters.OATH).color("#FFFACD").gifUrl("https://drive.google.com/thumbnail?id=1Y8aYbu5QrJg2RU4n5MW0GrLPsX9HjF6Q&sz=w1000").splashUrl(PLACE_HOLDER).build(),
                ChampsColorsNUrl.builder().hunter(Hunters.ELLUNA).color("#D3D3D3").gifUrl("https://drive.google.com/thumbnail?id=1wzSPHbobzPuV6WIooAkYUI4nbiTdy362&sz=w1000").splashUrl(PLACE_HOLDER).build(),
                ChampsColorsNUrl.builder().hunter(Hunters.ZEPH).color("#C5E1A5").gifUrl("https://drive.google.com/thumbnail?id=1GdcfjToDXfQoVWwyHa0YybkvnAoPIh8r&sz=w1000").splashUrl(PLACE_HOLDER).build(),
                ChampsColorsNUrl.builder().hunter(Hunters.BEEBO).color("#B3E5FC").gifUrl("https://drive.google.com/thumbnail?id=1iICUpMvc-EhDsLVjkipYwZcMwwm3jBUO&sz=w1000").splashUrl(PLACE_HOLDER).build(),
                ChampsColorsNUrl.builder().hunter(Hunters.CELESTE).color("#81D4FA").gifUrl("https://drive.google.com/thumbnail?id=159-zXm7GuECB6RbnMTp1kxL5hYDxXugB&sz=w1000").splashUrl(PLACE_HOLDER).build(),
                ChampsColorsNUrl.builder().hunter(Hunters.HUDSON).color("#D8BFD8").gifUrl("https://drive.google.com/thumbnail?id=102rRuC9ixymiT91aKwiZoHmi-QihVqmB&sz=w1000").splashUrl(PLACE_HOLDER).build(),
                ChampsColorsNUrl.builder().hunter(Hunters.VOID).color("#4B0082").gifUrl("https://drive.google.com/thumbnail?id=1CQzsdFYbS2Jo1OliBvPgK_nfP3ZWYlxz&sz=w1000").splashUrl(PLACE_HOLDER).build(),
                ChampsColorsNUrl.builder().hunter(Hunters.CRYSTA).color("#FF69B4").gifUrl("https://drive.google.com/thumbnail?id=1V8e-XZBzgnWS2Ism8g80HPJjiAaNFGn5&sz=w1000").splashUrl(PLACE_HOLDER).build()
        );

        int rowNum = 1;
        for (ChampsColorsNUrl champ: CHAMPS_AND_COLORS_DATA) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(champ.getHunter().name());
            row.createCell(1).setCellValue(champ.getColor());
            row.createCell(2).setCellValue(champ.getGifUrl());
            row.createCell(3).setCellValue(champ.getSplashUrl());
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

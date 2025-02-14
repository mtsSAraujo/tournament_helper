package com.tournament_helper.common.utils;

import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelScoreCalculator {
    private static final Map<Integer, Integer> POSITION_POINTS = new HashMap<>();

    static {
        POSITION_POINTS.put(1, 20);
        POSITION_POINTS.put(2, 15);
        POSITION_POINTS.put(3, 12);
        POSITION_POINTS.put(4, 10);
        POSITION_POINTS.put(5, 8);
        POSITION_POINTS.put(6, 6);
        POSITION_POINTS.put(7, 4);
        POSITION_POINTS.put(8, 2);
        POSITION_POINTS.put(9, 1);
        POSITION_POINTS.put(10, 0);
    }

    public static void updateTotalSheet(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Arquivo não encontrado: " + filePath);
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Map<String, Integer> teamScores = new HashMap<>();
            Map<String, Pair<Double, Integer>> playerKDAStats = new HashMap<>();

            for (Sheet sheet : workbook) {
                if (sheet.getSheetName().startsWith("Match ")) {
                    processMatchSheet(sheet, teamScores, playerKDAStats);
                }
            }

            updateTotalSheetData(workbook, teamScores);
            updatePlayersKDASheetData(workbook, playerKDAStats);

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            System.out.println("Planilha 'Total' atualizada com sucesso!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processMatchSheet(Sheet sheet, Map<String, Integer> teamScores, Map<String, Pair<Double, Integer>> playerKDAStats) {
        Map<String, Integer> teamKills = new HashMap<>();
        Map<String, Integer> teamPosition = new HashMap<>();
        Map<String, Double> playerKDA = new HashMap<>();

        int numRows = sheet.getPhysicalNumberOfRows();

        for (int i = 1; i < numRows; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            int position = (int) row.getCell(0).getNumericCellValue();
            String teamTag = row.getCell(1).getStringCellValue();
            String playerName = row.getCell(4).getStringCellValue();
            int kills = (int) row.getCell(5).getNumericCellValue();
            double kda = row.getCell(8).getNumericCellValue();

            teamKills.put(teamTag, teamKills.getOrDefault(teamTag, 0) + kills);
            playerKDA.put(playerName, kda);

            teamPosition.putIfAbsent(teamTag, position);
        }

        // Calcular pontuação final de cada time
        for (String team : teamPosition.keySet()) {
            int positionPoints = POSITION_POINTS.getOrDefault(teamPosition.get(team), 0);
            int killPoints = teamKills.getOrDefault(team, 0);

            int totalPoints = positionPoints + killPoints;

            teamScores.put(team, teamScores.getOrDefault(team, 0) + totalPoints);
        }

        for (Map.Entry<String, Double> entry : playerKDA.entrySet()) {
            String playerName = entry.getKey();
            double newKDA = entry.getValue();

            Pair<Double, Integer> existingData = playerKDAStats.getOrDefault(playerName, new Pair<>(0.0, 0));
            double totalKDA = existingData.getFirst() * existingData.getSecond() + newKDA;
            int newMatchCount = existingData.getSecond() + 1;
            double updatedAvgKDA = totalKDA / newMatchCount;

            playerKDAStats.put(playerName, new Pair<>(updatedAvgKDA, newMatchCount));
        }
    }

    private static void updateTotalSheetData(Workbook workbook, Map<String, Integer> teamScores) {
        Sheet totalSheet = workbook.getSheet("Total");
        if (totalSheet == null) {
            totalSheet = workbook.createSheet("Total");
        } else {
            int lastRow = totalSheet.getLastRowNum();
            for (int i = lastRow; i >= 0; i--) {
                totalSheet.removeRow(totalSheet.getRow(i));
            }
        }

        Row headerRow = totalSheet.createRow(0);
        headerRow.createCell(0).setCellValue("TAG");
        headerRow.createCell(1).setCellValue("PONTOS");

        int rowNum = 1;
        List<Map.Entry<String, Integer>> sortedTeams = new ArrayList<>(teamScores.entrySet());
        sortedTeams.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        for (Map.Entry<String, Integer> entry : sortedTeams) {
            Row row = totalSheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }
        totalSheet.autoSizeColumn(0);
        totalSheet.autoSizeColumn(1);
    }

    private static void updatePlayersKDASheetData(Workbook workbook, Map<String, Pair<Double, Integer>> playerKDAStats) {
        Sheet playersSheet = workbook.getSheet("Players KDA");
        if (playersSheet == null) {
            playersSheet = workbook.createSheet("Players KDA");
        } else {
            int lastRow = playersSheet.getLastRowNum();
            for (int i = lastRow; i >= 0; i--) {
                playersSheet.removeRow(playersSheet.getRow(i));
            }
        }

        Row headerRow = playersSheet.createRow(0);
        headerRow.createCell(0).setCellValue("PLAYER");
        headerRow.createCell(1).setCellValue("KDA MÉDIO");
        headerRow.createCell(2).setCellValue("PARTIDAS");

        int rowNum = 1;
        List<Map.Entry<String, Pair<Double, Integer>>> sortedPlayers = new ArrayList<>(playerKDAStats.entrySet());
        sortedPlayers.sort((a, b) -> Double.compare(b.getValue().getFirst(), a.getValue().getFirst()));

        for (Map.Entry<String, Pair<Double, Integer>> entry : sortedPlayers) {
            Row row = playersSheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue().getFirst());
            row.createCell(2).setCellValue(entry.getValue().getSecond());
        }
        playersSheet.autoSizeColumn(0);
        playersSheet.autoSizeColumn(1);
        playersSheet.autoSizeColumn(2);
    }
}

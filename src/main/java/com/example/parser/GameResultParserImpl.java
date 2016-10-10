package com.example.parser;

import com.example.GameResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GameResultParserImpl implements GameResultParser {
    private static final String datePatternString = "(\\d{1,2}月.*\\d{1,2}日)";
    private static final String resultsPatternString =
            "<tr>" +
                    "<td>(<a.*\">)?([^<]*)(</a>)?</td>" +
                    "<td.*>(.)</td>" +
                    "<td>(<a.*\">)?([^<]*)(</a>)?</td>" +
                    "<td>(<a.*\">)?([^<]*)(</a>)?</td>" +
                    "<td.*>(.)</td>" +
                    "<td>.*</td>" +
                    "</tr>";
//<tr>
//    <td>順位戦A級 </td>
//    <td class="tac">●</td>
//    <td><a href="/player/pro/208.html">行方尚史</a></td>
//    <td><a href="/player/pro/175.html">羽生善治</a></td>
//    <td class="tac">○</td>
//    <td><a href="http://www.meijinsen.jp/" target="_blank">名人戦棋譜速報</a>・<a href="http://www.shogi.or.jp/mobile/">携帯中継</a></td>
//</tr>
    private String year;
    private String month;
    private String day;

    public List<GameResult> parseResultsOnShogiAssoc(String resultPage, String targetGameDate) {
        String[] currentDate = Pattern.compile("[年月日]").split(targetGameDate);
        this.year = currentDate[0] + "年";
        this.month = currentDate[1] + "月";
        this.day = currentDate[2] + "日";

        Pattern datePattern = Pattern.compile(datePatternString);
        Pattern resultsPattern = Pattern.compile(resultsPatternString);
        String gameDate = "";
        List<GameResult> gameResults = new ArrayList<>();

        List<String> resultPageRows = Arrays.asList(resultPage.split("\n"));

        for (String resultPageRow : resultPageRows) {
            Matcher dateResult = datePattern.matcher(resultPageRow);
            if (dateResult.find()) {
                gameDate = convertGameDate(dateResult.group(1));
                continue;
            } else {
                Matcher matchingResult = resultsPattern.matcher(resultPageRow);
                if (matchingResult.find()) {
                    gameResults.add(new GameResult(
                                    matchingResult.group(6),
                                    convertResultCharacter(matchingResult.group(4)),
                                    matchingResult.group(9),
                                    convertResultCharacter(matchingResult.group(11)),
                                    matchingResult.group(2).trim(),
                                    gameDate
                            )
                    );
                }
            }
        }
        return gameResults.stream()
                .filter(gameResult -> gameResult.getGameDate().contains(targetGameDate))
                .collect(Collectors.toList());
    }

    private String convertResultCharacter(String resultCharacter) {
        switch (resultCharacter) {
            case "○":
            case "□":
                return "勝";
            case "●":
            case "■":
                return "負";
            default:
                return "";
        }
    }

    private String convertGameDate(String gameDate) {
        List<String> gameDates = Arrays.asList(gameDate.split("・"));
        if (gameDates.size() == 1) {
            return year + gameDate;
        } else {
            String firstDay = year + gameDates.get(0) + "日";
            String secondDay =
                    gameDates.get(1).contains("月") ? year + gameDates.get(1) : year + month + gameDates.get(1);
            return firstDay + "," + secondDay;
        }
    }
}

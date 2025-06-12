package databaseHelpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class SqlLoader {

    private static final Logger Log = LogManager.getLogger(SqlLoader.class.getName());
    private static String sql;

    public SqlLoader(String filePath) {
        parseSql(filePath);
    }

    private void parseSql(String filePath) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(filePath)), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }

        } catch (Exception ex) {
            Log.error(ex);
        }
        sql = sb.toString().trim();
    }

    public List<String> getParamsOrder() {
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(":(\\w+)").matcher(sql);
        java.util.List<String> paramNames = new java.util.ArrayList<>();
        while (matcher.find()) {
            paramNames.add(matcher.group(1));
        }
        return paramNames;
    }

    public String getParsedSql() {
        return sql.replaceAll(":(\\w+)", "?");
    }

}

package net.greenmanov.anime.ImageSorter.fetching;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilenameFetcher extends AFetcher {

    // KONACHAN
    private static final Pattern KONACHAN_PATTERN = Pattern.compile("Konachan.com - (\\d+)");
    private static final String KONACHAN_URL = "http://konachan.com/post/show/";

    // YANDER.RE
    private static final Pattern YANDE_RE_PATTERN = Pattern.compile("yande\\.re (\\d+)");
    private static final String YANDE_RE_URL = "https://yande.re/post/show/";

    private static final Logger LOGGER = LogManager.getLogger(FilenameFetcher.class.getName());

    public FilenameFetcher() {
    }

    /**
     * Fetch info for all files in path, saves them into database and moves them
     *
     * @param from          Source dir
     * @param to            Dir for fetched files
     * @param minSimilarity Minimal similarity to be considered info match
     * @param delay         Delay between api calls in ms
     * @param noMatchDir    Dir for files without match, can be null
     * @throws InterruptedException When problem with thread sleeping for delay
     */
    @Override
    public void fetch(Path from, Path to, int minSimilarity, int delay, Path noMatchDir) throws InterruptedException {
        super.fetch(from, to, minSimilarity, delay, noMatchDir);
    }

    /**
     * Fetch info about file and moves it if needed
     *
     * @param filePath      Path to file
     * @param to            Dir for parsed files
     * @param minSimilarity Minimal similarity to be considered info match
     * @param noMatchDir    Dir for files without match, can be null
     */
    @Override
    protected void fetchFile(Path filePath, Path to, int minSimilarity, Path noMatchDir) throws InterruptedException {
        String fileName = filePath.getFileName().toString();
        String url = getKonachanUrl(fileName);
        url = url == null ? getYandeReUrl(fileName) : url;
        if (url != null) {
            fetchUrl(url, filePath, to);
        }
    }

    /**
     * Get konachan url by file name if one exists
     *
     * @param fileName File name
     * @return string url or null
     */
    protected String getKonachanUrl(String fileName) {
        Matcher matcher = KONACHAN_PATTERN.matcher(fileName);
        if (matcher.find()) {
            int konachanId = Integer.valueOf(matcher.group(1));
            return KONACHAN_URL + konachanId;
        }
        return null;
    }

    /**
     * Get yande.re url by file name if one exists
     *
     * @param fileName File name
     * @return string url or null
     */
    protected String getYandeReUrl(String fileName) {
        Matcher matcher = YANDE_RE_PATTERN.matcher(fileName);
        if (matcher.find()) {
            int id = Integer.valueOf(matcher.group(1));
            return YANDE_RE_URL + id;
        }
        return null;
    }
}
